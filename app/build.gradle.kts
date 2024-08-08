import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

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

    flavorDimensions("environment")

    productFlavors {
        create("Dev") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"http://16.171.27.231:3000\"")
        }

        create("Prod") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"http://16.171.27.231:3000\"")
        }
        create("Local") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"http://16.171.27.231:3000\"")
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