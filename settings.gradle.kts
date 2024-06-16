pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Diplom"
include(":app")
include(":data")
include(":libApp")
include(":domain")
include(":feature")
include(":feature:rss")
include(":feature:rsa")
include(":feature:translateML")
include(":feature:speachTextCompose")
include(":feature:telegramApi")
