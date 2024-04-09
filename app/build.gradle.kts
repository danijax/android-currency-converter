import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.libsDirectory
import org.jetbrains.kotlin.tooling.core.closure
import java.util.Properties
import java.io.*

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.danijax.paypayxchange"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.danijax.paypayxchange"
        minSdk = 30
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        println("Build: ${getLocalProperty("APP_ID")}")

        val APPID = getLocalProperty("APP_ID")


        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "HOST_URL", "\"https://openexchangerates.org/\"")
            buildConfigField("String", "EXCHANGE_APP_ID", APPID.toString())
        }

        debug {
            buildConfigField("String", "HOST_URL", "\"https://openexchangerates.org/\"")
            buildConfigField("String", "EXCHANGE_APP_ID", getLocalProperty("APP_ID") as String)
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
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    kapt {
        correctErrorTypes = true
    }
    sourceSets {
        sourceSets.getByName("test") {
            resources.srcDirs("src/main/assets")
        }
    }
}

dependencies {
    //Android
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation(libs.lifecycle.runtime.compose)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation(libs.datastore.preferences)
    implementation(libs.work.runtime.ktx)
    implementation(libs.hilt.common)
    implementation(libs.hilt.work)
    implementation(libs.room.runtime)
    kapt(libs.room.compiler)
    implementation(libs.room.ktx)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation (libs.kotlinx.coroutines.test)
   // Optional -- Mockito framework
    testImplementation (libs.mockito.core)
    // Optional -- mockito-kotlin
    testImplementation (libs.mockito.kotlin)
    // Optional -- Mockk framework
    //testImplementation (libs.mockk)


    //3rp party
    //implementation(libs.kotlin.serialization)
    implementation(libs.kotlin.serialization)
    implementation(libs.okhttp3.logging.interceptor)
    //implementation(libs.retrofit2.converter.gson)
    implementation(libs.retrofit2)
    implementation(libs.kotlinx.serialization.converter)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    implementation( libs.accompanist.systemuicontroller)


}

fun Project.getLocalProperty(key: String, file: String = "development.properties"): Any {
    val properties = Properties()
    val localProperties = File(file)
    if (localProperties.isFile) {
        InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }
    } else error("File from not found")

    return properties.getProperty(key)
}

task("printKey") {
    doLast {
        val key = getLocalProperty("APP_ID")
        println(key)
    }
}