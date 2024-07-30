plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}
val darajaConsumerKey = System.getenv("DARAJA_CONSUMER_KEY") ?: "hLsQeG9GEDvPNjfaH1x3t3wMcuSKlAIH6jyEJsIRJOtmScKl"
val darajaConsumerSecret = System.getenv("DARAJA_CONSUMER_SECRET") ?: "UeJiRo3KHPAsMNGdaKxuDxckG9lNCMFb9T11AD4ZqIM0H8NFlm2EcpwgumzIR3cD"

android {
    namespace = "com.example.smartsalestracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.smartsalestracker"
        minSdk = 31
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
        // Applying consumer keys to all build types using forEach
        forEach { buildType ->
            buildType.buildConfigField("String", "CONSUMER_KEY", "\"$darajaConsumerKey\"")
            buildType.buildConfigField("String", "CONSUMER_SECRET", "\"$darajaConsumerSecret\"")
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        mlModelBinding = true
    }

    defaultConfig {
        buildConfigField("String", "CONSUMER_KEY", "\"4m5aHh8G9mZFpq5sij8jLQUGHUNp5ASVFk1zSUixekPbSfOd\"")
        buildConfigField ("String", "CONSUMER_SECRET", "\"HMDHlxfBfuVkZlTKa8NDc8TNVolFx4EAPtoztcDc4jMgohhi8K1SqTGUQUJWUtox\"")
        multiDexEnabled = true
    }




    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.analytics)
    implementation(libs.tensorflow.lite.metadata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("com.github.dhaval2404:imagepicker:2.1")
    implementation( "com.github.bumptech.glide:glide:4.12.0")

    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    implementation ("com.github.AnyChart:AnyChart-Android:1.1.5")


    implementation("cn.pedant.sweetalert:library:1.3") {
        exclude (group = "com.android.support")
    }
    implementation ("com.jakewharton.timber:timber:4.7.1")
    implementation ("com.squareup.retrofit2:retrofit:2.5.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.5.0")

    implementation ("com.squareup.okhttp3:okhttp:3.12.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:3.12.0")

    implementation ("com.google.code.gson:gson:2.8.5")
    implementation ("com.squareup.okio:okio:2.1.0")

    implementation ("org.tensorflow:tensorflow-lite:2.6.0")
    implementation ("org.tensorflow:tensorflow-lite-support:0.1.0")
}