package logger.target

import java.util.concurrent.ConcurrentHashMap

/**
 * Prevents creating the same instance
 * Usually the number of APIs, FileSystems and Emails to use for logging is not a lot and perhaps there is only 1 API, FileSystem and Email
 */
class LogTargetFactory{

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

    fun deleteEmailLogTargets(emailAddress: String) {
        emailLogTargets.remove(emailAddress)
    }

    fun deleteFileSystemLogTargets(fileLocation: String) {
        fileSystemLogTargets.remove(fileLocation)
    }

    fun deleteApiLogTargets(apiUrl: String) {
        apiLogTargets.remove(apiUrl)
    }
}
