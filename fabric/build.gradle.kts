val moduleName by extra("dev.faststats.fabric")

plugins {
    id("fabric-loom") version ("1.15-SNAPSHOT")
}

dependencies {
    api(project(":core"))
    mappings(loom.officialMojangMappings())
    minecraft("com.mojang:minecraft:1.21.11")
    modCompileOnly("net.fabricmc.fabric-api:fabric-api:0.148.1+26.2")
    modImplementation("net.fabricmc:fabric-loader:0.19.2")
}
