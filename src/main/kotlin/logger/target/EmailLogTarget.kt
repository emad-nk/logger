package com.game.logger.target

import com.game.logger.LogLevel
import com.game.logger.LogLevel.INFO
import java.time.Instant.now

class EmailLogTarget(private val emailAddress: String) : LogTarget{

    override var logLevel = INFO
    override fun logMessage(message: String, logLevel: LogLevel) {
        if(logLevel >= this.logLevel) {
            println("[${now()}] [Email to $emailAddress] $message")
        }
    }
}