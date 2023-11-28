package per.pslilysm.filecleaner.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import per.pslilysm.filecleaner.R
import per.pslilysm.filecleaner.dagger.FCAppComponent
import per.pslilysm.filecleaner.databinding.ActivityMainBinding
import per.pslilysm.filecleaner.service.FileScanResultSummary
import per.pslilysm.filecleaner.service.FileScanService
import pers.pslilysm.sdk_library.extention.autoFormatFileSize
import java.util.concurrent.CancellationException
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
                        repeatOnLifecycle(Lifecycle.State.STARTED) {
                            val scanResult = try {
                                fileScanService.startScan()
                            } catch (e: CancellationException) {
                                Log.d("MainActivity", "onCreate: ", e)
                                return@repeatOnLifecycle
                            }
                            withContext(Dispatchers.Main) {
                                refreshUIWithScanResult(scanResult)
                            }
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
    private fun refreshUIWithScanResult(scanResult: FileScanResultSummary) {
        val sts = scanResult.storageTotalSize
        binding.tvMainUsedPercentValue.text = "${scanResult.storageUsedSize * 100 / sts}%"
        binding.tvMainScanCost.text = getString(R.string.scan_cost, scanResult.scanCost)
        binding.tvMainStorageUsedSizeValue.text = scanResult.storageUsedSize.autoFormatFileSize()
        binding.tvMainStorageTotalSizeValue.text = sts.autoFormatFileSize()

        binding.sabMain.dataAndColorArray = arrayOf(
            (scanResult.imageScanResult.queueFileSize.get() * 1000 / sts / 1000f) to getColor(R.color.fff08273),
            (scanResult.videoScanResult.queueFileSize.get() * 1000 / sts / 1000f) to getColor(R.color.ffc897f0),
            (scanResult.audioScanResult.queueFileSize.get() * 1000 / sts / 1000f) to getColor(R.color.ff8cb2fc),
            (scanResult.documentScanResult.queueFileSize.get() * 1000 / sts / 1000f) to getColor(R.color.ffceaf81),
            (scanResult.apkFileScanResult.queueFileSize.get() * 1000 / sts / 1000f) to getColor(R.color.ffa5d934),
            (scanResult.compressedFileScanResult.queueFileSize.get() * 1000 / sts / 1000f) to getColor(R.color.ff94a6be),
            (scanResult.emptyDirScanResult.queueFileSize.get() * 1000 / sts / 1000f) to getColor(R.color.ff55afb7),
            (scanResult.noExtScanResult.queueFileSize.get() * 1000 / sts / 1000f) to getColor(R.color.ff4673ab),
            (scanResult.unknownExtScanResult.queueFileSize.get() * 1000 / sts / 1000f) to getColor(R.color.ff8ea9bc),
            (scanResult.otherFileSize * 1000 / sts / 1000f) to getColor(R.color.ff868895),
        )
        binding.sabMain.invalidate()

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