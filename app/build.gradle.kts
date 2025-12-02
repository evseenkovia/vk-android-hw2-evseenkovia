plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.evseenkovia.vk_android_hw2_evseenkovia"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.evseenkovia.vk_android_hw2_evseenkovia"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.7.3"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.material3)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // Fragment KTX
    implementation("androidx.fragment:fragment-ktx:1.8.9")

    // Coil
        // Core
        implementation("io.coil-kt:coil:2.2.2")
        // Compose
        implementation("io.coil-kt:coil-compose:2.2.2")

    implementation(libs.androidx.fragment)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Coil для загрузки изображений / GIF
    implementation(libs.coil.kt.coil.compose)
    implementation(libs.coil.gif)

    // Room для кэширования
    val room_version = "2.8.4"
    implementation("androidx.room:room-runtime:${room_version}")
    ksp("androidx.room:room-compiler:$room_version")


}
