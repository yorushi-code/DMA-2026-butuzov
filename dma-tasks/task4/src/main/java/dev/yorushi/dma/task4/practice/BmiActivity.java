package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;
import dev.yorushi.dma.task4.logic.Calc;

/** P11: weight / (height/100)^2 with a verdict. */
public class BmiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        EditText etWeight = findViewById(R.id.etWeight);
        EditText etHeight = findViewById(R.id.etHeight);
        TextView tvResult = findViewById(R.id.tvResult);
        findViewById(R.id.btnCalc).setOnClickListener(v -> {
            Double weight = Ui.positive(etWeight);
            Double height = Ui.positive(etHeight);
            if (weight == null || height == null) {
                return;
            }
            double bmi = Calc.bmi(weight, height);
            int verdict;
            switch (Calc.bmiVerdict(bmi)) {
                case UNDERWEIGHT:
                    verdict = R.string.p11_underweight;
                    break;
                case NORMAL:
                    verdict = R.string.p11_normal;
                    break;
                default:
                    verdict = R.string.p11_overweight;
            }
            tvResult.setText(getString(R.string.p11_result, Ui.decimal(bmi), getString(verdict)));
        });
    }
}
