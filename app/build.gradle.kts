val kotlinVersion = "1.9.10"
plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    kotlin("android") version "1.9.10"
    kotlin("kapt") version "1.9.10"
}

android {
    namespace = "com.example.recipe"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.recipe"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.annotation)

    // Firebase BoM - manages versions of Firebase libraries
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.google.firebase:firebase-storage:20.2.1")

    // Firebase services dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    implementation("com.google.android.gms:play-services-base:18.1.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.github.bumptech.glide:glide:4.15.0")
    kapt("com.github.bumptech.glide:compiler:4.15.0")
}
