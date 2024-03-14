package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.video_pickers

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseFragment
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.FragmentMainPickerBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.OnTabSelectedListener

class MainPickerFragment : BaseFragment<FragmentMainPickerBinding>(R.layout.fragment_main_picker) {
    override fun getViewBinding(view: View): FragmentMainPickerBinding {
        return FragmentMainPickerBinding.bind(view)
    }

    override fun initView() {
        setToolbar(
            binding.mToolbar,
            "Import",
            AppCompatResources.getDrawable(requireActivity(), R.drawable.baseline_arrow_back_24)!!,
            true
        )
        binding.apply {
            val pagerAdapter = ViewPagerAdapter(requireActivity())
            binding.viewPager.adapter = pagerAdapter

            tabLayout.setOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(position: Int) {
                    viewPager.setCurrentItem(position, true)
                }
            })
            tabLayout.attach(viewPager, "Videos", "Folders")
            tabLayout.setTabSelected(0)
        }



        binding.mToolbar.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }
}