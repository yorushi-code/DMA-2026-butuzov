package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;
import dev.yorushi.dma.task4.logic.Calc;

/** P09: inches = cm / 2.54. */
public class CmToInchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cm_to_inch);

        EditText etCm = findViewById(R.id.etCm);
        TextView tvResult = findViewById(R.id.tvResult);
        findViewById(R.id.btnConvert).setOnClickListener(v -> {
            Double cm = Ui.nonNegative(etCm);
            if (cm != null) {
                tvResult.setText(getString(R.string.p09_result, Ui.decimal(cm), Ui.decimal(Calc.cmToInches(cm))));
            }
        });
    }
}
