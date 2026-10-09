plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.util.skinnable.skinnable.main"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.util.skinnable.main"
        minSdk = 23
        targetSdk = 37
        versionCode = 1
        versionName = "1.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
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

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.espresso.core)
}