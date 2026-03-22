val moduleName by extra("dev.faststats.sponge")

repositories {
    maven("https://repo.spongepowered.org/repository/maven-public/")
}

dependencies {
    api(projects.core)

    compileOnly(libs.minecraft.sponge)
}
