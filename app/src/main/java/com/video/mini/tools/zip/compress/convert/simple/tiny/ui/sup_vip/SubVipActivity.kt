package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.sup_vip

import android.content.Intent
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.main.MainActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ActivitySubVipBinding

class SubVipActivity : BaseActivity<ActivitySubVipBinding>() {
    override fun getViewBinding() = ActivitySubVipBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {

            btnUseFree.setOnClickListener {
                startActivity(Intent(this@SubVipActivity, MainActivity::class.java))
                finish()
            }

        }
    }
}