package com.video.mini.tools.zip.compress.convert.simple.tiny.google

import android.app.Activity
import com.access.pro.config.ConfigModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.video.mini.tools.zip.compress.convert.simple.tiny.BuildConfig

object RemoteConfigHelper {
    fun getConfigData() {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 10
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                ConfigModel.timeInter = remoteConfig["time_inter"].asLong().toInt()
                AdsHelper.ENABLE_ADS = remoteConfig["enable_ads"].asBoolean()
                if (ConfigModel.forceUpdate && ConfigModel.forceUpdateVer > BuildConfig.VERSION_CODE) {
                    // Hien diglog bat phai update, tu code lay 1 cai
                }
            } else {
                // myApp.remoteDone = false
            }
        }
    }
}