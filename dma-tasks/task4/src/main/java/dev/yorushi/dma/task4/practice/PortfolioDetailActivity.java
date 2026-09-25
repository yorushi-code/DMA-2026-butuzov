package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;

/** P30: one section of the portfolio. */
public class PortfolioDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio_detail);

        ((TextView) findViewById(R.id.tvSection)).setText(getIntent().getIntExtra(PortfolioActivity.EXTRA_TITLE, 0));
        ((TextView) findViewById(R.id.tvText)).setText(getIntent().getIntExtra(PortfolioActivity.EXTRA_TEXT, 0));
    }
}
