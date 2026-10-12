plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinCompose)
}

android {
    namespace = "com.util.skin.library"
    compileSdk = 37

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.recyclerview)
    compileOnly(libs.compose.runtime)
    compileOnly(libs.composeUi)
    compileOnly(libs.composeFoundation)
    compileOnly(libs.composeMaterial3)
    implementation(libs.coroutines.android)
    implementation(libs.lifecycle.common)
    testImplementation(libs.junit)
    testImplementation(libs.compose.runtime)
    testImplementation(libs.composeUi)
    testImplementation(libs.composeFoundation)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.espresso.core)
}