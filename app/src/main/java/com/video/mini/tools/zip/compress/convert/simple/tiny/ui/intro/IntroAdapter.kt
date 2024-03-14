package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.intro

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class IntroAdapter(private val activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        if (position == 0) return Intro1Fragment()
        return Intro2Fragment()
    }
}