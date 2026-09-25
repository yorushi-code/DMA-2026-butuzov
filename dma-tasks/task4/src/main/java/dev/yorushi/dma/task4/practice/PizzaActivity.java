package dev.yorushi.dma.task4.practice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;
import dev.yorushi.dma.task4.logic.Orders;

/** P22: size, quantity and address; the receipt is the second screen. */
public class PizzaActivity extends AppCompatActivity {

    public static final String EXTRA_SIZE = "EXTRA_SIZE";
    public static final String EXTRA_COUNT = "EXTRA_COUNT";
    public static final String EXTRA_ADDRESS = "EXTRA_ADDRESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza);

        RadioGroup rgSize = findViewById(R.id.rgSize);
        EditText etCount = findViewById(R.id.etCount);
        EditText etAddress = findViewById(R.id.etAddress);
        findViewById(R.id.btnOrder).setOnClickListener(v -> {
            Integer count = Ui.positiveInt(etCount);
            if (count == null || !Ui.required(etAddress)) {
                return;
            }
            int checked = rgSize.getCheckedRadioButtonId();
            Orders.Size size = checked == R.id.rbSmall ? Orders.Size.SMALL
                    : checked == R.id.rbLarge ? Orders.Size.LARGE : Orders.Size.MEDIUM;
            startActivity(new Intent(this, ReceiptActivity.class)
                    .putExtra(EXTRA_SIZE, size.name())
                    .putExtra(EXTRA_COUNT, count.intValue())
                    .putExtra(EXTRA_ADDRESS, Ui.text(etAddress)));
        });
    }
}
