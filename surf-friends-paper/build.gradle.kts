plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.friends.paper.PaperMain")
    generateLibraryLoader(false)
    foliaSupported(true)

    withSurfRedis()
    withCorePaper()

    authors.add("red")
}

dependencies {
    api(project(":surf-friends-core"))
    runtimeOnly(project(":surf-friends-backend"))
}