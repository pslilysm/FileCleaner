package per.pslilysm.filecleaner.entity

import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicLong

/**
 * 应用扫描结果总计
 *
 * @author caoxuedong
 * Created on 2023/11/30 16:04
 * @since 1.0
 */
data class AppScanResultSummary(
    val appQueue: Queue<AppScanResult> = ConcurrentLinkedQueue(),
    val queueSize: AtomicLong = AtomicLong(0),
)
