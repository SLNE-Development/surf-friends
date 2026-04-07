plugins {
    id("dev.slne.surf.api.gradle.core")
}

surfCoreApi {
    withSurfRedis()
    withCoreCommon()
}

dependencies {
    api(projects.surfFriendsCore.surfFriendsCoreCommon)
    compileOnlyApi("dev.slne.surf.settings:surf-settings-api:+")
}
