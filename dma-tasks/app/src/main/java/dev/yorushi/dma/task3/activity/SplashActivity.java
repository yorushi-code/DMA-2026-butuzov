package dev.yorushi.dma.task3.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

/**
 * T3.1: the single launcher entry point.
 * P03: installs the core-splashscreen splash declared by Theme.App.Starting,
 * then hands over to the inspector.
 */
public final class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, InspectorActivity.class));
        finish();
    }
}
