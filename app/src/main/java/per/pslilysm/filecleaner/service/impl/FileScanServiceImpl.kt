package per.pslilysm.filecleaner.service.impl

import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import android.util.Log
import per.pslilysm.filecleaner.service.FileScanService
import per.pslilysm.filecleaner.service.FileScanServiceConfig
import per.pslilysm.filecleaner.service.TotalFileScanResult
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

    override fun startScan(): TotalFileScanResult {
        throwIfMainThread()
        Log.i(TAG, "startScan: prepare")
        val l = SystemClock.elapsedRealtime()
        initIOExecutorsIfNeed()
        val totalFileScanResult = TotalFileScanResult()
        ioExecutors.execute {
            processDir(Environment.getExternalStorageDirectory(), totalFileScanResult)
        }
        totalFileScanResult.countDownLatch.await()
        Log.i(TAG, "startScan: done, cost ${SystemClock.elapsedRealtime() - l}ms")
        val statFs = StatFs(Environment.getExternalStorageDirectory().absolutePath)
        totalFileScanResult.storageTotalSize = statFs.blockCountLong * statFs.blockSizeLong
        totalFileScanResult.calcSize()
        return totalFileScanResult
    }

    override fun stopScanIfNeed() {
        if (this::ioExecutors.isInitialized) {
            ioExecutors.shutdownNow()
//            ioExecutors.shutdown()
        }
    }

    private fun processDir(dir: File, totalFileScanResult: TotalFileScanResult) {
        Log.d(TAG, "processDir() called with: dir = $dir")
        totalFileScanResult.scanTaskNum.incrementAndGet()
        try {
            val listFiles = dir.listFiles()
            if (listFiles.isNullOrEmpty()) {
                totalFileScanResult.emptyDirScanResult.fileQueue.offer(dir)
            } else {
                val listPair = listFiles.partition { it.isDirectory }
                for (lDir in listPair.first) {
                    try {
                        ioExecutors.execute { processDir(lDir, totalFileScanResult) }
                    } catch (e: Exception) {
                        Log.e(TAG, "processDir: ", e)
                    }
                }
                for (lFile in listPair.second) {
                    val fileExt = lFile.extension
                    if (fileExt.isEmpty()) {
                        totalFileScanResult.noExtScanResult.offerFileAndAddFileSize(lFile)
                    } else if (FileScanServiceConfig.imageFileExtSet.contains(lFile.extension)) {
                        totalFileScanResult.imageScanResult.offerFileAndAddFileSize(lFile)
                    } else if (FileScanServiceConfig.videoFileExtSet.contains(lFile.extension)) {
                        totalFileScanResult.videoScanResult.offerFileAndAddFileSize(lFile)
                    } else if (FileScanServiceConfig.audioFileExtSet.contains(lFile.extension)) {
                        totalFileScanResult.audioScanResult.offerFileAndAddFileSize(lFile)
                    } else if (FileScanServiceConfig.documentFileExtSet.contains(lFile.extension)) {
                        totalFileScanResult.documentScanResult.offerFileAndAddFileSize(lFile)
                    } else if (FileScanServiceConfig.apkFileExt == lFile.extension) {
                        totalFileScanResult.apkFileScanResult.offerFileAndAddFileSize(lFile)
                    } else if (FileScanServiceConfig.compressedFileExt.contains(lFile.extension)) {
                        totalFileScanResult.compressedFileScanResult.offerFileAndAddFileSize(lFile)
                    } else {
                        totalFileScanResult.unknownExtScanResult.offerFileAndAddFileSize(lFile)
                    }
                }
            }
        } finally {
            if (totalFileScanResult.scanTaskNum.decrementAndGet() <= 0) {
                totalFileScanResult.countDownLatch.countDown()
            }
        }
    }

    private fun TotalFileScanResult.calcSize() {
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