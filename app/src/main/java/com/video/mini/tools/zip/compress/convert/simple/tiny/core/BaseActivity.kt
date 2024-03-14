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
import com.android.billingclient.api.queryProductDetails
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
    private var subWeekProduct: ProductDetails? = null
    private var subMonthProduct: ProductDetails? = null
    private var lifeTimeProduct: ProductDetails? = null
    private var currentProduct: ProductDetails? = null
    private var restore = false

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        proApplication.isSubVip = true
                        //update UI hiển thị đã mua
                        if (!purchase.isAcknowledged) {
                            val acknowledgePurchaseParams =
                                AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.purchaseToken).build()

                            billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                            }
                        }
                    }
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            } else {
            }
        }

    private fun initBillingClient() {
        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
    }

    private suspend fun getPurchasesProductDetail(
        packId: String,
        productType: String
    ): ProductDetails? {
        val productList = mutableListOf<QueryProductDetailsParams.Product>()
        productList.add(
            QueryProductDetailsParams.Product.newBuilder().setProductId(packId)
                .setProductType(productType).build()
        )
        val paramsSub = QueryProductDetailsParams.newBuilder()
        paramsSub.setProductList(productList)
        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient!!.queryProductDetails(paramsSub.build())
        }
        if (!productDetailsResult.productDetailsList.isNullOrEmpty()) {
            return productDetailsResult.productDetailsList!!.first()
        } else {
            return null
        }
    }

    private fun showPurchaseDialog() {
        if (currentProduct != null) {
            val productDetailsParamsList: List<BillingFlowParams.ProductDetailsParams>
            if (currentProduct!!.productType == BillingClient.ProductType.SUBS) {
                productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(currentProduct!!)
                        .setOfferToken(currentProduct!!.subscriptionOfferDetails!!.first().offerToken)
                        .build()
                )
            } else {
                productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(currentProduct!!)
                        .build()
                )
            }
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()
            billingClient!!.launchBillingFlow(this@BaseActivity, billingFlowParams)
        } else {
            restore = false
            startConnection()
        }
    }

    private fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    lifecycleScope.launch {
                        processPurchases()
                     //   getActivePurchase()
                        runOnUiThread {
                            if (proApplication.isSubVip) {
                                //update UI hien thi da mua vip
                            } else {
                                //update UI hien thi chua mua
                            }

                            if (restore) {
                                Toast.makeText(
                                    this@BaseActivity,
                                    "Restore Success",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        this@BaseActivity,
                        billingResult.debugMessage,
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

            override fun onBillingServiceDisconnected() {
                Toast.makeText(
                    this@BaseActivity,
                    "Connect billing service failed! Check Your network connection.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    suspend fun processPurchases() {
        subMonthProduct =
            getPurchasesProductDetail("pack_sub_month", BillingClient.ProductType.SUBS)
        subWeekProduct = getPurchasesProductDetail("pack_sub_week", BillingClient.ProductType.SUBS)
        lifeTimeProduct =
            getPurchasesProductDetail("pack_life_time", BillingClient.ProductType.INAPP)
         //  runOnUiThread {
        fillDataToUI()
        // }
    }

    private fun fillDataToUI() {
//        if (subMonthProduct != null && !subMonthProduct!!.subscriptionOfferDetails.isNullOrEmpty()) {
//            val product = subMonthProduct!!.subscriptionOfferDetails!!.first()
//            val pricingPhase = product.pricingPhases.pricingPhaseList
//            if (pricingPhase.size > 1) {
//                binding.txtPriceMonth.text =
//                    "Then\n".plus(product.pricingPhases.pricingPhaseList[1].formattedPrice)
//                        .plus("/month")
//                binding.txtMonth.text =
//                    product.pricingPhases.pricingPhaseList[0].formattedPrice.plus("\nFor 3 Day")
//            } else if (pricingPhase.isNotEmpty()) {
//                binding.txtPriceMonth.text =
//                    "Then\n".plus(
//                        product.pricingPhases.pricingPhaseList[0].formattedPrice
//                    ).plus("/month")
//                binding.txtMonth.text =
//                    product.pricingPhases.pricingPhaseList[0].formattedPrice
//            }
//        }
//        if (subWeekProduct != null && !subWeekProduct!!.subscriptionOfferDetails.isNullOrEmpty()) {
//            var product = subWeekProduct!!.subscriptionOfferDetails!!.first()
//            var pricingPhase = product.pricingPhases.pricingPhaseList
//            if (pricingPhase.size > 1) {
//                binding.txtPriceWeek.text = "Then\n".plus(
//                    product.pricingPhases.pricingPhaseList[1].formattedPrice
//                ).plus("/week")
//                binding.txtWeek.text =
//                    product.pricingPhases.pricingPhaseList[0].formattedPrice.plus("\nFor 3 Day")
//            } else if (pricingPhase.isNotEmpty()) {
//                binding.txtPriceWeek.text = "Then\n".plus(
//                    product.pricingPhases.pricingPhaseList[0].formattedPrice
//                ).plus("/week")
//                binding.txtWeek.text =
//                    product.pricingPhases.pricingPhaseList[0].formattedPrice
//            }
//        }
//        if (lifeTimeProduct != null) {
//            val product = lifeTimeProduct!!.oneTimePurchaseOfferDetails
//            if (product != null) {
//                binding.txtPriceLifeTime.text = product.formattedPrice
//            }
//        }
//
//        Log.d("ádasdsdd", subMonthProduct.toString() + "" + subWeekProduct + lifeTimeProduct)
    }


    open fun showBannerAds(viewContainer: ViewGroup) {
//        if (!proApplication.isSubVip) {
//            val banner = AdsBannerView.getView(windowManager, this, viewContainer)
//            AdsBannerView.loadAds(AdsBannerView.BANNER_BOTTOM, banner)
//        }
    }

    open fun showInterstitial(now: Boolean, call: (Boolean) -> Unit) {
        if (!proApplication.isSubVip) {
            showAds(now, object : OnShowInterstitialListener {
                override fun onCloseAds(hasAds: Boolean) {
                    call(hasAds)
                }
            })
        } else {
            call(true)
        }
    }

    open fun showNativeAds(viewContainer: ViewGroup, call: (() -> Unit)? = null) {
        if (!proApplication.isSubVip) {
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
        Toast.makeText(this, message, if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG).show()

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