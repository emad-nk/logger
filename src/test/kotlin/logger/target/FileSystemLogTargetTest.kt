package logger.target

import logger.Logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FileSystemLogTargetTest {

    @Test
    fun `deleting an instance only delete that specific instance from the LogTargetFactory`(){
        val logger = Logger("someClass")
        val instance1 = FileSystemLogTarget.createOrGetInstance("location1", logger)
        val instance2 = FileSystemLogTarget.createOrGetInstance("location2", logger)

        val instances = logger.logTargetFactoryInstance.fileSystemLogTargetsMap

        assertThat(instances).hasSize(2)
        assertThat(instances.map { it.value }).contains(instance1, instance2)

        instance1.deleteLogTargetInstance(logger)

        assertThat(instances).hasSize(1)
        assertThat(instances.map { it.value }).contains(instance2)
    }
}
