buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }

    dependencies {
        classpath(libs.ktlint.compose) {
            exclude(group = "org.jetbrains.kotlin", module = "kotlin-compiler-embeddable")
        }
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.sqlDelight) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinter) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
