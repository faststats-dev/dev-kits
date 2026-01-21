plugins {
    id("org.gradle.toolchains.foojay-resolver-convention").version("1.0.0")
}

rootProject.name = "dev-kits"
include("bukkit")
include("bukkit:example-plugin")
include("bungeecord")
include("core")
include("hytale")
include("hytale:example-plugin")
include("minestom")
include("nukkit")
include("sponge")
include("sponge:example-plugin")
include("velocity")