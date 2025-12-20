java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks.compileJava {
    options.release.set(25)
}

dependencies {
    api(project(":core"))
    compileOnly("net.minestom:minestom:2025.12.20-1.21.11")
}