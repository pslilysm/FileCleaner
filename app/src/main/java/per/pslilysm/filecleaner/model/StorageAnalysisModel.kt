package per.pslilysm.filecleaner.model

import android.os.Environment
import android.os.StatFs
import per.pslilysm.filecleaner.entity.AppScanResultSummary
import per.pslilysm.filecleaner.entity.FileScanResultSummary
import per.pslilysm.filecleaner.entity.StorageStat
import per.pslilysm.filecleaner.service.AppScanService
import per.pslilysm.filecleaner.service.FileScanService
import java.util.concurrent.CancellationException
import javax.inject.Inject

/**
 * 存储数据模型
 *
 * @author caoxuedong
 * Created on 2023/11/30 13:57
 * @since 1.0
 */
class StorageAnalysisModel @Inject constructor(
    private val fileScanService: FileScanService,
    private val appScanService: AppScanService,
) {

    fun calcStorageStatFs(): StorageStat {
        return StatFs(Environment.getExternalStorageDirectory().path).let { statFs ->
            val storageAvailableSize = statFs.availableBytes
            val storageTotalSize = statFs.totalBytes
            StorageStat(
                storageTotalSize - storageAvailableSize,
                storageAvailableSize,
                storageTotalSize
            )
        }
    }

    @Throws(CancellationException::class)
    fun scanFile(): FileScanResultSummary {
        return fileScanService.start()
    }

    @Throws(CancellationException::class)
    fun scanApp(): AppScanResultSummary {
        return appScanService.start()
    }

    fun stopAllScanIfNeed() {
        fileScanService.stopIfNeed()
        appScanService.stopIfNeed()
    }

}