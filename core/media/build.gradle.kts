plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.paranid5.crescendo.core.media"
    compileSdk = 34

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    api(project(":core:common"))
    api(project(":core:resources"))
    api(project(":core:utils"))

    api(project(":domain:audio_effects"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.palette.ktx)
    implementation(libs.material)

    implementation(libs.coil.compose)

    implementation(libs.kotlinx.collections.immutable)

    implementation(libs.arrow.fx.coroutines)

    implementation(libs.mobile.ffmpeg.full)
    implementation(libs.jaudiotagger)

    implementation(libs.yt.url.extractor.kt)
}