package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.join_video

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseBottomSheetFragment
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.FragmentOptionSettingsJoinBottomSheetBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.Resolution
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.Utils

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

    private var formatBefore = "RECOMMENDED"

    override fun initView() {
        binding.resolution.text = resolution.toString()
        binding.totalTime.text = Utils.formatTime(totalTime!!)
        binding.apply {
            close.setOnClickListener { dismiss() }
            spnFormat.setAdapterSpinner(false)
            spnCodec.setAdapterSpinner(true, "MP4")
            spnCodec.setSelection(0)
            spnFormat.setSelection(0)
            onItemSlectedFormat()
            done.setOnClickListener {
                done()
            }
        }
    }

    private fun done() {
       binding.apply {
           if (title.text.isNotBlank() && isAdded) {
               dismiss()
               (requireActivity() as JoinVideoActivity).onDone(
                   title.text.toString(),
                   spnFormat.selectedItem.toString(),
                   if (spnCodec.selectedItemPosition == 0) null else spnCodec.selectedItem.toString()
               )
           }
       }
    }

    private fun onItemSlectedFormat() {
        binding.apply {
            spnFormat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (formatBefore != spnFormat.selectedItem && formatBefore != "RECOMMENDED") spnCodec.setAdapterSpinner(
                        true,
                        spnFormat.selectedItem as String
                    )
                    formatBefore = spnFormat.selectedItem as String
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

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

    private fun Spinner.setAdapterSpinner(isCodec: Boolean, format: String = "MP4") {
        var list = if (!isCodec) Utils.formatVideos.map { it.key }
            .toList() else Utils.formatVideos[format]!!.toList()
        list = ArrayList(list).apply { if (isCodec) add(0, "RECOMMENDED") }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            list
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.adapter = adapter
    }
}