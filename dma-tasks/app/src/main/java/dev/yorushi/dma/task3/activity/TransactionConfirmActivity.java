package dev.yorushi.dma.task3.activity;

import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.R;

/**
 * P19: protection against tapjacking. The layout ignores touches while it is
 * obscured, and on Android 12+ the window also hides every non-system overlay
 * (allowed by the HIDE_OVERLAY_WINDOWS permission in the manifest).
 */
public final class TransactionConfirmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getWindow().setHideOverlayWindows(true);
        }
        setContentView(R.layout.activity_transaction_confirm);
        findViewById(R.id.confirm).setOnClickListener(v ->
                Toast.makeText(this, R.string.tx_confirmed, Toast.LENGTH_SHORT).show());
    }
}
