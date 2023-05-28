dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
}

tasks {
    withType<Jar> { enabled = true }
    withType<org.springframework.boot.gradle.tasks.bundling.BootJar> { enabled = false }
}
