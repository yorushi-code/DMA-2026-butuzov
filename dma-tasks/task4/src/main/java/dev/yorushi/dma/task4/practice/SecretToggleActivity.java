package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;

/** P04: toggles the text between VISIBLE and GONE. */
public class SecretToggleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret);

        TextView tvSecret = findViewById(R.id.tvSecret);
        Button btnToggle = findViewById(R.id.btnToggle);
        btnToggle.setOnClickListener(v -> {
            boolean show = tvSecret.getVisibility() != View.VISIBLE;
            tvSecret.setVisibility(show ? View.VISIBLE : View.GONE);
            btnToggle.setText(show ? R.string.p04_hide : R.string.p04_show);
        });
    }
}
