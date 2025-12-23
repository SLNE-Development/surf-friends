import dev.slne.surf.surfapi.gradle.util.slneReleases

plugins {
    `maven-publish`
}

allprojects {
    group = "dev.slne.surf.friends"
    version = findProperty("version") as String
}

buildscript {
    repositories {
        gradlePluginPortal()
        maven("https://repo.slne.dev/repository/maven-public/") { name = "maven-public" }
    }
    dependencies {
        classpath("dev.slne.surf:surf-api-gradle-plugin:1.21.11+")
    }
}

subprojects {
    afterEvaluate {
        plugins.withType<PublishingPlugin> {
            configure<PublishingExtension> {
                repositories {
                    slneReleases()
                }
            }
        }
    }
}
