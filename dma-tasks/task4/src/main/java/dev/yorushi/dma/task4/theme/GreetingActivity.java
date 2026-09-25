package dev.yorushi.dma.task4.theme;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;
import dev.yorushi.dma.task4.logic.Texts;

/**
 * Theme 4 (code). T4.1 Toast on success; T4.2 names shorter than 2 characters
 * rejected; T4.3 header text change; T4.4 result hidden with GONE; T4.5 counter.
 */
public class GreetingActivity extends AppCompatActivity {

    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

        TextView tvHeader = findViewById(R.id.tvHeader);
        EditText etName = findViewById(R.id.etName);
        TextView tvResult = findViewById(R.id.tvResult);
        TextView tvCount = findViewById(R.id.tvCount);

        findViewById(R.id.btnGreet).setOnClickListener(v -> {
            String name = Ui.text(etName);
            if (!Texts.isNameLongEnough(name)) {
                etName.setError(getString(R.string.t4_name_too_short));
                return;
            }
            etName.setError(null);
            tvResult.setVisibility(View.VISIBLE);
            tvResult.setText(getString(R.string.t4_greeting, name));
            Toast.makeText(this, R.string.t4_greeting_toast, Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btnChangeHeader).setOnClickListener(v -> tvHeader.setText(R.string.t4_header_changed));
        findViewById(R.id.btnHideResult).setOnClickListener(v -> tvResult.setVisibility(View.GONE));
        findViewById(R.id.btnCount).setOnClickListener(v -> tvCount.setText(String.valueOf(++count)));
    }
}
