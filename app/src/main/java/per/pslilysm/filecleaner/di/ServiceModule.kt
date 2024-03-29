package per.pslilysm.filecleaner.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import per.pslilysm.filecleaner.service.AppScanService
import per.pslilysm.filecleaner.service.FileScanService
import per.pslilysm.filecleaner.service.impl.AppScanServiceImpl
import per.pslilysm.filecleaner.service.impl.FileScanServiceImpl
import javax.inject.Singleton

/**
 * 服务模块
 *
 * @author caoxuedong
 * Created on 2023/10/24 14:39
 * @since 1.0
 */
@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {

    @Provides
    @Singleton
    fun fileScanService(): FileScanService {
        return FileScanServiceImpl()
    }

    @Provides
    @Singleton
    fun appScanService(): AppScanService {
        return AppScanServiceImpl()
    }

}