package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.main

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseAdapter
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ItemActionBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.DetailActionMedia


class ActionVideoAdapter : BaseAdapter<ItemActionBinding, DetailActionMedia>() {
    override fun getViewBinding(inflater: LayoutInflater, parent: ViewGroup) =
        ItemActionBinding.inflate(inflater, parent, false)

    override fun onBind(binding: ItemActionBinding, item: DetailActionMedia) {
        binding.apply {
            mCConstraint.backgroundTintList = ColorStateList.valueOf(item.backGroundAction)
            imageIcon.backgroundTintList = ColorStateList.valueOf(item.backGroundIcon)
            imageIcon.setImageDrawable(item.icon)
            title.text = item.title
            imgIsSubVip.visibility = if (!item.isSubVip) View.GONE else View.VISIBLE
        }
    }
}