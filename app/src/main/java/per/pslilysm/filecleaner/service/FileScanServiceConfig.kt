package per.pslilysm.filecleaner.service

/**
 * 文件扫描服务配置
 *
 * @author caoxuedong
 * Created on 2023/10/27 17:52
 * @since 1.0
 */
interface FileScanServiceConfig {

    companion object {
        val knownFileExtSet = hashSetOf(
            // 文档
            "txt", "doc", "docx", "xls", "xlsx", "pdf", "xmind", "ppt", "pptx",
            // 压缩
            "zip", "7z", "rar",
            // 图像
            "bmg", "jpg", "png", "gif", "tif", "pic",
            // 音频
            "wav", "mp3",
            // 视频
            "mp4",
            // 其他
            "bak",
            )
    }

}