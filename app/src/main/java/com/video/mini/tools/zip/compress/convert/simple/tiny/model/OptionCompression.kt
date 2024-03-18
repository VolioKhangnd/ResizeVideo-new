package com.video.mini.tools.zip.compress.convert.simple.tiny.model

import android.content.Context
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.ResolutionUtils.calculateResolution

data class OptionCompression(
    val title: String,
    var description: String,
    var resolution: Resolution? = null,
    var detail: String = "",
    val type: OptionCompressType,
    var isSelected: Boolean = false,
    val isSubVip: Boolean = false,
) {
    companion object {
        fun getOptionsCompression(
            context: Context,
            resolutionOrigin: Resolution?
        ): List<OptionCompression> {
            val listTitle = context.resources.getStringArray(R.array.list_title_options)
            val listDescription = context.resources.getStringArray(R.array.list_description_options)
            val listCompressType = listOf(
                OptionCompressType.Small,
                OptionCompressType.Medium,
                OptionCompressType.Large,
                OptionCompressType.Origin,
                OptionCompressType.Custom,
                OptionCompressType.CustomFileSize,
                OptionCompressType.AdvanceTypeOption
            )

            val options = mutableListOf<OptionCompression>()
            for (index in listTitle.indices) {
                val title = listTitle[index]
                val resolution = resolutionOrigin?.calculateResolution(listCompressType[index])
                val description = listDescription[index]
                val compressType = listCompressType[index]
                val isSelected = index == 0
                val detail =
                    if (resolutionOrigin != null && index < 4) resolution.toString() else ""
                val option = OptionCompression(
                    title,
                    description,
                    resolution,
                    detail,
                    compressType,
                    isSelected
                )
                options.add(option)
            }
            return options
        }
    }

}
