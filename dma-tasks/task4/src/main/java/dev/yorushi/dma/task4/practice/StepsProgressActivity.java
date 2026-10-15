package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.logic.Calc;

/** P28: percentage and a progress bar. */
public class StepsProgressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_progress);

        int target = getIntent().getIntExtra(StepsFormActivity.EXTRA_TARGET, 1);
        int done = getIntent().getIntExtra(StepsFormActivity.EXTRA_DONE, 0);
        int percent = Calc.stepsPercent(done, target);
        ((TextView) findViewById(R.id.tvPercent)).setText(getString(R.string.p28_percent, percent));
        ((ProgressBar) findViewById(R.id.progress)).setProgress(Math.min(100, percent));
        ((TextView) findViewById(R.id.tvSteps)).setText(getString(R.string.p28_steps, done, target));
    }
}
