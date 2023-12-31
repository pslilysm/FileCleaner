package per.pslilysm.filecleaner.entity

/**
 * 存储状态
 *
 * @author caoxuedong
 * Created on 2023/11/30 16:01
 * @since 1.0
 */
data class StorageStat(
    val usedSize: Long,
    val availableSize: Long,
    val totalSize: Long,
)
