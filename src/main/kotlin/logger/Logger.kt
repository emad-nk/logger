package logger

import java.util.concurrent.ConcurrentHashMap
import logger.LogLevel.DEBUG
import logger.LogLevel.ERROR
import logger.LogLevel.INFO
import logger.LogLevel.WARN
import logger.target.LogTarget
import logger.target.LogTargetFactory
import logger.target.LogTargetFactoryManager

class Logger(
    private val className: String,
) {

    private val logTargets: ConcurrentHashMap<LogTarget, LogLevel> = ConcurrentHashMap()
    private val logTargetFactory: LogTargetFactory by lazy {
        LogTargetFactory()
    }

    /**
     * Gets map of log targets with their log level
     */
    val logTargetsMap: Map<LogTarget, LogLevel>
        get() = logTargets

    val logTargetFactoryInstance: LogTargetFactory
        get() = logTargetFactory

    /**
     * Adds log targets
     * @param logTargets map
     */
    fun addLogTargets(logTargets: Map<LogTarget, LogLevel>) {
        logTargets.forEach { (logTarget, logLevel) ->
            this.logTargets[logTarget] = logLevel
        }
    }


    /**
     * Deletes log targets from the Logger and their associated instances in the LogTargetFactory
     * @param logTargets vararg
     */
    fun deleteLogTargets(vararg logTargets: LogTarget) {
        logTargets.forEach { logTarget ->
            this.logTargets.remove(logTarget)?.let {
                if (logTarget is LogTargetFactoryManager) {
                    logTarget.deleteLogTargetInstance(this)
                }
            }
        }
    }

    /**
     * Deletes all log targets from the Logger and their associated instances in the LogTargetFactory
     */
    fun deleteAllLogTargets() {
        logTargets.keys.forEach { logTarget ->
            if (logTarget is LogTargetFactoryManager) {
                logTarget.deleteLogTargetInstance(this)
            }
        }
        logTargets.clear()
    }


    /**
     * Logs messages with the log level
     * @param logLevel
     * @param message
     */
    fun log(logLevel: LogLevel, message: String, exception: Throwable? = null) {
        if (logTargets.isEmpty()) throw RuntimeException("No log targets have been added")
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
