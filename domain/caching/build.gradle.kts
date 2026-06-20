plugins {
    alias(libs.plugins.android.library)
    id("kotlin-parcelize")
}

android {
    namespace = "com.paranid5.crescendo.domain.caching"
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
    api(projects.core.resources)
    api(projects.domain.files)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}
