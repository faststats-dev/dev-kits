plugins {
    alias(libs.plugins.gradle.minecraft.fabric.loom)
}

dependencies {
    implementation(projects.fabric)

    mappings(loom.officialMojangMappings())

    minecraft(libs.minecraft.mojang)

    modImplementation(libs.minecraft.fabric.loader)
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(
        project(projects.fabric.path)
            .sourceSets
            .main
            .map(SourceSet::getOutput),
        project(projects.core.path)
            .sourceSets
            .main
            .map(SourceSet::getOutput)
    )
}