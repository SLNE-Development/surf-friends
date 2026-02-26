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
}

dependencies {
    api(project(":surf-friends-core"))
}