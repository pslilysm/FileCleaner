package per.pslilysm.filecleaner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import per.pslilysm.filecleaner.databinding.ItemFileBinding
import pers.pslilysm.sdk_library.extention.autoFormatFileSize
import java.io.File

/**
 * 扫描结果适配器
 *
 * @author caoxuedong
 * Created on 2023/10/24 17:16
 * @since 1.0
 */
class ScanResultAdapter(private val fileList: List<File>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    class FileVH(val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root) {
        
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FileVH(ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FileVH) {
            val file = fileList[position]
            holder.binding.tvFileName.text = file.absolutePath
            holder.binding.tvFileSize.text = if (file.isDirectory) "文件夹" else file.length().autoFormatFileSize()
        }
    }

    override fun getItemCount(): Int {
        return fileList.size
    }


}