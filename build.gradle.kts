// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {

        classpath(libs.gradle)
        classpath(libs.kotlin.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}