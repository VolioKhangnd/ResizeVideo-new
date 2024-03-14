package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.fast_forward

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.text.format.Formatter
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.core.BaseActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.databinding.ActivityFastForwardOptionsBinding
import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.MediaAction
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.OptionCompressType
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.OptionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.process.ProcessActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.getOptionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passActionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passOptionMedia

class FastForwardOptionsActivity : BaseActivity<ActivityFastForwardOptionsBinding>() {
    override fun getViewBinding(): ActivityFastForwardOptionsBinding {
        return ActivityFastForwardOptionsBinding.inflate(layoutInflater)
    }

    private lateinit var media: OptionMedia
    private lateinit var videoInfo: MediaInfo
    private val listSize = arrayOf(OptionCompressType.Origin, OptionCompressType.Small, OptionCompressType.Medium, OptionCompressType.Large)
    private var indexSize = 2
    override fun initData() {
        media = intent.getOptionMedia()!!
        videoInfo = media.dataOriginal[0]
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {
        binding.apply {
            setToolbar(
                binding.toolbar, "Edit Video", getDrawable(R.drawable.baseline_arrow_back_24)!!
            )
            setDataVideoInfo()

            radioGroup.setOnCheckedChangeListener { radioBtn, i ->
                indexSize = radioGroup.indexOfChild(findViewById(radioGroup.checkedRadioButtonId));
            }

            continues.setOnClickListener {
                val intent = Intent(this@FastForwardOptionsActivity, ProcessActivity::class.java)
                intent.passOptionMedia(createOptionMedia())
                intent.passActionMedia(MediaAction.FastForward)
                startActivity(intent)
            }
        }
    }

    private fun setDataVideoInfo() {
        binding.apply {
            size.text = Formatter.formatFileSize(this@FastForwardOptionsActivity, videoInfo.size)
            name.text = videoInfo.name
            time.text = videoInfo.time
            resolution.text = videoInfo.resolution.toString()
            Glide.with(this@FastForwardOptionsActivity)
                .load("file:///" + videoInfo.path)
                .error(getDrawable(R.drawable.info)!!.setTint(Color.GRAY))
                .into(binding.thumbnail);
        }
    }

    private fun createOptionMedia(): OptionMedia {
        return media.copy(
            optionCompressType = listSize[indexSize],
            mimetype = media.mimetype,
            withAudio = binding.cbAudio.isChecked
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish();return true
        return super.onOptionsItemSelected(item)
    }
}