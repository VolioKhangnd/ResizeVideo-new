package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.cut_trim

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.CropModel


class SpinCropAdapter(
    private val context: Context,
    private val layout: Int,
    private val listCrop: Array<CropModel>
) : ArrayAdapter<CropModel>(context, layout, listCrop) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return (super.getView(position, convertView, parent) as MaterialButton).apply {
            icon = context.getDrawable(listCrop[position].drawable)
            text=listCrop[position].title
        }
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return (super.getDropDownView(position, convertView, parent) as MaterialButton).apply {
            icon = context.getDrawable(listCrop[position].drawable)
            text=listCrop[position].title
        }
    }
}