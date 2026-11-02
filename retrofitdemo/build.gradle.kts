// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

allprojects {
    layout.buildDirectory.set(File(System.getProperty("user.home"), ".android_builds/${rootProject.name}/${project.name}"))
}