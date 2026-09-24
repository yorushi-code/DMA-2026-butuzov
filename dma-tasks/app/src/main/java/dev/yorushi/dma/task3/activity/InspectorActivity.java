package dev.yorushi.dma.task3.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import dev.yorushi.dma.MainApplication;
import dev.yorushi.dma.R;
import dev.yorushi.dma.task3.IntentCatalog;
import dev.yorushi.dma.task3.ManifestReport;
import java.util.Arrays;
import java.util.List;

/**
 * Main screen of the task-3 app: shows how Android resolved every intent filter
 * and what it registered from the manifest, and opens the internal screens.
 */
public final class InspectorActivity extends AppCompatActivity {

    private static final String NAMESPACE = MainApplication.class.getPackage().getName();

    /** Screens without intent filters, reachable only from inside the app. */
    private static final List<Object[]> INTERNAL_SCREENS = Arrays.asList(
            new Object[] {"T3.3", GameActivity.class},
            new Object[] {"T3.4", IncomingCallActivity.class},
            new Object[] {"P05", PipPlayerActivity.class},
            new Object[] {"P08", MasterPasswordActivity.class},
            new Object[] {"P17", AccessibilityGatedActivity.class},
            new Object[] {"P19", TransactionConfirmActivity.class},
            new Object[] {"P41", FoldableActivity.class});

    private ComponentName defaultLauncher;
    private ComponentName newYearLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.inspector_title);
        setContentView(R.layout.activity_inspector);

        defaultLauncher = new ComponentName(this, SplashActivity.class);
        newYearLauncher = new ComponentName(getPackageName(), NAMESPACE + ".NewYearLauncher");
        findViewById(R.id.toggleIcon).setOnClickListener(v -> toggleLauncherIcon());
        showIconState();

        addProbeButtons();
        addInternalScreenButtons();
        ((TextView) findViewById(R.id.report)).setText(ManifestReport.build(this));
    }

    /** One button per intent filter; it shows the component Android resolved and opens it. */
    private void addProbeButtons() {
        LinearLayout container = findViewById(R.id.probes);
        PackageManager pm = getPackageManager();
        for (IntentCatalog.Probe probe : IntentCatalog.PROBES) {
            Intent intent = probe.intentFor(getPackageName());
            // Flags 0 rather than MATCH_DEFAULT_ONLY: LAUNCHER, SEARCH, NFC and USB
            // filters legitimately omit CATEGORY_DEFAULT.
            ResolveInfo resolved = pm.resolveActivity(intent, 0);
            String target = resolved == null ? getString(R.string.not_resolved)
                    : resolved.activityInfo.name.substring(resolved.activityInfo.name.lastIndexOf('.') + 1);
            MaterialButton button = button(probe.itemId + "  " + probe.describe() + "\n→ " + target);
            if (resolved != null) {
                ComponentName component = new ComponentName(resolved.activityInfo.packageName,
                        resolved.activityInfo.name);
                button.setOnClickListener(v -> startActivity(new Intent(intent).setComponent(component)));
            } else {
                button.setEnabled(false);
            }
            container.addView(button);
        }
    }

    private void addInternalScreenButtons() {
        LinearLayout container = findViewById(R.id.internalScreens);
        for (Object[] screen : INTERNAL_SCREENS) {
            Class<?> activity = (Class<?>) screen[1];
            MaterialButton button = button(screen[0] + "  " + activity.getSimpleName());
            button.setOnClickListener(v -> startActivity(new Intent(this, activity)));
            container.addView(button);
        }
    }

    private MaterialButton button(String text) {
        MaterialButton button = new MaterialButton(this, null,
                com.google.android.material.R.attr.materialButtonOutlinedStyle);
        button.setText(text);
        button.setAllCaps(false);
        button.setTextAlignment(TextView.TEXT_ALIGNMENT_VIEW_START);
        button.setTextSize(12f);
        return button;
    }

    /** P04: exactly one of the two launcher entries is enabled at any time. */
    private void toggleLauncherIcon() {
        boolean newYearNow = isEnabled(newYearLauncher);
        setEnabled(newYearLauncher, !newYearNow);
        setEnabled(defaultLauncher, newYearNow);
        showIconState();
    }

    private void showIconState() {
        ((TextView) findViewById(R.id.iconState)).setText(isEnabled(newYearLauncher)
                ? R.string.icon_now_new_year : R.string.icon_now_default);
    }

    private boolean isEnabled(ComponentName component) {
        return getPackageManager().getComponentEnabledSetting(component)
                == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
    }

    private void setEnabled(ComponentName component, boolean enabled) {
        getPackageManager().setComponentEnabledSetting(component, enabled
                        ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
