package dev.yorushi.dma.task3;

import android.app.SearchManager;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import dev.yorushi.dma.task3.activity.ComposeSmsActivity;
import dev.yorushi.dma.task3.activity.ConfigFileActivity;
import dev.yorushi.dma.task3.activity.DialerActivity;
import dev.yorushi.dma.task3.activity.HiddenEntryActivity;
import dev.yorushi.dma.task3.activity.MapLinkActivity;
import dev.yorushi.dma.task3.activity.MarketplaceActivity;
import dev.yorushi.dma.task3.activity.NfcReaderActivity;
import dev.yorushi.dma.task3.activity.OAuthCallbackActivity;
import dev.yorushi.dma.task3.activity.OfficeDocumentActivity;
import dev.yorushi.dma.task3.activity.PdfViewerActivity;
import dev.yorushi.dma.task3.activity.ProductDetailsActivity;
import dev.yorushi.dma.task3.activity.SearchActivity;
import dev.yorushi.dma.task3.activity.ShareTargetActivity;
import dev.yorushi.dma.task3.activity.SplashActivity;
import dev.yorushi.dma.task3.activity.TextViewerActivity;
import dev.yorushi.dma.task3.activity.TvHomeActivity;
import dev.yorushi.dma.task3.activity.UsbDeviceActivity;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implicit intents that exercise every intent filter of the manifest, each with
 * the activity expected to receive it. The inspector screen sends these, and the
 * instrumented tests assert that Android resolves each one to its target.
 */
public final class IntentCatalog {

    /** One implicit intent and the component its filter should route it to. */
    public static final class Probe {
        public final String itemId;
        public final Class<?> expected;
        private final Intent intent;

        Probe(String itemId, Intent intent, Class<?> expected) {
            this.itemId = itemId;
            this.intent = intent;
            this.expected = expected;
        }

        /** A fresh copy restricted to {@code packageName}, so other installed apps cannot win. */
        public Intent intentFor(String packageName) {
            return new Intent(intent).setPackage(packageName);
        }

        public String describe() {
            Uri data = intent.getData();
            String type = intent.getType();
            return intent.getAction() + (data != null ? " " + data : "") + (type != null ? " [" + type + "]" : "");
        }
    }

    public static final List<Probe> PROBES = Collections.unmodifiableList(Arrays.asList(
            new Probe("T3.1", new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),
                    SplashActivity.class),
            new Probe("T4.1", new Intent(Intent.ACTION_SEND).setType("image/png")
                    .putExtra(Intent.EXTRA_STREAM, Uri.parse("content://media/external/images/7")),
                    ShareTargetActivity.class),
            new Probe("T4.2", new Intent(Intent.ACTION_VIEW, Uri.parse("tel:+79990001122")),
                    DialerActivity.class),
            new Probe("T4.3", new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://docs.example.com/reports/annual.v2.pdf")), PdfViewerActivity.class),
            new Probe("T4.4", new Intent(Intent.ACTION_VIEW).setDataAndType(
                    Uri.parse("content://notes.example/page/1"), "text/html"), TextViewerActivity.class),
            new Probe("T4.5", new Intent(Intent.ACTION_VIEW, Uri.parse("myapp://products/view?id=123")),
                    ProductDetailsActivity.class),
            new Probe("T4.5", new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://store.university.ru/item/42")), ProductDetailsActivity.class),
            new Probe("P02", new Intent("dev.yorushi.dma.action.OPEN_FROM_PARTNER"), HiddenEntryActivity.class),
            new Probe("P21", new Intent(NfcAdapter.ACTION_TECH_DISCOVERED), NfcReaderActivity.class),
            new Probe("P22", new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=Tula+Kremlin")),
                    MapLinkActivity.class),
            new Probe("P23", new Intent(Intent.ACTION_VIEW).setDataAndType(
                    Uri.parse("content://mail.example/attachments/budget.xlsx"),
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
                    OfficeDocumentActivity.class),
            new Probe("P25", new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://marketplace.com/catalog/shoes?brand=nike")), MarketplaceActivity.class),
            new Probe("P27", new Intent(Intent.ACTION_SEARCH).putExtra(SearchManager.QUERY, "coffee"),
                    SearchActivity.class),
            new Probe("P28", new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:+79990001122")),
                    ComposeSmsActivity.class),
            new Probe("P29", new Intent(Intent.ACTION_VIEW,
                    Uri.parse("org.example.app://oauth-callback?code=4f2a&state=xyz")), OAuthCallbackActivity.class),
            new Probe("P30", new Intent(Intent.ACTION_VIEW).setDataAndType(
                    Uri.parse("content://files.example/download/router.mycfg"), "application/octet-stream"),
                    ConfigFileActivity.class),
            new Probe("P44", new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER),
                    TvHomeActivity.class),
            new Probe("P49", new Intent(UsbManager.ACTION_USB_DEVICE_ATTACHED), UsbDeviceActivity.class)));

    private IntentCatalog() {
    }
}
