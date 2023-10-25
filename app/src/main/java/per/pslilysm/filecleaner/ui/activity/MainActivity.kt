package per.pslilysm.filecleaner.ui.activity

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import per.pslilysm.filecleaner.adapter.ScanResultAdapter
import per.pslilysm.filecleaner.dagger.FCAppComponent
import per.pslilysm.filecleaner.databinding.ActivityMainBinding
import per.pslilysm.filecleaner.service.FileScanService
import pers.pslilysm.sdk_library.extention.dip2px
import java.io.File
import javax.inject.Inject

/**
 * 主页面
 *
 * @author caoxuedong
 * Created on 2023/10/23 16:03
 * @since 1.0
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var fileScanService: FileScanService

    override fun onCreate(savedInstanceState: Bundle?) {
        FCAppComponent.instance.injectMainActivity(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        binding.rvMain.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val pos = parent.getChildAdapterPosition(view)
                if (pos == 0) {
                    outRect.top = 8f.dip2px()
                } else if (pos == state.itemCount - 1) {
                    outRect.bottom = 8f.dip2px()
                }
            }
        })
        XXPermissions.with(this)
            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
            .request { _, allGranted ->
                if (allGranted) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val scanResult = fileScanService.startScan()
                        val fileList = ArrayList<File>()
                        fileList.addAll(scanResult.emptyDirQueue.toMutableList().apply { sort() })
                        fileList.addAll(scanResult.noExtFileQueue.toMutableList().apply { sort() })
                        Log.i("MainActivity", "onCreate: ${fileList.size}")
                        withContext(Dispatchers.Main) {
                            val adapter = ScanResultAdapter(fileList)
                            binding.rvMain.adapter = adapter
                        }
                    }
                }
            }
    }

    override fun onStop() {
        super.onStop()
        fileScanService.stopScanIfNeed()
    }

}