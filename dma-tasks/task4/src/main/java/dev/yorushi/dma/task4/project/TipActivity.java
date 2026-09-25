package dev.yorushi.dma.task4.project;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;
import dev.yorushi.dma.task4.logic.Calc;

/** Project 2: bill plus 10% tip, split between the guests. */
public class TipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip);

        EditText etBill = findViewById(R.id.etTotalBill);
        EditText etPersons = findViewById(R.id.etPersonsCount);
        TextView tvTip = findViewById(R.id.tvTipAmount);
        TextView tvTotal = findViewById(R.id.tvTotal);
        TextView tvPerPerson = findViewById(R.id.tvPerPerson);

        findViewById(R.id.btnCalculate).setOnClickListener(v -> {
            Double bill = Ui.positive(etBill);
            Integer persons = Ui.positiveInt(etPersons);
            if (bill == null || persons == null) {
                return;
            }
            double[] r = Calc.tip(bill, persons);
            tvTip.setText(getString(R.string.pr2_tip, Ui.money(r[0])));
            tvTotal.setText(getString(R.string.pr2_total, Ui.money(r[1])));
            tvPerPerson.setText(getString(R.string.pr2_per_person, Ui.money(r[2])));
        });
    }
}
