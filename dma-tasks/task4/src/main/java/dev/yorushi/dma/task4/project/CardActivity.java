package dev.yorushi.dma.task4.project;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;

/** Project 3, second screen: the card itself. */
public class CardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        ((TextView) findViewById(R.id.tvName)).setText(getIntent().getStringExtra(CardFormActivity.KEY_NAME));
        ((TextView) findViewById(R.id.tvGroup)).setText(
                getString(R.string.pr3_group, getIntent().getStringExtra(CardFormActivity.KEY_GROUP)));
        ((TextView) findViewById(R.id.tvSkill)).setText(
                getString(R.string.pr3_skill, getIntent().getStringExtra(CardFormActivity.KEY_SKILL)));
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
