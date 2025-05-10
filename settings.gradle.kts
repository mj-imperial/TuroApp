pluginManagement {
    repositories {
        gradlePluginPortal()  // for KSP, Kotlin, Hilt plugins
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TuroMobileApp"
include(":app")
