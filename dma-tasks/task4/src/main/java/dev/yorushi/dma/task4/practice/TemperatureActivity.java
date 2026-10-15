package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;
import dev.yorushi.dma.task4.logic.Calc;

/** P13: F = C * 1.8 + 32 and K = C + 273.15. */
public class TemperatureActivity extends AppCompatActivity {

    /** Rejects temperatures below absolute zero, which have no Kelvin value. */
    private Double celsius(EditText field) {
        Double c = Ui.number(field);
        if (c != null && c < -273.15) {
            field.setError(getString(R.string.p13_below_zero));
            return null;
        }
        return c;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        EditText etCelsius = findViewById(R.id.etCelsius);
        TextView tvResult = findViewById(R.id.tvResult);
        findViewById(R.id.btnToF).setOnClickListener(v -> {
            Double c = celsius(etCelsius);
            if (c != null) {
                tvResult.setText(getString(R.string.p13_result_f, Ui.decimal(c), Ui.decimal(Calc.celsiusToFahrenheit(c))));
            }
        });
        findViewById(R.id.btnToK).setOnClickListener(v -> {
            Double c = celsius(etCelsius);
            if (c != null) {
                tvResult.setText(getString(R.string.p13_result_k, Ui.decimal(c), Ui.decimal(Calc.celsiusToKelvin(c))));
            }
        });
    }
}
