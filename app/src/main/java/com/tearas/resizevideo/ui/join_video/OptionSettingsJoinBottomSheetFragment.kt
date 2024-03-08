package com.tearas.resizevideo.ui.join_video

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Spinner
import com.tearas.resizevideo.R
import com.tearas.resizevideo.core.BaseBottomSheetFragment
import com.tearas.resizevideo.databinding.FragmentOptionSettingsJoinBottomSheetBinding
import com.tearas.resizevideo.model.Resolution
import com.tearas.resizevideo.utils.Utils

class OptionSettingsJoinBottomSheetFragment :
    BaseBottomSheetFragment<FragmentOptionSettingsJoinBottomSheetBinding>(R.layout.fragment_option_settings_join_bottom_sheet) {
    override fun getViewBinding(view: View) =
        FragmentOptionSettingsJoinBottomSheetBinding.bind(view)

    private var resolution: Resolution? = null
    private var totalTime: Long? = null
    override fun initData() {
        arguments?.apply {
            resolution = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getSerializable("resolution", Resolution::class.java)
            } else {
                getSerializable("resolution") as Resolution
            }
            totalTime = getLong("totalTime")
        }
    }

    override fun initView() {
        binding.resolution.text = resolution.toString()
        binding.totalTime.text = Utils.formatTime(totalTime!!)
        binding.apply {
            spnFormat.setAdapterSpinner(false)
            spnCodec.setAdapterSpinner(true, "mp4")
            spnCodec.setSelection(0)
            spnFormat.setSelection(0)


            done.setOnClickListener {
                if (title.text.isNotBlank()) {
                    dismiss()
                    (requireActivity() as JoinVideoActivity).onDone(
                        title.text.toString(),
                        spnFormat.selectedItem.toString(),
                        spnCodec.selectedItem.toString()
                    )
                }
            }
        }
    }

    companion object {
        fun getInstance(resolution: Resolution, totalTime: Long) =
            OptionSettingsJoinBottomSheetFragment().apply {
                val bundle = Bundle()
                bundle.putSerializable("resolution", resolution)
                bundle.putLong("totalTime", totalTime)
                arguments = bundle
            }
    }

    private fun Spinner.setAdapterSpinner(isCodec: Boolean, format: String = "mp3") {
        var list = if (!isCodec) Utils.formatVideos.map { it.key }
            .toList() else Utils.formatVideos[format]!!.toList()
        list = ArrayList(list).apply { add(0, "RECOMMENDED") }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            list
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.adapter = adapter
    }
}