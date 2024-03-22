package com.video.mini.tools.zip.compress.convert.simple.tiny.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.main.MainActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg.MediaAction
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfos
import com.video.mini.tools.zip.compress.convert.simple.tiny.ui.result.ResultActivity
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passActionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passMediaInput
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.IntentUtils.passMediaOutput
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.TimeZone

object Utils {
    fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName // Lấy tên phiên bản
        } catch (e: PackageManager.NameNotFoundException) {
            "N/A" // Trường hợp không tìm thấy phiên bản
        }
    }
    val formatVideos = hashMapOf(
        "MP4" to listOf(
            "h264baseline",
            "h264high",
            "h264main",
            "mpeg4",
            "mpeg2video",
            "mpeg1video",
            "libkvazaar",

            ),
        "GP3" to listOf("h264baseline", "h264high", "h264main", "mpeg4"),
        "ASF" to listOf("h264baseline", "h264high", "h264main", "mpeg4"),
        "AVI" to listOf(
            "h264baseline",
            "h264high",
            "h264main",
            "mpeg4",
            "mpeg2video",
            "mpeg1video",
            "vp9",
            "vp8",
            "libxvid",

            ),
        "F4V" to listOf("h264baseline", "h264high", "h264main"),
        "M4V" to listOf("flv1", "h264baseline", "h264high", "h264main"),
        "MKV" to listOf(
            "h264baseline",
            "h264high",
            "h264main",
            "mpeg4",
            "mpeg2video",
            "mpeg1video",
            "vp9",
            "vp8",

            ),
        "MOV" to listOf(
            "h264baseline",
            "h264high",
            "h264main",
            "mpeg4",
            "mpeg2video",
            "mpeg1video",
            "flv1",

            ),
        "MPEG" to listOf("mpeg2video", "mpeg1video"),
        "MPG" to listOf("mpeg2video", "mpeg1video"),
        "M2TS" to listOf("h264baseline", "h264high", "h264main", "wmv2"),
        "MTS" to listOf(
            "h264baseline",
            "h264high",
            "h264main",
            "mpeg4",
            "mpeg2video",
            "mpeg1video",

            ),
        "TS" to listOf(
            "h264baseline",
            "h264high",
            "h264main",
            "mpeg4",
            "mpeg2video",
            "mpeg1video",

            ),
        "VOB" to listOf("mpeg2video", "mpeg1video"),
        "OGV" to listOf("libtheora"),
        "WEBM" to listOf("vp9", "vp8"),
        "WMV" to listOf("wmv2", "wmv1", "h264baseline", "h264high", "h264main")
    )

    fun convertKBtoBit(kb: Long) = kb * 8 * 1024
    fun convertMBtoBit(mb: Long) = mb * 8 * 1024 * 1024

    fun Context.startActivityResult(mediaInput: List<MediaInfo>, mediaOutPut: List<MediaInfo>) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.passMediaOutput(mediaOutPut)
        intent.passMediaInput(mediaInput)
        intent.passActionMedia(MediaAction.ExtractAudio)
        startActivity(intent)
    }

    fun Context.loadImage(path: String, image: ImageView) {
        Glide.with(this)
            .load(path)
            .error(getDrawable(R.drawable.logo)!!.setTint(Color.GRAY))
            .into(image);
    }

    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
        .apply { timeZone = TimeZone.getTimeZone("GMT") }
    private val simpleDateFormat2 = SimpleDateFormat("dd/MM/yyyy").apply { timeZone = TimeZone.getTimeZone("GMT+7") }
    private val decimalFormat = DecimalFormat("#").apply {
        roundingMode = RoundingMode.CEILING
    }

    fun Float.formatToInt(): Int = decimalFormat.format(this).toInt()
    fun formatTime(time: Long): String = simpleDateFormat.format(time)
    fun formatDate(miliS: Long): String = simpleDateFormat2.format(miliS)
    fun convertTimeToMiliSeconds(timeString: String): Long {
        val date = simpleDateFormat.parse(timeString) ?: return 0
        return date.time
    }

    fun MediaInfos.getResolutionMax() =
        this.maxByOrNull { it.resolution!!.width * it.resolution!!.height }

    fun Context.shareMultiple(isVideo: Boolean, videoFilePaths: List<Uri>) {
        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        shareIntent.type = if (isVideo) "video/*" else "image/*"
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(videoFilePaths))
        startActivity(Intent.createChooser(shareIntent, "Share:"))
    }

    fun Context.share(isVideo: Boolean, videoFilePaths: Uri) {
        val videoUris = listOf(videoFilePaths)

        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        shareIntent.type = if (isVideo) "video/*" else "image/*"
        shareIntent.putParcelableArrayListExtra(
            Intent.EXTRA_STREAM,
            ArrayList(videoUris)
        )
        startActivity(Intent.createChooser(shareIntent, "Share:"))
    }

    @SuppressLint("NewApi")
    fun Context.isDarkMode() = resources.configuration.isNightModeActive
    fun getUri(context: Context, path: String): Uri =
        FileProvider.getUriForFile(context, "com.video.mini.tools.zip.compress.convert.simple.tiny", File(path))

    fun Context.startToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }
}