package dev.yorushi.dma.task4.theme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;

/** Theme 6 (Intents), first screen: collects a String, a boolean and a double. */
public class IntentStartActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "EXTRA_NAME";
    public static final String EXTRA_AGREED = "EXTRA_AGREED";
    public static final String EXTRA_BALANCE = "EXTRA_BALANCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_start);

        EditText etName = findViewById(R.id.etName);
        CheckBox cbAgree = findViewById(R.id.cbAgree);
        EditText etBalance = findViewById(R.id.etBalance);

        findViewById(R.id.btnNext).setOnClickListener(v -> {
            Double balance = Ui.number(etBalance);
            if (balance == null) {
                return;
            }
            String name = Ui.text(etName);
            Intent intent = new Intent(this, IntentSecondActivity.class);
            // T6.3: an empty name is sent as null, the case the second screen handles.
            intent.putExtra(EXTRA_NAME, name.isEmpty() ? null : name);
            intent.putExtra(EXTRA_AGREED, cbAgree.isChecked());
            intent.putExtra(EXTRA_BALANCE, balance.doubleValue());
            startActivity(intent);
        });
    }
}
