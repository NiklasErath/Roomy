plugins {
    // Applies the Android application plugin via version catalog alias
    alias(libs.plugins.android.application)

    // Applies the Kotlin Android plugin (for Android apps) via version catalog alias
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)


    // Applies Kotlin Serialization plugin (explicitly, with version)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0-RC1"

    /*
      If you ever need a *standalone* plugin statement for Compose in the *future*
      (e.g., Kotlin 2.0 or Compose Multiplatform), it might look like:
      id("org.jetbrains.compose") version "<compose_version>"
      But for standard Android + Compose, this is not needed right now.
    */
}

android {
    namespace = "com.example.roomy"
    compileSdk = 34

    buildFeatures {

        buildConfig = true
    }
    buildTypes {
        release {
            // Use the OPENAI_API_KEY from gradle.properties
            buildConfigField("String", "OPENAI_API_KEY", "\"${findProperty("OPENAI_API_KEY")}\"")
        }
        debug {
            // Optionally, you can also add a debug API key or a different value
            buildConfigField("String", "OPENAI_API_KEY", "\"${findProperty("OPENAI_API_KEY")}\"")
        }
    }


    defaultConfig {
        applicationId = "com.example.roomy"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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

    // Set Java compatibility
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // Kotlin options
    kotlinOptions {
        jvmTarget = "1.8"
    }

    // Enable Jetpack Compose in your module
    buildFeatures {
        compose = true
    }

    // Specify the Compose compiler extension (matches Compose version)
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    // Exclude certain META-INF files to avoid packaging conflicts
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ----- Kotlin Serialization -----
    // (Only need ONE line for the JSON library)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")


    // ----- Jetpack Compose -----
    // Use the Compose BOM for version alignment
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation in Compose
    implementation(libs.androidx.navigation.compose)

    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ----- Compose debugging/testing -----
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ----- Supabase -----
    // BOM ensures all Supabase modules use a consistent version
    implementation(platform("io.github.jan-tennert.supabase:bom:3.0.1"))
    // Individual Supabase modules
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    // If you need more, add them here

    // ----- Ktor for networking on Android -----
    implementation("io.ktor:ktor-client-android:3.0.0")
}
