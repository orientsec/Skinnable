plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlinCompose)
}

android {
    namespace = "com.util.skinnable.skinnable.main"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.util.skinnable.main"
        minSdk = 23
        targetSdk = 37
        versionCode = 230
        versionName = "2.3.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    sourceSets {
        getByName("main") {
            res.srcDirs("src/main/res", "src/main/res-night")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.androidx.legacy.support.v4)
    implementation(project(":librarys:skin-base"))

    implementation(libs.androidx.appcompat)
    implementation(project(":librarys:skin-compat"))

    implementation(libs.material)
    implementation(project(":librarys:skin-constraintlayout"))

    implementation(libs.androidx.constraintlayout)
    implementation(project(":librarys:skin-material-design"))
    
    implementation(libs.coroutines.android)
    implementation(libs.lifecycle.runtime.ktx)

    implementation(libs.compose.runtime)
    implementation(libs.composeUi)
    implementation(libs.composeFoundation)
    implementation(libs.composeMaterial3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.espresso.core)
}