import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotestVersion = "5.5.4"

plugins {
    id("org.springframework.boot") version "2.7.11" apply false
    id("io.spring.dependency-management") version "1.0.15.RELEASE" apply false
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21" apply false
    kotlin("plugin.jpa") version "1.6.21" apply false

    id("org.jlleitschuh.gradle.ktlint") version "11.3.2"

    kotlin("kapt") version "1.6.21" apply false

    id("org.sonarqube") version "4.2.1.3168"
    id("jacoco")
}

java.sourceCompatibility = JavaVersion.VERSION_11

sonarqube {
    properties {
        property("sonar.projectKey", "YAPP-Github_mukpat-server")
        property("sonar.organization", "yapp-github")
        property("sonar.host.url", "https://sonarcloud.io")
        // sonar additional settings
        property("sonar.sources", "src")
        property("sonar.language", "Kotlin")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.test.inclusions", "**/*Test.java")
        property("sonar.exclusions", "**/test/**, **/Q*.kt, **/*Doc*.kt, **/resources/** ,**/*Application*.kt , **/*Config*.kt, **/*Dto*.kt, **/*Request*.kt, **/*Response*.kt ,**/*Exception*.kt ,**/*ErrorCode*.kt")
        property("sonar.java.coveragePlugin", "jacoco")
    }
}

allprojects {
    group = "com.yapp"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "jacoco")

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
        testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
        testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
        testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")
        testImplementation("io.mockk:mockk:1.12.4")
        testImplementation("com.ninja-squad:springmockk:3.1.2")
    }

    tasks.test {
        finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
    }
    tasks.jacocoTestReport {
        dependsOn(tasks.test) // tests are required to run before generating the report
        reports {
            xml.required.set(true)
            xml.outputLocation.set(File("$buildDir/reports/jacoco.xml"))
        }
        classDirectories.setFrom(
            files(
                classDirectories.files.map {
                    fileTree(it) { // 테스트 커버리지 측정 제외 목록
                        exclude(
                            "**/*Application*",
                            "**/*Config*",
                            "**/*Dto*",
                            "**/*Request*",
                            "**/*Response*",
                            "**/*Interceptor*",
                            "**/*Exception*",
                            "**/Q*"
                        ) // QueryDsl 용이나 Q로 시작하는 클래스 뺄 위험 존재
                    }
                }
            )
        )
    }

    sonarqube {
        properties {
            property("sonar.java.binaries", "$buildDir/classes")
            property("sonar.coverage.jacoco.xmlReportPaths", "$buildDir/reports/jacoco.xml")
        }
    }
}
