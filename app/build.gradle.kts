plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id ("com.google.gms.google-services")
}

android {
    namespace = "com.example.snapcash"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.snapcash"
        minSdk = 26
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
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.places)
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation ("androidx.paging:paging-compose:3.3.0")
    implementation ("androidx.paging:paging-runtime-ktx:3.3.0")

    ksp("androidx.room:room-compiler:2.5.0")
    implementation ("com.google.dagger:hilt-android:2.51")
    ksp("com.google.dagger:hilt-android-compiler:2.51")
    implementation ("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.compose.foundation:foundation:1.5.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Sesuaikan versi Kotlin dan KSP dengan versi yang sesuai
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.23")

    implementation ("androidx.datastore:datastore-preferences:1.1.1")

    implementation("androidx.navigation:navigation-compose:2.7.5")

    implementation("com.google.android.material:material:1.9.0")

    implementation("io.coil-kt.coil3:coil-compose:3.1.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")  // ✅ Required for CameraX
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.accompanist:accompanist-permissions:0.31.1-alpha")
    implementation(platform("androidx.compose:compose-bom:2025.02.00"))
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("com.google.firebase:firebase-auth-ktx:22.1.1")
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))

    implementation ("co.yml:ycharts:2.1.0")

}