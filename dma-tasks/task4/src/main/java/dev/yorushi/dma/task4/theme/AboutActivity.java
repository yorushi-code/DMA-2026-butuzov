package dev.yorushi.dma.task4.theme;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;

/** Theme 6, T6.1: the third screen, with the author and an exit button. */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        boolean agreed = getIntent().getBooleanExtra(IntentStartActivity.EXTRA_AGREED, false);
        double balance = getIntent().getDoubleExtra(IntentStartActivity.EXTRA_BALANCE, 0);
        ((TextView) findViewById(R.id.tvAgreed)).setText(agreed ? R.string.t6_agreed_yes : R.string.t6_agreed_no);
        ((TextView) findViewById(R.id.tvBalance)).setText(getString(R.string.t6_balance, Ui.money(balance)));
        findViewById(R.id.btnExit).setOnClickListener(v -> finish());
    }
}
