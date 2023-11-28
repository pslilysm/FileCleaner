package per.pslilysm.filecleaner.service

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
    fun startScan(): FileScanResultSummary

    /**
     * Stop scan if need
     *
     */
    fun stopScanIfNeed()

}