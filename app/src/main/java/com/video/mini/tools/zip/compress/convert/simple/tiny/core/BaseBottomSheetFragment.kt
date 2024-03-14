package com.video.mini.tools.zip.compress.convert.simple.tiny.core

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetFragment<VB : ViewBinding>(private val layout: Int) :
    BottomSheetDialogFragment(layout) {
    private var _binding: VB? = null
    val binding get() = _binding!!
    private lateinit var activity: FragmentActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity = requireActivity()
        _binding = getViewBinding(view)

        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet). apply {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    peekHeight = 0
                }
            }
            val layoutParams = bottomSheet?.layoutParams
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
            layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
            bottomSheet?.layoutParams = layoutParams
            isCancelable = true
        }

        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    abstract fun getViewBinding(view: View): VB
    abstract fun initView()
    open fun initData(){}
    open fun initObserve() {}
}