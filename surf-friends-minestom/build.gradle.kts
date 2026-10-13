import dev.slne.surf.api.gradle.util.slneReleases
import dev.slne.surf.microservice.gradle.plugin.rabbit.RabbitModule

plugins {
    id("dev.slne.surf.api.gradle.minestom")
    id("dev.slne.surf.microservice")
}

surfMinestomApi {
    withCoreMinestom()
    withSurfRedis()
}

minestomPluginFile {
    main = "dev.slne.surf.friends.minestom.FriendsMinestomPlugin"
    authors = listOf("twisti")

    pluginDependencies {
        register("surf-rabbitmq-minestom")
        register("surf-redis-minestom")
        register("surf-settings-minestom")
    }
}

surfMicroservice {
    withRabbitModule(RabbitModule.CLIENT_API)
}

dependencies {
    api(projects.surfFriendsCore.surfFriendsCoreClient)
}


publishing {
    repositories {
        slneReleases()
    }
}
