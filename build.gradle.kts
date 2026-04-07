import dev.slne.surf.api.gradle.util.slneReleases

allprojects {
    group = "dev.slne.surf.friends"
    version = findProperty("version") as String
}

buildscript {
    repositories {
        gradlePluginPortal()
        maven("https://reposilite.slne.dev/public/") { name = "public" }
    }
    dependencies {
        classpath("dev.slne.surf.api:surf-api-gradle-plugin:+")
        classpath("dev.slne.surf.microservice:surf-microservice-gradle-plugin:+")
    }
}

subprojects {
    afterEvaluate {
        extensions.findByType<PublishingExtension>()?.apply {
            repositories {
                slneReleases()
            }
        }
    }
}
