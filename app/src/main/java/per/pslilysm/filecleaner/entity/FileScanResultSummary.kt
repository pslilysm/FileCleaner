package per.pslilysm.filecleaner.entity

import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

/**
 * 文件扫描结果总计
 *
 * @author caoxuedong
 * Created on 2023/10/24 16:36
 * @since 1.0
 */
data class FileScanResultSummary(
    val image: FileScanResult = FileScanResult(),
    val video: FileScanResult = FileScanResult(),
    val audio: FileScanResult = FileScanResult(),
    val document: FileScanResult = FileScanResult(),
    val apkFile: FileScanResult = FileScanResult(),
    val compressedFile: FileScanResult = FileScanResult(),
    val emptyDir: FileScanResult = FileScanResult(),
    val noExt: FileScanResult = FileScanResult(),
    val unknownExt: FileScanResult = FileScanResult(),
//    val largeFileQueue: Queue<File> = ConcurrentLinkedQueue(),
    var scanCost: Int = 0,

    val scanTaskNum: AtomicInteger = AtomicInteger(),
    val countDownLatch: CountDownLatch = CountDownLatch(1),
)
