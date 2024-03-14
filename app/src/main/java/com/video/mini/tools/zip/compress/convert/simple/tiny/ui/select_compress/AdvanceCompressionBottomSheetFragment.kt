package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.select_compress

import android.view.View
import android.widget.ArrayAdapter
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseBottomSheetFragment
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.AdvanceCompressionBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.OptionCompressType

interface ChoseAdvance {
    fun onChoseAdvance(
        optionCompressType: OptionCompressType, bitRate: Long, frameRate: Int
    )
}

class AdvanceCompressionBottomSheetFragment(
    private var bitrate: Long, private val choseAdvance: ChoseAdvance
) : BaseBottomSheetFragment<AdvanceCompressionBinding>(R.layout.advance_compression) {
    private var listSize: Array<OptionCompressType> = arrayOf(
        OptionCompressType.Small,
        OptionCompressType.Medium,
        OptionCompressType.Large,
        OptionCompressType.Origin,
    )

    override fun getViewBinding(view: View): AdvanceCompressionBinding {
        return AdvanceCompressionBinding.bind(view)
    }

    var frameRate = 15
    private val listRate = arrayOf(15, 30, 45, 60, 90, 120)
    private var size: OptionCompressType = OptionCompressType.Medium
    override fun initView() {
        binding.apply {
            setupFrameRate()
            setupRadioGroup()
            setupDoneButton()
        }
    }

    private fun setupFrameRate() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listRate)
        binding.spinnerFrameRate.setAdapter(adapter)
        binding.spinnerFrameRate.setSelection(0)

    }

    private fun setupRadioGroup() {
        binding.radioGroup3.setOnCheckedChangeListener { radioGroup, checkedId ->
            val index = radioGroup.indexOfChild(radioGroup.findViewById<View>(checkedId))
            size = listSize[index]
        }
    }

    private fun setupDoneButton() {
        binding.done.setOnClickListener {
            val bitrate = binding.edtBitrate.text.toString().toLong()
            val frameRate = binding.spinnerFrameRate.selectedItem as Int
            if (bitrate > 0) {
                choseAdvance.onChoseAdvance(
                    size,
                    bitrate,
                    frameRate
                )
                dismissNow()
            }
        }
    }

}