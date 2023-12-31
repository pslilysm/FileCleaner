package per.pslilysm.filecleaner.service

import per.pslilysm.filecleaner.entity.FileScanResultSummary
import java.util.concurrent.CancellationException

/**
 * 文件扫描服务
 *
 * @author caoxuedong
 * Created on 2023/10/24 14:33
 * @since 1.0
 */
interface FileScanService {

    /**
     * Start scan
     *
     * @return the scan result
     */
    @Throws(CancellationException::class)
    fun start(): FileScanResultSummary

    /**
     * Stop scan if need
     *
     */
    fun stopIfNeed()

}