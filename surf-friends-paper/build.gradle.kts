plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

surfPaperPluginApi {
    withCorePaper()

    mainClass("dev.slne.surf.friends.paper.SurfFriendsPaper")
}

dependencies {
    api(projects.surfFriendsCore.surfFriendsCoreClient)
}

