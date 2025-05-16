plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.netflixcloneui"
    compileSdk = 35


    defaultConfig {
        applicationId = "com.netflixcloneui"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.legacy.support.v4)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp (Optional, but can be used for logging)
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // UI
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // Glide (Thêm vào đây)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.squareup.picasso:picasso:2.4.0")

    implementation ("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.github.chthai64:SwipeRevealLayout:1.4.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //Media3
    implementation ("androidx.media3:media3-exoplayer:1.2.0")
    implementation ("androidx.media3:media3-exoplayer-dash:1.2.0")
    implementation ("androidx.media3:media3-ui:1.2.0")


}