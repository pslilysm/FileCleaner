package per.pslilysm.filecleaner.ui.i

import androidx.lifecycle.LifecycleOwner
import per.pslilysm.filecleaner.entity.StorageScanResultSummary

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
     * @param storageScanResultSummary the data to show in UI
     */
    fun refreshStorageAnalysisUI(storageScanResultSummary: StorageScanResultSummary)

}