package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.setting

import android.content.Intent
import android.view.MenuItem
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.main.MainActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ActivitySettingBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.MyMenu.showPopupMenuTheme
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.Utils.startToMainActivity

class SettingActivity : BaseActivity<ActivitySettingBinding>() {
    override fun getViewBinding(): ActivitySettingBinding {
        return ActivitySettingBinding.inflate(layoutInflater)
    }

    override fun initView() {

        binding.apply {
            setToolbar(
                binding.toolbar,
                "Settings",
                getDrawable(R.drawable.baseline_arrow_back_24)!!
            )
          mLayoutTheme.setOnClickListener { it ->
              it.showPopupMenuTheme {
                  txtTheme.text = it
              }
          }
            showNativeAds(nativeAds){ }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startToMainActivity()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}