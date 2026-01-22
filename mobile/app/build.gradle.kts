import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.br.salesbuddy"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.br.salesbuddy"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"


        val localProperties = Properties()

        val localPropertiesFile = rootProject.file("local.properties")


        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }


        val baseUrl = localProperties.getProperty("BASE_URL") ?: "\"http://10.0.2.2:3001\""


        buildConfigField("String", "BASE_URL", baseUrl)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
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
    implementation(libs.swiperefreshlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}