import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinter)
    id("kotlin-parcelize")
}

// kotlinter {
//    ktlintVersion = "1.8.0"
// }

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }

    dependencies {
        implementation(projects.core.common)
        implementation(projects.core.media)
        implementation(projects.core.impl)
        implementation(projects.core.ui)
        implementation(projects.core.resources)
        implementation(projects.core.utils)

        implementation(projects.data)

        implementation(projects.feature.audioEffects)
        implementation(projects.feature.cache)
        implementation(projects.feature.currentPlaylist)
        implementation(projects.feature.metaEditor)
        implementation(projects.feature.play.main)
        implementation(projects.feature.play.tracks)
        implementation(projects.feature.playing)
        implementation(projects.feature.preferences)
        implementation(projects.feature.splash)
        implementation(projects.feature.stream.main)
        implementation(projects.feature.trimmer)

        implementation(projects.system.common)
        implementation(projects.system.receivers)
        implementation(projects.system.services.stream)
        implementation(projects.system.services.track)
        implementation(projects.system.services.videoCache)
        implementation(projects.system.worker.trimmer)

        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.lifecycle.extensions)
        implementation(libs.androidx.lifecycle.viewmodel.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.tooling.preview)
        implementation(libs.androidx.material)
        implementation(libs.androidx.material3)
        implementation(libs.androidx.navigation.compose)
        implementation(libs.androidx.media)
        implementation(libs.androidx.media3.exoplayer)
        implementation(libs.androidx.media3.exoplayer.dash)
        implementation(libs.androidx.media3.exoplayer.hls)
        implementation(libs.androidx.media3.exoplayer.rtsp)
        implementation(libs.androidx.media3.exoplayer.smoothstreaming)
        implementation(libs.androidx.media3.ui)
        implementation(libs.androidx.media3.session)
        implementation(libs.androidx.datastore.preferences)
        implementation(libs.androidx.constraintlayout.compose)
        implementation(libs.androidx.palette.ktx)

        implementation(libs.koin.androidx.compose)
        implementation(libs.koin.test)

        implementation(libs.ktor.client.core)

        implementation(libs.coil.compose)

        implementation(libs.arrow.fx.coroutines)

        implementation(libs.kotlin.test)
        implementation(libs.kotlin.test.junit)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlinx.collections.immutable)

        implementation(libs.jaudiotagger)

        implementation(libs.isoparser)

        implementation(libs.compose.markdown)

        implementation(libs.amplituda)

        implementation(libs.yt.url.extractor.kt)
        implementation(libs.ytdl.lib)

        implementation(libs.timber)

        implementation(
            files(
                "libs/jave-1.0.2.jar",
                "libs/ffmpeg-kit-full-gpl-6.0-2.LTS.aar",
                "libs/audiovisualizer-0.9.2.aar",
            ),
        )
    }
}

android {
    namespace = "com.paranid5.crescendo"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.paranid5.crescendo"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "0.4.0.0"
        ndkVersion = "29.0.13113456"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.directories.add("libs")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        prefab = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/INDEX.LIST"
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

tasks.configureEach {
    val taskName = name

    if (taskName == "assembleDebug" || taskName == "assembleRelease") {
        dependsOn("testDebugUnitTest")
    }
}

tasks.check {
    dependsOn("installKotlinterPrePushHook")
}
