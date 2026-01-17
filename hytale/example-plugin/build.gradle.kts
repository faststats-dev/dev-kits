val libsDir: Directory = project(":hytale").layout.projectDirectory.dir("libs")
val hytaleServerJar: RegularFile = libsDir.file("HytaleServer.jar")

dependencies {
    compileOnly(files(hytaleServerJar))
    implementation(project(":hytale"))
}

tasks.shadowJar {
    // optionally relocate faststats
    relocate("dev.faststats", "com.example.utils.faststats")
}
