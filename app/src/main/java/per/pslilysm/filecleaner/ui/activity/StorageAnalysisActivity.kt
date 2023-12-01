package per.pslilysm.filecleaner.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import per.pslilysm.filecleaner.R
import per.pslilysm.filecleaner.dagger.FCAppComponent
import per.pslilysm.filecleaner.databinding.ActivityStorageAnalysisBinding
import per.pslilysm.filecleaner.entity.FileScanResultSummary
import per.pslilysm.filecleaner.entity.StorageScanResultSummary
import per.pslilysm.filecleaner.entity.StorageStat
import per.pslilysm.filecleaner.ui.i.StorageAnalysisUI
import per.pslilysm.filecleaner.viewmodel.StorageAnalysisVM
import pers.pslilysm.sdk_library.extention.autoFormatFileSize
import javax.inject.Inject


/**
 * 存储分析页面
 *
 * @author caoxuedong
 * Created on 2023/10/23 16:03
 * @since 1.0
 */
class StorageAnalysisActivity : AppCompatActivity(), View.OnClickListener, StorageAnalysisUI {

    private lateinit var binding: ActivityStorageAnalysisBinding

    @Inject
    lateinit var storageAnalysisVM: StorageAnalysisVM

    override fun onCreate(savedInstanceState: Bundle?) {
        FCAppComponent.instance.injectMainActivity(this)
        super.onCreate(savedInstanceState)
        binding = ActivityStorageAnalysisBinding.inflate(layoutInflater).also { setContentView(it.root) }
        binding.llMainAppSizeContainer.setOnClickListener(this)
        binding.llMainImageSizeContainer.setOnClickListener(this)
        binding.llMainVideoSizeContainer.setOnClickListener(this)
        binding.llMainAudioSizeContainer.setOnClickListener(this)
        binding.llMainDocumentSizeContainer.setOnClickListener(this)
        binding.llMainApkFileSizeContainer.setOnClickListener(this)
        binding.llMainCompressedFileSizeContainer.setOnClickListener(this)
        binding.llMainEmptyDirSizeContainer.setOnClickListener(this)
        binding.llMainNoExtFileSizeContainer.setOnClickListener(this)
        binding.llMainUnknownExtFileSizeContainer.setOnClickListener(this)
        binding.llMainOtherSizeContainer.setOnClickListener(this)
        storageAnalysisVM.observeScanResult(this)
    }

    override fun onStart() {
        super.onStart()
        XXPermissions.with(this)
            .permission(Permission.MANAGE_EXTERNAL_STORAGE, Permission.PACKAGE_USAGE_STATS)
            .request { _, allGranted ->
                if (allGranted) {
                    storageAnalysisVM.startScanStorage()
                }
            }
    }

    override fun onStop() {
        super.onStop()
        storageAnalysisVM.stopScanStorageIfNeed()
    }

    override fun onClick(v: View?) {

    }

    override fun refreshStorageAnalysisUI(storageScanResultSummary: StorageScanResultSummary) {
        refreshStorageStatUI(storageScanResultSummary.storageStat)
        val appScanResultSummary = storageScanResultSummary.appScanResultSummary
        val fileScanResultSummary = storageScanResultSummary.fileScanResultSummary
        val otherFileSize = storageScanResultSummary.otherStorageSize
        if (appScanResultSummary != null) {
            binding.tvMainAppSize.text = appScanResultSummary.queueAppsSize.get().autoFormatFileSize()
        }
        if (fileScanResultSummary != null) {
            refreshFileScanResultUI(fileScanResultSummary)
        }
        if (otherFileSize != null) {
            binding.tvMainOtherSize.text = otherFileSize.autoFormatFileSize()
        }
        if (appScanResultSummary != null && fileScanResultSummary != null && otherFileSize != null) {
            val storageTotalSize = storageScanResultSummary.storageStat.storageTotalSize
            binding.sabMain.dataAndColorArray = arrayOf(
                (appScanResultSummary.queueAppsSize.get() * 1000 / storageTotalSize / 1000f) to getColor(R.color.ff00bcd4),
                (fileScanResultSummary.imageScanResult.queueFileSize.get() * 1000 / storageTotalSize / 1000f) to getColor(R.color.fff08273),
                (fileScanResultSummary.videoScanResult.queueFileSize.get() * 1000 / storageTotalSize / 1000f) to getColor(R.color.ffc897f0),
                (fileScanResultSummary.audioScanResult.queueFileSize.get() * 1000 / storageTotalSize / 1000f) to getColor(R.color.ff8cb2fc),
                (fileScanResultSummary.documentScanResult.queueFileSize.get() * 1000 / storageTotalSize / 1000f) to getColor(R.color.ffceaf81),
                (fileScanResultSummary.apkFileScanResult.queueFileSize.get() * 1000 / storageTotalSize / 1000f) to getColor(R.color.ffa5d934),
                (fileScanResultSummary.compressedFileScanResult.queueFileSize.get() * 1000 / storageTotalSize / 1000f) to getColor(R.color.ff94a6be),
                (fileScanResultSummary.emptyDirScanResult.queueFileSize.get() * 1000 / storageTotalSize / 1000f) to getColor(R.color.ff55afb7),
                (fileScanResultSummary.noExtScanResult.queueFileSize.get() * 1000 / storageTotalSize / 1000f) to getColor(R.color.ff4673ab),
                (fileScanResultSummary.unknownExtScanResult.queueFileSize.get() * 1000 / storageTotalSize / 1000f) to getColor(R.color.ff8ea9bc),
                (otherFileSize * 1000 / storageTotalSize / 1000f) to getColor(R.color.ff868895),
            )
            binding.sabMain.invalidate()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun refreshStorageStatUI(storageStat: StorageStat) {
        val sts = storageStat.storageTotalSize
        binding.tvMainUsedPercentValue.text = "${storageStat.storageUsedSize * 100 / sts}%"
        binding.tvMainStorageUsedSizeValue.text = storageStat.storageUsedSize.autoFormatFileSize()
        binding.tvMainStorageTotalSizeValue.text = sts.autoFormatFileSize()
    }

    @SuppressLint("SetTextI18n")
    private fun refreshFileScanResultUI(scanResult: FileScanResultSummary) {
        binding.tvMainScanCost.text = getString(R.string.scan_cost, scanResult.scanCost)
        binding.tvMainImageSize.text = scanResult.imageScanResult.queueFileSize.get().autoFormatFileSize()
        binding.tvMainVideoSize.text = scanResult.videoScanResult.queueFileSize.get().autoFormatFileSize()
        binding.tvMainAudioSize.text = scanResult.audioScanResult.queueFileSize.get().autoFormatFileSize()
        binding.tvMainDocumentSize.text = scanResult.documentScanResult.queueFileSize.get().autoFormatFileSize()
        binding.tvMainApkFileSize.text = scanResult.apkFileScanResult.queueFileSize.get().autoFormatFileSize()
        binding.tvMainCompressedFileSize.text = scanResult.compressedFileScanResult.queueFileSize.get().autoFormatFileSize()
        binding.tvMainEmptyDirSize.text = scanResult.emptyDirScanResult.fileQueue.size.toString()
        binding.tvMainNoExtFileSize.text = scanResult.noExtScanResult.queueFileSize.get().autoFormatFileSize()
        binding.tvMainUnknownExtFileSize.text = scanResult.unknownExtScanResult.queueFileSize.get().autoFormatFileSize()
    }

}