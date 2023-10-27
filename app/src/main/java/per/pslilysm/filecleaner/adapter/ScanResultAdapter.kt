package per.pslilysm.filecleaner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import per.pslilysm.filecleaner.databinding.ItemClassicBinding
import per.pslilysm.filecleaner.databinding.ItemFileBinding
import per.pslilysm.filecleaner.entity.FileClassic
import pers.pslilysm.sdk_library.extention.autoFormatFileSize
import java.io.File

/**
 * 扫描结果适配器
 *
 * @author caoxuedong
 * Created on 2023/10/24 17:16
 * @since 1.0
 */
class ScanResultAdapter(private val dataList: List<Any>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CLASSIC = 1
        private const val VIEW_TYPE_FILE = 2
    }

    class ClassicVH(val binding: ItemClassicBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {  }
        }
    }
    
    class FileVH(val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {  }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_CLASSIC -> ClassicVH(ItemClassicBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            VIEW_TYPE_FILE -> FileVH(ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw RuntimeException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FileVH) {
            val file = dataList[position] as File
            holder.binding.tvFileName.text = file.absolutePath
            holder.binding.tvFileSize.text = if (file.isDirectory) "文件夹" else file.length().autoFormatFileSize()
        } else if (holder is ClassicVH) {
            val fileClassic = dataList[position] as FileClassic
            holder.binding.tvClassicName.text = fileClassic.classicName
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (dataList[position]) {
            is FileClassic -> VIEW_TYPE_CLASSIC
            is File -> VIEW_TYPE_FILE
            else -> throw IllegalArgumentException("check dataList in position $position, the data is ${dataList[position]}")
        }
    }


}