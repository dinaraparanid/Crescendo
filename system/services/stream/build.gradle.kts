plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.paranid5.crescendo.system.services.stream"
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
    implementation(project(":core:common"))
    implementation(project(":core:impl"))
    implementation(project(":core:media"))
    implementation(project(":core:utils"))
    implementation(project(":core:resources"))

    implementation(project(":domain:metadata"))
    implementation(project(":domain:playback"))
    implementation(project(":domain:stream"))

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
    implementation(libs.ktor.client.core)
    implementation(libs.bundles.arrow)

    implementation(libs.yt.url.extractor.kt)
    implementation(libs.ytdl.lib)
}