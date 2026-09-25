package dev.yorushi.dma.task4.practice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;

/** P27: the route. */
public class TicketFormActivity extends AppCompatActivity {

    public static final String EXTRA_FROM = "EXTRA_FROM";
    public static final String EXTRA_TO = "EXTRA_TO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_form);

        EditText etFrom = findViewById(R.id.etFrom);
        EditText etTo = findViewById(R.id.etTo);
        findViewById(R.id.btnIssue).setOnClickListener(v -> {
            if (!Ui.required(etFrom) | !Ui.required(etTo)) {
                return;
            }
            if (Ui.text(etFrom).equalsIgnoreCase(Ui.text(etTo))) {
                etTo.setError(getString(R.string.p27_same));
                return;
            }
            startActivity(new Intent(this, TicketActivity.class)
                    .putExtra(EXTRA_FROM, Ui.text(etFrom))
                    .putExtra(EXTRA_TO, Ui.text(etTo)));
        });
    }
}
