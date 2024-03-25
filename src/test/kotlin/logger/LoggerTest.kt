package logger

import com.game.logger.LogLevel.DEBUG
import com.game.logger.LogLevel.ERROR
import com.game.logger.LogLevel.INFO
import com.game.logger.LogLevel.WARN
import com.game.logger.Logger
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoggerTest{

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

    @Test
    fun `logs debug message`() {
        val className = "testClass"
        val logMessage = "This is a log message"
        val logLevel = DEBUG
        val logger = Logger(className = className)
        logger.setLogLevel(logLevel)

        logger.debug(message = logMessage)

        assertThat(outputStreamCaptor.toString()).isEqualTo("[$logLevel] $className: $logMessage\n")
    }

    @Test
    fun `logs info message`() {
        val className = "testClass"
        val logMessage = "This is a log message"
        val logLevel = INFO
        val logger = Logger(className = className)
        logger.setLogLevel(logLevel)

        logger.info(message = logMessage)

        assertThat(outputStreamCaptor.toString()).isEqualTo("[$logLevel] $className: $logMessage\n")
    }

    @Test
    fun `logs warn message`() {
        val className = "testClass"
        val logMessage = "This is a log message"
        val logLevel = WARN
        val logger = Logger(className = className)
        logger.setLogLevel(logLevel)

        logger.warn(message = logMessage)

        assertThat(outputStreamCaptor.toString()).isEqualTo("[$logLevel] $className: $logMessage\n")
    }

    @Test
    fun `logs error message`() {
        val className = "testClass"
        val logMessage = "This is a log message"
        val logLevel = ERROR
        val logger = Logger(className = className)
        logger.setLogLevel(logLevel)

        logger.error(message = logMessage)

        assertThat(outputStreamCaptor.toString()).isEqualTo("[$logLevel] $className: $logMessage\n")
    }

    @Test
    fun `does not log those logs that their log level is lower than specified log level`() {
        val className = "testClass"
        val logMessage = "This is a new log message"
        val logger = Logger(className = className)
        logger.setLogLevel(ERROR)

        logger.debug(message = logMessage)
        logger.info(message = logMessage)
        logger.warn(message = logMessage)

        assertThat(outputStreamCaptor.toString()).isEmpty()
    }

    @Test
    fun `logs a message with exception`() {
        val className = "testClass"
        val logMessage = "This is a new log message"
        val logger = Logger(className = className)
        logger.setLogLevel(WARN)

        logger.error(message = logMessage, exception = RuntimeException("something went wrong"))

        assertThat(outputStreamCaptor.toString()).contains("This is a new log message", "something went wrong")
    }

}