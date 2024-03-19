import java.io.FileInputStream
import java.util.Properties
import com.android.build.OutputFile

plugins {
    id("kotlin-kapt")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//     id("androidx.navigation.safeargs")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}
val apikeyPropertiesFile = rootProject.file("apikey.properties")
val apikeyProperties = Properties()
apikeyProperties.load(FileInputStream(apikeyPropertiesFile))
android {
    namespace = "com.video.mini.tools.zip.compress.convert.simple.tiny"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.video.mini.tools.zip.compress.convert.simple.tiny"
        minSdk = 24
        targetSdk = 34
        versionCode = 102
        versionName = "1.0.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "GG_APP_OPEN", apikeyProperties["GG_APP_OPEN"] as String)
        buildConfigField("String", "GG_BANNER", apikeyProperties["GG_BANNER"] as String)
        buildConfigField("String", "GG_NATIVE", apikeyProperties["GG_NATIVE"] as String)
        buildConfigField("String", "GG_FULL", apikeyProperties["GG_FULL"] as String)
        buildConfigField("String", "GG_REWARDED", apikeyProperties["GG_REWARDED"] as String)

//        ndk {
//            abiFilters.add("arm64-v8a")
//        }
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
        buildConfig = true
    }
    buildFeatures {
        viewBinding = true
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }


}




dependencies {
    implementation ("com.theartofdev.edmodo:android-image-cropper:2.8.0")

    implementation ("com.github.akshaaatt:Google-IAP:1.6.0")
    // Dependencies for media playback
    implementation("androidx.media3:media3-exoplayer:1.3.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.3.0")
    implementation("androidx.media3:media3-ui:1.3.0")

// Custom library
    implementation(files("libs/adshelper.aar"))

// Multidex support
    implementation("androidx.multidex:multidex:2.0.1")

// Google Play Services dependencies
    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
    implementation("com.google.android.gms:play-services-ads:23.0.0")

// ExoPlayer for media playback
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")

// Billing client
    implementation("com.android.billingclient:billing-ktx:6.2.0")

// UI components
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.airbnb.android:lottie:6.0.0")
    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation("com.github.Jay-Goo:RangeSeekBar:v3.0.0")

// JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")

// Image loading and caching
    implementation("com.github.bumptech.glide:glide:4.16.0")

// Multimedia processing with FFmpeg
    implementation("com.arthenica:ffmpeg-kit-full-gpl:6.0-2")

// UI components
    implementation("me.relex:circleindicator:2.1.6")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

// Navigation components
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("com.google.firebase:firebase-config-ktx:21.6.3")


// Lifecycle components
    val lifecycle_version = "2.7.0"
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    annotationProcessor("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")

// Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

// Activity component
    implementation("androidx.activity:activity-ktx:1.8.2")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")


}