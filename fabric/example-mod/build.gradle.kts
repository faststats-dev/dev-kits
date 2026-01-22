plugins {
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT"
}

loom {
    splitEnvironmentSourceSets()
}

dependencies {
    implementation(project(":fabric"))
    mappings(loom.officialMojangMappings())
    minecraft("com.mojang:minecraft:1.21.11")
    modImplementation("net.fabricmc:fabric-loader:0.18.4")
}

tasks.jar {
    from(project(":fabric").tasks.jar)
    from(project(":core").tasks.jar)
}