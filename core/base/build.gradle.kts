plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.plugin.serialization)
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
        create("nightly") {
            initWith(getByName("release"))
            matchingFallbacks += "release"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    namespace = "ir.mostafa.launcher.base"
}

dependencies {
    implementation(libs.bundles.kotlin)

    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.datastore)
    implementation(libs.materialcomponents.core)

    implementation(libs.koin.android)
    implementation(libs.androidx.palette)
    runtimeOnly(libs.androidx.compose.ui)
    runtimeOnly(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.materialicons)

    implementation(project(":core:ktx"))
    implementation(project(":core:i18n"))
    implementation(project(":libs:material-color-utilities"))
    api(project(":core:shared"))

    testImplementation(libs.bundles.tests)
}