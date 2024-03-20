package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.join_video

import android.content.Intent
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.ItemTouchHelper
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ActivityJoinVideoBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.MediaAction
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfos
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.OptionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.process.ProcessActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.select_compress.ItemSpacingDecoration
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.getActionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.getOptionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passActionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passOptionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.Utils
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.Utils.getResolutionMax
import java.util.Collections

class JoinVideoActivity : BaseActivity<ActivityJoinVideoBinding>() {
    override fun getViewBinding() = ActivityJoinVideoBinding.inflate(layoutInflater)
    private lateinit var listInfo: ArrayList<MediaInfo>

    override fun initData() {
        listInfo = intent.getOptionMedia()!!.dataOriginal
    }

    override fun initView() {
        binding.apply {
            setToolbar(
                toolbar,
                "Join Video",
                getDrawable(R.drawable.baseline_arrow_back_24)!!
            )
            showBannerAds(bannerAds)
            setUpAdapter()


            val mediaInfos = intent.getOptionMedia()!!.dataOriginal
            val maxResolution = mediaInfos.getResolutionMax()!!.resolution!!


            next.setOnClickListener {
                if (videoJoinAdapter.itemCount > 0) {
                    OptionSettingsJoinBottomSheetFragment.getInstance(
                        maxResolution,
                        mediaInfos.sumOf { Utils.convertTimeToMiliSeconds(it.time) }).show(
                        supportFragmentManager,
                        OptionSettingsJoinBottomSheetFragment::class.simpleName
                    )
                }
            }
        }
    }

    private lateinit var videoJoinAdapter: VideoJoinAdapter
    private fun setUpAdapter() {
        binding.apply {
            videoJoinAdapter = VideoJoinAdapter {
                videoJoinAdapter.notifyItemRemoved(listInfo.indexOf(it))
                listInfo.remove(it)
            }
            videoJoinAdapter.submitData = listInfo
            listInfo = videoJoinAdapter.submitData
            val dragDropCallback = DragDropCallback(videoJoinAdapter, object : ItemTouchListenner {
                override fun onMove(oldPosition: Int, newPosition: Int) {
                    Collections.swap(listInfo, oldPosition, newPosition)
                }

                override fun swipe(position: Int, direction: Int) {

                }
            })
            val itemTouchHelper = ItemTouchHelper(dragDropCallback)
            itemTouchHelper.attachToRecyclerView(rcy)
            rcy.addItemDecoration(ItemSpacingDecoration(30))
            rcy.adapter = videoJoinAdapter
        }
    }

    fun onDone(title: String, format: String, codec: String?) {
        val intent = Intent(this, ProcessActivity::class.java)
        intent.passOptionMedia(createOptionMedia(title, format, codec))
        intent.passActionMedia(this@JoinVideoActivity.intent.getActionMedia()!!)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun createOptionMedia(title: String, format: String, codec: String?): OptionMedia {
        Log.d("Nguyễn duy khang", videoJoinAdapter.submitData.toString())
        return OptionMedia(
            dataOriginal = MediaInfos().apply { addAll(videoJoinAdapter.submitData) },
            mediaAction = MediaAction.JoinVideo,
            mimetype = format,
            codec = codec,
            nameOutput = title
        )
    }
}