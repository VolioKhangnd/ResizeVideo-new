package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.intro

import android.content.Intent
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ActivityIntroBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.main.MainActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.sup_vip.SubVipActivity

class IntroActivity : BaseActivity<ActivityIntroBinding>() {
    override fun getViewBinding() = ActivityIntroBinding.inflate(layoutInflater)
    override fun initView() {

        binding.apply {
            showBannerAds(bannerAds)
            val introAdapter = IntroAdapter(this@IntroActivity)
            viewPager2.adapter = introAdapter
            circleIndicator3.setViewPager(viewPager2)

            btnGetStarted.setOnClickListener {
                val currentItem = viewPager2.currentItem
                if (currentItem == 1) {
                    startActivity(Intent(this@IntroActivity, MainActivity::class.java))
                    finish()
                    return@setOnClickListener
                }
                viewPager2.setCurrentItem(currentItem + 1, true)

            }
        }

    }
}