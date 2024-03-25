package com.game.logger.target

import com.game.logger.LogLevel

interface LogTarget {

    var logLevel: LogLevel

    fun logMessage(message: String, logLevel: LogLevel)
}