package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.logic.Randoms;
import java.util.Random;

/** P02: a random number from 1 to 6. */
public class DiceActivity extends AppCompatActivity {

    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice);

        TextView tvDie = findViewById(R.id.tvDie);
        findViewById(R.id.btnRoll).setOnClickListener(v -> tvDie.setText(String.valueOf(Randoms.rollDie(random))));
    }
}
