plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.paranid5.crescendo.system.services.track"
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
    implementation(project(":core:common"))
    implementation(project(":core:impl"))
    implementation(project(":core:media"))
    implementation(project(":core:utils"))
    implementation(project(":core:resources"))

    implementation(project(":domain:tracks"))
    implementation(project(":domain:playback"))
    implementation(project(":domain:current_playlist"))

    implementation(project(":system:common"))
    implementation(project(":system:receivers"))
    api(project(":system:services:common"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.media)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.session)

    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.koin.androidx.compose)
    implementation(libs.bundles.arrow)
}