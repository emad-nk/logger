package logger.target

import logger.LogLevel
import logger.LogLevel.INFO
import java.time.Instant.now
import logger.Logger

class EmailLogTarget internal constructor(
    private val emailAddress: String,
) : LogTarget, LogTargetFactoryManager {

    override var logLevel = INFO

    override fun logMessage(message: String, logLevel: LogLevel) {
        if (logLevel >= this.logLevel) {
            println("[${now()}] [Email to $emailAddress] $message")
        }
    }

    override fun deleteLogTargetInstance(logger: Logger) {
        logger.logTargetFactoryInstance.deleteEmailLogTargets(emailAddress)
    }


    companion object {
        fun createOrGetInstance(emailAddress: String, logger: Logger): EmailLogTarget {
            return logger.logTargetFactoryInstance.getEmailLogTarget(emailAddress)
        }
    }
}
