

plugins {
    id("org.springframework.boot") version "3.5.8"
    id("io.spring.dependency-management") version "1.1.7"
    java
}

group = "com.paynalty"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    dependencies {
        // Spring Boot
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-security")
        implementation("org.springframework.boot:spring-boot-starter-webflux")
        implementation("org.springframework.boot:spring-boot-starter-validation")

        // JWT
        implementation("io.jsonwebtoken:jjwt-api:0.13.0")
        runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
        runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

        // Swagger (SpringDoc)
        implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.3")

        // Database
        runtimeOnly("com.mysql:mysql-connector-j")
        runtimeOnly("com.h2database:h2")

        // Lombok
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")

        // DevTools
        developmentOnly("org.springframework.boot:spring-boot-devtools")

        // Test
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.springframework.security:spring-security-test")
    }

}

tasks.withType<Test> {
    useJUnitPlatform()
}
