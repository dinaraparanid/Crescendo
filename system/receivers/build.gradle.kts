plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.paranid5.receiver"
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
    implementation(projects.core.impl)
    implementation(projects.core.media)
    implementation(projects.core.resources)

    implementation(projects.domain.caching)

    implementation(projects.system.common)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}