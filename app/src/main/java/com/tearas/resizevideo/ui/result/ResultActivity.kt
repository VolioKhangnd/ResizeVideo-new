package com.tearas.resizevideo.ui.result

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import com.tearas.resizevideo.BuildConfig
import com.tearas.resizevideo.ui.main.MainActivity
import com.tearas.resizevideo.R
import com.tearas.resizevideo.core.BaseActivity
import com.tearas.resizevideo.core.DialogClickListener
import com.tearas.resizevideo.databinding.ActivityResultBinding
import com.tearas.resizevideo.ffmpeg.MediaAction
import com.tearas.resizevideo.model.MediaInfo
import com.tearas.resizevideo.ui.compare.CompareActivity
import com.tearas.resizevideo.ui.select_compress.ItemSpacingDecoration
import com.tearas.resizevideo.ui.sup_vip.SubVipActivity
import com.tearas.resizevideo.utils.DialogUtils
import com.tearas.resizevideo.utils.HandleMediaVideo
import com.tearas.resizevideo.utils.HandleSaveResult
import com.tearas.resizevideo.utils.IntentUtils.getActionMedia
import com.tearas.resizevideo.utils.IntentUtils.getMediaInput
import com.tearas.resizevideo.utils.IntentUtils.getMediaOutput
import com.tearas.resizevideo.utils.Utils
import com.tearas.resizevideo.utils.Utils.shareMultiple
import com.tearas.resizevideo.utils.Utils.startToMainActivity
import java.io.File
import java.io.FileInputStream


class ResultActivity : BaseActivity<ActivityResultBinding>(), OnItemMenuMoreSelectedListen {
    override fun getViewBinding(): ActivityResultBinding {
        return ActivityResultBinding.inflate(layoutInflater)
    }

    private lateinit var dataRs: List<MediaInfo>
    private lateinit var handleSaveResult: HandleSaveResult
    private lateinit var handleMediaVideo: HandleMediaVideo
    override fun initData() {
        dataRs = intent.getMediaOutput()
        handleSaveResult = HandleSaveResult(this@ResultActivity)
        handleMediaVideo = HandleMediaVideo(this@ResultActivity)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {
        setToolbar(
            binding.toolbar,
            getString(R.string.result),
            getDrawable(R.drawable.baseline_arrow_back_24)!!
        )
        binding.apply {
            setUpAdapterRs()
            showNativeAds(binding.container) {}
        }
    }

    private val manageAllFilesAccessPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleReplace(item)
        }
    private lateinit var item: Pair<MediaInfo, MediaInfo>

    private fun showDialogReplace() {
        DialogUtils.showDialogReplace(this, object : DialogClickListener {
            override fun onPositive() {
                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        handleReplace(item)
                    } else {
                        val intent =
                            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                        manageAllFilesAccessPermissionLauncher.launch(intent)
                    }
                } else {
                    handleReplace(item)
                }
            }

            override fun onNegative() {

            }
        })
    }

    private fun handleReplace(item: Pair<MediaInfo, MediaInfo>) {
        handleMediaVideo.replaceFiles(
            item.first.path,
            item.second.path
        )
    }

    private fun share() {
        shareMultiple(
            dataRs[0].isVideo,
            dataRs.map {
                Utils.getUri(this@ResultActivity, it.path)
            }.toList()
        )
    }

    private fun handleSaveMedia() {
        adapter.submitData.map { it.second }.forEachIndexed { index, mediaInfo ->
            val file = handleMediaVideo.saveFileToExternalStorage(
                this@ResultActivity,
                mediaInfo.isVideo,
                FileInputStream(mediaInfo.path),
                mediaInfo.name
            )
            file?.let { savedFile ->
                handleSaveResult.getPathInput(mediaInfo.path).let { inputPath ->
                    handleSaveResult.save(inputPath, savedFile.path)
                    File(mediaInfo.path).delete()
                }
            }
        }
    }

    private fun startCompareActivity(item: Pair<MediaInfo, MediaInfo>) {
        val intent = Intent(this, CompareActivity::class.java)
        intent.putExtra("Media", item)
        startActivity(intent)
    }

    private lateinit var adapter: ResultAdapter
    private fun setUpAdapterRs() {
        val mediaOutput = intent.getMediaOutput()
        val mediaInput = intent.getMediaInput()
        adapter = ResultAdapter(
            this,
            if (intent.getActionMedia()!! != MediaAction.JoinVideo) 0L else mediaInput.sumOf { it.size },
            this
        )

        adapter.submitData = ArrayList(mediaOutput.mapIndexed { index, mediaInfo ->
            Pair(mediaInput[index], mediaInfo)
        }.toList())

        if (intent.getActionMedia()!! != MediaAction.JoinVideo) {
            adapter.submitData.forEachIndexed { index, pair ->
                handleSaveResult.save(
                    pair.first.path,
                    pair.second.path,
                )
            }
        }

        val itemSpacingDecoration = ItemSpacingDecoration(30)
        binding.rcyRs.addItemDecoration(itemSpacingDecoration)
        binding.rcyRs.adapter = adapter
    }

    private fun showDialogRename(item: Pair<MediaInfo, MediaInfo>) {
        RenameDialogFragment {
            handleRename(item, it)
        }.show(supportFragmentManager, RenameDialogFragment::class.simpleName)
    }

    private fun handleRename(item: Pair<MediaInfo, MediaInfo>, newName: String) {
        val position = adapter.submitData.indexOf(item)
        val itemUpdate = adapter.submitData[position]
        adapter.notifyItemChanged(
            position,
            itemUpdate.apply {
                second.name = newName + "." + second.mime
            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startToMainActivity()
        finish()
    }

    override fun getMenu(): Int {
        return R.menu.menu_bottom_rs
    }

    override fun onRename(item: Pair<MediaInfo, MediaInfo>) {
        showDialogRename(item)
    }

    override fun onCompare(item: Pair<MediaInfo, MediaInfo>) {
        startCompareActivity(item)
    }

    override fun onReplace(item: Pair<MediaInfo, MediaInfo>) {
        if (proApplication.isSubVip) {
            this@ResultActivity.item = item
            showDialogReplace()
        } else {
            startActivity(Intent(this, SubVipActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return true
            }

            R.id.share -> {
                share()
                return true
            }

            R.id.save -> {
                handleSaveMedia()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}