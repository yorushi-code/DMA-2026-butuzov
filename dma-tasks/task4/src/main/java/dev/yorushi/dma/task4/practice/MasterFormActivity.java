package dev.yorushi.dma.task4.practice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;

/** P24: name, profession and phone. */
public class MasterFormActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "EXTRA_NAME";
    public static final String EXTRA_JOB = "EXTRA_JOB";
    public static final String EXTRA_PHONE = "EXTRA_PHONE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_form);

        EditText etName = findViewById(R.id.etName);
        EditText etJob = findViewById(R.id.etJob);
        EditText etPhone = findViewById(R.id.etPhone);
        findViewById(R.id.btnCreate).setOnClickListener(v -> {
            if (!Ui.required(etName) | !Ui.required(etJob) | !Ui.required(etPhone)) {
                return;
            }
            startActivity(new Intent(this, MasterCardActivity.class)
                    .putExtra(EXTRA_NAME, Ui.text(etName))
                    .putExtra(EXTRA_JOB, Ui.text(etJob))
                    .putExtra(EXTRA_PHONE, Ui.text(etPhone)));
        });
    }
}
