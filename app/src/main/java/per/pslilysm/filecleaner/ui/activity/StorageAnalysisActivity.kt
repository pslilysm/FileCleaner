package per.pslilysm.filecleaner.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import dagger.hilt.android.AndroidEntryPoint
import per.pslilysm.filecleaner.databinding.ActivityStorageAnalysisBinding
import per.pslilysm.filecleaner.viewmodel.StorageAnalysisVM


/**
 * 存储分析页面
 *
 * @author caoxuedong
 * Created on 2023/10/23 16:03
 * @since 1.0
 */
@AndroidEntryPoint
class StorageAnalysisActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityStorageAnalysisBinding

    private val storageAnalysisVM: StorageAnalysisVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStorageAnalysisBinding.inflate(layoutInflater).apply {
            vm = storageAnalysisVM
            lifecycleOwner = this@StorageAnalysisActivity
            layoutItemScanResult.apply {
                vm = storageAnalysisVM
                activity = this@StorageAnalysisActivity
                lifecycleOwner = this@StorageAnalysisActivity
            }
            layoutUnusedApps.apply {
                activity = this@StorageAnalysisActivity
            }
            layoutLargeFiles.apply {
                activity = this@StorageAnalysisActivity
            }
        }.also {
            setContentView(it.root)
        }
    }

    override fun onStart() {
        super.onStart()
        XXPermissions.with(this)
            .permission(Permission.MANAGE_EXTERNAL_STORAGE, Permission.PACKAGE_USAGE_STATS, Permission.GET_INSTALLED_APPS)
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