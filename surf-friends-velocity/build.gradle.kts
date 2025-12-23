plugins {
    id("dev.slne.surf.surfapi.gradle.velocity")
}

velocityPluginFile {
    main = "dev.slne.surf.friends.velocity.SurfFriendsPlugin"
    name = "SurfFriends"
    id = "surf-friends"
    authors = listOf("red")
    version = "${rootProject.version}"

    pluginDependencies {
        register("commandapi")
    }
}

dependencies {
    api(project(":surf-friends-core"))
    implementation("dev.slne.surf:surf-redis:1.0.0-SNAPSHOT")
    runtimeOnly(project(":surf-friends-fallback"))
}