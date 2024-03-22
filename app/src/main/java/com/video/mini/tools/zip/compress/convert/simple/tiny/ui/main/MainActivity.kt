package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.main

import android.Manifest.permission.READ_MEDIA_IMAGES
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.GravityCompat
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.AppOpenAdManager

import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ActivityMainBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.MediaAction
import com.video.mini.tools.zip.compress.convert.simple.tiny.google.AdsHelper
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.OnTabSelectedListener
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.extract_audio.ShowAudioActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.setting.SettingActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.video_pickers.MainPickerActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.HandleMediaVideo
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passActionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.READ_EXTERNAL_STORAGE
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.READ_MEDIA_VIDEO
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.RequestPermission
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.Utils
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.WRITE_EXTERNAL_STORAGE
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.checkPermission


class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val requestPermission = RequestPermission(this)

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var mediaAction: MediaAction

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun initView() {
        binding.apply {
            setupToolbar()
            setupAds()
            setupNavigation()
            setUpViewPager()
            mTab.setOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(position: Int) {
                    viewPager.setCurrentItem(position, true)
                }
            })
            mTab.attach(viewPager, "Home", "Files Saved")
            mTab.setTabSelected(0)
        }
    }

    private fun setUpViewPager() {
        val mainViewPager = MainViewPager(this)
        binding.viewPager.adapter = mainViewPager
    }

    private fun setupToolbar() {
        val drawable = AppCompatResources.getDrawable(this@MainActivity, R.drawable.ic_menu)!!
        val originalBitmap = (drawable as BitmapDrawable).bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 24, 24, true)
        val scaledDrawable = BitmapDrawable(resources, scaledBitmap)

        setToolbar(binding.toolbar, "", scaledDrawable, true)
        binding.toolbar.navigationIcon = scaledDrawable

    }

    private fun setupAds() {
        binding.apply {
            showBannerAds(bannerAds)
            if (SHOW_OPEN_FIRST_MAIN) showInterstitial(true) {}
        }
    }

    private fun setupNavigation() {
        binding.navigationView.inflateHeaderView(R.layout.header).apply {
            this.findViewById<TextView>(R.id.version).text =
                "Version:" + Utils.getAppVersion(this.context)
        }
        binding.navigationView.apply {
            setCheckedItem(R.id.home)
            setNavigationItemSelectedListener { menuItem ->
                handleNavigationItemSelected(menuItem)
                true
            }
        }
    }

    private fun handleNavigationItemSelected(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.audio -> {
                startActivity(Intent(this@MainActivity, ShowAudioActivity::class.java))
            }

            R.id.compressVideos -> {
                startPickerVideo(MediaAction.CompressVideo)
            }

            R.id.settings -> startActivity(
                Intent(
                    this@MainActivity,
                    SettingActivity::class.java
                )
            )

            R.id.info -> {
                val email = "khangnguyen.teras@gmail.com"

                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$email")
                }

                startActivity(intent)
            }

            R.id.share -> {
                val textToShare = "Content you want to share"

                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, textToShare)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, "Share:"))
            }

            R.id.star -> {
                val url =
                    "http://play.google.com/store/apps/details?id=com.video.mini.tools.zip.compress.convert.simple.tiny"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }
        if (menuItem.isChecked) menuItem.setChecked(false);
        else menuItem.setChecked(true);
        binding.drawerLayout.closeDrawers()
    }

    val videoHandler = HandleMediaVideo(this)

    override fun onResume() {
        super.onResume()
        binding.mTab.visibility =
            if (videoHandler.getVideoSave().isEmpty()) View.GONE else View.VISIBLE
        binding.viewPager.isUserInputEnabled = videoHandler.getVideoSave().isNotEmpty()

        if (videoHandler.getVideoSave().isEmpty()) {
            binding.viewPager.currentItem = 0
        }

    }

    override fun initObserver() {
        requestPermission.observe(this) {
            if (it) {
                val intent = Intent(this, MainPickerActivity::class.java)
                intent.passActionMedia(mediaAction)
                startActivity(intent)
            } else {
                showMessage("Permission denied")
            }
        }
    }

    private var permission = arrayOf(
        READ_EXTERNAL_STORAGE,
        WRITE_EXTERNAL_STORAGE
    )

    private fun requestPermissionMedia(): Boolean {
        val isPermissionGranted: Boolean =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permission = arrayOf(READ_MEDIA_VIDEO, READ_MEDIA_IMAGES)
                checkPermission(READ_MEDIA_VIDEO) && checkPermission(READ_MEDIA_IMAGES)
            } else {
                checkPermission(permission)
            }
        return isPermissionGranted
    }

    @SuppressLint("ResourceType")
    fun startPickerVideo(mediaAction: MediaAction) {
        this.mediaAction = mediaAction
        val checkPermission = requestPermissionMedia()
        if (checkPermission) {
            val intent = Intent(this, MainPickerActivity::class.java)
            intent.passActionMedia(mediaAction)
            startActivity(intent)
            overridePendingTransition(R.anim.enter_transition, R.anim.exit_transition)
        } else {
            requestPermission.launch(permission)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }
}