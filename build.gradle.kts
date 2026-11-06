plugins {
    java
    application
    id("org.springframework.boot") version "4.1.2"
    id("io.spring.dependency-management") version "1.1.6"
}

application {
    mainClass.set("edu.nd.oose.springbootdemo.PollApplication")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    // SQLite JDBC Driver & Dialect for Spring JDBC
    implementation("org.xerial:sqlite-jdbc:3.45.2.0")
    implementation("org.hibernate.orm:hibernate-community-dialects")
}