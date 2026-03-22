repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.minecraft.velocity)

    annotationProcessor(libs.minecraft.velocity)

    implementation(projects.velocity)
}

tasks.shadowJar {
    // optionally relocate faststats
    relocate("dev.faststats", "com.example.utils.faststats")
}
