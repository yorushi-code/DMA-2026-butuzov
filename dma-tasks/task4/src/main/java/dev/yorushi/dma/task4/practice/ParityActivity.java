package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;
import dev.yorushi.dma.task4.logic.Calc;

/** P10: even or odd, negatives included. */
public class ParityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parity);

        EditText etNumber = findViewById(R.id.etNumber);
        TextView tvResult = findViewById(R.id.tvResult);
        findViewById(R.id.btnCheck).setOnClickListener(v -> {
            Long n = Ui.whole(etNumber);
            if (n != null) {
                tvResult.setText(getString(Calc.isEven(n) ? R.string.p10_even : R.string.p10_odd, n));
            }
        });
    }
}
