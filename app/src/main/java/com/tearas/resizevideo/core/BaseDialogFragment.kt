package com.tearas.resizevideo.core

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding

abstract class BaseDialogFragment<VB : ViewBinding>(private val layout: Int) :
    DialogFragment(layout) {
    private var _binding: VB? = null
    val binding get() = _binding!!
    private lateinit var activity: FragmentActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        activity = requireActivity()
        _binding = getViewBinding(view)
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
        initObserver()

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    abstract fun getViewBinding(view: View): VB
    abstract fun initView()
    open fun initData() {}
    open fun initObserver() {}

}