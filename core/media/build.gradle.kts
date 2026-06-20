plugins {
    alias(libs.plugins.android.library)
    id("kotlin-parcelize")
}

android {
    namespace = "com.paranid5.crescendo.core.media"
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
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    api(projects.core.common)
    api(projects.core.resources)
    api(projects.core.utils)

    api(projects.domain.audioEffects)
    implementation(projects.domain.metadata)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.palette.ktx)
    implementation(libs.material)

    implementation(libs.coil.compose)

    implementation(libs.kotlinx.collections.immutable)

    implementation(libs.bundles.arrow)

    implementation(libs.jaudiotagger)

    implementation(libs.yt.url.extractor.kt)

    implementation(libs.smart.exception.java)

    implementation(
        files(
            "../../app/libs/jave-1.0.2.jar",
            "../../app/libs/ffmpeg-kit-full-gpl-6.0-2.LTS.aar",
            "../../app/libs/audiovisualizer-0.9.2.aar",
        )
    )
}