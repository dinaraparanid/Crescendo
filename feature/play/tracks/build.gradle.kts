plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinter)
    id("kotlin-parcelize")
}

android {
    namespace = "com.paranid5.crescendo.feature.play.tracks"
    compileSdk = 37

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.impl)
    implementation(projects.core.media)
    implementation(projects.core.ui)
    implementation(projects.core.utils)
    implementation(projects.core.resources)

    implementation(projects.domain.currentPlaylist)
    implementation(projects.domain.playback)
    implementation(projects.domain.tracks)

    implementation(projects.system.services.track)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.palette.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material)
    implementation(libs.material)

    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.koin.androidx.compose)
    implementation(libs.coil.compose)
    implementation(libs.bundles.arrow)
}
