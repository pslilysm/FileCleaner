package per.pslilysm.filecleaner.service

import per.pslilysm.filecleaner.entity.AppScanResultSummary
import java.util.concurrent.CancellationException

/**
 * 应用扫描服务
 *
 * @author caoxuedong
 * Created on 2023/11/30 16:46
 * @since 1.0
 */
interface AppScanService {

    /**
     * Start scan
     *
     * @return the scan result
     */
    @Throws(CancellationException::class)
    fun start(): AppScanResultSummary

    /**
     * Stop scan if need
     *
     */
    fun stopIfNeed()

}