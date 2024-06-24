import com.android.build.gradle.internal.api.BaseVariantOutputImpl
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

    applicationVariants.all {
        outputs.all {
            if (this is BaseVariantOutputImpl) {
                outputFileName = "TDSC-V${versionName}(${versionCode})-${buildType.name}-${timeNow()}.apk"
            }
        }
    }

    signingConfigs {
        create("release") {
            storeFile = File(rootDir, "ftd.jks")
            storePassword = "ftd62024"
            keyAlias = "ftd"
            keyPassword = "ftd62024"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs["release"]
        }
        release {
            signingConfig = signingConfigs["release"]
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

fun timeNow(): String {
    return SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.CHINA).format(Date())
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // lifecycle
    // no need to proguard
    implementation(libs.androidx.fragment.ktx)

    // dagger
    // no need proguard
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // add sdk-ktx to your dependencies
    // proguard bundled in aar
    implementation(libs.sdk.ktx)

    // 权限请求框架：https://github.com/getActivity/XXPermissions
    implementation(libs.xxpermissions)

    // https://mvnrepository.com/artifact/commons-io/commons-io
    implementation(libs.commons.io)

}

kapt {
    correctErrorTypes = true
}