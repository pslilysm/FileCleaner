package per.pslilysm.filecleaner.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import per.pslilysm.filecleaner.databinding.ActivityMainBinding

/**
 * 主页面
 *
 * @author caoxuedong
 * Created on 2023/10/23 16:03
 * @since 1.0
 */
class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
    }

}