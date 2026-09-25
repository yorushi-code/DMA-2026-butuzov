package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.logic.Randoms;
import java.util.Random;

/** P17: no digit repeats the one before it. */
public class PinActivity extends AppCompatActivity {

    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        RadioGroup rgLength = findViewById(R.id.rgLength);
        TextView tvPin = findViewById(R.id.tvPin);
        findViewById(R.id.btnGenerate).setOnClickListener(v -> tvPin.setText(
                Randoms.pin(rgLength.getCheckedRadioButtonId() == R.id.rb6 ? 6 : 4, random)));
    }
}
