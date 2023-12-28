import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "per.pslilysm.filecleaner"
    compileSdk = 34

    defaultConfig {
        applicationId = "per.pslilysm.filecleaner"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    android.applicationVariants.all {
        val variant = this
        this.outputs
            .map { it as com.android.build.gradle.internal.api.ApkVariantOutputImpl }
            .all {
                it.outputFileName = "TDSC-V${variant.versionName}(${variant.versionCode})-${variant.buildType.name}-${timeNow()}.apk"
                false
            }
    }

    signingConfigs {
        create("release") {
            keyAlias = "ftd"
            keyPassword = "ftd62024"
            storeFile = File(rootDir, "ftd.jks")
            storePassword = "ftd62024"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getAt("release")
        }
        release {
            signingConfig = signingConfigs.getAt("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        dataBinding = true
    }
}

fun timeNow(): String {
    return SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.CHINA).format(Date())
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // lifecycle
    // no need to proguard
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    // dagger
    // no need to proguard
    implementation("com.google.dagger:hilt-android:2.49")
    kapt("com.google.dagger:hilt-compiler:2.49")

    // add sdk-ktx to your dependencies
    // proguard bundled in aar
    implementation("com.github.pslilysm:sdk-ktx:2.2.9")

    // 权限请求框架：https://github.com/getActivity/XXPermissions
    // no need to proguard
    implementation ("com.github.getActivity:XXPermissions:18.5")

    // https://mvnrepository.com/artifact/commons-io/commons-io
    // no need to proguard
    implementation("commons-io:commons-io:2.15.1")

}