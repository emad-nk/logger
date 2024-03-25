package com.game.logger

import com.game.logger.LogLevel.DEBUG
import com.game.logger.LogLevel.ERROR
import com.game.logger.LogLevel.INFO
import com.game.logger.LogLevel.WARN
import com.game.logger.target.ConsoleLogTarget
import com.game.logger.target.LogTarget

class Logger(
    private val className: String,
    private val logTargets: Map<LogTarget, LogLevel> = mapOf(ConsoleLogTarget() to INFO)
) {

    /**
     * Logs messages with the log level
     * @param logLevel
     * @param message
     */
    fun log(logLevel: LogLevel, message: String, exception: Throwable? = null) {
        val logMessage = "[$logLevel] $className: $message"
        if (exception != null) {
            logTargets.forEach { (logTarget, registeredLogLevel) ->
                logTarget.logLevel = registeredLogLevel
                logTarget.logMessage(message = "$logMessage\n${exception.stackTraceToString()}", logLevel = logLevel)
            }
        } else {
            logTargets.forEach { (logTarget, registeredLogLevel) ->
                logTarget.logLevel = registeredLogLevel
                logTarget.logMessage(message = logMessage, logLevel = logLevel)
            }
        }
    }

    /**
     * Logs messages with the DEBUG level
     * @param message of type String (required)
     * @param exception of type Throwable (optional)
     */
    fun debug(message: String, exception: Throwable? = null) {
        log(logLevel = DEBUG, message = message, exception = exception)
    }

    /**
     * Logs messages with the INFO level
     * @param message of type String (required)
     * @param exception of type Throwable (optional)
     */
    fun info(message: String, exception: Throwable? = null) {
        log(logLevel = INFO, message = message, exception = exception)
    }

    /**
     * Logs messages with the WARN level
     * @param message of type String (required)
     * @param exception of type Throwable (optional)
     */
    fun warn(message: String, exception: Throwable? = null) {
        log(logLevel = WARN, message = message, exception = exception)
    }

    /**
     * Logs messages with the ERROR level
     * @param message of type String (required)
     * @param exception of type Throwable (optional)
     */
    fun error(message: String, exception: Throwable? = null) {
        log(logLevel = ERROR, message = message, exception = exception)
    }
}