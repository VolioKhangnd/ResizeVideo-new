package com.video.mini.tools.zip.compress.convert.simple.tiny.ffmpeg

class CommandConfiguration {
    private val stringBuilder = StringBuilder()

    fun appendCommand(command: String): CommandConfiguration {
        stringBuilder.append("${command.trim()} ")
        return this
    }
    fun appendCommandNotSpace(command: String): CommandConfiguration {
        stringBuilder.append(command.trim())
        return this
    }
    fun getCommand() = stringBuilder.toString().trim()

    companion object {
        fun getInstance(): CommandConfiguration {
            return CommandConfiguration()
        }
    }
}