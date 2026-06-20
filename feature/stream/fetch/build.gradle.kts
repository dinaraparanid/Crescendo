plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinter)
    id("kotlin-parcelize")
}

android {
    namespace = "com.paranid5.crescendo.feature.stream.fetch"
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
    implementation(projects.core.ui)
    implementation(projects.core.utils)
    implementation(projects.core.resources)
    implementation(projects.core.media)

    implementation(projects.domain.image)
    implementation(projects.domain.playback)
    implementation(projects.domain.stream)

    implementation(projects.feature.cache)
    implementation(projects.feature.metadata)

    implementation(projects.system.services.stream)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.ui)
    implementation(libs.bundles.material)

    implementation(libs.koin.androidx.compose)

    implementation(libs.coil.compose)
    implementation(libs.compose.markdown)
}
