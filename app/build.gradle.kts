plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    /* ---- cấu hình chung ---- */
    namespace   = "com.example.shoes_project"

    /* API 34 (Android 14) là SDK ổn định cao nhất tính tới 07‑2025.
       Dùng 35/36 cần AGP preview và SDK preview, dễ phát sinh lỗi. */
    compileSdk  = 36

    defaultConfig {
        applicationId = "com.example.shoes_project"
        minSdk        = 23
        targetSdk     = 34   // nên khớp compileSdk trừ khi bạn muốn thử preview

        versionCode   = 1
        versionName   = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    /* ── FIX trùng file META‑INF ──
       Kể từ AGP 7.0, dùng block packaging.resources thay cho packagingOptions */
    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE",
                "META-INF/LICENSE.*",
                "META-INF/NOTICE",
                "META-INF/NOTICE.*",
                "META-INF/DEPENDENCIES",
            )
        }
    }
}

dependencies {
    /* AndroidX & UI */
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    /* Room (Java) */
    implementation(libs.room.runtime.android)
    implementation(libs.room.common.jvm)
    annotationProcessor(libs.room.compiler)

    /* Test */
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    /* Firebase */
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    /* Google Sign‑In */
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    /* Facebook Login & Custom Tabs */
    implementation(libs.facebook.login)
    implementation(libs.androidx.browser)

    /* JavaMail */
    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")
}
