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

        // Inicializa o leitor de propriedades
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")

        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }

        // Lê as variáveis do arquivo (com valores padrão para segurança)
        val baseUrl = localProperties.getProperty("BASE_URL") ?: "\"http://10.0.2.2:3001\""

        // Vendas e Auth
        val endpointVendas = localProperties.getProperty("ENDPOINT_VENDAS") ?: "/sales/create"
        val endpointVendasEmail = localProperties.getProperty("ENDPOINT_VENDAS_EMAIL") ?: "/sales/email"
        val endpointAuthLogin = localProperties.getProperty("ENDPOINT_AUTH_LOGIN") ?: "/auth/login"
        val endpointAuthVerify = localProperties.getProperty("ENDPOINT_AUTH_VERIFY") ?: "/auth/verify"

        // Reprocessamento (NOVOS)
        val endpointReprocessCreate = localProperties.getProperty("ENDPOINT_REPROCESS_CREATE") ?: "/reprocessing"
        val endpointReprocessList = localProperties.getProperty("ENDPOINT_REPROCESS_LIST") ?: "/reprocessing/list"
        val endpointReprocessApprove = localProperties.getProperty("ENDPOINT_REPROCESS_APPROVE") ?: "/reprocessing/approve"

        // Injeta as variáveis no Java (BuildConfig)
        buildConfigField("String", "BASE_URL", baseUrl)

        buildConfigField("String", "ENDPOINT_VENDAS", "\"$endpointVendas\"")
        buildConfigField("String", "ENDPOINT_VENDAS_EMAIL", "\"$endpointVendasEmail\"")

        buildConfigField("String", "ENDPOINT_AUTH_LOGIN", "\"$endpointAuthLogin\"")
        buildConfigField("String", "ENDPOINT_AUTH_VERIFY", "\"$endpointAuthVerify\"")

        buildConfigField("String", "ENDPOINT_REPROCESS_CREATE", "\"$endpointReprocessCreate\"")
        buildConfigField("String", "ENDPOINT_REPROCESS_LIST", "\"$endpointReprocessList\"")
        buildConfigField("String", "ENDPOINT_REPROCESS_APPROVE", "\"$endpointReprocessApprove\"")

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
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(libs.swiperefreshlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}