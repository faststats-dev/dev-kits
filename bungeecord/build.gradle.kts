val moduleName by extra("dev.faststats.bungee")

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(projects.core)

    compileOnly(libs.minecraft.bungeecord)
}
