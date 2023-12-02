package per.pslilysm.filecleaner.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import per.pslilysm.filecleaner.dagger.FCAppComponent
import per.pslilysm.filecleaner.databinding.ActivityStorageAnalysisBinding
import per.pslilysm.filecleaner.viewmodel.StorageAnalysisVM
import javax.inject.Inject


/**
 * 存储分析页面
 *
 * @author caoxuedong
 * Created on 2023/10/23 16:03
 * @since 1.0
 */
class StorageAnalysisActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityStorageAnalysisBinding

    @Inject
    lateinit var storageAnalysisVM: StorageAnalysisVM

    override fun onCreate(savedInstanceState: Bundle?) {
        FCAppComponent.instance.injectMainActivity(this)
        super.onCreate(savedInstanceState)
        binding = ActivityStorageAnalysisBinding.inflate(layoutInflater).apply {
            vm = storageAnalysisVM
            lifecycleOwner = this@StorageAnalysisActivity
            layoutItemScanResult.apply {
                vm = storageAnalysisVM
                lifecycleOwner = this@StorageAnalysisActivity
            }
        }.also {
            setContentView(it.root)
        }
        binding.layoutItemScanResult.llMainAppSizeContainer.setOnClickListener(this)
        binding.layoutItemScanResult.llMainImageSizeContainer.setOnClickListener(this)
        binding.layoutItemScanResult.llMainVideoSizeContainer.setOnClickListener(this)
        binding.layoutItemScanResult.llMainAudioSizeContainer.setOnClickListener(this)
        binding.layoutItemScanResult.llMainDocumentSizeContainer.setOnClickListener(this)
        binding.layoutItemScanResult.llMainApkFileSizeContainer.setOnClickListener(this)
        binding.layoutItemScanResult.llMainCompressedFileSizeContainer.setOnClickListener(this)
        binding.layoutItemScanResult.llMainEmptyDirSizeContainer.setOnClickListener(this)
        binding.layoutItemScanResult.llMainNoExtFileSizeContainer.setOnClickListener(this)
        binding.layoutItemScanResult.llMainUnknownExtFileSizeContainer.setOnClickListener(this)
        binding.layoutItemScanResult.llMainOtherSizeContainer.setOnClickListener(this)

        binding.layoutUnusedApps.clUnusedAppsTitleContainer.setOnClickListener(this)
        binding.layoutUnusedApps.clUnusedApps1.setOnClickListener(this)
        binding.layoutUnusedApps.clUnusedApps2.setOnClickListener(this)
        binding.layoutUnusedApps.clUnusedApps3.setOnClickListener(this)
        binding.layoutUnusedApps.clUnusedApps4.setOnClickListener(this)

        binding.layoutLargeFiles.clLargeFilesTitleContainer.setOnClickListener(this)
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

    override fun onDestroy() {
        super.onDestroy()
        storageAnalysisVM.stopScanStorageIfNeed()
    }

    override fun onClick(v: View?) {

    }

}