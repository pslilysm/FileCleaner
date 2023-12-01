package per.pslilysm.filecleaner.entity

import android.content.pm.ApplicationInfo

/**
 * 应用扫描结果
 *
 * @author caoxuedong
 * Created on 2023/11/30 16:05
 * @since 1.0
 */
data class AppScanResult(
    val applicationInfo: ApplicationInfo,
    val appName: String,
    val appStorageSize: Long
)
