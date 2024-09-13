import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
/*
Alias name: alias
        Creation date: 31 Jul, 2024
Entry type: PrivateKeyEntry
        Certificate chain length: 1
Certificate[1]:
Owner: OU=gfgf, CN=vfgfg gfg
        Issuer: OU=gfgf, CN=vfgfg gfg
        Serial number: 1
Valid from: Wed Jul 31 22:48:15 IST 2024 until: Sun Jul 25 22:48:15 IST 2049
Certificate fingerprints:
SHA1: 87:D2:91:DF:D7:88:6E:0E:8D:0C:C5:0B:B9:24:29:15:9D:D7:B6:F5
        SHA256: 6B:16:5F:32:76:84:DA:76:39:C9:03:F4:46:74:DC:22:0B:5B:41:12:BD:9C:A8:C6:30:73:47:24:D8:0E:49:DE
Signature algorithm name: SHA256withRSA
Subject Public Key Algorithm: 2048-bit RSA key
Version: 1*/

plugins {
    alias(libs.plugins.android.application)
    id("kotlin-kapt")
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.ruchitech.cashentery"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ruchitech.cashentery"
        minSdk = 23
        targetSdk = 34
        versionCode = 4
        versionName = "1.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    flavorDimensions += listOf("environment")

    applicationVariants.all {
        outputs.all {
            val SEP = "_"
            val flavor = flavorName
            val buildType = buildType.name
            val version = "${versionCode}(${versionName})"
            val date = Date()
            val formattedDate = SimpleDateFormat("ddMMyyyy_HHmmss", Locale.US).format(date)

            val newApkName =
                "Cash Entry${SEP}$flavor${SEP}$buildType${SEP}$version${SEP}$formattedDate.apk"
            File(newApkName)
        }
    }

    productFlavors {
        create("Dev") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"https://cash-entry-backend.vercel.app\"")
        }

        create("Prod") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"https://cash-entry-backend.vercel.app\"")
        }
        create("Local") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"https://cash-entry-backend.vercel.app\"")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
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
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.camera.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //hilt
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    //navhost
    implementation(libs.androidx.navigation.compose)
    //hilt nav
    implementation(libs.androidx.hilt.navigation.compose)
    //room db
    implementation(libs.gson)

    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.androidx.navigation.compose.v277)
    implementation(libs.coil.compose)
    implementation(libs.accompanist.drawablepainter)
    implementation(libs.accompanist.swiperefresh)
    implementation(libs.kotlinx.coroutines.play.services) // Update to the latest version
    implementation(libs.integrity)
    implementation(libs.capturable)
    implementation (libs.play.services.ads)
   // implementation ("com.google.android.play:core:1.10.3")
    //gson
    implementation("com.google.code.gson:gson:2.10.1")
    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //OkHttp
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")
}
// Add this to enable annotation processing with Hilt
kapt {
    correctErrorTypes = true
}