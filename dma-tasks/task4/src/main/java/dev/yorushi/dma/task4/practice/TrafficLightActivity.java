package dev.yorushi.dma.task4.practice;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;

/** P01: each button paints the whole screen its colour. */
public class TrafficLightActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_light);

        View root = findViewById(R.id.root);
        findViewById(R.id.btnRed).setOnClickListener(v -> root.setBackgroundColor(Color.parseColor("#EF9A9A")));
        findViewById(R.id.btnYellow).setOnClickListener(v -> root.setBackgroundColor(Color.parseColor("#FFF59D")));
        findViewById(R.id.btnGreen).setOnClickListener(v -> root.setBackgroundColor(Color.parseColor("#A5D6A7")));
    }
}
