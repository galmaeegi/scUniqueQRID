plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.uniqueqridsystemsc"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.uniqueqridsystemsc"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }


    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {
    implementation ("com.google.zxing:core:3.4.1")
    implementation ("androidx.cardview:cardview:1.0.0") // Use the latest version
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.2.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.zxing:core:3.4.1")
    implementation ("androidx.appcompat:appcompat:1.2.0")
    implementation(platform("com.google.firebase:firebase-bom:32.3.1")) // Use the latest version from the Firebase BoM
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database") // Use the Firebase BoM version
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
