import org.springframework.boot.gradle.tasks.bundling.BootJar

val containerVer = "1.17.6"

plugins {
    kotlin("kapt")
    kotlin("plugin.noarg")
}

dependencies {
    val kapt by configurations
    // querydsl
    api("com.querydsl:querydsl-jpa:5.0.0")
    kapt("com.querydsl:querydsl-apt:5.0.0:jpa")
    // https://mvnrepository.com/artifact/org.hibernate.orm/hibernate-spatial
    implementation("org.hibernate:hibernate-spatial:5.6.15.Final")

    runtimeOnly("com.mysql:mysql-connector-j")

    testImplementation("org.testcontainers:testcontainers:$containerVer")
    testImplementation("org.testcontainers:junit-jupiter:$containerVer")
    testImplementation("org.testcontainers:mysql:$containerVer")
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

noArg {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

tasks {
    withType<Jar> { enabled = true }
    withType<BootJar> { enabled = false }
}
