plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "dev.yorushi.dma.task4"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.yorushi.dma.task4"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests.all {
            it.testLogging {
                events("passed", "failed", "skipped")
            }
        }
    }

    lint {
        // Theme 5 is "no text in code or layouts": make lint enforce it.
        error += listOf("HardcodedText", "SetTextI18n")
        abortOnError = true
        checkReleaseBuilds = false
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-serial"))
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.material)

    testImplementation(libs.junit4)

    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso)
}
