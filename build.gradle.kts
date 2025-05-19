plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.tbssmarthub"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tbssmarthub"
        minSdk = 27
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
    buildscript {
        repositories {
            google()
            mavenCentral()
        }
        dependencies {
            classpath("com.google.gms:google-services:4.4.0")
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
        compose = true
    }
}

dependencies {

    // --- Core AndroidX dependencies ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    //Icons
    implementation(libs.androidx.material.icons.extended)


    // --- Jetpack Compose platform BOM ---
    implementation(platform(libs.androidx.compose.bom))

    // --- Jetpack Compose UI components ---
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // --- Jetpack Navigation for Compose ---
    implementation(libs.navigation.compose)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Import the Firebase BoM
    implementation( platform(libs.firebase.bom.v3231))


    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation(libs.firebase.analytics)


    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries

    // Firebase products
    implementation(libs.play.services.auth)
    implementation(libs.firebase.auth.ktx)

    //Add coil
    implementation(libs.coil.compose)

    // Firebase BoM
    implementation(libs.firebase.bom.v3250)

    // Firebase Authentication
    implementation(libs.google.firebase.auth.ktx)

    // Firebase Firestore
    implementation(libs.google.firebase.firestore.ktx)

    // CSV parsing library
    implementation(libs.opencsv)


}

