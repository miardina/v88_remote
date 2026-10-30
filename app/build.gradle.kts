
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.v88remote"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.v88remote"
        minSdk = 23
        targetSdk = 28
        versionCode = 1
        versionName = "1.0"
    }

    // Configuration Java 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Configuration Kotlin 17
    kotlinOptions {
        jvmTarget = "17"
    }
}
