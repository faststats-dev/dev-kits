repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

tasks.compileJava {
    options.release.set(17)
}

configurations.compileClasspath {
    attributes {
        attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
    }
}

dependencies {
    api(projects.core)

    compileOnly(libs.minecraft.paper)
}
