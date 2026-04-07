pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://reposilite.slne.dev/public/") { name = "public" }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.slne.surf.api.gradle.settings") version "+"
}

include("surf-friends-api")
include("surf-friends-core:surf-friends-core-common")
include("surf-friends-core:surf-friends-core-client")
include("surf-friends-velocity")
include("surf-friends-microservice")
