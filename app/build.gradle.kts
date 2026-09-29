import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "lgbt.kami"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "lgbt.kami"
        minSdk = 31
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0-Mabel"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load Supabase configuration from environment variables or local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }

        val supabaseUrl = System.getenv("SUPABASE_URL") ?: localProperties.getProperty("SUPABASE_URL") ?: "\"\""
        val supabaseAnonKey = System.getenv("SUPABASE_ANON_KEY") ?: localProperties.getProperty("SUPABASE_ANON_KEY") ?: "\"\""
        val googleWebClientId = System.getenv("GOOGLE_WEB_CLIENT_ID") ?: localProperties.getProperty("GOOGLE_WEB_CLIENT_ID") ?: "\"\""

        buildConfigField("String", "SUPABASE_URL", if (supabaseUrl.startsWith("\"")) supabaseUrl else "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", if (supabaseAnonKey.startsWith("\"")) supabaseAnonKey else "\"$supabaseAnonKey\"")
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", if (googleWebClientId.startsWith("\"")) googleWebClientId else "\"$googleWebClientId\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "SYMBOL_TABLE"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    @Suppress("AvoidDuplicateDependencies")
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.navigation.compose)
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.gotrue)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.core)
    testImplementation(libs.junit)
    @Suppress("AvoidDuplicateDependencies")
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}