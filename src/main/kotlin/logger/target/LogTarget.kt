package logger.target

import logger.LogLevel

interface LogTarget {

    var logLevel: LogLevel

    fun logMessage(message: String, logLevel: LogLevel)
}
