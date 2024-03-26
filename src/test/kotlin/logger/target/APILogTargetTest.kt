package logger.target

import logger.Logger
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class APILogTargetTest {

    @Test
    fun `deleting an instance only delete that specific instance from the LogTargetFactory`(){
        val logger = Logger("someClass")
        val instance1 = APILogTarget.createOrGetInstance("api1", logger)
        val instance2 = APILogTarget.createOrGetInstance("api2", logger)

        val instances = logger.logTargetFactoryInstance.apiLogTargetsMap

        Assertions.assertThat(instances).hasSize(2)
        Assertions.assertThat(instances.map { it.value }).contains(instance1, instance2)

        instance1.deleteLogTargetInstance(logger)

        Assertions.assertThat(instances).hasSize(1)
        Assertions.assertThat(instances.map { it.value }).contains(instance2)
    }
}
