import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("kapt")
    kotlin("plugin.noarg")
    `java-test-fixtures`
}

dependencies {
    implementation(project(":muckpot-infra"))

    val kapt by configurations
    // querydsl
    api("com.querydsl:querydsl-jpa:5.0.0")
    kapt("com.querydsl:querydsl-apt:5.0.0:jpa")
    // https://mvnrepository.com/artifact/org.hibernate.orm/hibernate-spatial
    implementation("org.hibernate:hibernate-spatial:5.6.15.Final")

    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
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
