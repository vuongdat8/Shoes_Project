plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    /* ---- cấu hình chung ---- */
    namespace   = "com.example.shoes_project"
    compileSdk  = 36

    defaultConfig {
        applicationId = "com.example.shoes_project"
        minSdk        = 26
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
    dependencies {
        implementation ("androidx.room:room-runtime:2.5.1")  // Phiên bản mới nhất của Room
        annotationProcessor ("androidx.room:room-compiler:2.5.1")
    }
    implementation ("androidx.recyclerview:recyclerview:1.4.0")
    implementation ("androidx.cardview:cardview:1.0.0")

    /* AndroidX & UI */
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    /* Room (Java) */
    implementation(libs.room.runtime.android)
    implementation(libs.room.common.jvm)
    implementation(libs.litert.support.api)
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
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation("com.google.code.gson:gson:2.11.0")
}