package dev.yorushi.dma.task4.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;

/** Project 3, first screen: the card's data. */
public class CardFormActivity extends AppCompatActivity {

    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_GROUP = "KEY_GROUP";
    public static final String KEY_SKILL = "KEY_SKILL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_form);

        EditText etName = findViewById(R.id.etName);
        EditText etGroup = findViewById(R.id.etGroup);
        EditText etSkill = findViewById(R.id.etSkill);

        findViewById(R.id.btnGenerateCard).setOnClickListener(v -> {
            if (!Ui.required(etName) | !Ui.required(etGroup) | !Ui.required(etSkill)) {
                return;
            }
            Intent intent = new Intent(this, CardActivity.class);
            intent.putExtra(KEY_NAME, Ui.text(etName));
            intent.putExtra(KEY_GROUP, Ui.text(etGroup));
            intent.putExtra(KEY_SKILL, Ui.text(etSkill));
            startActivity(intent);
        });
    }
}
