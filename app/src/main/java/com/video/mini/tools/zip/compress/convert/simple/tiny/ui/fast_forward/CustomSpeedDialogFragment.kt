package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.fast_forward

import android.view.View
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseDialogFragment
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.FragmentDialogCustomSpeedBinding

class CustomSpeedDialogFragment(
    private val isFast: Boolean = true,
    private val onValue: (Float) -> Unit
) :
    BaseDialogFragment<FragmentDialogCustomSpeedBinding>(R.layout.fragment_dialog_custom_speed) {
    override fun getViewBinding(view: View): FragmentDialogCustomSpeedBinding {
        return FragmentDialogCustomSpeedBinding.bind(view)
    }

    override fun initView() {
        binding.apply {

            dismiss.setOnClickListener {
                dismiss()
            }
            save.setOnClickListener {
                val value = value.text.toString()
                if (isFast) {
                    if (value.isNotBlank() && value.toFloat() in 1f..6f) {
                        onValue.invoke(value.toFloat())
                        dismiss()
                    }
                } else {
                    if (value.isNotBlank() && value.toFloat() in 0.2f..1f) {
                        onValue.invoke(value.toFloat())
                        dismiss()
                    }
                }
            }
        }
    }
}