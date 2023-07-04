val kotestVersion = "5.5.4"
val testContainerVersion = "1.17.6"

plugins {
    `java-test-fixtures`
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.redisson:redisson:3.20.0")

    testFixturesImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testFixturesImplementation("org.testcontainers:testcontainers:$testContainerVersion")
    testFixturesImplementation("org.testcontainers:junit-jupiter:$testContainerVersion")
}

tasks {
    withType<Jar> { enabled = true }
    withType<org.springframework.boot.gradle.tasks.bundling.BootJar> { enabled = false }
}
