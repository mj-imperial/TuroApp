pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()  // for KSP, Kotlin, Hilt plugins
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TuroMobileApp"
include(":app")