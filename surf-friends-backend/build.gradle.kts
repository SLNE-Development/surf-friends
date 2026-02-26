plugins {
    id("dev.slne.surf.surfapi.gradle.paper-raw")
}

dependencies {
    api(project(":surf-friends-core"))
}

surfRawPaperApi {
    withSurfDatabaseR2dbc("1.3.0", "dev.slne.surf.friends.libs")
    withSurfRedis()
}