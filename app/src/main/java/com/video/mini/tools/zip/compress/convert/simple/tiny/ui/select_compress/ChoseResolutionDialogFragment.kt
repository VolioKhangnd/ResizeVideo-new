package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.select_compress

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseDialogFragment
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.FragmentDialogChoseResolutionBinding

import com.video.mini.tools.zip.compress.convert.simple.tiny.model.Resolution


class ChoseResolutionDialogFragment :
    BaseDialogFragment<FragmentDialogChoseResolutionBinding>(R.layout.fragment_dialog_chose_resolution) {

    private val viewModel: SelectCompressViewModel by lazy {
        ViewModelProvider(requireActivity())[SelectCompressViewModel::class.java]
    }
    private val viewModelChoseResolution: ChoseResolutionViewModel by lazy {
        ViewModelProvider(requireActivity())[ChoseResolutionViewModel::class.java]
    }

    fun show(context: FragmentActivity) = this.show(
        context.supportFragmentManager, com.video.mini.tools.zip.compress.convert.simple.tiny.ui.select_compress.ChoseResolutionDialogFragment::class.simpleName
    )

    companion object {
        fun newInstance() = ChoseResolutionDialogFragment()
    }

    override fun getViewBinding(view: View): FragmentDialogChoseResolutionBinding {
        return FragmentDialogChoseResolutionBinding.bind(view)
    }

    private lateinit var resolution: Resolution
    override fun initData() {
        binding.apply {
            binding.choseResolutionViewModel = viewModelChoseResolution
            binding.lifecycleOwner = this@ChoseResolutionDialogFragment
            resolution = viewModel.resolutionOriginLiveData.value!!
            viewModelChoseResolution.postOriginalResolution(resolution)
            viewModelChoseResolution.setTextOptions()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelChoseResolution.clearData()
    }

    override fun initObserver() {
        viewModelChoseResolution.apply {

            choseResolution.observe(this@ChoseResolutionDialogFragment) {
                resolution = it
            }

        }

        viewModel.resolutionCustomLiveData.observe(this@ChoseResolutionDialogFragment) {
            binding.custom.text = it.toString()
            resolution = it
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun initView() {
        binding.apply {
            save.setOnClickListener {
                viewModel.postResolution(resolution)
                dismissNow()
            }

            dismiss.setOnClickListener {
                dismissNow()
            }
        }
    }


}