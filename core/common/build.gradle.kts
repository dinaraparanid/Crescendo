plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    api(libs.kotlinx.collections.immutable)
    api(libs.kotlinx.serialization.json)
    api(libs.arrow.fx.coroutines)
}