plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

surfPaperPluginApi {
    withCorePaper()
    mainClass("dev.slne.surf.friends.paper.SurfFriendsPaper")

    serverDependencies {
        register("surf-settings-paper")
    }
}

dependencies {
    api(projects.surfFriendsCore.surfFriendsCoreClient)
}

