package com.video.mini.tools.zip.compress.convert.simple.tiny.ui

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseDialogFragment
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.FragmentDialogLoadingBinding

class DialogLoading :
    BaseDialogFragment<FragmentDialogLoadingBinding>(R.layout.fragment_dialog_loading) {
    override fun getViewBinding(view: View): FragmentDialogLoadingBinding {
        return FragmentDialogLoadingBinding.bind(view)
    }

    companion object {
        private var dialogLoading: DialogLoading = DialogLoading()
        fun show(context: FragmentActivity) {
            dialogLoading.show(context.supportFragmentManager, DialogLoading::class.simpleName)
        }

        fun hide() = dialogLoading.dismiss()

    }

    override fun initView() {

    }
}