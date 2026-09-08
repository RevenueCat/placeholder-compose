pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    // The Kotlin js/wasmJs plugin registers the Node and Yarn distribution repositories on the
    // project, so FAIL_ON_PROJECT_REPOS and PREFER_SETTINGS both break :kotlinNodeJsSetup.
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "placeholder"
include(":app")
include(":placeholder")
include(":sample:shared")
include(":sample:web")
