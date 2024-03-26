package logger.target

import logger.LogLevel
import logger.LogLevel.INFO
import java.time.Instant.now
import logger.Logger

class APILogTarget internal constructor(
    private val apiUrl: String,
) : LogTarget, LogTargetFactoryManager {

    override var logLevel = INFO

    override fun logMessage(message: String, logLevel: LogLevel) {
        if (logLevel >= this.logLevel) {
            println("[${now()}] [API $apiUrl] $message")
        }
    }

    override fun deleteLogTargetInstance(logger: Logger) {
        logger.logTargetFactoryInstance.deleteApiLogTargets(apiUrl)
    }

    companion object {
        fun createOrGetInstance(apiUrl: String, logger: Logger): APILogTarget {
            return logger.logTargetFactoryInstance.getAPILogTarget(apiUrl)
        }
    }
}
