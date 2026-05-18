plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services) apply false
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
    }
}

android {
    namespace = "com.abplus.k2a2recorder"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.abplus.k2a2recorder"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "1.0.0"

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
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    testImplementation(libs.junit)

    // Choose one of the following:
    // Material Design 3
    implementation(libs.androidx.compose.material3)
    // or Material Design 2
    implementation(libs.androidx.compose.material)
    // or skip Material Design and build directly on top of foundational components
    implementation(libs.androidx.compose.foundation)
    // or only import the main APIs for the underlying toolkit systems,
    // such as input and measurement/layout
    implementation(libs.androidx.compose.ui)

    // Android Studio Preview support
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // UI Tests
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Optional - Included automatically by material, only add when you need
    // the icons but not the material library (e.g. when using Material3 or a
    // custom design system based on Foundation)
    implementation(libs.androidx.compose.material.icons.core)
    // Optional - Add full set of material icons
    implementation(libs.androidx.compose.material.icons.extended)
    // Optional - Add window size utils
    implementation(libs.androidx.compose.material3.window.size)

    // Optional - Integration with activities
    implementation(libs.androidx.activity.compose)
    // Optional - Integration with ViewModels
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Optional - Integration with LiveData
    implementation(libs.androidx.compose.runtime.livedata)
    // Optional - Integration with RxJava
    implementation(libs.androidx.compose.runtime.rxjava2)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.health.connect.client)
    implementation(platform(libs.firebase.bom))
}
