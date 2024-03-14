package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.result

import android.view.View
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseDialogFragment
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.FragmentDialogRenameBinding

class RenameDialogFragment(private val onValue: (String) -> Unit) :
    BaseDialogFragment<FragmentDialogRenameBinding>(R.layout.fragment_dialog_rename) {
    override fun getViewBinding(view: View): FragmentDialogRenameBinding {
        return FragmentDialogRenameBinding.bind(view)
    }

    override fun initView() {
        binding.apply {

            dismiss.setOnClickListener {
                dismiss()
            }
            save.setOnClickListener {
                val value = value.text.toString()
                if (value.isNotBlank()) {
                    onValue.invoke(value)
                    dismiss()
                }
            }
        }
    }
}