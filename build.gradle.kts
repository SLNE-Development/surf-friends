plugins {
    `maven-publish`
}

allprojects {
    group = "dev.slne"
    version = "1.7.0-SNAPSHOT"
}

buildscript {
    repositories {
        gradlePluginPortal()
        maven("https://repo.slne.dev/repository/maven-public/") { name = "maven-public" }
    }
    dependencies {
        classpath("dev.slne.surf:surf-api-gradle-plugin:1.21.4+")
    }
}