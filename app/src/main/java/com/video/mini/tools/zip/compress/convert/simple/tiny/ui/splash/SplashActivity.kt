package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.main.MainActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ActivitySplashBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.intro.IntroActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.sup_vip.SubVipActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.OpenAppManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override fun getViewBinding() = ActivitySplashBinding.inflate(layoutInflater)

    override fun initView() {
        val openAppManager = OpenAppManager(this)

        val intentClass = when {
            !openAppManager.isOpenAppFirstTime() -> IntroActivity::class.java
            proApplication.isSubVip -> MainActivity::class.java
            else -> MainActivity::class.java
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, intentClass))
            finish()
        }, 3000)
    }
}