package logger.target

import java.util.concurrent.ConcurrentHashMap

object LogTargetFactory {

    private val emailLogTargets: ConcurrentHashMap<String, EmailLogTarget> = ConcurrentHashMap()
    private val fileSystemLogTargets: ConcurrentHashMap<String, FileSystemLogTarget> = ConcurrentHashMap()
    private val apiLogTargets: ConcurrentHashMap<String, APILogTarget> = ConcurrentHashMap()

    internal val emailLogTargetsMap: Map<String, EmailLogTarget>
        get() = emailLogTargets

    internal val fileSystemLogTargetsMap: Map<String, FileSystemLogTarget>
        get() = fileSystemLogTargets

    internal val apiLogTargetsMap: Map<String, APILogTarget>
        get() = apiLogTargets


    fun getEmailLogTarget(emailAddress: String): EmailLogTarget {
        return emailLogTargets.getOrPut(emailAddress) { EmailLogTarget(emailAddress) }
    }

    fun getFileSystemLogTarget(fileSystemLocation: String): FileSystemLogTarget {
        return fileSystemLogTargets.getOrPut(fileSystemLocation) { FileSystemLogTarget(fileSystemLocation) }
    }

    fun getAPILogTarget(apiUrl: String): APILogTarget {
        return apiLogTargets.getOrPut(apiUrl) { APILogTarget(apiUrl) }
    }

    fun deleteEmailLogTargets() {
        emailLogTargets.clear()
    }

    fun deleteFileSystemLogTargets() {
        fileSystemLogTargets.clear()
    }

    fun deleteApiLogTargets() {
        apiLogTargets.clear()
    }
}
