package per.pslilysm.filecleaner.adapter

import android.util.Log
import android.widget.TextView
import androidx.databinding.BindingAdapter
import per.pslilysm.filecleaner.R
import per.pslilysm.filecleaner.ui.widget.StorageAnalysisBar
import pers.pslilysm.sdk_library.extention.autoFormatFileSize

/**
 * DataBinding适配器
 *
 * @author caoxuedong
 * Created on 2023/12/02 20:32
 * @since 1.0
 */

private const val TAG = "BindingAdapters"

@BindingAdapter("app:fileSizeToText")
fun fileSizeToText(textView: TextView, size: Long?) {
    textView.text = size?.autoFormatFileSize()
}

@BindingAdapter("app:fileSizeToTextDefaultComputing")
fun fileSizeToTextDefaultComputing(textView: TextView, size: Long?) {
    Log.d(
        TAG, "set() called with: id = " +
                "${textView.resources.getResourceName(textView.id).substringAfter("/")}, size = $size"
    )
    textView.text = size?.autoFormatFileSize() ?: textView.context.getString(R.string.common_computing)
}

@BindingAdapter("app:fileCountToTextDefaultComputing")
fun fileCountToTextDefaultComputing(textView: TextView, size: Int?) {
    textView.text = size?.toString() ?: textView.context.getString(R.string.common_computing)
}

@BindingAdapter("app:dataAndColorArray")
fun dataAndColorArray(storageAnalysisBar: StorageAnalysisBar, data: Array<Pair<Float, Int>>?) {
    storageAnalysisBar.dataAndColorArray = data
    storageAnalysisBar.invalidate()
}