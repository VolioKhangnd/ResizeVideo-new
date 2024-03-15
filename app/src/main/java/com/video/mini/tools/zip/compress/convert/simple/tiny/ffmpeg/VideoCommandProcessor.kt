package com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg

import android.content.Context
import com.arthenica.ffmpegkit.FFprobeKit
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.MediaInfo
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.OptionCompressType
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.OptionMedia
import com.video.mini.tools.zip.compress.convert.simple.tiny.model.Resolution
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.ResolutionUtils.calculateResolution
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.ResolutionUtils.calculateResolutionByRadio
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.Utils
import com.video.mini.tools.zip.compress.convert.simple.tiny.utils.Utils.getResolutionMax

class VideoCommandProcessor(
    private val context: Context,
    private val pathOutputFolderVideo: String,
    private val pathOutputFolderAudio: String
) {
    private fun compressByFileSize(inputPath: String, targetBit: Long): String {
        val logFilePath = "${context.cacheDir}/ffmpeg2pass"
        return CommandConfiguration.getInstance()
            .appendCommand("-y")
            .appendCommand("-i")
            .appendCommand(inputPath)
            .appendCommand("-c:v libx264")
            .appendCommand("-r 60")
            .appendCommand("-passlogfile")
            .appendCommand(logFilePath)
            .appendCommand("-preset medium")
            .appendCommand("-b:v $targetBit")
            .appendCommand("-b:a 127k")
            .appendCommand("-pass 1")
            .appendCommand("-c:a aac")
            .appendCommand("-b:a 128k")
            .appendCommand("-f mp4")
            .appendCommand("/dev/null")
            .appendCommand("-y -i")
            .appendCommand(inputPath)
            .appendCommand("-c:v libx264")
            .appendCommand("-r 60")
            .appendCommand("-passlogfile")
            .appendCommand(logFilePath)
            .appendCommand("-preset medium")
            .appendCommand("-b:v  $targetBit")
            .appendCommand("-pass 2")
            .appendCommand("-c:a aac")
            .appendCommand("-b:a 127k")
            .appendCommand(getPathOutPut())
            .getCommand()
    }

    private fun compressVideoCommand(
        resolution: Resolution,
        inputPath: String,
        option: OptionCompressType,
        fileSizeCompress: Long?,
        frameRate: Int,
        bitrate: Long,
        mime: String
    ): String {
        return when (option) {
            is OptionCompressType.CustomFileSize -> {
                val originalDuration =
                    FFprobeKit.getMediaInformation(inputPath).mediaInformation.duration.toFloat()
                        .toLong()
                val targetBitrate = (fileSizeCompress!! / originalDuration) - 127000
                val x = compressByFileSize(inputPath, targetBitrate)
                x
            }

            is OptionCompressType.Origin -> {
                CommandConfiguration.getInstance()
                    .appendCommand("-i $inputPath")
                    .appendCommand("-preset medium")
                    .appendCommand(getPathOutPut(mime))
                    .getCommand()
            }

            is OptionCompressType.AdvanceTypeOption -> {
                CommandConfiguration.getInstance()
                    .appendCommand("-i $inputPath")
                    .appendCommand("-s ${resolution.width}x${resolution.height}")
                    .appendCommand("-r $frameRate")
                    .appendCommand("-b:v ${bitrate}k")
                    .appendCommand("-preset medium")
                    .appendCommand(getPathOutPut(mime))
                    .getCommand()
            }

            else -> {
                CommandConfiguration.getInstance()
                    .appendCommand("-i $inputPath")
                    .appendCommand("-s ${resolution.width}x${resolution.height}")
                    .appendCommand(getPathOutPut(mime))
                    .getCommand()
            }
        }
    }


    private fun getPathOutPut(mime: String = "mp4", isVideo: Boolean = true) = if (isVideo) {
        "$pathOutputFolderVideo/VDI_TERAS_${System.currentTimeMillis()}${(Math.random() * 1000).toInt()}.$mime"
    } else {
        "$pathOutputFolderAudio/AUDIO_TERAS_${System.currentTimeMillis()}${(Math.random() * 1000).toInt()}.$mime"
    }


    private fun trimVideoCommand(
        resolution: Resolution,
        inputPath: String,
        startTime: Long,
        endTime: Long,
        mime: String
    ): String {
        return CommandConfiguration.getInstance()
            .appendCommand("-i $inputPath")
            .appendCommand("-s ${resolution.width}x${resolution.height}")
            .appendCommand("-ss ${Utils.formatTime(startTime * 1000)}")
            .appendCommand("-t ${Utils.formatTime((endTime - startTime) * 1000)}")
            .appendCommand("-c copy")
            .appendCommand(getPathOutPut(mime))
            .getCommand()

    }

    private fun cutVideoCommand(
        resolution: Resolution,
        inputPath: String,
        startTime: Long,
        endTime: Long,
        mime: String
    ): String {
        return CommandConfiguration.getInstance()
            .appendCommand("-i $inputPath")
            .appendCommand("-s ${resolution.width}x${resolution.height}")
            .appendCommand("-vf  \"select='not(between(t,$startTime,$endTime))',  setpts=N/FRAME_RATE/TB\"")
            .appendCommand(" -af \"aselect='not(between(t,$startTime,$endTime))', asetpts=N/SR/TB\"")
            .appendCommand(getPathOutPut(mime))
            .getCommand();
    }

    private fun extractAudioCommand(mime: String, inputPath: String): String {
        return CommandConfiguration.getInstance()
            .appendCommand("-i $inputPath")
            .appendCommand("-vn")
            .appendCommand(getPathOutPut(mime, false))
            .getCommand()
    }

    //mp3 flac ogg
    private fun fastOrSlowVideoCommand(
        resolution: Resolution,
        withAudio: Boolean,
        isFastVideo: Boolean,
        speed: Float,
        inputPath: String,
        mime: String
    ): String {
        val commandProcessor = CommandConfiguration.getInstance()
        val speedVideo = if (!isFastVideo) 1.0 + (1 - speed) else 1 / speed
        val speedAudio = if (isFastVideo) speed else 1.0 / (1.0 + (1 - speed))

        if (resolution.height % 2 != 0) resolution.height += 1
        commandProcessor
            .appendCommand("-i $inputPath")
            .appendCommand("-s ${resolution.width}x${resolution.height}")
            .appendCommand("-preset medium")
            .appendCommand("-filter_complex")
        if (withAudio) {
            commandProcessor.appendCommand("[0:v]setpts=$speedVideo*PTS[v];[0:a]atempo=$speedAudio[a]")
                .appendCommand("-map [v] -map [a]")
        } else {
            commandProcessor.appendCommand("[0:v]setpts=$speedVideo*PTS[v]")
                .appendCommand("-map [v]")
        }
            .appendCommand(getPathOutPut(mime))
        return commandProcessor.getCommand()

    }


    private fun joinVideo(mediaOption: OptionMedia): String {
        val commandProcessor = CommandConfiguration.getInstance()
        mediaOption.dataOriginal.forEach {
            commandProcessor.appendCommand("-i")
            commandProcessor.appendCommand(it.path)
        }
        if (mediaOption.codec != null) commandProcessor.appendCommand(
            "-vcodec  ${
                if (mediaOption.codec.startsWith(
                        "h264"
                    )
                ) "h264" else mediaOption.codec
            }"
        )
        var videoStream = ""
        val resolutionMax = mediaOption.dataOriginal.getResolutionMax()
        commandProcessor.appendCommandNotSpace("-filter_complex \"")
        val resolution =
            "${resolutionMax!!.resolution!!.width}:${resolutionMax.resolution!!.height}"

        mediaOption.dataOriginal.forEachIndexed { index, _ ->
            commandProcessor.appendCommandNotSpace("[$index:v]setpts=PTS-STARTPTS,scale=$resolution:force_original_aspect_ratio=decrease,fps=60,pad=$resolution:(ow-iw)/2:(oh-ih)/2,setsar=1[v$index];")
            videoStream += "[v$index][$index:a:0]"
        }
        commandProcessor.appendCommand("${videoStream}concat=n=${mediaOption.dataOriginal.size}:v=1:a=1[v][a]\"")
        commandProcessor.appendCommand("-map \"[v]\" -map \"[a]\"")
        commandProcessor.appendCommand("-b:v ${resolutionMax.bitrate}")
        commandProcessor.appendCommand("-vsync 1")
        commandProcessor.appendCommand("$pathOutputFolderVideo/${mediaOption.nameOutput + "_" + System.currentTimeMillis()}.${mediaOption.mimetype}")
        return commandProcessor.getCommand()
    }


    private fun reverseVideo(
        pathVideo: String,
        mime: String,
        reverseAudio: Boolean,
        preset: String
    ): String {
        return "-i $pathVideo -vf reverse ${if (reverseAudio) "-af areverse" else ""} -preset $preset ${
            getPathOutPut(
                mime
            )
        }"
    }

    fun createCommandList(optionMedia: OptionMedia): List<String> {
        var resolution: Resolution

        if (optionMedia.mediaAction is MediaAction.JoinVideo) {

            return listOf(

                joinVideo(
                    optionMedia
                )
            )
        }
        return optionMedia.dataOriginal.map { mediaItem ->
            resolution = calculateResolutionForCompressVideo(mediaItem, optionMedia)

            when (optionMedia.mediaAction) {
                is MediaAction.CompressVideo -> compressVideoCommand(
                    resolution,
                    mediaItem.path,
                    optionMedia.optionCompressType!!,
                    optionMedia.fileSize,
                    optionMedia.frameRate,
                    optionMedia.bitrate,
                    mediaItem.mime
                )

                is MediaAction.CutOrTrim.CutVideo -> {
                    cutVideoCommand(
                        resolution,
                        mediaItem.path,
                        optionMedia.startTime,
                        optionMedia.endTime,
                        mediaItem.mime
                    )
                }

                is MediaAction.CutOrTrim.TrimVideo -> trimVideoCommand(
                    resolution,
                    mediaItem.path,
                    optionMedia.startTime,
                    optionMedia.endTime,
                    mediaItem.mime
                )

                is MediaAction.ExtractAudio -> extractAudioCommand(
                    optionMedia.mimetype!!,
                    mediaItem.path
                )

                is MediaAction.FastForward, MediaAction.SlowVideo -> fastOrSlowVideoCommand(
                    resolution,
                    optionMedia.withAudio,
                    optionMedia.isFastVideo,
                    optionMedia.speed,
                    mediaItem.path,
                    mediaItem.mime
                )


                is MediaAction.ReveresVideo -> reverseVideo(
                    optionMedia.dataOriginal[0].path,
                    mediaItem.mime,
                    optionMedia.reverseAudio,
                    optionMedia.preset
                )

                else -> {
                    ""
                }
            }
        }
    }

    private fun calculateResolutionForCompressVideo(
        mediaItem: MediaInfo,
        optionMedia: OptionMedia
    ): Resolution {
        // Khởi tạo giá trị resolution ban đầu
        var resolution = Resolution()

        // Kiểm tra điều kiện và tính toán resolution mới dựa trên action và option
        if ((optionMedia.mediaAction is MediaAction.CompressVideo &&
                    optionMedia.optionCompressType != OptionCompressType.CustomFileSize) ||
            optionMedia.mediaAction is MediaAction.CutOrTrim.CutVideo ||
            optionMedia.mediaAction is MediaAction.CutOrTrim.TrimVideo ||
            optionMedia.mediaAction is MediaAction.FastForward ||
            optionMedia.mediaAction is MediaAction.SlowVideo

        ) {
            val originalResolution = mediaItem.resolution!!
            resolution = if (
                optionMedia.optionCompressType == OptionCompressType.Custom
            ) {
                originalResolution.calculateResolutionByRadio(
                    originalResolution.getRatio(), optionMedia.newResolution.width, null
                )
            } else {
                originalResolution.calculateResolution(optionMedia.optionCompressType!!)
            }
        }
        return resolution
    }

    fun getDurations() {

    }
}

