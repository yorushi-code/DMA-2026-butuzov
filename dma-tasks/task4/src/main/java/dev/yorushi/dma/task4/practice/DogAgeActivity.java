package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;
import dev.yorushi.dma.task4.logic.Calc;

/** P06: human years times seven. */
public class DogAgeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_age);

        EditText etYears = findViewById(R.id.etYears);
        TextView tvResult = findViewById(R.id.tvResult);
        findViewById(R.id.btnCalc).setOnClickListener(v -> {
            Double years = Ui.nonNegative(etYears);
            if (years != null) {
                tvResult.setText(getString(R.string.p06_result, Ui.decimal(Calc.dogYears(years))));
            }
        });
    }
}
