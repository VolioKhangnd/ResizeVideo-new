package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.error

import android.annotation.SuppressLint
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ActivityShowErrorBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.Utils.startToMainActivity

class ShowErrorActivity : BaseActivity<ActivityShowErrorBinding>() {
    override fun getViewBinding(): ActivityShowErrorBinding {
        return ActivityShowErrorBinding.inflate(layoutInflater)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {
        setToolbar(
            binding.toolbar,
            getString(R.string.error),
            getDrawable(R.drawable.baseline_arrow_back_24)!!
        )
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
               startToMainActivity()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            startToMainActivity()
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}