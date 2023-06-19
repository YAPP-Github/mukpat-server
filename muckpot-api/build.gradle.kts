dependencies {
    implementation(project(":muckpot-domain"))
    implementation(project(":muckpot-infra"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.springfox:springfox-boot-starter:3.0.0")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.auth0:java-jwt:4.2.1")
    implementation("org.redisson:redisson:3.20.0")

    testImplementation(testFixtures(project(":muckpot-domain")))
}
