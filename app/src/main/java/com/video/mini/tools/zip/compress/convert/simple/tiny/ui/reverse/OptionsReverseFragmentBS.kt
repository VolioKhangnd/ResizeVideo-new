package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.reverse

import android.view.View
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseBottomSheetFragment
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.FragmentOptionReveresBottomSheetBinding

class OptionsReverseFragmentBS :
    BaseBottomSheetFragment<FragmentOptionReveresBottomSheetBinding>(R.layout.fragment_option_reveres_bottom_sheet) {
    override fun getViewBinding(view: View): FragmentOptionReveresBottomSheetBinding {
        return FragmentOptionReveresBottomSheetBinding.bind(view)
    }

    private val presets = listOf("fast", "slow")
    private var preset = presets[0]
    override fun initView() {
        binding.apply {
            close.setOnClickListener { dismiss() }
            onOptionsChecked()
            done.setOnClickListener {
                (requireActivity() as ReversesActivity).onDone(cbReverseAudio.isChecked, preset)
            }
        }
    }

    private fun onOptionsChecked() {
        val onClick = View.OnClickListener {
            when (it.id) {
                R.id.mOptionFast -> {
                    preset = presets[0]
                    binding.cbFast.isChecked = true
                    binding.cbStronger.isChecked = false
                }

                R.id.mOptionStronger -> {
                    preset = presets[1]
                    binding.cbFast.isChecked = false
                    binding.cbStronger.isChecked = true
                }
            }
        }
        binding.mOptionStronger.setOnClickListener(onClick)
        binding.mOptionFast.setOnClickListener(onClick)
    }
}