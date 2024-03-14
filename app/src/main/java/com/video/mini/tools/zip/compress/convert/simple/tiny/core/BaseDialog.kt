package com.video.mini.tools.zip.compress.convert.simple.tiny.core

import android.view.View
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.FragmentDialogBaseBinding
 import com.video.mini.tools.zip.compress.convert.simple.tiny.model.DialogModel

interface DialogClickListener {
    fun onPositive()
    fun onNegative()
}

class BaseDialog(
    private val dialogModel: DialogModel,
    private val dialogClickListener: DialogClickListener?
) :
    BaseDialogFragment<FragmentDialogBaseBinding>(R.layout.fragment_dialog_base) {


    override fun getViewBinding(view: View): FragmentDialogBaseBinding {
        return FragmentDialogBaseBinding.bind(view)
    }

    override fun initView() {
        binding.apply {
            dialogModel = this@BaseDialog.dialogModel
            dismiss.setOnClickListener {
                dialogClickListener?.onNegative()
                dismiss()
            }
            save.setOnClickListener {
                dialogClickListener?.onPositive()
                dismiss()
            }
        }
    }
}
