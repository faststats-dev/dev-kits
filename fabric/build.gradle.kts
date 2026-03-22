val moduleName by extra("dev.faststats.fabric")

plugins {
    alias(libs.plugins.gradle.minecraft.fabric.loom)
}

dependencies {
    api(projects.core)

    mappings(loom.officialMojangMappings())

    minecraft(libs.minecraft.mojang)

    modCompileOnly(libs.minecraft.fabric.api)

    modImplementation(libs.minecraft.fabric.loader)
}
