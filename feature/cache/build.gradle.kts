plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinter)
    id("kotlin-parcelize")
}

android {
    namespace = "com.paranid5.crescendo.cache"
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
    implementation(projects.core.resources)
    implementation(projects.core.ui)
    implementation(projects.core.utils)

    implementation(projects.domain.stream)

    implementation(projects.system.services.videoCache)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.material)

    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.koin.androidx.compose)
}
