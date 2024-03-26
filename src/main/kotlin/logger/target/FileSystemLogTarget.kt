package logger.target

import logger.LogLevel
import java.time.Instant.now
import logger.Logger

class FileSystemLogTarget internal constructor(
    private val fileSystemLocation: String,
) : LogTarget, LogTargetFactoryManager {

    override var logLevel = LogLevel.INFO

    override fun logMessage(message: String, logLevel: LogLevel) {
        if (logLevel >= this.logLevel) {
            println("[${now()}] [FileSystem location $fileSystemLocation] $message")
        }
    }

    override fun deleteLogTargetInstance(logger: Logger) {
        logger.logTargetFactoryInstance.deleteFileSystemLogTargets(fileSystemLocation)
    }

    companion object {
        fun createOrGetInstance(fileSystemLocation: String, logger: Logger): FileSystemLogTarget {
            return logger.logTargetFactoryInstance.getFileSystemLogTarget(fileSystemLocation)
        }
    }
}
