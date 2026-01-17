plugins {
    id("com.gradleup.shadow") version "9.3.1"
}

dependencies {
    compileOnly(files("../HytaleServer.jar"))
    implementation(project(":hytale"))
}

tasks.shadowJar {
    // optionally relocate faststats
    relocate("dev.faststats", "com.example.utils.faststats")
}
