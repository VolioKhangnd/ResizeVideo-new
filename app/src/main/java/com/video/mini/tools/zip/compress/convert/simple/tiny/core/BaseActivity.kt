package com.video.mini.tools.zip.compress.convert.simple.tiny.core

 import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.access.pro.activity.BaseActivity
import com.access.pro.callBack.OnShowInterstitialListener
import com.access.pro.callBack.OnShowNativeListener
import com.access.pro.config.ConfigModel
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
 import com.android.billingclient.api.QueryPurchasesParams
 import com.android.billingclient.api.queryProductDetails
 import com.android.billingclient.api.queryPurchasesAsync
 import com.google.android.gms.ads.nativead.NativeAd
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
 import com.google.firebase.remoteconfig.FirebaseRemoteConfig
 import com.google.firebase.remoteconfig.get
 import com.google.firebase.remoteconfig.ktx.remoteConfig
 import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

 import com.video.mini.tools.zip.compress.convert.simple.tiny.BuildConfig
 import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseActivity<VB : ViewBinding> : BaseActivity() {

    private var _binding: VB? = null
    val binding get() = _binding!!
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    var billingClient: BillingClient? = null


    fun setupBilling(purchasesUpdatedListener: PurchasesUpdatedListener) {
        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    lifecycleScope.launch {
                        getActivePurchase()
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }


    suspend fun getActivePurchase(): Boolean {
        val subResult = billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS).build()
        )
        val inappResult = billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP).build()
        )
        proApplication.isSubVip =
            (subResult != null && subResult.purchasesList.isNotEmpty())
                    || (inappResult != null && inappResult.purchasesList.isNotEmpty())
        return proApplication.isSubVip
    }


    open fun showBannerAds(viewContainer: ViewGroup) {
//        if (!proApplication.isSubVip) {
//            val banner = AdsBannerView.getView(windowManager, this, viewContainer)
//            AdsBannerView.loadAds(AdsBannerView.BANNER_BOTTOM, banner)
//        }
    }

    open fun showInterstitial(now: Boolean, call: (Boolean) -> Unit) {
//        if (!proApplication.isSubVip) {
//            showAds(now, object : OnShowInterstitialListener {
//                override fun onCloseAds(hasAds: Boolean) {
//                    call(hasAds)
//                }
//            })
//        } else {
//            call(true)
//        }
    }

    open fun showNativeAds(viewContainer: ViewGroup, call: (() -> Unit)? = null) {
//        if (!proApplication.isSubVip) {
//            nativeRender.prepareNative()
//            nativeRender.loadNativeAds(object : OnShowNativeListener {
//                override fun onLoadDone(hasAds: Boolean, currentNativeAd: NativeAd?) {
//                    // load dc native
//                    if (call != null) {
//                        call()
//                    }
//                }
//
//            }, viewContainer)
//        }
    }

    fun getConfigData(isSplash: Boolean) {
        var delayTime = if (isSplash) {
            1000L
        } else {
            0L
        }
//        if (!myApp.remoteDone) {
        android.os.Handler(mainLooper).postDelayed({
            val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 10
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.fetchAndActivate().addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    ConfigModel.timeInter = remoteConfig["time_inter"].asLong().toInt()
                    ConfigModel.forceUpdate = remoteConfig["force_update"].asBoolean()
                    ConfigModel.showSub = remoteConfig["show_sub"].asBoolean()
                    ConfigModel.subDefaultPack = remoteConfig["sub_default_pack"].asString()
                    ConfigModel.showCloseButton = remoteConfig["show_close_button"].asBoolean()
                    ConfigModel.forceUpdateVer =
                        remoteConfig["force_update_vercode"].asLong()
                            .toInt()                    //  myApp.remoteDone = true
                    if (ConfigModel.timeInter == 0) {
                        ConfigModel.timeInter = 20
                    }
                    if (ConfigModel.forceUpdate && ConfigModel.forceUpdateVer > BuildConfig.VERSION_CODE && !isSplash) {
                        // Hien diglog bat phai update, tu code lay 1 cai
                    }
                } else {
                    // myApp.remoteDone = false
                }
            }
        }, delayTime)
        //  }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        setContentView(binding.root)

        initData()
        initView()
        initObserver()
    }

    fun showMessage(message: String, isShort: Boolean = true) =
       runOnUiThread {  Toast.makeText(this, message, if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG).show() }

    fun setToolbar(
        toolbar: Toolbar,
        titleToolbar: String? = null,
        iconDrawable: Drawable,
        enabled: Boolean = true
    ) {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(iconDrawable)
            setDisplayShowHomeEnabled(enabled)
            if (titleToolbar != null) title = titleToolbar
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu != null && getMenu() != -1) {
            menuInflater.inflate(getMenu(), menu)
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    open fun getMenu(): Int = -1
    open fun initData() {}
    open fun initView() {}
    open fun initObserver() {}
    abstract fun getViewBinding(): VB
}