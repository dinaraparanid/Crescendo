plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.serialization)
    id("kotlin-parcelize")
}

android {
    namespace = "com.paranid5.crescendo.domain.metadata"
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
    api(projects.core.common) // Track
    api(projects.domain.image)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.media)

    api(libs.yt.url.extractor.kt)
}
