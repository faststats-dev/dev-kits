val moduleName by extra("dev.faststats.hytale")

dependencies {
    api(project(":core"))
    compileOnly(files("HytaleServer.jar"))
}