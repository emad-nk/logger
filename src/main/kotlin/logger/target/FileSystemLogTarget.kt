package com.game.logger.target

import com.game.logger.LogLevel
import java.time.Instant.now

class FileSystemLogTarget internal constructor(private val fileSystemLocation: String) : LogTarget {

    override var logLevel = LogLevel.INFO
    override fun logMessage(message: String, logLevel: LogLevel) {
        if(logLevel >= this.logLevel) {
            println("[${now()}] [FileSystem location $fileSystemLocation] $message")
        }
    }

    companion object {
        fun createInstance(fileSystemLocation: String): FileSystemLogTarget {
            return LogTargetFactory.getFileSystemLogTarget(fileSystemLocation)
        }
    }
}