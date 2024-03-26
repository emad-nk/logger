package logger

import logger.LogLevel.DEBUG
import logger.LogLevel.ERROR
import logger.LogLevel.INFO
import logger.LogLevel.WARN
import logger.target.APILogTarget
import logger.target.ConsoleLogTarget
import logger.target.EmailLogTarget
import logger.target.FileSystemLogTarget
import logger.target.LogTargetFactory
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LoggerTest {

    private val standardOut = System.out
    private val outputStreamCaptor = ByteArrayOutputStream()
    private lateinit var logger: Logger

    @BeforeEach
    fun setUp() {
        System.setOut(PrintStream(outputStreamCaptor))
        logger = Logger(className = CLASS_NAME)
    }

    @AfterEach
    fun tearDown() {
        System.setOut(standardOut)
        logger.deleteAllLogTargets()
    }

    @Nested
    inner class ConsoleLogTargetTests {
        @Test
        fun `logs debug message`() {
            val className = "testClass"
            val logMessage = "This is a log message"
            val logger = Logger(className = className)
            logger.addLogTargets(logTargets = mapOf(ConsoleLogTarget to DEBUG))

            logger.info(message = logMessage)

            val consoleOutput = outputStreamCaptor.toString()
            assertThat(consoleOutput).containsPattern(TIMESTAMP_PATTERN)
            assertThat(consoleOutput).contains("[Console] [INFO] $className: $logMessage\n")
        }

        @Test
        fun `logs debug message with exception`() {
            val className = "testClass"
            val logMessage = "This is a log message"
            val logLevel = DEBUG
            val logger = Logger(className = className)
            logger.addLogTargets(logTargets = mapOf(ConsoleLogTarget to logLevel))

            logger.debug(message = logMessage, exception = RuntimeException(ERROR_MESSAGE))

            val consoleOutput = outputStreamCaptor.toString()
            assertThat(consoleOutput).containsPattern(TIMESTAMP_PATTERN)
            assertThat(consoleOutput).contains(
                "[Console] [$logLevel] $className: $logMessage\n",
                ERROR_MESSAGE
            )
        }

        @Test
        fun `does not add log target multiple times to the logsTargets`() {
            val logger = Logger(className = CLASS_NAME)
            logger.addLogTargets(
                logTargets = mapOf(
                    ConsoleLogTarget to DEBUG,
                    ConsoleLogTarget to ERROR
                )
            )

            val logTargets = logger.logTargetsMap

            assertThat(logTargets).hasSize(1)
            assertThat(logTargets.map { it.key }.first()).isEqualTo(ConsoleLogTarget)
            assertThat(logTargets.map { it.value }.first()).isEqualTo(ERROR)
        }

        @Test
        fun `does not log when log level is higher than the coming log message`() {
            val className = "testClass"
            val logMessage = "This is a log message"
            val logLevel = ERROR
            val logger = Logger(className = className)
            logger.addLogTargets(logTargets = mapOf(ConsoleLogTarget to logLevel))

            logger.warn(message = logMessage)

            assertThat(outputStreamCaptor.toString()).isEmpty()
        }
    }

    @Nested
    inner class APILogTargetTests {
        @Test
        fun `logs debug message`() {
            val logger = Logger(className = CLASS_NAME)
            logger.addLogTargets(logTargets = mapOf(APILogTarget.createInstance("example.com") to DEBUG))

            logger.info(message = LOG_MESSAGE)

            val consoleOutput = outputStreamCaptor.toString()
            assertThat(consoleOutput).containsPattern(TIMESTAMP_PATTERN)
            assertThat(consoleOutput).contains("[API example.com] [INFO] $CLASS_NAME: $LOG_MESSAGE\n")
        }

        @Test
        fun `logs debug message with exception`() {
            val logLevel = DEBUG
            val logger = Logger(className = CLASS_NAME)
            logger.addLogTargets(logTargets = mapOf(APILogTarget.createInstance("example.com") to logLevel))

            logger.debug(message = LOG_MESSAGE, exception = RuntimeException(ERROR_MESSAGE))

            val consoleOutput = outputStreamCaptor.toString()
            assertThat(consoleOutput).containsPattern(TIMESTAMP_PATTERN)
            assertThat(consoleOutput).contains(
                "[API example.com] [$logLevel] $CLASS_NAME: $LOG_MESSAGE\n",
                ERROR_MESSAGE
            )
        }

        @Test
        fun `does not add log target multiple times to the logsTargets if the API URL is the same`() {
            val logger = Logger(className = CLASS_NAME)
            logger.addLogTargets(
                logTargets = mapOf(
                    APILogTarget.createInstance("example.com") to DEBUG,
                    APILogTarget.createInstance("example.com") to ERROR
                )
            )

            val logTargets = logger.logTargetsMap

            assertThat(logTargets).hasSize(1)
            assertThat(logTargets.map { it.key }.first()).isEqualTo(APILogTarget.createInstance("example.com"))
            assertThat(logTargets.map { it.value }.first()).isEqualTo(ERROR)
        }

        @Test
        fun `does not log when log level is higher than the coming log message`() {
            val logLevel = ERROR
            val logger = Logger(className = CLASS_NAME)
            logger.addLogTargets(logTargets = mapOf(APILogTarget.createInstance("example.com") to logLevel))

            logger.warn(message = LOG_MESSAGE)

            assertThat(outputStreamCaptor.toString()).isEmpty()
        }
    }

    @Nested
    inner class EmailLogTargetTests {
        @Test
        fun `logs debug message`() {
            val email = "example@example.com"
            val logger = Logger(className = CLASS_NAME)
            logger.addLogTargets(logTargets = mapOf(EmailLogTarget.createInstance("example@example.com") to DEBUG))

            logger.info(message = LOG_MESSAGE)

            val consoleOutput = outputStreamCaptor.toString()
            assertThat(consoleOutput).containsPattern(TIMESTAMP_PATTERN)
            assertThat(consoleOutput).contains("[Email to $email] [INFO] $CLASS_NAME: $LOG_MESSAGE\n")
        }

        @Test
        fun `logs debug message with exception`() {
            val email = "example@example.com"
            val logLevel = DEBUG
            val logger = Logger(className = CLASS_NAME)
            logger.addLogTargets(logTargets = mapOf(EmailLogTarget.createInstance(email) to logLevel))

            logger.debug(message = LOG_MESSAGE, exception = RuntimeException(ERROR_MESSAGE))

            val consoleOutput = outputStreamCaptor.toString()
            assertThat(consoleOutput).containsPattern(TIMESTAMP_PATTERN)
            assertThat(consoleOutput).contains(
                "[Email to $email] [$logLevel] $CLASS_NAME: $LOG_MESSAGE\n",
                ERROR_MESSAGE
            )
        }

        @Test
        fun `does not add log target multiple times to the logsTargets if the email address is the same`() {
            val email = "example@example.com"
            val logger = Logger(className = CLASS_NAME)
            logger.addLogTargets(
                logTargets = mapOf(
                    EmailLogTarget.createInstance(email) to DEBUG,
                    EmailLogTarget.createInstance(email) to ERROR
                )
            )

            val logTargets = logger.logTargetsMap

            assertThat(logTargets).hasSize(1)
            assertThat(logTargets.map { it.key }.first()).isEqualTo(EmailLogTarget.createInstance(email))
            assertThat(logTargets.map { it.value }.first()).isEqualTo(ERROR)
        }

        @Test
        fun `does not log when log level is higher than the coming log message`() {
            val className = "testClass"
            val logMessage = "This is a log message"
            val logLevel = ERROR
            val logger = Logger(className = className)
            logger.addLogTargets(logTargets = mapOf(EmailLogTarget.createInstance("example@example.com") to logLevel))

            logger.warn(message = logMessage)

            assertThat(outputStreamCaptor.toString()).isEmpty()
        }
    }

    @Nested
    inner class FileSystemLogTargetTests {
        @Test
        fun `logs debug message`() {
            val location = "/documents/logs"
            val logger = Logger(className = CLASS_NAME)
            logger.addLogTargets(logTargets = mapOf(FileSystemLogTarget.createInstance(location) to DEBUG))

            logger.info(message = LOG_MESSAGE)

            val consoleOutput = outputStreamCaptor.toString()
            assertThat(consoleOutput).containsPattern(TIMESTAMP_PATTERN)
            assertThat(consoleOutput).contains("[FileSystem location $location] [INFO] $CLASS_NAME: $LOG_MESSAGE\n")
        }

        @Test
        fun `logs debug message with exception`() {
            val location = "/documents/logs"
            val logLevel = DEBUG
            val logger = Logger(className = CLASS_NAME)
            logger.addLogTargets(logTargets = mapOf(FileSystemLogTarget.createInstance(location) to logLevel))

            logger.debug(message = LOG_MESSAGE, exception = RuntimeException(ERROR_MESSAGE))

            val consoleOutput = outputStreamCaptor.toString()
            assertThat(consoleOutput).containsPattern(TIMESTAMP_PATTERN)
            assertThat(consoleOutput).contains(
                "[FileSystem location $location] [$logLevel] $CLASS_NAME: $LOG_MESSAGE\n",
                ERROR_MESSAGE
            )
        }

        @Test
        fun `does not add log target multiple times to the logsTargets if the location is the same`() {
            val location = "/documents/logs"
            val logger = Logger(className = CLASS_NAME)
            logger.addLogTargets(
                logTargets = mapOf(
                    FileSystemLogTarget.createInstance(location) to DEBUG,
                    FileSystemLogTarget.createInstance(location) to ERROR
                )
            )

            val logTargets = logger.logTargetsMap

            assertThat(logTargets).hasSize(1)
            assertThat(logTargets.map { it.key }.first()).isEqualTo(FileSystemLogTarget.createInstance(location))
            assertThat(logTargets.map { it.value }.first()).isEqualTo(ERROR)
        }

        @Test
        fun `does not log when log level is higher than the coming log message`() {
            val className = "testClass"
            val logMessage = "This is a log message"
            val logLevel = ERROR
            val logger = Logger(className = className)
            logger.addLogTargets(logTargets = mapOf(FileSystemLogTarget.createInstance("/documents/logs") to logLevel))

            logger.warn(message = logMessage)

            assertThat(outputStreamCaptor.toString()).isEmpty()
        }
    }

    @Nested
    inner class MultipleLogTargetsTests {
        @Test
        fun `logs debug message on multiple targets`() {
            val location = "/documents/logs"
            val api = "api.com"
            val email = "example@example.com"
            val logger = Logger(className = CLASS_NAME)
            logger.addLogTargets(
                logTargets = mapOf(
                    ConsoleLogTarget to INFO,
                    APILogTarget.createInstance(api) to INFO,
                    FileSystemLogTarget.createInstance(location) to INFO,
                    EmailLogTarget.createInstance(email) to INFO
                )
            )

            logger.info(message = LOG_MESSAGE)

            val consoleOutput = outputStreamCaptor.toString()
            assertThat(consoleOutput).containsPattern(TIMESTAMP_PATTERN)
            assertThat(consoleOutput).contains("[Console] [INFO] $CLASS_NAME: $LOG_MESSAGE\n")
            assertThat(consoleOutput).contains("[FileSystem location $location] [INFO] $CLASS_NAME: $LOG_MESSAGE\n")
            assertThat(consoleOutput).contains("[API $api] [INFO] $CLASS_NAME: $LOG_MESSAGE\n")
            assertThat(consoleOutput).contains("[Email to $email] [INFO] $CLASS_NAME: $LOG_MESSAGE\n")
        }

        @Test
        fun `does not log to some of the targets that their log level is higher than the log message level`() {
            val location = "/documents/logs"
            val api = "api.com"
            val email = "example@example.com"
            val logger = Logger(className = CLASS_NAME)
            logger.addLogTargets(
                logTargets = mapOf(
                    ConsoleLogTarget to INFO,
                    APILogTarget.createInstance(api) to INFO,
                    FileSystemLogTarget.createInstance(location) to ERROR,
                    EmailLogTarget.createInstance(email) to WARN
                )
            )

            logger.info(message = LOG_MESSAGE)

            val consoleOutput = outputStreamCaptor.toString()
            assertThat(consoleOutput).containsPattern(TIMESTAMP_PATTERN)
            assertThat(consoleOutput).contains("[Console] [INFO] $CLASS_NAME: $LOG_MESSAGE\n")
            assertThat(consoleOutput).contains("[API $api] [INFO] $CLASS_NAME: $LOG_MESSAGE\n")
            assertThat(consoleOutput).doesNotContain("[FileSystem location $location] [INFO] $CLASS_NAME: $LOG_MESSAGE\n")
            assertThat(consoleOutput).doesNotContain("[Email to $email] [INFO] $CLASS_NAME: $LOG_MESSAGE\n")
        }
    }

    @Nested
    inner class LoggerTests {
        @Test
        fun `throws runtime exception when user does not add any log targets`() {
            val logger = Logger(className = CLASS_NAME)

            assertThrows<RuntimeException> { logger.info(message = LOG_MESSAGE) }
        }

        @Test
        fun `deletes log targets and their associated instances`() {
            val location = "/documents/logs"
            val api = "example.com"
            val email = "example@example.com"
            val logger = Logger(className = CLASS_NAME)
            logger.addLogTargets(
                logTargets = mapOf(
                    ConsoleLogTarget to INFO,
                    APILogTarget.createInstance(api) to INFO,
                    FileSystemLogTarget.createInstance(location) to ERROR,
                    EmailLogTarget.createInstance(email) to WARN
                )
            )

            assertThat(LogTargetFactory.apiLogTargetsMap).hasSize(1)
            assertThat(LogTargetFactory.fileSystemLogTargetsMap).hasSize(1)
            assertThat(LogTargetFactory.emailLogTargetsMap).hasSize(1)

            logger.deleteLogTargets(
                APILogTarget.createInstance(api),
                FileSystemLogTarget.createInstance(location),
                EmailLogTarget.createInstance(email),
                ConsoleLogTarget
            )

            assertThat(LogTargetFactory.apiLogTargetsMap).isEmpty()
            assertThat(LogTargetFactory.fileSystemLogTargetsMap).isEmpty()
            assertThat(LogTargetFactory.emailLogTargetsMap).isEmpty()
            assertThat(logger.logTargetsMap).isEmpty()
        }
    }

    companion object {
        private const val TIMESTAMP_PATTERN = "\\[\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}Z\\]"
        private const val CLASS_NAME = "testClass"
        private const val LOG_MESSAGE = "This is a log message"
        private const val ERROR_MESSAGE = "Something went wrong"
    }

}
