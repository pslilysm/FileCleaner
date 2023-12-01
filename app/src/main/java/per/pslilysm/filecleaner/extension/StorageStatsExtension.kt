package per.pslilysm.filecleaner.extension

import android.app.usage.StorageStats

/**
 * Extension for [StorageStats]
 *
 * @author caoxuedong
 * Created on 2023/12/01 13:32
 * @since 1.0
 */

fun StorageStats.totalSize(): Long {
    return this.appBytes + this.dataBytes
}