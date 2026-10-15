package dev.yorushi.dma.task4.practice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;

/** P28: goal and steps walked. */
public class StepsFormActivity extends AppCompatActivity {

    public static final String EXTRA_TARGET = "EXTRA_TARGET";
    public static final String EXTRA_DONE = "EXTRA_DONE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_form);

        EditText etTarget = findViewById(R.id.etTarget);
        EditText etDone = findViewById(R.id.etDone);
        findViewById(R.id.btnShow).setOnClickListener(v -> {
            Integer target = Ui.positiveInt(etTarget);
            Integer done = Ui.nonNegativeInt(etDone);
            if (target != null && done != null) {
                startActivity(new Intent(this, StepsProgressActivity.class)
                        .putExtra(EXTRA_TARGET, target.intValue())
                        .putExtra(EXTRA_DONE, done.intValue()));
            }
        });
    }
}
