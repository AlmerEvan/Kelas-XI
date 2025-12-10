plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.example.firebasetutorialapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.firebasetutorialapp"
        minSdk = 26
        targetSdk = 36
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
            // Opsional: Tambahkan signingConfig untuk release build yang ditandatangani
            // signingConfig = signingConfigs.getByName("release")
        }
        
        debug {
            // Konfigurasi debug build standar
            isMinifyEnabled = false
        }
    }
    
    // Konfigurasi output APK dengan nama yang jelas untuk App Distribution
    // Uncomment jika ingin custom output filename
    // applicationVariants.all {
    //     outputs.all {
    //         val variant = this.variant
    //         val outputFileName = "app-${variant.name}-v${variant.versionName}.apk"
    //         (this as com.android.build.gradle.internal.api.ApkOutputFile).outputFileName = outputFileName
    //     }
    // }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = false
    }
}

dependencies {
    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    
    // Firebase Analytics (KTX for Kotlin)
    implementation("com.google.firebase:firebase-analytics-ktx:21.6.0")
    
    // Firebase Crashlytics (KTX for Kotlin)
    implementation("com.google.firebase:firebase-crashlytics-ktx:18.6.0")
    
    // Firebase Cloud Messaging (KTX for Kotlin)
    implementation("com.google.firebase:firebase-messaging-ktx")
    
    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}