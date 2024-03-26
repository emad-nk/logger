package com.game.logger.target

import com.game.logger.LogLevel
import com.game.logger.LogLevel.INFO
import java.time.Instant.now

class APILogTarget internal constructor(private val apiUrl: String): LogTarget {

    override var logLevel = INFO
    override fun logMessage(message: String, logLevel: LogLevel) {
        if(logLevel >= this.logLevel) {
            println("[${now()}] [API $apiUrl] $message")
        }
    }

    companion object {
        fun createInstance(apiUrl: String): APILogTarget {
            return LogTargetFactory.getAPILogTarget(apiUrl)
        }
    }
}