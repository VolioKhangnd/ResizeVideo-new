package com.tearas.resizevideo.ui.join_video

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import com.tearas.resizevideo.R
import com.tearas.resizevideo.core.BaseActivity
import com.tearas.resizevideo.databinding.ActivityJoinVideoBinding
import com.tearas.resizevideo.ffmpeg.MediaAction
import com.tearas.resizevideo.model.MediaInfo
import com.tearas.resizevideo.model.MediaInfos
import com.tearas.resizevideo.model.OptionMedia
import com.tearas.resizevideo.ui.process.ProcessActivity
import com.tearas.resizevideo.ui.select_compress.ItemSpacingDecoration
import com.tearas.resizevideo.ui.select_compress.SelectCompressActivity
import com.tearas.resizevideo.utils.IntentUtils.getActionMedia
import com.tearas.resizevideo.utils.IntentUtils.getMediaInput
import com.tearas.resizevideo.utils.IntentUtils.getOptionMedia
import com.tearas.resizevideo.utils.IntentUtils.passActionMedia
import com.tearas.resizevideo.utils.IntentUtils.passOptionMedia
import com.tearas.resizevideo.utils.Utils
import com.tearas.resizevideo.utils.Utils.getResolutionMax
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

            setUpAdapter()


            val mediaInfos = intent.getOptionMedia()!!.dataOriginal
            val maxResolution = mediaInfos.getResolutionMax()!!.resolution!!
            val optionSettingsJoinBottomSheetFragment =
                OptionSettingsJoinBottomSheetFragment.getInstance(
                    maxResolution,
                    mediaInfos.sumOf { Utils.convertTimeToMiliSeconds(it.time) })

            next.setOnClickListener {
                if (videoJoinAdapter.itemCount > 0) {
                    optionSettingsJoinBottomSheetFragment.show(
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
            val dragDropCallback =
                DragDropCallback(videoJoinAdapter, object : ItemTouchListenner {
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
        return OptionMedia(
            dataOriginal = MediaInfos().apply { addAll(videoJoinAdapter.submitData) },
            mediaAction = MediaAction.JoinVideo,
            mimetype = format,
            codec = codec,
            nameOutput = title
        )
    }
}