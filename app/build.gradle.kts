plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "fr.isen.beaule.androiderestaurant"
    compileSdk = 34

    defaultConfig {
        applicationId = "fr.isen.beaule.androiderestaurant"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.volley)
    implementation(libs.androidx.tools.core)
    implementation ("com.google.accompanist:accompanist-pager:0.24.7-alpha")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("io.coil-kt:coil:1.4.0") // Vérifiez la dernière version sur https://github.com/coil-kt/coil
    implementation ("androidx.compose.ui:ui:1.0.5") // replace with the latest version
    implementation ("androidx.compose.material:material:1.0.5") // replace with the latest version
    implementation ("androidx.compose.ui:ui-tooling:1.0.5") // replace with the latest version
    implementation ("io.coil-kt:coil-compose:1.4.0")
    implementation ("androidx.compose.material:material:1.1.0") // Vérifiez pour la dernière version
    implementation ("androidx.compose.ui:ui-tooling:1.1.0")
    implementation(libs.material) // Coil pour Compose
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}