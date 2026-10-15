package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;
import dev.yorushi.dma.task4.logic.Calc;

/** P12: litres per 100 km. */
public class FuelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel);

        EditText etKm = findViewById(R.id.etKm);
        EditText etLitres = findViewById(R.id.etLitres);
        TextView tvResult = findViewById(R.id.tvResult);
        findViewById(R.id.btnCalc).setOnClickListener(v -> {
            Double km = Ui.positive(etKm);
            Double litres = Ui.nonNegative(etLitres);
            if (km != null && litres != null) {
                tvResult.setText(getString(R.string.p12_result, Ui.decimal(Calc.fuelPer100Km(km, litres))));
            }
        });
    }
}
