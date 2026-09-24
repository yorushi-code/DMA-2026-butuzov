plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "dev.yorushi.dma"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.yorushi.dma"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // Custom runner, also declared through <instrumentation> (task 3, P10).
        testInstrumentationRunner = "dev.yorushi.dma.CustomTestRunner"

        // Keys stay out of the manifest source and are substituted at merge time.
        manifestPlaceholders["yandexMapKitKey"] = "YANDEX_MAPKIT_KEY_PLACEHOLDER"
    }

    // A watch app needs its own APK: declaring android.hardware.type.watch in the
    // phone build would hide it from phones on Google Play. The wear flavor
    // overlays only the watch declarations onto the shared main manifest (P42).
    flavorDimensions += "formFactor"
    productFlavors {
        create("mobile") {
            dimension = "formFactor"
            isDefault = true
            variantResources("dev.yorushi.dma")
        }
        create("wear") {
            dimension = "formFactor"
            applicationIdSuffix = ".wear"
            variantResources("dev.yorushi.dma.wear")
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            // src/release/AndroidManifest.xml additionally forces
            // android:debuggable="false" with tools:replace (P15).
            isDebuggable = false
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }

    lint {
        // Some items deliberately declare restricted permissions (exact alarms,
        // battery optimisation) to demonstrate them; report them, do not fail.
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.emoji2)
    implementation(libs.material)

    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.junit)
}

/**
 * XML resources such as syncadapter.xml cannot use ${applicationId}; the values
 * that depend on it are generated as string resources for each flavor instead.
 */
fun com.android.build.api.dsl.ApplicationProductFlavor.variantResources(appId: String) {
    resValue("string", "notes_authority", "$appId.notes")
    resValue("string", "account_type", "$appId.account")
}
