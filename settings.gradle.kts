import java.net.URI
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Add it in your root settings.gradle.kts at the end of repositories:
        maven { url = URI("https://jitpack.io") }
    }
}

rootProject.name = "FileCleaner"
include(":app")
