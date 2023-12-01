package per.pslilysm.filecleaner.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import per.pslilysm.filecleaner.entity.StorageScanResultSummary
import per.pslilysm.filecleaner.model.StorageAnalysisModel
import per.pslilysm.filecleaner.ui.i.StorageAnalysisUI
import java.util.concurrent.CancellationException
import java.util.concurrent.Executors
import javax.inject.Inject

/**
 * 存储分析ViewModel
 *
 * @author caoxuedong
 * Created on 2023/11/30 15:28
 * @since 1.0
 */
class StorageAnalysisVM @Inject constructor(
    private val storageAnalysisModel: StorageAnalysisModel
) : ViewModel() {

    private val scanExecutors = Executors.newFixedThreadPool(2)

    private val scanResultSummaryLiveData: MutableLiveData<StorageScanResultSummary> = MutableLiveData()

    fun observeScanResult(storageAnalysisUI: StorageAnalysisUI) {
        scanResultSummaryLiveData.observe(storageAnalysisUI) {
            storageAnalysisUI.refreshStorageAnalysisUI(it)
        }
    }

    fun startScanStorage() {
        viewModelScope.launch(Dispatchers.IO) {
            val storageScanResultSummary = StorageScanResultSummary(storageAnalysisModel.calcStorageStatFs())
            scanExecutors.execute {
                val fileScanResultSummary = try {
                    storageAnalysisModel.scanFile()
                } catch (e: CancellationException) {
                    return@execute
                }
                storageScanResultSummary.fileScanResultSummary = fileScanResultSummary
                storageScanResultSummary.calcOtherStorageSizeIfCan()
                scanResultSummaryLiveData.postValue(storageScanResultSummary)
            }
            scanExecutors.execute {
                val appScanResultSummary = try {
                    storageAnalysisModel.scanApp()
                } catch (e: CancellationException) {
                    return@execute
                }
                storageScanResultSummary.appScanResultSummary = appScanResultSummary
                storageScanResultSummary.calcOtherStorageSizeIfCan()
                scanResultSummaryLiveData.postValue(storageScanResultSummary)
            }
        }
    }

    fun stopScanStorageIfNeed() {
        storageAnalysisModel.stopAllScanIfNeed()
    }

    override fun onCleared() {
        super.onCleared()
        scanExecutors.shutdownNow()
    }

}