package logger.target

import logger.Logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EmailLogTargetTest {

    @Test
    fun `deleting an instance only delete that specific instance from the LogTargetFactory`() {
        val logger = Logger("someClass")
        val instance1 = EmailLogTarget.createOrGetInstance("email1", logger)
        val instance2 = EmailLogTarget.createOrGetInstance("email2", logger)

        val instances = logger.logTargetFactoryInstance.emailLogTargetsMap

        assertThat(instances).hasSize(2)
        assertThat(instances.map { it.value }).contains(instance1, instance2)

        instance1.deleteLogTargetInstance(logger)

        assertThat(instances).hasSize(1)
        assertThat(instances.map { it.value }).contains(instance2)
    }
}
