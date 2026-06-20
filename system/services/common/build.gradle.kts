plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinter)
}

android {
    namespace = "com.paranid5.system.services.common"
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
    api(projects.core.common)
    api(projects.core.impl)
    api(projects.core.media)
    api(projects.core.resources)

    api(projects.domain.audioEffects)

    api(projects.system.common)

    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.androidx.lifecycle.extensions)
    api(libs.androidx.media)
    api(libs.androidx.media3.session)

    api(libs.koin.androidx.compose)
}
