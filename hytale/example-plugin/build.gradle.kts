repositories {
    maven("https://maven.hytale.com/pre-release")
}

dependencies {
    compileOnly("com.hypixel.hytale:Server:2026.02.06-0baf7c5aa")
    implementation(project(":hytale"))
}

tasks.shadowJar {
    // optionally relocate faststats
    relocate("dev.faststats", "com.example.utils.faststats")
}
