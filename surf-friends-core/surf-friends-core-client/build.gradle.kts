plugins {
    id("dev.slne.surf.api.gradle.paper-raw")
}

surfRawPaperApi {
    withSurfRedis()
    withCorePaper()
}


dependencies {
    api(projects.surfFriendsCore.surfFriendsCoreCommon)
    compileOnlyApi("dev.slne.surf.settings:surf-settings-api:+")
}
