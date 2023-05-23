dependencies {
}

tasks {
    withType<Jar> { enabled = true }
    withType<org.springframework.boot.gradle.tasks.bundling.BootJar> { enabled = false }
}
