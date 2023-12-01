package per.pslilysm.filecleaner.dagger

import dagger.Component
import per.pslilysm.filecleaner.ui.activity.StorageAnalysisActivity
import javax.inject.Singleton

/**
 * 应用组件
 *
 * @author caoxuedong
 * Created on 2023/10/24 14:39
 * @since 1.0
 */
@Singleton
@Component(modules = [ServiceModule::class])
interface FCAppComponent {

    fun injectMainActivity(storageAnalysisActivity: StorageAnalysisActivity)

    companion object {

        val instance: FCAppComponent by lazy { DaggerFCAppComponent.create() }

    }

}