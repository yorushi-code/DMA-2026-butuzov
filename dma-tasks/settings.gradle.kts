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

rootProject.name = "dma-tasks"

// :task2 is plain Java (runs on the JVM); :app is the Android app of task 3 and
// :legacy-analytics a stub library whose manifest the app has to override.
include(":task2", ":app", ":legacy-analytics")
