repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.21-R0.5-SNAPSHOT")
    implementation(project(":bungeecord"))
}

tasks.shadowJar {
    // optionally relocate faststats
    relocate("dev.faststats", "com.example.utils.faststats")
}
