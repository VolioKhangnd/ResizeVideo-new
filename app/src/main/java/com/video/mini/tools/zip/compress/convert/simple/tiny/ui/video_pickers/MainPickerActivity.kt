package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.video_pickers

import android.annotation.SuppressLint
import android.content.Intent
import android.provider.MediaStore
import android.text.format.Formatter
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ActivityMainPickerBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.MediaAction
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.OptionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.cut_trim.CutTrimActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.extract_audio.ExtractAudioActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.fast_forward.FastForwardActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.join_video.JoinVideoActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.process.ProcessActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.reverse.ReversesActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.select_compress.SelectCompressActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.AnimUtils
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.getActionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passActionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passOptionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.MyMenu.handleClickSortAction


class MainPickerActivity : BaseActivity<ActivityMainPickerBinding>() {
    private val viewModel: PickerViewModel by lazy {
        ViewModelProvider(this)[PickerViewModel::class.java]
    }
    private var orderBy = MediaStore.Video.Media.DATE_ADDED + " DESC"

    override fun getViewBinding(): ActivityMainPickerBinding {
        return ActivityMainPickerBinding.inflate(layoutInflater)
    }

    private lateinit var hostNav: NavHostFragment
    override fun initData() {
        hostNav =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        viewModel.actionSort.postValue(orderBy)
    }


    override fun initView() {

        setUpNavigation()
        binding.apply {
            showBannerAds(bannerAds)

            close.setOnClickListener {
                AnimUtils.pullUpView(binding.layoutSelected) {
                    layoutSelected.visibility = View.GONE
                    Log.d("àkjfkldjfkljslf","end")
                }
                viewModel.closeLiveData.postValue(true)
            }

            layoutSelected.setOnClickListener {
                if (viewModel.size() > 0) {
                    val destination = when (intent.getActionMedia()!!) {
                        is MediaAction.CompressVideo -> SelectCompressActivity::class.java
                        is MediaAction.CutTrim -> CutTrimActivity::class.java
                        is MediaAction.ExtractAudio -> ExtractAudioActivity::class.java
                        is MediaAction.FastForward, MediaAction.SlowVideo -> FastForwardActivity::class.java
                        is MediaAction.JoinVideo -> JoinVideoActivity::class.java
                        is MediaAction.ReveresVideo -> ReversesActivity::class.java
                        else -> ProcessActivity::class.java
                    }
                    val intent = Intent(this@MainPickerActivity, destination)
                    intent.passOptionMedia(createOptionMedia())
                    intent.passActionMedia(this@MainPickerActivity.intent.getActionMedia()!!)
                    startActivity(intent)
                }
            }

        }
    }

    private fun createOptionMedia(): OptionMedia {
        return OptionMedia(
            dataOriginal = viewModel.videos,
            mediaAction = intent.getActionMedia()!!,
        )
    }

    override fun getMenu() = R.menu.menu_sort
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.handleClickSortAction {
            viewModel.actionSort.postValue(it)
        }
        return true
    }

    @SuppressLint("SetTextI18n")
    override fun initObserver() {
        viewModel.videosLiveData.observe(this) { info ->
            val sizeListVideos = viewModel.size()
            if (sizeListVideos == 0) {
                AnimUtils.pullUpView(binding.layoutSelected) {
                    binding.layoutSelected.visibility = View.GONE
                    Log.d("àkjfkldjfkljslf","end")
                }
            } else {
                binding.layoutSelected.visibility = View.VISIBLE
                AnimUtils.dropDownView(binding.layoutSelected)
            }

            binding.count.text = "$sizeListVideos Selected (${
                Formatter.formatFileSize(
                    this,
                    viewModel.sumSizeVideos
                )
            })"
        }
    }

    private fun setUpNavigation() {
        val navController: NavController = hostNav.navController
        AppBarConfiguration(navController.graph)
    }

    override fun onNavigateUp(): Boolean {
        return hostNav.navController.navigateUp() || super.onNavigateUp()
    }
}