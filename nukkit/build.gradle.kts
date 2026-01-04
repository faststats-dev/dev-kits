val moduleName by extra("dev.faststats.nukkit")

repositories {
    maven("https://repo.opencollab.dev/maven-releases")
    maven("https://repo.opencollab.dev/maven-snapshots")
}

dependencies {
    api(project(":core"))
    compileOnly("cn.nukkit:nukkit:1.0-SNAPSHOT")
}
