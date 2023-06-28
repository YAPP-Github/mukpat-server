package config

import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.spec.AutoScan
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName

@AutoScan
object DomainContainerManager : BeforeProjectListener, AfterProjectListener {
    private val DOMAIN_PROPERTIES = DomainProperties

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
    }

    override suspend fun afterProject() {
        mariaDBContainer.stop()
    }
}
