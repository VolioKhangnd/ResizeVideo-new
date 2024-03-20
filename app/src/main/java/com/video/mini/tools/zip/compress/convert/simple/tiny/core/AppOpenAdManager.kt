package com.video.mini.tools.zip.compress.convert.simple.tiny.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.AdError

import com.google.android.gms.ads.FullScreenContentCallback
import com.video.mini.tools.zip.compress.convert.simple.tiny.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

class AppOpenAdManager constructor(val application: Application) :
    Application.ActivityLifecycleCallbacks {

    private var isShowingAd = false
    private var appOpenAd: AppOpenAd? = null
    private var loadCallback: AppOpenAdLoadCallback = object : AppOpenAdLoadCallback() {
        override fun onAdLoaded(ad: AppOpenAd) {
            appOpenAd = ad
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            appOpenAd = null
        }
    }

    private var currentActivity: Activity? = null


    private fun fetchAd() {
        // Have unused ad, no need to fetch another.
        if (appOpenAd != null) {
            return
        }
        AppOpenAd.load(
            application, BuildConfig.GG_APP_OPEN,
            provideAdRequest(), AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback
        )
    }

    fun showAdIfAvailable() {
        if (!isShowingAd && currentActivity != null && appOpenAd != null) {
            appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.
                    appOpenAd = null
                    isShowingAd = false
                    fetchAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {}
                override fun onAdShowedFullScreenContent() {
                    isShowingAd = true
                }
            }
            appOpenAd!!.show(currentActivity!!)
        } else {
            fetchAd()
        }
    }

    private fun provideAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }
}