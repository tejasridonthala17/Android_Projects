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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// Redirect build directories outside OneDrive to prevent Windows file locking issues
val tempBuildDir = java.io.File(System.getProperty("java.io.tmpdir"), "scientificcalculator_build")
gradle.beforeProject {
    layout.buildDirectory.set(java.io.File(tempBuildDir, project.name))
}

rootProject.name = "scientific calculator"
include(":app")
