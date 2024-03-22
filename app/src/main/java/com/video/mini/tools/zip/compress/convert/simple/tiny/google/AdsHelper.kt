package com.video.mini.tools.zip.compress.convert.simple.tiny.google

import android.util.Log
import android.view.ViewGroup
import com.access.pro.activity.BaseActivity
import com.access.pro.adcontrol.AdsBannerView
import com.access.pro.callBack.OnShowInterstitialListener
import com.access.pro.callBack.OnShowNativeListener
import com.access.pro.config.ConfigModel
import com.google.android.gms.ads.nativead.NativeAd

open class AdsHelper : BaseActivity() {
    companion object {
        val DELAY_NEXT_SHOW_AD
            get() = if (ConfigModel.timeInter == 0) 60000 else ConfigModel.timeInter
        var ENABLE_ADS = true
        var LAST_TIME_SHOW_INTERSTITIAL = -1L
        var IS_SUB_VIP = false
        var  SHOW_OPEN_FIRST_MAIN = false
    }

    open fun showBannerAds(viewContainer: ViewGroup) {
        if (!proApplication.isSubVip && ENABLE_ADS) {
            val banner = AdsBannerView.getView(windowManager, this, viewContainer)
            AdsBannerView.loadAds(AdsBannerView.BANNER_BOTTOM, banner)
        }
    }

    open fun showInterstitial(now: Boolean, call: (Boolean) -> Unit) {
        if (!proApplication.isSubVip && System.currentTimeMillis() - LAST_TIME_SHOW_INTERSTITIAL >= DELAY_NEXT_SHOW_AD && ENABLE_ADS) {
            showAds(true, object : OnShowInterstitialListener {
                override fun onCloseAds(hasAds: Boolean) {
                    if (hasAds) LAST_TIME_SHOW_INTERSTITIAL = System.currentTimeMillis()
                    call(hasAds)
                }
            })
        }
    }

    open fun showNativeAds(viewContainer: ViewGroup, call: (() -> Unit)? = null) {
        if (!proApplication.isSubVip && ENABLE_ADS) {
            nativeRender.prepareNative()
            nativeRender.loadNativeAds(object : OnShowNativeListener {
                override fun onLoadDone(hasAds: Boolean, currentNativeAd: NativeAd?) {
                    // load dc native
                    if (call != null) {
                        call()
                    }
                }

            }, viewContainer)
        }
    }
}