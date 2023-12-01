package per.pslilysm.filecleaner.entity

/**
 * 存储扫描结果总结
 *
 * @author caoxuedong
 * Created on 2023/11/30 16:03
 * @since 1.0
 */
data class StorageScanResultSummary(
    val storageStat: StorageStat,
    var appScanResultSummary: AppScanResultSummary? = null,
    var fileScanResultSummary: FileScanResultSummary? = null,
    var otherStorageSize: Long? = null
) {

    fun calcOtherStorageSizeIfCan() {
        val appScanResultSummary = this.appScanResultSummary
        val fileScanResultSummary = this.fileScanResultSummary
        if(appScanResultSummary != null && fileScanResultSummary != null) {
            otherStorageSize = storageStat.storageUsedSize -
                    appScanResultSummary.queueAppsSize.get() -
                    fileScanResultSummary.imageScanResult.queueFileSize.get() -
                    fileScanResultSummary.videoScanResult.queueFileSize.get() -
                    fileScanResultSummary.audioScanResult.queueFileSize.get() -
                    fileScanResultSummary.documentScanResult.queueFileSize.get() -
                    fileScanResultSummary.apkFileScanResult.queueFileSize.get() -
                    fileScanResultSummary.compressedFileScanResult.queueFileSize.get() -
                    fileScanResultSummary.emptyDirScanResult.queueFileSize.get() -
                    fileScanResultSummary.noExtScanResult.queueFileSize.get() -
                    fileScanResultSummary.unknownExtScanResult.queueFileSize.get()
        }
    }

}
