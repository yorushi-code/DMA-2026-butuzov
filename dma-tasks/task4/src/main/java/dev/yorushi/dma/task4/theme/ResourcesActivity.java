package dev.yorushi.dma.task4.theme;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;

/**
 * Theme 5 (resources). T5.1 no text in code or layouts (lint enforces it);
 * T5.2 brand colours; T5.3 vector icon; T5.4 welcome_user with a placeholder;
 * T5.5 values-ru, shown here by the name of the active language.
 */
public class ResourcesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);

        EditText etName = findViewById(R.id.etName);
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        TextView tvLanguage = findViewById(R.id.tvLanguage);

        tvLanguage.setText(getString(R.string.t5_language, getString(R.string.language_name)));
        findViewById(R.id.btnWelcome).setOnClickListener(v -> {
            String name = Ui.text(etName);
            tvWelcome.setText(getString(R.string.welcome_user, name.isEmpty() ? getString(R.string.guest) : name));
        });
        findViewById(R.id.btnReset).setOnClickListener(v -> {
            etName.setText("");
            tvWelcome.setText("");
        });
    }
}
