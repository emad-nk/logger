package logger.target

import logger.LogLevel
import logger.LogLevel.INFO
import java.time.Instant.now

class EmailLogTarget internal constructor(
    private val emailAddress: String,
) : LogTarget, LogTargetFactoryManager {

    override var logLevel = INFO
    override fun logMessage(message: String, logLevel: LogLevel) {
        if (logLevel >= this.logLevel) {
            println("[${now()}] [Email to $emailAddress] $message")
        }
    }

    override fun deleteLogTargetInstances() {
        LogTargetFactory.deleteEmailLogTargets()
    }


    companion object {
        fun createInstance(emailAddress: String): EmailLogTarget {
            return LogTargetFactory.getEmailLogTarget(emailAddress)
        }
    }
}
