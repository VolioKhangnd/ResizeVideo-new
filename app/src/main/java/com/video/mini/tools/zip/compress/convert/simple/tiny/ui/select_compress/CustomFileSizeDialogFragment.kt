package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.select_compress

import android.annotation.SuppressLint
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseDialogFragment
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.FragmentDialogCustomFileSizeBinding


class CustomFileSizeDialogFragment(private val onSize: (Long, String) -> Unit) :
    BaseDialogFragment<FragmentDialogCustomFileSizeBinding>(R.layout.fragment_dialog_custom_file_size) {

    fun show(context: FragmentActivity) = this.show(
        context.supportFragmentManager,
        CustomFileSizeDialogFragment::class.simpleName
    )

    override fun getViewBinding(view: View): FragmentDialogCustomFileSizeBinding {
        return FragmentDialogCustomFileSizeBinding.bind(view)
    }

    private var unitSelected: String = "KB"
    private var unit = arrayOf(
        "KB", "MB"
    )

    @SuppressLint("ResourceAsColor")
    override fun initView() {
        binding.apply {
            val ad = ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_spinner_item,
                unit
            )
            ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = ad
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    unitSelected = unit[p2]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }

            dismiss.setOnClickListener {
                dismiss()
            }

            save.setOnClickListener {
                val sizeText = size.text.toString()

                if (sizeText.isNotBlank()) {
                    val sizeValue = sizeText.toLong()
                    val convertedSize = sizeValue
                    onSize.invoke(convertedSize, unitSelected)
                    dismiss()
                }
            }
        }
    }
}
