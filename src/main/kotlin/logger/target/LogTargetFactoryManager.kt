package logger.target

import logger.Logger

interface LogTargetFactoryManager {
    fun deleteLogTargetInstance(logger: Logger)
}
