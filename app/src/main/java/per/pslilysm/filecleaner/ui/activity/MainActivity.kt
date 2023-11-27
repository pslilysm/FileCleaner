package per.pslilysm.filecleaner.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import per.pslilysm.filecleaner.dagger.FCAppComponent
import per.pslilysm.filecleaner.databinding.ActivityMainBinding
import per.pslilysm.filecleaner.service.FileScanService
import per.pslilysm.filecleaner.service.TotalFileScanResult
import pers.pslilysm.sdk_library.extention.autoFormatFileSize
import javax.inject.Inject


/**
 * 主页面
 *
 * @author caoxuedong
 * Created on 2023/10/23 16:03
 * @since 1.0
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var fileScanService: FileScanService

    override fun onCreate(savedInstanceState: Bundle?) {
        FCAppComponent.instance.injectMainActivity(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
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
        XXPermissions.with(this)
            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
            .request { _, allGranted ->
                if (allGranted) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val scanResult = fileScanService.startScan()
                        withContext(Dispatchers.Main) {
                            refreshUIWithScanResult(scanResult)
                        }
                    }
                }
            }
    }

    override fun onStop() {
        super.onStop()
        fileScanService.stopScanIfNeed()
    }

    override fun onClick(v: View?) {

    }

    @SuppressLint("SetTextI18n")
    private fun refreshUIWithScanResult(scanResult: TotalFileScanResult) {
        binding.tvMainUsedPercentValue.text = "${scanResult.storageUsedSize * 100 / scanResult.storageTotalSize}%"
        binding.tvMainStorageUsedSizeValue.text = scanResult.storageUsedSize.autoFormatFileSize()
        binding.tvMainStorageTotalSizeValue.text = scanResult.storageTotalSize.autoFormatFileSize()
        binding.tvMainImageSize.text = scanResult.imageScanResult.queueFileSize.get().autoFormatFileSize()
        binding.tvMainVideoSize.text = scanResult.videoScanResult.queueFileSize.get().autoFormatFileSize()
        binding.tvMainAudioSize.text = scanResult.audioScanResult.queueFileSize.get().autoFormatFileSize()
        binding.tvMainDocumentSize.text = scanResult.documentScanResult.queueFileSize.get().autoFormatFileSize()
        binding.tvMainApkFileSize.text = scanResult.apkFileScanResult.queueFileSize.get().autoFormatFileSize()
        binding.tvMainCompressedFileSize.text = scanResult.compressedFileScanResult.queueFileSize.get().autoFormatFileSize()
        binding.tvMainEmptyDirSize.text = scanResult.emptyDirScanResult.fileQueue.size.toString()
        binding.tvMainNoExtFileSize.text = scanResult.noExtScanResult.queueFileSize.get().autoFormatFileSize()
        binding.tvMainUnknownExtFileSize.text = scanResult.unknownExtScanResult.queueFileSize.get().autoFormatFileSize()
        binding.tvMainOtherSize.text = scanResult.otherFileSize.autoFormatFileSize()
    }

}