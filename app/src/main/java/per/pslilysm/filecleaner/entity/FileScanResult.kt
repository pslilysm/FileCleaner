package per.pslilysm.filecleaner.entity

import java.io.File
import java.nio.file.Files
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicLong

/**
 * 文件扫描结果
 *
 * @author caoxuedong
 * Created on 2023/11/27 17:49
 * @since 1.0
 */
data class FileScanResult(
    val fileQueue: ConcurrentLinkedQueue<File> = ConcurrentLinkedQueue(),
    val queueFileSize: AtomicLong = AtomicLong(0)
) {
    fun offerFileAndAddFileSize(file: File) {
        fileQueue.offer(file)
        queueFileSize.getAndAdd(Files.size(file.toPath()))
    }
}
