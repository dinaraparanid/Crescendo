plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.paranid5.crescendo.system.services"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

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
    implementation(project(":core:media"))
    implementation(project(":core:utils"))
    implementation(project(":core:resources"))
    implementation(project(":data"))
    implementation(project(":system:common"))
    implementation(project(":system:receivers"))
    api(project(":system:services:common"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.extensions)

    implementation(libs.ktor.client.core)
    implementation(libs.koin.androidx.compose)
    implementation(libs.arrow.fx.coroutines)

    implementation(libs.yt.url.extractor.kt)
    implementation(libs.ytdl.lib)
}