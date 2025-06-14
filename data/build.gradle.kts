plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.sqlDelight)
}

sqldelight {
    databases {
        create("CurrentPlaylist") {
            packageName.set("com.paranid5.crescendo.data")
        }
    }
}

android {
    namespace = "com.paranid5.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isJniDebuggable = false
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
        freeCompilerArgs = listOf("-Xcontext-receivers")
    }
}

dependencies {
    api(project(":core:common"))
    api(project(":core:media")) // TODO убрать
    api(project(":core:resources"))
    api(project(":core:utils"))

    api(project(":domain:audio_effects"))
    api(project(":domain:current_playlist"))
    api(project(":domain:files"))
    api(project(":domain:genius"))
    api(project(":domain:github"))
    api(project(":domain:image"))
    api(project(":domain:metadata"))
    api(project(":domain:playback"))
    api(project(":domain:stream"))
    api(project(":domain:tags"))
    api(project(":domain:tracks"))
    api(project(":domain:waveform"))
    api(project(":domain:web"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.koin.androidx.compose)
    implementation(libs.bundles.arrow)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.content.negotiation)

    implementation(libs.sqldelight.android.driver)
    implementation(libs.sqldelight.coroutines.extensions)

    implementation(libs.coil.compose)
    implementation(libs.jaudiotagger)
    implementation(libs.yt.url.extractor.kt)
}
