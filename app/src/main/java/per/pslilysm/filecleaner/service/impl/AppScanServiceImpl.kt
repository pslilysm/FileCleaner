package per.pslilysm.filecleaner.service.impl

import android.app.usage.StorageStatsManager
import android.content.pm.ApplicationInfo
import android.os.SystemClock
import android.os.storage.StorageManager
import android.util.Log
import per.pslilysm.filecleaner.entity.AppScanResult
import per.pslilysm.filecleaner.entity.AppScanResultSummary
import per.pslilysm.filecleaner.extension.totalSize
import per.pslilysm.filecleaner.service.AppScanService
import per.pslilysm.sdk_library.app
import per.pslilysm.sdk_library.extention.throwIfMainThread
import per.pslilysm.sdk_library.util.concurrent.ExecutorsLinkedBlockingQueue
import java.io.File
import java.util.concurrent.CancellationException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Predicate


/**
 * 应用扫描服务
 *
 * @author caoxuedong
 * Created on 2023/11/30 16:47
 * @since 1.0
 */
class AppScanServiceImpl : AppScanService {

    companion object {
        private const val TAG = "AppScanServiceImpl"
    }

    private val ioThreadNum = AtomicInteger()

    private lateinit var ioExecutors: ThreadPoolExecutor

    private fun initIOExecutorsIfNeed() {
        if (!this::ioExecutors.isInitialized || ioExecutors.isShutdown) {
            val corePoolSize = 0
            val maxPoolSize = Runtime.getRuntime().availableProcessors() * 2
            val keepAliveTimeSeconds = 30
            val maxQueueSize = maxPoolSize * 0xFF
            val workQueue = ExecutorsLinkedBlockingQueue(maxQueueSize)
            val threadFactory = ThreadFactory { r: Runnable? -> Thread(r, "ass-io-${ioThreadNum.incrementAndGet()}-thread") }
            val rejectedExecutionHandler = RejectedExecutionHandler { r: Runnable, executor: ThreadPoolExecutor ->
                if (!executor.isShutdown && workQueue.size < maxQueueSize) {
                    executor.execute(r)
                } else {
                    throw RejectedExecutionException("Task $r rejected from $executor")
                }
            }
            ioExecutors = ThreadPoolExecutor(
                corePoolSize, maxPoolSize, keepAliveTimeSeconds.toLong(), TimeUnit.SECONDS,
                workQueue, threadFactory, rejectedExecutionHandler
            )
            workQueue.setExecutor(ioExecutors)
        }
    }

    @Throws(CancellationException::class)
    override fun start(): AppScanResultSummary {
        throwIfMainThread()
        Log.i(TAG, "startScan: prepare")
        val l = SystemClock.elapsedRealtime()
        val appScanResultSummary = AppScanResultSummary()
        val pkgManager = app.packageManager
        val storageService = app.getSystemService(StorageManager::class.java)
        val storageStatService = app.getSystemService(StorageStatsManager::class.java)
        val applicationInfoList = pkgManager.getInstalledApplications(0).apply {
            removeIf(Predicate {
                return@Predicate it.flags and ApplicationInfo.FLAG_SYSTEM == ApplicationInfo.FLAG_SYSTEM
            })
        }
        val countDownLatch = CountDownLatch(applicationInfoList.size)
        initIOExecutorsIfNeed()
        applicationInfoList.forEach { applicationInfo ->
            ioExecutors.execute {
                try {
                    val pkgName = applicationInfo.packageName
                    val path = File(app.dataDir.parent, pkgName)
                    val uuid = storageService.getUuidForPath(path)
                    val appStats = storageStatService.queryStatsForUid(uuid, applicationInfo.uid)
                    val appScanResult = AppScanResult(
                        applicationInfo,
                        pkgManager.getApplicationLabel(applicationInfo).toString(),
                        appStats.totalSize()
                    )
                    appScanResultSummary.appQueue.offer(appScanResult)
                    appScanResultSummary.queueSize.getAndAdd(appScanResult.appStorageSize)
                } finally {
                    countDownLatch.countDown()
                }
            }
        }
        countDownLatch.await()
        if (ioExecutors.isShutdown) {
            throw CancellationException("App scan task has been stopped")
        }
        Log.i(TAG, "startScan: scan app cost ${SystemClock.elapsedRealtime() - l} ms")
        return appScanResultSummary
    }

    override fun stopIfNeed() {
        if (this::ioExecutors.isInitialized && ioExecutors.activeCount > 0) {
            Log.i(TAG, "stopIfNeed: stop now")
            ioExecutors.shutdownNow()
        }
    }
}