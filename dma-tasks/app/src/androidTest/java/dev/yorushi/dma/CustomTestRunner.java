package dev.yorushi.dma;

import android.os.Bundle;
import android.util.Log;
import androidx.test.runner.AndroidJUnitRunner;

/** P10: instrumentation runner declared through <instrumentation>. */
public final class CustomTestRunner extends AndroidJUnitRunner {

    @Override
    public void onCreate(Bundle arguments) {
        Log.i("CustomTestRunner", "DMA manifest tests started by the custom runner");
        super.onCreate(arguments);
    }
}
