package per.pslilysm.filecleaner.ui.i

import androidx.lifecycle.LifecycleOwner
import per.pslilysm.filecleaner.entity.StorageScanResult

/**
 * 存储分析UI接口
 *
 * @author caoxuedong
 * Created on 2023/11/30 15:58
 * @since 1.0
 */
interface StorageAnalysisUI: LifecycleOwner {

    /**
     * Refresh storage analysis ui
     *
     * @param storageScanResult the data to show in UI
     */
    fun refreshStorageAnalysisUI(storageScanResult: StorageScanResult)

}