plugins {
    id("org.gradle.toolchains.foojay-resolver-convention").version("1.0.0")
}

rootProject.name = "dev-kits"
include("bukkit")
include("bungeecord")
include("core")
include("velocity")