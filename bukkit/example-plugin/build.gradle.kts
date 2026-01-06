plugins {
    id("com.gradleup.shadow") version "9.3.1"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation(project(":bukkit"))
}

tasks.shadowJar {
    // optionally relocate faststats
    relocate("dev.faststats", "com.example.utils.faststats")
}
