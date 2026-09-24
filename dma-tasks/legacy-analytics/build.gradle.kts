plugins {
    alias(libs.plugins.android.library)
}

// Stand-in for a third-party SDK with an outdated manifest. It has no code: it
// exists so the app's merge directives (T1.2, T1.3, P01, P15) resolve real
// conflicts instead of being no-ops.
android {
    namespace = "com.example.legacy.analytics"
    compileSdk = 35

    defaultConfig {
        minSdk = 28
    }
}
