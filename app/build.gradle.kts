plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.tp_final_v1"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.tp_final_v1"
        minSdk = 24
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
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.espresso.intents)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.android.material:material:1.10.0");
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.libraries.places:places:3.4.0")

    // Dependencias normales de la app
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")


    // TESTS UNITARIOS (corren en escritorio, sin Android)
    testImplementation("junit:junit:4.13.2")

    // TESTS INSTRUMENTADOS (corren en el dispositivo)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}