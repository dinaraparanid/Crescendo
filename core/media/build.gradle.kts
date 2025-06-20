plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.paranid5.crescendo.core.media"
    compileSdk = 35

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
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    api(project(":core:common"))
    api(project(":core:resources"))
    api(project(":core:utils"))

    api(project(":domain:audio_effects"))
    implementation(project(":domain:metadata"))

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

    implementation(files("../../app/libs/jave-1.0.2.jar", "../../app/libs/ffmpeg-kit-full-gpl-6.0-2.LTS.aar"))
}