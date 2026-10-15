package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;
import dev.yorushi.dma.task4.logic.Calc;

/** P15: the discount and the final price. */
public class DiscountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);

        EditText etPrice = findViewById(R.id.etPrice);
        EditText etPercent = findViewById(R.id.etPercent);
        TextView tvDiscount = findViewById(R.id.tvDiscount);
        TextView tvFinal = findViewById(R.id.tvFinal);
        findViewById(R.id.btnCalc).setOnClickListener(v -> {
            Double price = Ui.nonNegative(etPrice);
            Double percent = Ui.nonNegative(etPercent);
            if (price == null || percent == null) {
                return;
            }
            if (percent > 100) {
                etPercent.setError(getString(R.string.p15_bad_percent));
                return;
            }
            double[] r = Calc.discount(price, percent);
            tvDiscount.setText(getString(R.string.p15_discount, Ui.money(r[0])));
            tvFinal.setText(getString(R.string.p15_final, Ui.money(r[1])));
        });
    }
}
