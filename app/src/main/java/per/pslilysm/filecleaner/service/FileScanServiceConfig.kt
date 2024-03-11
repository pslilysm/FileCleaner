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
        /**
         * Image File Suffix Set
         */
        val imageFileExtSet = hashSetOf("bmb", "jpg", "png", "gif", "tif", "pic")

        /**
         * Video File Suffix Set
         */
        val videoFileExtSet = hashSetOf("mp4")

        /**
         * Audio File Suffix Set
         */
        val audioFileExtSet = hashSetOf("wav", "mp3")

        /**
         * Document File Suffix Set
         */
        val documentFileExtSet = hashSetOf("txt", "doc", "docx", "xls", "xlsx", "pdf", "xmind", "ppt", "pptx")

        /**
         * Apk File Suffix
         */
        const val apkFileExt = "apk"

        /**
         * Compressed File Suffix Set
         */
        val compressedFileExt = hashSetOf("zip", "7z", "rar")
    }

}