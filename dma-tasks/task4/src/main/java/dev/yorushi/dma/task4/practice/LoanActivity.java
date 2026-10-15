package dev.yorushi.dma.task4.practice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;
import dev.yorushi.dma.task4.logic.Calc;

/** P26: the annuity payment; the schedule is the second screen. */
public class LoanActivity extends AppCompatActivity {

    public static final String EXTRA_AMOUNT = "EXTRA_AMOUNT";
    public static final String EXTRA_RATE = "EXTRA_RATE";
    public static final String EXTRA_MONTHS = "EXTRA_MONTHS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);

        EditText etAmount = findViewById(R.id.etAmount);
        EditText etRate = findViewById(R.id.etRate);
        EditText etMonths = findViewById(R.id.etMonths);
        TextView tvPayment = findViewById(R.id.tvPayment);
        View btnSchedule = findViewById(R.id.btnSchedule);
        findViewById(R.id.btnCalc).setOnClickListener(v -> {
            Double amount = Ui.positive(etAmount);
            Double rate = Ui.nonNegative(etRate);
            Integer months = Ui.positiveInt(etMonths);
            if (amount == null || rate == null || months == null) {
                return;
            }
            if (months > 600) {
                etMonths.setError(getString(R.string.p26_too_long));
                return;
            }
            tvPayment.setText(getString(R.string.p26_payment, Ui.money(Calc.monthlyPayment(amount, rate, months))));
            btnSchedule.setEnabled(true);
            btnSchedule.setOnClickListener(s -> startActivity(new Intent(this, LoanScheduleActivity.class)
                    .putExtra(EXTRA_AMOUNT, amount.doubleValue())
                    .putExtra(EXTRA_RATE, rate.doubleValue())
                    .putExtra(EXTRA_MONTHS, months.intValue())));
        });
    }
}
