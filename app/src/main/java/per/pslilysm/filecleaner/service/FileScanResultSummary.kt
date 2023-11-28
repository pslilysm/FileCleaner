package per.pslilysm.filecleaner.service

import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

/**
 * 文件扫描结果总结
 *
 * @author caoxuedong
 * Created on 2023/10/24 16:36
 * @since 1.0
 */
data class FileScanResultSummary(
    val imageScanResult: FileScanResult = FileScanResult(),
    val videoScanResult: FileScanResult = FileScanResult(),
    val audioScanResult: FileScanResult = FileScanResult(),
    val documentScanResult: FileScanResult = FileScanResult(),
    val apkFileScanResult: FileScanResult = FileScanResult(),
    val compressedFileScanResult: FileScanResult = FileScanResult(),
    val emptyDirScanResult: FileScanResult = FileScanResult(),
    val noExtScanResult: FileScanResult = FileScanResult(),
    val unknownExtScanResult: FileScanResult = FileScanResult(),
//    val largeFileQueue: Queue<File> = ConcurrentLinkedQueue(),
    var otherFileSize: Long = 0L,

    var storageUsedSize: Long = 0L,
    var storageAvailableSize: Long = 0L,
    var storageTotalSize: Long = 0L,

    val scanTaskNum: AtomicInteger = AtomicInteger(),
    val countDownLatch: CountDownLatch = CountDownLatch(1),
)
