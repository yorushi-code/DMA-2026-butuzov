package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.logic.Calc;
import java.util.List;
import java.util.Locale;

/** P26: month by month, the last payment closing the balance exactly. */
public class LoanScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_schedule);

        List<Calc.Payment> rows = Calc.schedule(
                getIntent().getDoubleExtra(LoanActivity.EXTRA_AMOUNT, 0),
                getIntent().getDoubleExtra(LoanActivity.EXTRA_RATE, 0),
                getIntent().getIntExtra(LoanActivity.EXTRA_MONTHS, 1));
        StringBuilder table = new StringBuilder();
        for (Calc.Payment p : rows) {
            table.append(String.format(Locale.getDefault(), "%3d  %9.2f  %8.2f  %10.2f%n",
                    p.month, p.payment, p.interest, p.balance));
        }
        ((TextView) findViewById(R.id.tvRows)).setText(table.toString());
    }
}
