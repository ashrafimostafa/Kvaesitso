plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
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
        viewBinding = true
    }
    namespace = "ir.mostafa.launcher.owncloud"
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.materialcomponents.core)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.constraintlayout.views)
    implementation(libs.androidx.securitycrypto)

    implementation(libs.bundles.androidx.lifecycle)

    implementation(libs.okhttp)

    api(project(":libs:webdav"))
    implementation(project(":core:crashreporter"))
    implementation(project(":core:ktx"))
    implementation(project(":core:i18n"))

}