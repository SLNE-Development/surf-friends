import dev.slne.surf.microservice.gradle.plugin.rabbit.RabbitModule

plugins {
    id("dev.slne.surf.api.gradle.core")
    id("dev.slne.surf.microservice")
}

surfCoreApi {
    withSurfDatabaseR2dbc("2.3.1", "dev.slne.surf.friends.libs.database")
}

surfMicroservice {
    withMicroserviceApi()
    withRabbitModule(RabbitModule.SERVER_API, true)
}

dependencies {
    api(projects.surfFriendsCore.surfFriendsCoreCommon)
}
