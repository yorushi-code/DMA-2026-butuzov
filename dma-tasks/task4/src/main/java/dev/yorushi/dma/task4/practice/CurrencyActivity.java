package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;
import dev.yorushi.dma.task4.logic.Calc;

/** P16: roubles to dollars and euros at a fixed rate, rounded. */
public class CurrencyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);

        EditText etRub = findViewById(R.id.etRub);
        TextView tvUsd = findViewById(R.id.tvUsd);
        TextView tvEur = findViewById(R.id.tvEur);
        ((TextView) findViewById(R.id.tvRates)).setText(getString(R.string.p16_rates,
                Ui.decimal(Calc.RUB_PER_USD), Ui.decimal(Calc.RUB_PER_EUR)));
        findViewById(R.id.btnConvert).setOnClickListener(v -> {
            Double rub = Ui.nonNegative(etRub);
            if (rub != null) {
                tvUsd.setText(getString(R.string.p16_usd, Ui.money(Calc.rubToUsd(rub))));
                tvEur.setText(getString(R.string.p16_eur, Ui.money(Calc.rubToEur(rub))));
            }
        });
    }
}
