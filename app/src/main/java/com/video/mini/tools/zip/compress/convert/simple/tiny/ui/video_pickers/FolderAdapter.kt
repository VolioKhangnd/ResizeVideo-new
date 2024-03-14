package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.video_pickers

import android.view.LayoutInflater
import android.view.ViewGroup
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseAdapter
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ItemFolderBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.FolderInfo

class FolderAdapter : BaseAdapter<ItemFolderBinding, FolderInfo>() {
    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemFolderBinding {
        return ItemFolderBinding.inflate(inflater, parent, false)
    }

    override fun onBind(binding: ItemFolderBinding, item: FolderInfo) {
        binding.apply {
            name.text = item.name
            count.text = item.count +" videos"
        }
    }

}