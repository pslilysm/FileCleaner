package per.pslilysm.filecleaner.entity

/**
 * 存储扫描结果
 *
 * @author caoxuedong
 * Created on 2023/11/30 16:03
 * @since 1.0
 */
data class StorageScanResult(
    var stat: StorageStat? = null,
    var app: AppScanResultSummary? = null,
    var file: FileScanResultSummary? = null,
    var other: Long? = null
) {

    fun calcOtherSizeIfCan() {
        val storageStat = this.stat
        val appScanResultSummary = this.app
        val fileScanResultSummary = this.file
        if (storageStat != null && appScanResultSummary != null && fileScanResultSummary != null) {
            other = storageStat.usedSize -
                    appScanResultSummary.queueSize.get() -
                    fileScanResultSummary.image.queueSize.get() -
                    fileScanResultSummary.video.queueSize.get() -
                    fileScanResultSummary.audio.queueSize.get() -
                    fileScanResultSummary.document.queueSize.get() -
                    fileScanResultSummary.apkFile.queueSize.get() -
                    fileScanResultSummary.compressedFile.queueSize.get() -
                    fileScanResultSummary.emptyDir.queueSize.get() -
                    fileScanResultSummary.noExt.queueSize.get() -
                    fileScanResultSummary.unknownExt.queueSize.get()
        }
    }

}
