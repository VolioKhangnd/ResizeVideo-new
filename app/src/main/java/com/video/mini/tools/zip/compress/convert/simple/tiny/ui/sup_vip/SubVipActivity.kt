package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.sup_vip

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
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
import com.limurse.iap.DataWrappers
import com.limurse.iap.IapConnector
import com.limurse.iap.PurchaseServiceListener
import com.limurse.iap.SubscriptionServiceListener
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.main.MainActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ActivitySubVipBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubVipActivity : BaseActivity<ActivitySubVipBinding>() {
    override fun getViewBinding() = ActivitySubVipBinding.inflate(layoutInflater)
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

    private val billingClientStateListener = object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                lifecycleScope.launch {
                    processPurchases()
                    getActivePurchase()
                    runOnUiThread {
                        if (proApplication.isSubVip) {
                            //update UI hien thi da mua vip
                        } else {
                            //update UI hien thi chua mua
                        }

                        if (restore) {
                            Toast.makeText(
                                this@SubVipActivity,
                                "Restore Success",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    this@SubVipActivity,
                    billingResult.debugMessage,
                    Toast.LENGTH_SHORT
                ).show()

            }
        }

        override fun onBillingServiceDisconnected() {
            Toast.makeText(
                this@SubVipActivity,
                "Connect billing service failed! Check Your network connection.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private var subWeekProduct: ProductDetails? = null
    private var subMonthProduct: ProductDetails? = null
    private var lifeTimeProduct: ProductDetails? = null
    private var currentProduct: ProductDetails? = null

    private fun setupBilling() {
        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        billingClient!!.startConnection(billingClientStateListener)
    }

    suspend fun processPurchases() {
        subMonthProduct =
            getPurchasesProductDetail("pack_sub_month", BillingClient.ProductType.SUBS)
        subWeekProduct = getPurchasesProductDetail("pack_sub_week", BillingClient.ProductType.SUBS)
        lifeTimeProduct =
            getPurchasesProductDetail("pack_life_time", BillingClient.ProductType.INAPP)
        when (ConfigModel.subDefaultPack) {
            "pack_sub_week" -> {
                currentProduct = subWeekProduct
            }

            "pack_sub_month" -> {
                currentProduct = subMonthProduct

            }

            "pack_life_time" -> {
                currentProduct = lifeTimeProduct

            }
        }


        //  runOnUiThread {
        fillDataToUI()
        // }
    }

    private fun fillDataToUI() {
        if (subMonthProduct != null && !subMonthProduct!!.subscriptionOfferDetails.isNullOrEmpty()) {
            var product = subMonthProduct!!.subscriptionOfferDetails!!.first()
            var pricingPhase = product.pricingPhases.pricingPhaseList
            if (pricingPhase.size > 1) {
                binding.txtPriceMonth.text = "Then\n".plus(product.pricingPhases.pricingPhaseList[1].formattedPrice ).plus("/month")
                binding.txtMonth.text =
                    product.pricingPhases.pricingPhaseList[0].formattedPrice.plus("\nFor 3 Day")
            } else if (pricingPhase.isNotEmpty()) {
                binding.txtPriceMonth.text =
                    "Then\n".plus(
                        product.pricingPhases.pricingPhaseList[0].formattedPrice
                    ).plus("/month")
                binding.txtMonth.text =
                    product.pricingPhases.pricingPhaseList[0].formattedPrice
            }
        }
        if (subWeekProduct != null && !subWeekProduct!!.subscriptionOfferDetails.isNullOrEmpty()) {
            var product = subWeekProduct!!.subscriptionOfferDetails!!.first()
            var pricingPhase = product.pricingPhases.pricingPhaseList
            if (pricingPhase.size > 1) {
                binding.txtPriceWeek.text = "Then\n".plus(
                    product.pricingPhases.pricingPhaseList[1].formattedPrice
                ).plus("/week")
                binding.txtWeek.text =
                    product.pricingPhases.pricingPhaseList[0].formattedPrice.plus("\nFor 3 Day")
            } else if (pricingPhase.isNotEmpty()) {
                binding.txtPriceWeek.text = "Then\n".plus(
                    product.pricingPhases.pricingPhaseList[0].formattedPrice
                ).plus("/week")
                binding.txtWeek.text =
                    product.pricingPhases.pricingPhaseList[0].formattedPrice
            }
        }
        if (lifeTimeProduct != null) {
            val product = lifeTimeProduct!!.oneTimePurchaseOfferDetails
            if (product != null) {
                binding.txtPriceLifeTime.text = product.formattedPrice
            }
        }

        Log.d("ádasdsdd", subMonthProduct.toString() + "" + subWeekProduct + lifeTimeProduct)
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


    private fun onClick() {
        // logic click hien thi subdialog

//        binding.btnClose.setOnClickListener {
//            nextActivity()
//        }
//        binding.btnContinue.setOnClickListener {
//            if (proApplication.isSubVip) {
//                nextActivity()
//            } else {
//                showPurchaseDialog()
//            }
//        }
//        binding.btnSubMonth.setOnClickListener {
//            currentProduct = subMonthProduct
//            showPurchaseDialog()
//        }
//        binding.btnSubWeek.setOnClickListener {
//            currentProduct = subWeekProduct
//            showPurchaseDialog()
//        }
//        binding.btnLifeTime.setOnClickListener {
//            currentProduct = lifeTimeProduct
//            showPurchaseDialog()
//        }
//
//        binding.btnReStore.setOnClickListener {
//            restore = true
//            billingClient!!.startConnection(billingClientStateListener)
//        }
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
            billingClient!!.launchBillingFlow(this@SubVipActivity, billingFlowParams)
        } else {
            restore = false
            billingClient!!.startConnection(billingClientStateListener)
        }
    }



    override fun initView() {
        setupBilling()

        binding.apply {
            btnUseFree.setOnClickListener {
                showPurchaseDialog()
            }


        }
    }

}