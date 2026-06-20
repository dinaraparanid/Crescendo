plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.paranid5.crescendo.system.worker.trimmer"
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
    implementation(project(":core:common"))
    implementation(project(":core:media"))
    implementation(project(":core:resources"))

    api(project(":domain:files"))
    api(project(":domain:metadata"))
    api(project(":domain:tags"))

    implementation(project(":system:receivers"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    api(libs.androidx.work.work.runtime.ktx)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.koin.androidx.compose)
}