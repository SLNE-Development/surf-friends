plugins {
    id("dev.slne.surf.surfapi.gradle.paper-raw")
}

surfRawPaperApi {
    withCorePaper()
    withSurfRedis()
}

dependencies {
    api(project(":surf-friends-api"))
}