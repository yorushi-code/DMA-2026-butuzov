package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;
import dev.yorushi.dma.task4.logic.Calc;

/** P19: hours and minutes from distance and speed. */
public class TravelTimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_time);

        EditText etKm = findViewById(R.id.etKm);
        EditText etSpeed = findViewById(R.id.etSpeed);
        TextView tvResult = findViewById(R.id.tvResult);
        findViewById(R.id.btnCalc).setOnClickListener(v -> {
            Double km = Ui.nonNegative(etKm);
            Double speed = Ui.positive(etSpeed);
            if (km != null && speed != null) {
                long[] t = Calc.travelTime(km, speed);
                tvResult.setText(getString(R.string.p19_result, t[0], t[1]));
            }
        });
    }
}
