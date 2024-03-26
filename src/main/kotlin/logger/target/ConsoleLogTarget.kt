package com.game.logger.target

import com.game.logger.LogLevel
import com.game.logger.LogLevel.INFO
import java.time.Instant.now

object ConsoleLogTarget: LogTarget {

    override var logLevel = INFO

    override fun logMessage(message: String, logLevel: LogLevel) {
        if(logLevel >= this.logLevel) {
            println("[${now()}] [Console] $message")
        }
    }
}