plugins {
    id("dev.slne.surf.surfapi.gradle.velocity")
}

surfVelocityApi {
    withSurfRedis()
    withCoreVelocity()
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
    runtimeOnly(project(":surf-friends-backend"))
}