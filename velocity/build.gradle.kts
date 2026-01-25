repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(project(":core"))
    compileOnly("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")
}
