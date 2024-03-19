package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.cut_trim

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object FrameRateUtils {

    fun getFrameByTime(path: String, timeUs: Long): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        return retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_NEXT_SYNC)
    }

    suspend fun getFrames(path: String): ArrayList<Bitmap> = withContext(Dispatchers.IO) {
        val frames = ArrayList<Bitmap>()
        val retriever = MediaMetadataRetriever()

        return@withContext suspendCoroutine { continuation ->
            try {
                retriever.setDataSource(path)

                val duration =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
                        ?: 0

                val frameInterval = duration * 1000 / 10.0

                for (i in 0 until 10) {
                    // Get the frame at a specific time
                    val timeUs = (i * frameInterval).toLong()
                    val bitmap =
                        retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_NEXT_SYNC)

                    bitmap?.let {
                        frames.add(it)
                    }
                }
                Log.d("Error getting frames", frames.toString())
                continuation.resume(frames)
            } catch (e: Exception) {
                Log.d("Error getting frames", e.message.toString())
                e.printStackTrace()
                continuation.resumeWithException(e)
            } finally {
                retriever.release()
            }
        }
    }

}