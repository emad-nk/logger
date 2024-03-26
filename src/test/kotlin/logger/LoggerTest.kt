package logger

import com.game.logger.LogLevel.DEBUG
import com.game.logger.LogLevel.ERROR
import com.game.logger.Logger
import com.game.logger.target.APILogTarget
import com.game.logger.target.ConsoleLogTarget
import com.game.logger.target.EmailLogTarget
import com.game.logger.target.FileSystemLogTarget
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

    @BeforeEach
    fun setUp() {
        System.setOut(PrintStream(outputStreamCaptor))
    }

    @AfterEach
    fun tearDown() {
        System.setOut(standardOut)
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

            assertThat(outputStreamCaptor.toString()).containsPattern(TIMESTAMP_PATTERN)
            assertThat(outputStreamCaptor.toString()).contains("[Console] [INFO] $className: $logMessage\n")
        }

        @Test
        fun `logs debug message with exception`() {
            val className = "testClass"
            val logMessage = "This is a log message"
            val logLevel = DEBUG
            val logger = Logger(className = className)
            logger.addLogTargets(logTargets = mapOf(ConsoleLogTarget to logLevel))

            logger.debug(message = logMessage, exception = RuntimeException(ERROR_MESSAGE))

            assertThat(outputStreamCaptor.toString()).containsPattern(TIMESTAMP_PATTERN)
            assertThat(outputStreamCaptor.toString()).contains(
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

            val logTargets = logger.getLogTargets()

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

            assertThat(outputStreamCaptor.toString()).containsPattern(TIMESTAMP_PATTERN)
            assertThat(outputStreamCaptor.toString()).contains("[API example.com] [INFO] $CLASS_NAME: $LOG_MESSAGE\n")
        }

        @Test
        fun `logs debug message with exception`() {
            val logLevel = DEBUG
            val logger = Logger(className = CLASS_NAME)
            logger.addLogTargets(logTargets = mapOf(APILogTarget.createInstance("example.com") to logLevel))

            logger.debug(message = LOG_MESSAGE, exception = RuntimeException(ERROR_MESSAGE))

            assertThat(outputStreamCaptor.toString()).containsPattern(TIMESTAMP_PATTERN)
            assertThat(outputStreamCaptor.toString()).contains(
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

            val logTargets = logger.getLogTargets()

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

            assertThat(outputStreamCaptor.toString()).containsPattern(TIMESTAMP_PATTERN)
            assertThat(outputStreamCaptor.toString()).contains("[Email to $email] [INFO] $CLASS_NAME: $LOG_MESSAGE\n")
        }

        @Test
        fun `logs debug message with exception`() {
            val email = "example@example.com"
            val logLevel = DEBUG
            val logger = Logger(className = CLASS_NAME)
            logger.addLogTargets(logTargets = mapOf(EmailLogTarget.createInstance(email) to logLevel))

            logger.debug(message = LOG_MESSAGE, exception = RuntimeException(ERROR_MESSAGE))

            assertThat(outputStreamCaptor.toString()).containsPattern(TIMESTAMP_PATTERN)
            assertThat(outputStreamCaptor.toString()).contains(
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

            val logTargets = logger.getLogTargets()

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

            assertThat(outputStreamCaptor.toString()).containsPattern(TIMESTAMP_PATTERN)
            assertThat(outputStreamCaptor.toString()).contains("[FileSystem location $location] [INFO] $CLASS_NAME: $LOG_MESSAGE\n")
        }

        @Test
        fun `logs debug message with exception`() {
            val location = "/documents/logs"
            val logLevel = DEBUG
            val logger = Logger(className = CLASS_NAME)
            logger.addLogTargets(logTargets = mapOf(FileSystemLogTarget.createInstance(location) to logLevel))

            logger.debug(message = LOG_MESSAGE, exception = RuntimeException(ERROR_MESSAGE))

            assertThat(outputStreamCaptor.toString()).containsPattern(TIMESTAMP_PATTERN)
            assertThat(outputStreamCaptor.toString()).contains(
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

            val logTargets = logger.getLogTargets()

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
            logger.addLogTargets(logTargets = mapOf(FileSystemLogTarget.createInstance("location") to logLevel))

            logger.warn(message = logMessage)

            assertThat(outputStreamCaptor.toString()).isEmpty()
        }
    }

    @Nested
    inner class LoggerTests {
        @Test
        fun `throws runtime exception when user does not add any log targets`() {
            val logger = Logger(className = CLASS_NAME)

            assertThrows<RuntimeException> { logger.info(message = LOG_MESSAGE) }
        }


    }

    companion object {
        private const val TIMESTAMP_PATTERN = "\\[\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}Z\\]"
        private const val CLASS_NAME = "testClass"
        private const val LOG_MESSAGE = "This is a log message"
        private const val ERROR_MESSAGE = "Something went wrong"
    }

}