plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinter)
}

android {
    namespace = "com.paranid5.crescendo.system.services"
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
    implementation(projects.core.media) // TODO: убрать
    implementation(projects.core.utils)
    implementation(projects.core.resources)

    api(projects.domain.caching)
    api(projects.domain.files)

    implementation(projects.data) // TODO: убрать

    implementation(projects.system.common)
    implementation(projects.system.receivers)
    api(projects.system.services.common)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.extensions)

    implementation(libs.ktor.client.core)
    implementation(libs.koin.androidx.compose)
    implementation(libs.bundles.arrow)

    implementation(libs.yt.url.extractor.kt)
    implementation(libs.ytdl.lib)
}
