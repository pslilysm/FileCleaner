package per.pslilysm.filecleaner.service

import java.io.File
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

/**
 * 文件扫描结果
 *
 * @author caoxuedong
 * Created on 2023/10/24 16:36
 * @since 1.0
 */
data class FileScanResult(
    val emptyDirQueue: Queue<File> = ConcurrentLinkedQueue(),
    val largeFileQueue: Queue<File> = ConcurrentLinkedQueue(),
    val noExtFileQueue: Queue<File> = ConcurrentLinkedQueue(),
    val scanTaskNum: AtomicInteger = AtomicInteger(),
    val countDownLatch: CountDownLatch = CountDownLatch(1),
)
