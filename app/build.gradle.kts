plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.dee.smrpos"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.dee.smrpos"
        minSdk = 24
        targetSdk = 36
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
    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    implementation ("com.google.auth:google-auth-library-oauth2-http:1.19.0")
    implementation ("com.google.api-client:google-api-client:1.33.0")
    implementation ("com.google.oauth-client:google-oauth-client-jetty:1.33.0")
    implementation("com.google.apis:google-api-services-sheets:v4-rev20210629-1.32.1")


}