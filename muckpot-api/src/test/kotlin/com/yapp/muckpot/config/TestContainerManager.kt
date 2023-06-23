package com.yapp.muckpot.config

import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.spec.AutoScan
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName

@AutoScan
object TestContainerManager : BeforeProjectListener, AfterProjectListener {
    private const val REDIS_PORT = 6379

    @Container
    private val redisContainer = GenericContainer(DockerImageName.parse("redis:alpine")).apply {
        withExposedPorts(REDIS_PORT)
    }

    @Container
    private val mariaDBContainer = MariaDBContainer(DockerImageName.parse("mariadb:10.5")).apply {
        withDatabaseName("muckpot_test")
        withUsername("test_user")
        withPassword("12345")
    }

    override suspend fun beforeProject() {
        mariaDBContainer.start()
        System.setProperty("spring.datasource.url", mariaDBContainer.jdbcUrl)
        System.setProperty("spring.datasource.username", mariaDBContainer.username)
        System.setProperty("spring.datasource.password", mariaDBContainer.password)

        redisContainer.start()
        System.setProperty("spring.redis.host", redisContainer.host)
        System.setProperty("spring.redis.port", redisContainer.getMappedPort(REDIS_PORT).toString())
    }

    override suspend fun afterProject() {
        mariaDBContainer.stop()
        redisContainer.stop()
    }
}
