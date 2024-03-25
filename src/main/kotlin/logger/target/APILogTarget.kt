package com.game.logger.target

import com.game.logger.LogLevel
import com.game.logger.LogLevel.INFO
import java.time.Instant.now

class APILogTarget(val apiUrl: String): LogTarget {

    override var logLevel = INFO
    override fun logMessage(message: String, logLevel: LogLevel) {
        if(logLevel >= this.logLevel) {
            println("[${now()}] [API $apiUrl] $message")
        }
    }
}