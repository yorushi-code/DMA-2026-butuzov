package dev.yorushi.dma.task4.theme;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;

/**
 * Theme 3 (layout). T3.1 orientation, switched at runtime so both states can be
 * seen without editing the XML; T3.2 numeric age field; T3.3 blue header;
 * T3.4 20dp top margins; T3.5 red "Clear all" button.
 */
public class LayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);

        EditText etName = findViewById(R.id.etName);
        EditText etAge = findViewById(R.id.etAge);
        LinearLayout buttonRow = findViewById(R.id.buttonRow);
        TextView tvOrientation = findViewById(R.id.tvOrientation);

        findViewById(R.id.btnSave).setOnClickListener(v -> Toast.makeText(this,
                getString(R.string.t3_saved, Ui.text(etName), Ui.text(etAge)), Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnClear).setOnClickListener(v -> {
            etName.setText("");
            etAge.setText("");
        });
        findViewById(R.id.btnOrientation).setOnClickListener(v -> {
            boolean vertical = buttonRow.getOrientation() == LinearLayout.VERTICAL;
            buttonRow.setOrientation(vertical ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
            showOrientation(buttonRow, tvOrientation);
        });
        showOrientation(buttonRow, tvOrientation);
    }

    private void showOrientation(LinearLayout row, TextView out) {
        int name = row.getOrientation() == LinearLayout.VERTICAL ? R.string.t3_vertical : R.string.t3_horizontal;
        out.setText(getString(R.string.t3_orientation, getString(name)));
    }
}
