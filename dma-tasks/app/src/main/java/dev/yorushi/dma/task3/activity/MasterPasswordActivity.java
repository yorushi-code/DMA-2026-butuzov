package dev.yorushi.dma.task3.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import dev.yorushi.dma.R;

/**
 * P08: excluded from Recents by the manifest.
 * P09: FLAG_SECURE complements it: the task could still appear in Recents after a
 * configuration change or on older launchers, and FLAG_SECURE guarantees the
 * thumbnail stays blank while also blocking screenshots and screen casting.
 */
public final class MasterPasswordActivity extends IntentEchoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setRecentsScreenshotEnabled(false);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String itemId() {
        return "P08";
    }

    @Override
    protected int description() {
        return R.string.desc_master_password;
    }

    @Override
    protected String extraDetails() {
        return "\nexcludeFromRecents: true (manifest)\nFLAG_SECURE: set (screenshots and casting blocked)";
    }
}
