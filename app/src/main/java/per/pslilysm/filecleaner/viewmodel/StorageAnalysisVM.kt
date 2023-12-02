package per.pslilysm.filecleaner.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import per.pslilysm.filecleaner.R
import per.pslilysm.filecleaner.entity.StorageScanResult
import per.pslilysm.filecleaner.model.StorageAnalysisModel
import pers.pslilysm.sdk_library.AppHolder
import java.util.concurrent.CancellationException
import java.util.concurrent.CountDownLatch
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
    
    val scanResult = MutableLiveData<StorageScanResult>()

    val sabData = MutableLiveData<Array<Pair<Float, Int>>?>()

    fun startScanStorage() {
        val storageScanResult = StorageScanResult()
        scanResult.value = storageScanResult
        sabData.value = null
        viewModelScope.launch(Dispatchers.IO) {
            val countDownLatch = CountDownLatch(2)
            scanExecutors.execute {
                try {
                    val fileScanResultSummary = try {
                        storageAnalysisModel.scanFile()
                    } catch (e: CancellationException) {
                        return@execute
                    }
                    storageScanResult.file = fileScanResultSummary
                    scanResult.postValue(storageScanResult)
                } finally {
                    countDownLatch.countDown()
                }
            }
            scanExecutors.execute {
                val appScanResultSummary = try {
                    storageAnalysisModel.scanApp()
                } catch (e: CancellationException) {
                    return@execute
                }
                storageScanResult.app = appScanResultSummary
                scanResult.postValue(storageScanResult)
                countDownLatch.countDown()
            }
            countDownLatch.await()
            val appResult = storageScanResult.app
            val fileResult = storageScanResult.file
            if (appResult!= null && fileResult != null) {
                val storageStat = storageAnalysisModel.calcStorageStatFs()
                val storageTotalSize = storageStat.totalSize
                storageScanResult.stat = storageStat
                storageScanResult.calcOtherSizeIfCan()
                scanResult.postValue(storageScanResult)
                sabData.postValue(
                    arrayOf(
                        (appResult.queueSize.get() * 1000 / storageTotalSize / 1000f) to AppHolder.get().getColor(R.color.ff00bcd4),
                        (fileResult.image.queueSize.get() * 1000 / storageTotalSize / 1000f) to AppHolder.get().getColor(R.color.fff08273),
                        (fileResult.video.queueSize.get() * 1000 / storageTotalSize / 1000f) to AppHolder.get().getColor(R.color.ffc897f0),
                        (fileResult.audio.queueSize.get() * 1000 / storageTotalSize / 1000f) to AppHolder.get().getColor(R.color.ff8cb2fc),
                        (fileResult.document.queueSize.get() * 1000 / storageTotalSize / 1000f) to AppHolder.get().getColor(R.color.ffceaf81),
                        (fileResult.apkFile.queueSize.get() * 1000 / storageTotalSize / 1000f) to AppHolder.get().getColor(R.color.ffa5d934),
                        (fileResult.compressedFile.queueSize.get() * 1000 / storageTotalSize / 1000f) to AppHolder.get().getColor(R.color.ff94a6be),
                        (fileResult.emptyDir.queueSize.get() * 1000 / storageTotalSize / 1000f) to AppHolder.get().getColor(R.color.ff55afb7),
                        (fileResult.noExt.queueSize.get() * 1000 / storageTotalSize / 1000f) to AppHolder.get().getColor(R.color.ff4673ab),
                        (fileResult.unknownExt.queueSize.get() * 1000 / storageTotalSize / 1000f) to AppHolder.get().getColor(R.color.ff8ea9bc),
                        (storageScanResult.other!! * 1000 / storageTotalSize / 1000f) to AppHolder.get().getColor(R.color.ff868895),
                    )
                )
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