package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.splash

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.main.MainActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ActivitySplashBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.google.RemoteConfigHelper
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.intro.IntroActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.OpenAppManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    override fun getViewBinding() = ActivitySplashBinding.inflate(layoutInflater)

    private lateinit var openAppManager: OpenAppManager

    override fun initView() {
        RemoteConfigHelper.getConfigData()
        openAppManager = OpenAppManager(this)
        setupBilling(::goToNextActivity) { _, _ ->

        }
    }


    private fun goToNextActivity() {
        val intentClass = when {
            !openAppManager.isOpenAppFirstTime() -> IntroActivity::class.java
            proApplication.isSubVip -> MainActivity::class.java
            else -> MainActivity::class.java
        }
        lifecycleScope.launch {
            delay(3000)
            startActivity(Intent(this@SplashActivity, intentClass))
            finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@SplashActivity, message, Toast.LENGTH_SHORT).show()
    }

}