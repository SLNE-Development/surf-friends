import dev.slne.surf.surfapi.gradle.util.registerSoft

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}


surfPaperPluginApi {
    mainClass("dev.slne.surf.friends.paper.PaperMain")
    generateLibraryLoader(false)
    foliaSupported(true)
    withCorePaper()
    withSurfRedis()

    authors.add("red")

    serverDependencies {
        registerSoft("surf-settings-paper")
    }
}

dependencies {
    api(project(":surf-friends-core"))
    runtimeOnly(project(":surf-friends-backend"))

    compileOnly("dev.slne.surf.settings:surf-settings-api:1.21.11-2.0.0-SNAPSHOT")
}