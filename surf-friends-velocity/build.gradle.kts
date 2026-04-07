plugins {
    id("dev.slne.surf.api.gradle.velocity")
}

surfVelocityApi {
    withCoreVelocity()
}

velocityPluginFile {
    authors = listOf("red")

    pluginDependencies {
        register("surf-settings-velocity")
    }
}

dependencies {
    api(projects.surfFriendsCore.surfFriendsCoreClient)
}