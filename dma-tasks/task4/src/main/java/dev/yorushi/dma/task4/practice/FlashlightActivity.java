package dev.yorushi.dma.task4.practice;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;

/** P08: white screen at full brightness; the window brightness is restored when turned off. */
public class FlashlightActivity extends AppCompatActivity {

    private boolean on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashlight);

        View root = findViewById(R.id.root);
        Button btnToggle = findViewById(R.id.btnToggle);
        btnToggle.setOnClickListener(v -> {
            on = !on;
            root.setBackgroundColor(on ? Color.WHITE : Color.BLACK);
            btnToggle.setText(on ? R.string.p08_off : R.string.p08_on);
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.screenBrightness = on ? WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
                    : WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
            getWindow().setAttributes(params);
        });
    }
}
