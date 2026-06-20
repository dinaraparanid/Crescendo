plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinter)
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
    implementation(projects.core.utils)
    implementation(projects.core.resources)

    implementation(projects.domain.metadata)
    implementation(projects.domain.playback)
    implementation(projects.domain.stream)

    implementation(projects.system.common)
    implementation(projects.system.receivers)
    api(projects.system.services.common)

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
