package logger.target

import logger.LogLevel
import logger.LogLevel.INFO
import java.time.Instant.now

object ConsoleLogTarget : LogTarget {

    override var logLevel = INFO

    override fun logMessage(message: String, logLevel: LogLevel) {
        if (logLevel >= ConsoleLogTarget.logLevel) {
            println("[${now()}] [Console] $message")
        }
    }
}
