package per.pslilysm.filecleaner.service.impl

import android.os.Environment
import android.os.SystemClock
import android.util.Log
import per.pslilysm.filecleaner.service.FileScanResult
import per.pslilysm.filecleaner.service.FileScanService
import per.pslilysm.filecleaner.service.FileScanServiceConfig
import pers.pslilysm.sdk_library.extention.throwIfMainThread
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.roundToInt

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

    private lateinit var ioExecutors: ExecutorService

    private fun initIOExecutorsIfNeed() {
        if (!this::ioExecutors.isInitialized || ioExecutors.isShutdown) {
            ioExecutors = Executors.newFixedThreadPool(
                (Runtime.getRuntime().availableProcessors() * 3.25).roundToInt()
            ) { r: Runnable? ->
                Thread(r, "fss-io-" + ioThreadNum.incrementAndGet() + "-thread")
            }
        }
    }

    override fun startScan(): FileScanResult {
        throwIfMainThread()
        Log.i(TAG, "startScan: prepare")
        val l = SystemClock.elapsedRealtime()
        initIOExecutorsIfNeed()
        val fileScanResult = FileScanResult()
        ioExecutors.execute {
            processDir(Environment.getExternalStorageDirectory(), fileScanResult)
        }
        fileScanResult.countDownLatch.await()
        Log.i(TAG, "startScan: done, cost ${SystemClock.elapsedRealtime() - l}ms")
        return fileScanResult
    }

    override fun stopScanIfNeed() {
        if (this::ioExecutors.isInitialized) {
            ioExecutors.shutdownNow()
//            ioExecutors.shutdown()
        }
    }

    private fun processDir(dir: File, fileScanResult: FileScanResult) {
        Log.d(TAG, "processDir() called with: dir = $dir")
        fileScanResult.scanTaskNum.incrementAndGet()
        try {
            val listFiles = dir.listFiles()
            if (listFiles.isNullOrEmpty()) {
                fileScanResult.emptyDirQueue.offer(dir)
            } else {
                val listPair = listFiles.partition { it.isDirectory }
                for (lDir in listPair.first) {
                    try {
//                        Thread.sleep(50)
                        ioExecutors.execute { processDir(lDir, fileScanResult) }
                    } catch (e: Exception) {
                        Log.e(TAG, "processDir: ", e)
                    }
                }
                for (lFile in listPair.second) {
                    val fileExt = lFile.extension
                    if (fileExt.isEmpty()) {
                        fileScanResult.noExtFileQueue.offer(lFile)
                    } else if (FileScanServiceConfig.knownFileExtSet.contains(lFile.extension)) {
                        fileScanResult.knownExtFileQueue.offer(lFile)
                    } else {
                        fileScanResult.unknownExtFileQueue.offer(lFile)
                    }
                }
            }
        } finally {
            if (fileScanResult.scanTaskNum.decrementAndGet() <= 0) {
                fileScanResult.countDownLatch.countDown()
            }
        }
    }

}