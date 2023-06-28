package config

import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.spec.AutoScan
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName

@AutoScan
object InfraContainerManager : BeforeProjectListener, AfterProjectListener {
    private val INFRA_PROPERTIES = InfraProperties
    private const val REDIS_PORT = 6379

    @Container
    private val redisContainer = GenericContainer(DockerImageName.parse("redis:alpine")).apply {
        withExposedPorts(REDIS_PORT)
    }

    override suspend fun beforeProject() {
        redisContainer.start()
        System.setProperty("spring.redis.host", redisContainer.host)
        System.setProperty("spring.redis.port", redisContainer.getMappedPort(REDIS_PORT).toString())
    }

    override suspend fun afterProject() {
        redisContainer.stop()
    }
}
