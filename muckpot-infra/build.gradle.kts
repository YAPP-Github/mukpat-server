dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.redisson:redisson:3.20.0")
}

tasks {
    withType<Jar> { enabled = true }
    withType<org.springframework.boot.gradle.tasks.bundling.BootJar> { enabled = false }
}
