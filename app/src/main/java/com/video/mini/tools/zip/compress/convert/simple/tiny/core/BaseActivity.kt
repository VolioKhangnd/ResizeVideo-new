package com.video.mini.tools.zip.compress.convert.simple.tiny.core

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryPurchasesAsync
import com.google.firebase.analytics.FirebaseAnalytics

import com.video.mini.tools.zip.compress.convert.simple.tiny.google.AdsHelper
import kotlinx.coroutines.launch

abstract class BaseActivity<VB : ViewBinding> : AdsHelper() {

    private var _binding: VB? = null
    val binding get() = _binding!!
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    var billingClient: BillingClient? = null


    fun setupBilling(onNextActivity: () -> Unit,listener: PurchasesUpdatedListener){
        billingClient = BillingClient.newBuilder(this)
            .setListener(listener)
            .enablePendingPurchases()
            .build()
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    lifecycleScope.launch {
                        getActivePurchase()
                        onNextActivity()
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                onNextActivity()
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
        AdsHelper.IS_SUB_VIP = proApplication.isSubVip
        return proApplication.isSubVip
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
        runOnUiThread {
            Toast.makeText(
                this,
                message,
                if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
            ).show()
        }

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