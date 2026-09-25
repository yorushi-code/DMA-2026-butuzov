package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.logic.Orders;

/** P22: the receipt. */
public class ReceiptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        Orders.Size size = Orders.Size.valueOf(getIntent().getStringExtra(PizzaActivity.EXTRA_SIZE));
        int count = getIntent().getIntExtra(PizzaActivity.EXTRA_COUNT, 1);
        int sizeName = size == Orders.Size.SMALL ? R.string.p22_small
                : size == Orders.Size.LARGE ? R.string.p22_large : R.string.p22_medium;
        int delivery = Orders.delivery(size, count);
        ((TextView) findViewById(R.id.tvItems)).setText(getString(R.string.p22_items, count, getString(sizeName)));
        ((TextView) findViewById(R.id.tvAddress)).setText(
                getString(R.string.p22_address, getIntent().getStringExtra(PizzaActivity.EXTRA_ADDRESS)));
        ((TextView) findViewById(R.id.tvDelivery)).setText(delivery == 0
                ? getString(R.string.p22_delivery_free, Orders.FREE_DELIVERY_FROM)
                : getString(R.string.p22_delivery, delivery));
        ((TextView) findViewById(R.id.tvTotal)).setText(getString(R.string.p22_total, Orders.total(size, count)));
        findViewById(R.id.btnDone).setOnClickListener(v -> finish());
    }
}
