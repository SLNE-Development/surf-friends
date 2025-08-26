plugins {
    id("dev.slne.surf.surfapi.gradle.core")
}

dependencies {
    api(project(":surf-friends-api"))
}

kotlin {
    jvmToolchain(24)
}