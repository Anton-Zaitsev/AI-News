// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android) apply false
    alias(libs.plugins.android.jvm) apply false
    alias(libs.plugins.hiddenSecrets) apply false
    alias(libs.plugins.ksp) apply false
}
buildscript {
    dependencies {
        classpath(libs.gradle)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.hilt.android.gradle.plugin)
    }
}
