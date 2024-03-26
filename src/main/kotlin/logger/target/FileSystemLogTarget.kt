package logger.target

import logger.LogLevel
import java.time.Instant.now

class FileSystemLogTarget internal constructor(
    private val fileSystemLocation: String,
) : LogTarget, LogTargetFactoryManagement {

    override var logLevel = LogLevel.INFO
    override fun logMessage(message: String, logLevel: LogLevel) {
        if (logLevel >= this.logLevel) {
            println("[${now()}] [FileSystem location $fileSystemLocation] $message")
        }
    }

    override fun deleteLogTargetInstances() {
        LogTargetFactory.deleteFileSystemLogTargets()
    }

    companion object {
        fun createInstance(fileSystemLocation: String): FileSystemLogTarget {
            return LogTargetFactory.getFileSystemLogTarget(fileSystemLocation)
        }
    }
}
