plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Android Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation("com.google.android.material:material:1.6.0")


    // Architecture Components
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)

    // Networking (Add these new dependencies)
    implementation("com.squareup.okhttp3:okhttp:4.10.0") // HTTP client
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // REST client
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // GSON converter
    implementation("com.google.code.gson:gson:2.9.1") // JSON parsing

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Optional but recommended
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0") // Pull-to-refresh
    implementation("com.github.bumptech.glide:glide:4.13.2") // Image loading
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.2")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.google.code.gson:gson:2.8.9")
    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))

// Firebase Realtime Database
    implementation("com.google.firebase:firebase-database-ktx")

// (Optional but recommended)
    implementation("com.google.firebase:firebase-analytics-ktx")

    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")

    implementation("com.google.android.material:material:1.9.0")

    implementation("com.google.firebase:firebase-auth-ktx")

    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.androidx.gridlayout)
}