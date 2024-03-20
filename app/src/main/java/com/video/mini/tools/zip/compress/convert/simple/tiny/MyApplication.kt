package com.video.mini.tools.zip.compress.convert.simple.tiny;

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.access.pro.application.ProApplication
import com.access.pro.config.AdsConfigModel
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.AppOpenAdManager
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.DarkModeManager
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.DialogUtils


class MyApplication : ProApplication() {
    override fun onCreate() {
        super.onCreate()
        val testDeviceIds: List<String> =
            mutableListOf("0A616E7B2A72EB996BF974ACBF5855FC", "DEEC3DF04481FB7EA3741E6984AD9958")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
        DialogUtils.init(this)
        DarkModeManager(this).setDarkMode()
        MobileAds.initialize(this) {}
        AdsConfigModel.GG_APP_OPEN = BuildConfig.GG_APP_OPEN
        AdsConfigModel.GG_BANNER = BuildConfig.GG_BANNER
        AdsConfigModel.GG_NATIVE = BuildConfig.GG_NATIVE
        AdsConfigModel.GG_FULL = BuildConfig.GG_FULL
        AdsConfigModel.GG_REWARDED = BuildConfig.GG_REWARDED
        val openAdManager = AppOpenAdManager(this)
        registerActivityLifecycleCallbacks(openAdManager)
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() {
                openAdManager.showAdIfAvailable()
            }
        })

    }
}
