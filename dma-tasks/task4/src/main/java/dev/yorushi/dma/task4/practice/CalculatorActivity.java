package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;
import dev.yorushi.dma.task4.logic.Calc;

/** P14: four operations, division by zero refused. */
public class CalculatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        EditText etA = findViewById(R.id.etA);
        EditText etB = findViewById(R.id.etB);
        TextView tvResult = findViewById(R.id.tvResult);
        int[] ids = {R.id.btnPlus, R.id.btnMinus, R.id.btnTimes, R.id.btnDivide};
        Calc.Op[] ops = Calc.Op.values();
        for (int i = 0; i < ids.length; i++) {
            Calc.Op op = ops[i];
            findViewById(ids[i]).setOnClickListener(v -> {
                Double a = Ui.number(etA);
                Double b = Ui.number(etB);
                if (a == null || b == null) {
                    return;
                }
                try {
                    tvResult.setText(getString(R.string.p14_result, Ui.decimal(Calc.apply(a, b, op))));
                } catch (ArithmeticException e) {
                    tvResult.setText(R.string.p14_div_zero);
                }
            });
        }
    }
}
