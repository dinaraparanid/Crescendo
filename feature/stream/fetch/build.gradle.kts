plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("kotlin-parcelize")
}

android {
    namespace = "com.paranid5.crescendo.feature.stream.fetch"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:impl"))
    implementation(project(":core:ui"))
    implementation(project(":core:utils"))
    implementation(project(":core:resources"))
    implementation(project(":core:media"))

    implementation(project(":domain:stream"))
    implementation(project(":domain:cover"))
    implementation(project(":domain:playback"))

    implementation(project(":feature:cache"))

    implementation(project(":system:services:stream"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material)
    implementation(libs.androidx.material3)
    implementation(libs.material)

    implementation(libs.koin.androidx.compose)

    implementation(libs.coil.compose)
    implementation(libs.compose.markdown)
}