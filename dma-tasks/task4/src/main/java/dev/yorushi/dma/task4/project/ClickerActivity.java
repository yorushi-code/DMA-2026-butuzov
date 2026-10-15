package dev.yorushi.dma.task4.project;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.logic.Clicker;

/** Project 1: a counter that changes the background at 10 and 50 points. */
public class ClickerActivity extends AppCompatActivity {

    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicker);

        View root = findViewById(R.id.root);
        TextView tvCounter = findViewById(R.id.tvCounter);

        findViewById(R.id.btnClickMe).setOnClickListener(v -> {
            score++;
            tvCounter.setText(String.valueOf(score));
            String color = Clicker.milestoneColor(score);
            if (color != null) {
                root.setBackgroundColor(Color.parseColor(color));
                Toast.makeText(this, score == 10 ? R.string.pr1_at_10 : R.string.pr1_at_50,
                        Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.btnReset).setOnClickListener(v -> {
            score = 0;
            tvCounter.setText(R.string.zero);
            root.setBackgroundColor(Color.parseColor(Clicker.START_COLOR));
            Toast.makeText(this, R.string.pr1_was_reset, Toast.LENGTH_SHORT).show();
        });
    }
}
