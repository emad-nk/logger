package com.game.logger.target

object LogTargetFactory {

    private val emailLogTargets = mutableMapOf<String, EmailLogTarget>()
    private val FileSystemLogTargets = mutableMapOf<String, FileSystemLogTarget>()
    private val apiLogTargets = mutableMapOf<String, APILogTarget>()

    fun getEmailLogTarget(emailAddress: String): EmailLogTarget {
        return emailLogTargets.getOrPut(emailAddress) { EmailLogTarget(emailAddress) }
    }

    fun getFileSystemLogTarget(fileSystemLocation: String): FileSystemLogTarget {
        return FileSystemLogTargets.getOrPut(fileSystemLocation) { FileSystemLogTarget(fileSystemLocation) }
    }

    fun getAPILogTarget(apiUrl: String): APILogTarget {
        return apiLogTargets.getOrPut(apiUrl) { APILogTarget(apiUrl) }
    }
}