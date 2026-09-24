package dev.yorushi.dma;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import android.app.LocaleConfig;
import android.app.PictureInPictureParams;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Build;
import android.os.LocaleList;
import android.os.PatternMatcher;
import android.security.NetworkSecurityPolicy;
import android.util.Rational;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.core.content.FileProvider;
import androidx.emoji2.text.EmojiCompat;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.work.WorkManager;
import dev.yorushi.dma.task3.IntentCatalog;
import dev.yorushi.dma.task3.ManifestReport;
import dev.yorushi.dma.task3.activity.AccessibilityGatedActivity;
import dev.yorushi.dma.task3.activity.GameActivity;
import dev.yorushi.dma.task3.activity.IncomingCallActivity;
import dev.yorushi.dma.task3.activity.InspectorActivity;
import dev.yorushi.dma.task3.activity.MasterPasswordActivity;
import dev.yorushi.dma.task3.activity.PipPlayerActivity;
import dev.yorushi.dma.task3.activity.SearchActivity;
import dev.yorushi.dma.task3.activity.SplashActivity;
import dev.yorushi.dma.task3.activity.TransactionConfirmActivity;
import dev.yorushi.dma.task3.provider.NotesProvider;
import dev.yorushi.dma.task3.receiver.BootReceiver;
import dev.yorushi.dma.task3.receiver.MediaButtonReceiver;
import dev.yorushi.dma.task3.receiver.SecureSyncReceiver;
import dev.yorushi.dma.task3.service.AutoMediaService;
import dev.yorushi.dma.task3.service.CourierLocationService;
import dev.yorushi.dma.task3.service.DatabaseSyncService;
import dev.yorushi.dma.task3.service.DmaAccessibilityService;
import dev.yorushi.dma.task3.service.IsolatedRendererService;
import dev.yorushi.dma.task3.service.NightSkyWallpaperService;
import dev.yorushi.dma.task3.service.PlaybackService;
import dev.yorushi.dma.task3.service.ProtectedApiService;
import dev.yorushi.dma.task3.service.ScreenCastService;
import dev.yorushi.dma.task3.service.SyncAdapterService;
import dev.yorushi.dma.task3.service.UploadService;
import dev.yorushi.dma.task3.service.VoiceNoteService;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Task 3 verified on a device: every assertion reads the installed, merged
 * manifest back from PackageManager (or exercises the declared behaviour), so a
 * passing test means Android itself accepted the declaration.
 */
@RunWith(AndroidJUnit4.class)
public class ManifestTest {

    private static Context context;
    private static PackageManager pm;
    private static PackageInfo info;

    @BeforeClass
    public static void loadPackage() {
        context = ApplicationProvider.getApplicationContext();
        pm = context.getPackageManager();
        // The inspector's icon switch changes component state at runtime; tests
        // start from the manifest defaults.
        pm.setComponentEnabledSetting(new ComponentName(context, SplashActivity.class),
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(newYearAlias(),
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
        // Granted up front: the inspector would otherwise open the system
        // permission dialog on top of the screens the tests launch.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            InstrumentationRegistry.getInstrumentation().getUiAutomation().grantRuntimePermission(
                    context.getPackageName(), "android.permission.POST_NOTIFICATIONS");
        }
        info = ManifestReport.packageInfo(context);
    }

    // ---------------------------------------------------------------- theme 1

    @Test
    public void T1_2_mergerRemovedReadPhoneState() {
        assertFalse(requested("android.permission.READ_PHONE_STATE"));
    }

    @Test
    public void T1_3_T2_1_backupIsDisabled() {
        assertEquals(0, info.applicationInfo.flags & ApplicationInfo.FLAG_ALLOW_BACKUP);
    }

