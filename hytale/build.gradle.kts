val moduleName by extra("dev.faststats.hytale")

repositories {
    maven("https://maven.hytale.com/pre-release")
}

dependencies {
    api(project(":core"))
    compileOnly("com.hypixel.hytale:Server:2026.05.07-5efa15f6d")
}
