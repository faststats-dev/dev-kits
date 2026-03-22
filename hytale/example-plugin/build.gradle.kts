repositories {
    maven("https://maven.hytale.com/pre-release")
}

dependencies {
    compileOnly(libs.hytale.server)
    implementation(projects.hytale)
}

tasks.shadowJar {
    // optionally relocate faststats
    relocate("dev.faststats", "com.example.utils.faststats")
}
