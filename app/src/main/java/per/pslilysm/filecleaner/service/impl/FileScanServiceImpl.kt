package per.pslilysm.filecleaner.service.impl

import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import android.util.Log
import per.pslilysm.filecleaner.service.FileScanResultSummary
import per.pslilysm.filecleaner.service.FileScanService
import per.pslilysm.filecleaner.service.FileScanServiceConfig
import pers.pslilysm.sdk_library.extention.throwIfMainThread
import pers.pslilysm.sdk_library.util.concurrent.ExecutorsLinkedBlockingQueue
import java.io.File
import java.util.concurrent.CancellationException
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * 文件扫描服务实现
 *
 * @author caoxuedong
 * Created on 2023/10/24 14:40
 * @since 1.0
 */
class FileScanServiceImpl : FileScanService {

    companion object {
        private const val TAG = "FileScanServiceImpl"
    }

    private val ioThreadNum = AtomicInteger()

    private lateinit var ioExecutors: ThreadPoolExecutor

    private fun initIOExecutorsIfNeed() {
        if (!this::ioExecutors.isInitialized || ioExecutors.isShutdown) {
            val corePoolSize = 0
            val maxPoolSize = Runtime.getRuntime().availableProcessors() * 10
            val keepAliveTimeSeconds = 2
            val maxQueueSize = maxPoolSize * 0xFF
            val workQueue = ExecutorsLinkedBlockingQueue(maxQueueSize)
            val threadFactory = ThreadFactory { r: Runnable? -> Thread(r, "fss-io-${ioThreadNum.incrementAndGet()}-thread") }
            val rejectedExecutionHandler = RejectedExecutionHandler { r: Runnable, executor: ThreadPoolExecutor ->
                if (!executor.isShutdown && workQueue.size < maxQueueSize) {
                    executor.execute(r)
                } else {
                    throw RejectedExecutionException("Task $r rejected from $executor")
                }
            }
            ioExecutors = ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTimeSeconds.toLong(), TimeUnit.SECONDS,
                workQueue, threadFactory, rejectedExecutionHandler)
            workQueue.setExecutor(ioExecutors)
        }
    }

    @Throws(CancellationException::class)
    override fun startScan(): FileScanResultSummary {
        throwIfMainThread()
        Log.i(TAG, "startScan: prepare")
        val l = SystemClock.elapsedRealtime()
        initIOExecutorsIfNeed()
        val fileScanResultSummary = FileScanResultSummary()
        ioExecutors.execute {
            processDir(Environment.getExternalStorageDirectory(), fileScanResultSummary)
        }
        fileScanResultSummary.countDownLatch.await()
        if (ioExecutors.isShutdown) {
            throw CancellationException("scan task has been stopped")
        }
        val statFs = StatFs(Environment.getExternalStorageDirectory().absolutePath)
        fileScanResultSummary.storageTotalSize = statFs.blockCountLong * statFs.blockSizeLong
        fileScanResultSummary.calcSize()
        val scanCost = SystemClock.elapsedRealtime() - l
        fileScanResultSummary.scanCost = scanCost.toInt()
        return fileScanResultSummary
    }

    override fun stopScanIfNeed() {
        if (this::ioExecutors.isInitialized && ioExecutors.activeCount > 0) {
            Log.i(TAG, "stopScanIfNeed: stop now")
            ioExecutors.shutdownNow()
        }
    }

    private fun processDir(dir: File, fileScanResultSummary: FileScanResultSummary) {
        fileScanResultSummary.scanTaskNum.incrementAndGet()
        try {
            val listFiles = dir.listFiles()
            if (listFiles.isNullOrEmpty()) {
                fileScanResultSummary.emptyDirScanResult.fileQueue.offer(dir)
            } else {
                val listPair = listFiles.partition { it.isDirectory }
                for (lDir in listPair.first) {
                    try {
                        ioExecutors.execute { processDir(lDir, fileScanResultSummary) }
                    } catch (e: Exception) {
                        // the executors has been terminated
                        // means our task should be interrupt
                        return
                    }
                }
                for (lFile in listPair.second) {
                    val fileExt = lFile.extension
                    if (fileExt.isEmpty()) {
                        fileScanResultSummary.noExtScanResult.offerFileAndAddFileSize(lFile)
                    } else if (FileScanServiceConfig.imageFileExtSet.contains(lFile.extension)) {
                        fileScanResultSummary.imageScanResult.offerFileAndAddFileSize(lFile)
                    } else if (FileScanServiceConfig.videoFileExtSet.contains(lFile.extension)) {
                        fileScanResultSummary.videoScanResult.offerFileAndAddFileSize(lFile)
                    } else if (FileScanServiceConfig.audioFileExtSet.contains(lFile.extension)) {
                        fileScanResultSummary.audioScanResult.offerFileAndAddFileSize(lFile)
                    } else if (FileScanServiceConfig.documentFileExtSet.contains(lFile.extension)) {
                        fileScanResultSummary.documentScanResult.offerFileAndAddFileSize(lFile)
                    } else if (FileScanServiceConfig.apkFileExt == lFile.extension) {
                        fileScanResultSummary.apkFileScanResult.offerFileAndAddFileSize(lFile)
                    } else if (FileScanServiceConfig.compressedFileExt.contains(lFile.extension)) {
                        fileScanResultSummary.compressedFileScanResult.offerFileAndAddFileSize(lFile)
                    } else {
                        fileScanResultSummary.unknownExtScanResult.offerFileAndAddFileSize(lFile)
                    }
                }
            }
        } finally {
            if (fileScanResultSummary.scanTaskNum.decrementAndGet() <= 0) {
                fileScanResultSummary.countDownLatch.countDown()
            }
        }
    }

    private fun FileScanResultSummary.calcSize() {
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val externalSf = StatFs(Environment.getExternalStorageDirectory().path)
            this.storageAvailableSize = externalSf.availableBytes
            this.storageTotalSize = externalSf.totalBytes
            this.storageUsedSize = this.storageTotalSize - this.storageAvailableSize
            this.otherFileSize = this.storageUsedSize -
                    (this.imageScanResult.queueFileSize.get() +
                            this.videoScanResult.queueFileSize.get() +
                            this.audioScanResult.queueFileSize.get() +
                            this.documentScanResult.queueFileSize.get() +
                            this.apkFileScanResult.queueFileSize.get() +
                            this.compressedFileScanResult.queueFileSize.get() +
                            this.noExtScanResult.queueFileSize.get() +
                            this.unknownExtScanResult.queueFileSize.get())
        }
    }

}