    @Test
    public void T1_4_sharedUserIdIgnoredFromApi33() {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU);
        assertNull(info.sharedUserId);
    }

    // ---------------------------------------------------------------- theme 2

    @Test
    public void T2_2_P12_cleartextOnlyForLocalDevServer() {
        NetworkSecurityPolicy policy = NetworkSecurityPolicy.getInstance();
        assertFalse(policy.isCleartextTrafficPermitted());
        assertFalse(policy.isCleartextTrafficPermitted("api.bank.example"));
        assertFalse(policy.isCleartextTrafficPermitted("example.com"));
        assertTrue(policy.isCleartextTrafficPermitted("192.168.1.50"));
    }

    @Test
    public void T2_3_customApplicationClassIsUsed() {
        assertEquals(MainApplication.class.getName(), info.applicationInfo.className);
        assertTrue(context instanceof MainApplication);
    }

    @Test
    public void T2_4_labelIsAStringResource() {
        assertEquals(R.string.app_name, info.applicationInfo.labelRes);
    }

    @Test
    public void T2_5_largeHeapRequested() {
        assertTrue((info.applicationInfo.flags & ApplicationInfo.FLAG_LARGE_HEAP) != 0);
    }

    // ---------------------------------------------------------------- theme 3

    @Test
    public void T3_1_splashIsTheOnlyLauncherEntry() {
        List<ResolveInfo> launchers = pm.queryIntentActivities(
                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
                        .setPackage(context.getPackageName()), 0);
        assertEquals(1, launchers.size());
        assertEquals(SplashActivity.class.getName(), launchers.get(0).activityInfo.name);
    }

    @Test
    public void T3_2_databaseSyncServiceIsPrivate() {
        assertFalse(service(DatabaseSyncService.class).exported);
    }

    @Test
    public void T3_3_gameHandlesRotationItself() {
        int expected = ActivityInfo.CONFIG_ORIENTATION | ActivityInfo.CONFIG_SCREEN_SIZE
                | ActivityInfo.CONFIG_KEYBOARD_HIDDEN;
        assertEquals(expected, activity(GameActivity.class).configChanges & expected);
    }

    @Test
    public void T3_4_incomingCallIsSingleInstance() {
        assertEquals(ActivityInfo.LAUNCH_SINGLE_INSTANCE, activity(IncomingCallActivity.class).launchMode);
    }

    @Test
    public void T3_5_fileProviderIsPrivateAndIssuesContentUris() throws Exception {
        ProviderInfo provider = provider(context.getPackageName() + ".fileprovider");
        assertFalse(provider.exported);
        assertTrue(provider.grantUriPermissions);
        File dir = new File(context.getFilesDir(), "camera_photos");
        assertTrue(dir.isDirectory() || dir.mkdirs());
        File photo = new File(dir, "shot.jpg");
        assertTrue(photo.exists() || photo.createNewFile());
        Uri uri = FileProvider.getUriForFile(context, provider.authority, photo);
        assertEquals("content", uri.getScheme());
        assertEquals(provider.authority, uri.getAuthority());
    }

    // ---------------------------------------------------------------- theme 4

    @Test
    public void T4_1_shareTargetReceivesImages() {
        assertRoutes("T4.1");
    }

    @Test
    public void T4_2_telLinksOpenDialer() {
        assertRoutes("T4.2");
    }

    @Test
    public void T4_3_pdfPathPatternMatchesEvenWithExtraDots() {
        assertRoutes("T4.3");
        Intent notPdf = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.example.com/report.docx"))
                .setPackage(context.getPackageName());
        for (ResolveInfo r : pm.queryIntentActivities(notPdf, 0)) {
            assertFalse(r.activityInfo.name.endsWith("PdfViewerActivity"));
        }
    }

    @Test
    public void T4_4_textAndHtmlAccepted() {
        assertRoutes("T4.4");
    }

    @Test
    public void T4_5_customSchemeAndAppLinkRouteToProduct() {
        assertRoutes("T4.5");
    }

    // ---------------------------------------------------------------- theme 5

    @Test
    public void T5_1_locationPermissionsForBackgroundTracking() {
        assertTrue(requested("android.permission.ACCESS_COARSE_LOCATION"));
        assertTrue(requested("android.permission.ACCESS_FINE_LOCATION"));
        assertTrue(requested("android.permission.ACCESS_BACKGROUND_LOCATION"));
    }

    @Test
    public void T5_2_notificationsPermissionRequested() {
        assertTrue(requested("android.permission.POST_NOTIFICATIONS"));
    }

    @Test
    public void T5_3_signaturePermissionGuardsProtectedService() {
        String name = context.getPackageName() + ".permission.BIND_PROTECTED_API";
        assertEquals(PermissionInfo.PROTECTION_SIGNATURE, protection(declared(name)));
        assertEquals(name, service(ProtectedApiService.class).permission);
    }

    @Test
    public void T5_4_writeStorageOnlyUpToApi28() {
        assumeTrue(Build.VERSION.SDK_INT > Build.VERSION_CODES.P);
        assertFalse(requested("android.permission.WRITE_EXTERNAL_STORAGE"));
    }

    @Test
    public void T5_5_exactAlarmPermissionRequested() {
        assertTrue(requested("android.permission.SCHEDULE_EXACT_ALARM"));
    }

    // ---------------------------------------------------------------- theme 6

    @Test
    public void T6_1_fingerprintIsOptional() {
        assertFalse(featureRequired("android.hardware.fingerprint"));
    }

    @Test
    public void T6_2_nfcIsRequired() {
        assertTrue(featureRequired("android.hardware.nfc"));
    }

    @Test
    public void T6_4_telephonyIsOptional() {
        assertFalse(featureRequired("android.hardware.telephony"));
    }

    @Test
    public void T6_5_gamepadIsOptional() {
        assertFalse(featureRequired("android.hardware.gamepad"));
    }

    // ---------------------------------------------------------------- theme 7

    @Test
    public void T7_1_mapKitKeyInjectedFromBuildScript() {
        assertEquals("YANDEX_MAPKIT_KEY_PLACEHOLDER",
                info.applicationInfo.metaData.getString("dev.yorushi.dma.YANDEX_MAPKIT_KEY"));
        assertEquals("YANDEX_MAPKIT_KEY_PLACEHOLDER", ((MainApplication) context).mapKitApiKey());
    }

    @Test
    public void T7_2_localeConfigOffersEnglishAndRussian() {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU);
        LocaleList locales = new LocaleConfig(context).getSupportedLocales();
        assertNotNull(locales);
        assertTrue(locales.toLanguageTags().contains("en"));
        assertTrue(locales.toLanguageTags().contains("ru"));
    }

    @Test
    public void T7_3_searchableBoundToSearchActivityOnly() {
        assertTrue(activity(SearchActivity.class).metaData.containsKey("android.app.searchable"));
        ActivityInfo other = activity(InspectorActivity.class);
        assertTrue(other.metaData == null || !other.metaData.containsKey("android.app.searchable"));
    }

    @Test
    public void T7_4_P35_startupInitializersReplacedByManualInit() {
        ProviderInfo startup = provider(context.getPackageName() + ".androidx-startup");
        assertFalse(startup.metaData != null && startup.metaData.containsKey("androidx.work.WorkManagerInitializer"));
        assertFalse(startup.metaData != null
                && startup.metaData.containsKey("androidx.emoji2.text.EmojiCompatInitializer"));
        assertTrue(EmojiCompat.isConfigured());
        assertNotNull(WorkManager.getInstance(context));
    }

    @Test
    public void T7_5_refreshRateRequestedInManifest() {
        assertEquals(120, info.applicationInfo.metaData.getInt("dev.yorushi.dma.PREFERRED_REFRESH_RATE"));
    }

    // ------------------------------------------------------- block 1: launch

    @Test
    public void P01_libraryMinSdkOverridden() {
        assertEquals(26, info.applicationInfo.minSdkVersion);
    }

    @Test
    public void P02_partnerActionReachesHiddenScreen() {
        assertRoutes("P02");
    }

    @Test
    public void P03_splashUsesSplashScreenTheme() {
        assertEquals(R.style.Theme_App_Starting, activity(SplashActivity.class).theme);
    }

    @Test
    public void P04_newYearAliasDisabledByDefault() {
        ActivityInfo alias = activity(newYearAlias().getClassName());
        assertFalse(alias.enabled);
        assertEquals(SplashActivity.class.getName(), alias.targetActivity);
    }

    @Test
    public void P05_playerEntersPictureInPicture() {
        assumeTrue(pm.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE));
        try (ActivityScenario<PipPlayerActivity> scenario = ActivityScenario.launch(PipPlayerActivity.class)) {
            boolean[] entered = {false};
            scenario.onActivity(a -> entered[0] = a.enterPictureInPictureMode(
                    new PictureInPictureParams.Builder().setAspectRatio(new Rational(16, 9)).build()));
            assertTrue(entered[0]);
        }
    }

    @Test
    public void P06_minimumWindowSizeIs300x450dp() {
        ActivityInfo.WindowLayout layout = activity(InspectorActivity.class).windowLayout;
        float density = context.getResources().getDisplayMetrics().density;
        assertEquals(300, Math.round(layout.minWidth / density));
        assertEquals(450, Math.round(layout.minHeight / density));
    }

    @Test
    public void P07_playbackRunsInSeparateProcess() {
        assertTrue(service(PlaybackService.class).processName.endsWith(":playback_process"));
        assertForegroundType(PlaybackService.class, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
    }

    @Test
    public void P08_masterPasswordExcludedFromRecents() {
        assertTrue((activity(MasterPasswordActivity.class).flags & ActivityInfo.FLAG_EXCLUDE_FROM_RECENTS) != 0);
    }

    @Test
    public void P09_masterPasswordWindowIsSecure() {
        try (ActivityScenario<MasterPasswordActivity> scenario =
                     ActivityScenario.launch(MasterPasswordActivity.class)) {
            scenario.onActivity(a -> assertTrue(
                    (a.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_SECURE) != 0));
        }
    }

    @Test
    public void P10_testsRunUnderTheCustomRunner() {
        assertTrue(InstrumentationRegistry.getInstrumentation() instanceof CustomTestRunner);
    }

    // ----------------------------------------------------- block 2: security

    @Test
    public void P11_rendererRunsInIsolatedProcess() {
        assertTrue((service(IsolatedRendererService.class).flags & ServiceInfo.FLAG_ISOLATED_PROCESS) != 0);
    }

    @Test
    public void P13_providerHasSeparateReadAndWritePermissions() {
        ProviderInfo notes = provider(context.getPackageName() + ".notes");
        assertEquals(NotesProvider.class.getName(), notes.name);
        assertEquals(context.getPackageName() + ".permission.READ_NOTES", notes.readPermission);
        assertEquals(context.getPackageName() + ".permission.WRITE_NOTES", notes.writePermission);
    }

    @Test
    public void P14_onlySharedDocsCanBeGranted() {
        ProviderInfo notes = provider(context.getPackageName() + ".notes");
        // The parser switches grantUriPermissions on whenever <grant-uri-permission>
        // is present; the patterns are what restrict it, so they are checked instead.
        assertNotNull(notes.uriPermissionPatterns);
        assertEquals(1, notes.uriPermissionPatterns.length);
        PatternMatcher pattern = notes.uriPermissionPatterns[0];
        assertEquals("/shared_docs/", pattern.getPath());
        assertEquals(PatternMatcher.PATTERN_PREFIX, pattern.getType());

        // Behaviour: a grant under /shared_docs/ succeeds, any other path is refused.
        String otherApp = "com.android.shell";
        int read = Intent.FLAG_GRANT_READ_URI_PERMISSION;
        Uri shared = Uri.parse("content://" + notes.authority + "/shared_docs/contract.txt");
        context.grantUriPermission(otherApp, shared, read);
        context.revokeUriPermission(otherApp, shared, read);
        Uri privateNote = Uri.parse("content://" + notes.authority + "/notes/1");
        assertThrows(SecurityException.class, () -> context.grantUriPermission(otherApp, privateNote, read));
    }

    @Test
    public void P16_syncReceiverGuardedBySignaturePermission() {
        ActivityInfo receiver = receiver(SecureSyncReceiver.class);
        assertFalse(receiver.exported);
        String permission = context.getPackageName() + ".permission.SYNC_BROADCAST";
        assertEquals(permission, receiver.permission);
        assertEquals(PermissionInfo.PROTECTION_SIGNATURE,
                protection(declared(permission)));
    }

    @Test
    public void P17_screenRequiresAccessibilityBindPermission() {
        assertEquals("android.permission.BIND_ACCESSIBILITY_SERVICE",
                activity(AccessibilityGatedActivity.class).permission);
    }

    @Test
    public void P19_transactionScreenFiltersObscuredTouches() {
        assertTrue(requested("android.permission.HIDE_OVERLAY_WINDOWS"));
        try (ActivityScenario<TransactionConfirmActivity> scenario =
                     ActivityScenario.launch(TransactionConfirmActivity.class)) {
            scenario.onActivity(a -> {
                ViewGroup content = a.findViewById(android.R.id.content);
                assertTrue(content.getChildAt(0).getFilterTouchesWhenObscured());
            });
        }
    }

    // --------------------------------------------- block 3: intent filters

    @Test
    public void P21_nfcTechDiscoveredRoutesToReader() {
        assertRoutes("P21");
    }

    @Test
    public void P22_geoLinksOpenMap() {
        assertRoutes("P22");
    }

    @Test
    public void P23_officeAttachmentsOpen() {
        assertRoutes("P23");
    }

    @Test
    public void P24_shortcutsDeclaredOnLauncherActivity() {
        assertTrue(activity(SplashActivity.class).metaData.containsKey("android.app.shortcuts"));
    }

    @Test
    public void P25_marketplaceAppLinkRoutes() {
        assertRoutes("P25");
    }

    @Test
    public void P26_mediaButtonReceiverRegistered() {
        assertBroadcastRoutes(Intent.ACTION_MEDIA_BUTTON, MediaButtonReceiver.class);
    }

    @Test
    public void P27_searchActionRoutes() {
        assertRoutes("P27");
    }

    @Test
    public void P28_smstoOpensComposer() {
        assertRoutes("P28");
    }

    @Test
    public void P29_oauthRedirectRoutes() {
        assertRoutes("P29");
    }

    @Test
    public void P30_mycfgFilesOpen() {
        assertRoutes("P30");
    }

    // ------------------------------------------------ block 4: services

    @Test
    public void P31_voiceNotesAreMicrophoneService() {
        assertForegroundType(VoiceNoteService.class, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE);
        assertTrue(requested("android.permission.FOREGROUND_SERVICE_MICROPHONE"));
    }

    @Test
    public void P32_courierIsLocationService() {
        assertForegroundType(CourierLocationService.class, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION);
        assertTrue(requested("android.permission.FOREGROUND_SERVICE_LOCATION"));
    }

    @Test
    public void P33_uploadIsDataSyncService() {
        assertForegroundType(UploadService.class, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        assertTrue(requested("android.permission.FOREGROUND_SERVICE_DATA_SYNC"));
    }

    @Test
    public void P34_castIsMediaProjectionService() {
        assertForegroundType(ScreenCastService.class, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
        assertTrue(requested("android.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION"));
    }

    @Test
    public void P36_bootReceiverRegistered() {
        assertTrue(requested("android.permission.RECEIVE_BOOT_COMPLETED"));
        assertBroadcastRoutes(Intent.ACTION_BOOT_COMPLETED, BootReceiver.class);
    }

    @Test
    public void P37_batteryOptimisationPermissionRequested() {
        assertTrue(requested("android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"));
    }

    @Test
    public void P38_accessibilityServiceDeclared() {
        ServiceInfo service = service(DmaAccessibilityService.class);
        assertEquals("android.permission.BIND_ACCESSIBILITY_SERVICE", service.permission);
        assertTrue(service.metaData.containsKey("android.accessibilityservice"));
    }

    @Test
    public void P39_syncAdapterAndAuthenticatorDeclared() {
        assertServiceRoutes("android.content.SyncAdapter", SyncAdapterService.class);
        assertTrue(service(SyncAdapterService.class).metaData.containsKey("android.content.SyncAdapter"));
    }

    @Test
    public void P40_liveWallpaperDeclared() {
        assertEquals("android.permission.BIND_WALLPAPER", service(NightSkyWallpaperService.class).permission);
        assertServiceRoutes("android.service.wallpaper.WallpaperService", NightSkyWallpaperService.class);
    }

    // ------------------------------------------------ block 5: hardware

    @Test
    public void P43_androidAutoDescriptorAndMediaService() {
        assertTrue(info.applicationInfo.metaData.containsKey("com.google.android.gms.car.application"));
        assertServiceRoutes("android.media.browse.MediaBrowserService", AutoMediaService.class);
    }

    @Test
    public void P44_tvLauncherEntryWithoutTouchscreen() {
        assertRoutes("P44");
        assertFalse(featureRequired("android.hardware.touchscreen"));
        assertFalse(featureRequired("android.software.leanback"));
    }

    @Test
    public void P45_advancedMultitouchIsOptional() {
        assertFalse(featureRequired("android.hardware.touchscreen.multitouch.jazzhand"));
    }

    @Test
    public void P46_smallScreensExcluded() {
        int flags = info.applicationInfo.flags;
        assertEquals(0, flags & ApplicationInfo.FLAG_SUPPORTS_SMALL_SCREENS);
        assertTrue((flags & ApplicationInfo.FLAG_SUPPORTS_NORMAL_SCREENS) != 0);
        assertTrue((flags & ApplicationInfo.FLAG_SUPPORTS_LARGE_SCREENS) != 0);
    }

    @Test
    public void P47_highSamplingRatePermissionRequested() {
        assertTrue(requested("android.permission.HIGH_SAMPLING_RATE_SENSORS"));
    }

    @Test
    public void P48_flashIsOptional() {
        assertFalse(featureRequired("android.hardware.camera.flash"));
    }

    @Test
    public void P49_usbAttachStartsTheApp() {
        assertRoutes("P49");
    }

    @Test
    public void P50_productionManifestHasAtLeastThreeDangerousPermissions() throws Exception {
        int dangerous = 0;
        for (String name : info.requestedPermissions) {
            try {
                PermissionInfo p = pm.getPermissionInfo(name, 0);
                if (protection(p) == PermissionInfo.PROTECTION_DANGEROUS) {
                    dangerous++;
                }
            } catch (PackageManager.NameNotFoundException unknownOnThisApiLevel) {
                // Permissions from newer API levels are simply not counted.
            }
        }
        assertTrue("dangerous permissions: " + dangerous, dangerous >= 3);
    }

    // ------------------------------------------- all filters at once

    @Test
    public void everyCatalogIntentRoutesToItsActivity() {
        for (IntentCatalog.Probe probe : IntentCatalog.PROBES) {
            assertProbe(probe);
        }
    }

    // --------------------------------------------------------------- helpers

    private static ComponentName newYearAlias() {
        return new ComponentName(context.getPackageName(), MainApplication.class.getPackage().getName()
                + ".NewYearLauncher");
    }

    private static void assertRoutes(String itemId) {
        int checked = 0;
        for (IntentCatalog.Probe probe : IntentCatalog.PROBES) {
            if (probe.itemId.equals(itemId)) {
                assertProbe(probe);
                checked++;
            }
        }
        assertTrue("no probe for " + itemId, checked > 0);
    }

    private static void assertProbe(IntentCatalog.Probe probe) {
        // Flags 0: LAUNCHER, SEARCH, NFC and USB filters legitimately have no DEFAULT category.
        ResolveInfo resolved = pm.resolveActivity(probe.intentFor(context.getPackageName()), 0);
        assertNotNull(probe.itemId + " not resolved: " + probe.describe(), resolved);
        assertEquals(probe.itemId + " " + probe.describe(), probe.expected.getName(), resolved.activityInfo.name);
    }

    private static void assertBroadcastRoutes(String action, Class<?> receiver) {
        List<ResolveInfo> receivers = pm.queryBroadcastReceivers(
                new Intent(action).setPackage(context.getPackageName()), PackageManager.MATCH_DISABLED_COMPONENTS);
        boolean found = false;
        for (ResolveInfo r : receivers) {
            found |= r.activityInfo.name.equals(receiver.getName());
        }
        assertTrue(action + " does not reach " + receiver.getSimpleName(), found);
    }

    private static void assertServiceRoutes(String action, Class<?> service) {
        List<ResolveInfo> services = pm.queryIntentServices(new Intent(action).setPackage(context.getPackageName()), 0);
        boolean found = false;
        for (ResolveInfo r : services) {
            found |= r.serviceInfo.name.equals(service.getName());
        }
        assertTrue(action + " does not reach " + service.getSimpleName(), found);
    }

    private static void assertForegroundType(Class<?> service, int type) {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q);
        assertEquals(ManifestReport.foregroundServiceType(type),
                ManifestReport.foregroundServiceType(service(service).getForegroundServiceType()));
    }

    private static boolean requested(String permission) {
        return info.requestedPermissions != null && Arrays.asList(info.requestedPermissions).contains(permission);
    }

    private static PermissionInfo declared(String name) {
        for (PermissionInfo p : info.permissions) {
            if (p.name.equals(name)) {
                return p;
            }
        }
        throw new AssertionError("permission not declared: " + name);
    }

    @SuppressWarnings("deprecation") // getProtection() exists only from API 28
    private static int protection(PermissionInfo p) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                ? p.getProtection() : p.protectionLevel & PermissionInfo.PROTECTION_MASK_BASE;
    }

    private static boolean featureRequired(String name) {
        for (FeatureInfo f : info.reqFeatures) {
            if (name.equals(f.name)) {
                return (f.flags & FeatureInfo.FLAG_REQUIRED) != 0;
            }
        }
        throw new AssertionError("feature not declared: " + name);
    }

    private static ActivityInfo activity(Class<?> type) {
        return activity(type.getName());
    }

    private static ActivityInfo activity(String name) {
        for (ActivityInfo a : info.activities) {
            if (a.name.equals(name)) {
                return a;
            }
        }
        throw new AssertionError("activity not declared: " + name);
    }

    private static ServiceInfo service(Class<?> type) {
        for (ServiceInfo s : info.services) {
            if (s.name.equals(type.getName())) {
                return s;
            }
        }
        throw new AssertionError("service not declared: " + type.getName());
    }

    private static ActivityInfo receiver(Class<?> type) {
        for (ActivityInfo r : info.receivers) {
            if (r.name.equals(type.getName())) {
                return r;
            }
        }
        throw new AssertionError("receiver not declared: " + type.getName());
    }

    private static ProviderInfo provider(String authority) {
        for (ProviderInfo p : info.providers) {
            if (authority.equals(p.authority)) {
                return p;
            }
        }
        throw new AssertionError("provider not declared: " + authority);
    }
}
