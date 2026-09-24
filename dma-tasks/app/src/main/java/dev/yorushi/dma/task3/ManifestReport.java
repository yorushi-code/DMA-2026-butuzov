package dev.yorushi.dma.task3;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PathPermission;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.PatternMatcher;
import android.security.NetworkSecurityPolicy;
import androidx.annotation.StringRes;
import dev.yorushi.dma.MainApplication;
import dev.yorushi.dma.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders what Android actually parsed from the installed manifest. Everything
 * here is read back from PackageManager, not from the XML source, so it shows
 * the merged manifest as the system sees it.
 */
public final class ManifestReport {

    private static final String NAMESPACE = MainApplication.class.getPackage().getName();

    private ManifestReport() {
    }

    @SuppressWarnings("deprecation")
    public static PackageInfo packageInfo(Context context) {
        int flags = PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES | PackageManager.GET_RECEIVERS
                | PackageManager.GET_PROVIDERS | PackageManager.GET_PERMISSIONS | PackageManager.GET_CONFIGURATIONS
                | PackageManager.GET_META_DATA | PackageManager.GET_URI_PERMISSION_PATTERNS
                | PackageManager.MATCH_DISABLED_COMPONENTS;
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), flags);
        } catch (PackageManager.NameNotFoundException impossible) {
            throw new IllegalStateException("own package not found", impossible);
        }
    }

    public static String build(Context context) {
        PackageInfo info = packageInfo(context);
        StringBuilder out = new StringBuilder();
        application(out, info);

        List<ActivityInfo> activities = new ArrayList<>();
        List<ActivityInfo> aliases = new ArrayList<>();
        for (ActivityInfo activity : info.activities) {
            (activity.targetActivity != null ? aliases : activities).add(activity);
        }
        section(context, out, R.string.section_activities, activities.size());
        for (ActivityInfo a : activities) {
            out.append(shortName(a.name)).append(activityAttributes(a)).append('\n');
        }
        section(context, out, R.string.section_aliases, aliases.size());
        for (ActivityInfo a : aliases) {
            out.append(shortName(a.name)).append(" -> ").append(shortName(a.targetActivity))
                    .append(" enabled(manifest)=").append(a.enabled).append('\n');
        }

        section(context, out, R.string.section_services, info.services.length);
        for (ServiceInfo s : info.services) {
            out.append(shortName(s.name)).append(serviceAttributes(s)).append('\n');
        }

        section(context, out, R.string.section_receivers, info.receivers.length);
        for (ActivityInfo r : info.receivers) {
            out.append(shortName(r.name)).append(" exported=").append(r.exported)
                    .append(r.permission != null ? " permission=" + shortName(r.permission) : "").append('\n');
        }

        section(context, out, R.string.section_providers, info.providers.length);
        for (ProviderInfo p : info.providers) {
            out.append(shortName(p.name)).append(providerAttributes(p)).append('\n');
        }

        section(context, out, R.string.section_permissions, info.requestedPermissions.length);
        for (int i = 0; i < info.requestedPermissions.length; i++) {
            boolean granted = (info.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0;
            out.append(granted ? "[granted] " : "[ask]     ").append(shortName(info.requestedPermissions[i]))
                    .append('\n');
        }

        PermissionInfo[] declared = info.permissions == null ? new PermissionInfo[0] : info.permissions;
        section(context, out, R.string.section_declared, declared.length);
        for (PermissionInfo p : declared) {
            out.append(shortName(p.name)).append(isSignature(p) ? " level=signature" : "").append('\n');
        }

        FeatureInfo[] features = info.reqFeatures == null ? new FeatureInfo[0] : info.reqFeatures;
        section(context, out, R.string.section_features, features.length);
        for (FeatureInfo f : features) {
            if (f.name == null) {
                continue;
            }
            boolean required = (f.flags & FeatureInfo.FLAG_REQUIRED) != 0;
            out.append(required ? "[required] " : "[optional] ").append(f.name).append('\n');
        }
        return out.toString().trim();
    }

    @SuppressWarnings("deprecation") // getProtection() exists only from API 28
    private static boolean isSignature(PermissionInfo p) {
        int base = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                ? p.getProtection() : p.protectionLevel & PermissionInfo.PROTECTION_MASK_BASE;
        return base == PermissionInfo.PROTECTION_SIGNATURE;
    }

    private static void application(StringBuilder out, PackageInfo info) {
        ApplicationInfo app = info.applicationInfo;
        NetworkSecurityPolicy network = NetworkSecurityPolicy.getInstance();
        out.append("package      ").append(info.packageName).append(" ").append(info.versionName).append('\n')
                .append("targetSdk    ").append(app.targetSdkVersion).append('\n')
                .append("application  ").append(shortName(app.className)).append('\n')
                .append("allowBackup  ").append((app.flags & ApplicationInfo.FLAG_ALLOW_BACKUP) != 0).append('\n')
                .append("largeHeap    ").append((app.flags & ApplicationInfo.FLAG_LARGE_HEAP) != 0).append('\n')
                .append("cleartext    api.bank.example=")
                .append(network.isCleartextTrafficPermitted("api.bank.example"))
                .append(", 192.168.1.50=").append(network.isCleartextTrafficPermitted("192.168.1.50")).append('\n')
                .append("sharedUserId ")
                .append(info.sharedUserId == null ? "not applied (sharedUserMaxSdkVersion=32)" : info.sharedUserId)
                .append('\n');
    }

    private static String activityAttributes(ActivityInfo a) {
        StringBuilder s = new StringBuilder();
        s.append(a.exported ? " exported" : "");
        String[] modes = {"", "singleTop", "singleTask", "singleInstance", "singleInstancePerTask"};
        if (a.launchMode > 0 && a.launchMode < modes.length) {
            s.append(" launchMode=").append(modes[a.launchMode]);
        }
        if ((a.flags & ActivityInfo.FLAG_EXCLUDE_FROM_RECENTS) != 0) {
            s.append(" excludeFromRecents");
        }
        if (a.permission != null) {
            s.append(" permission=").append(shortName(a.permission));
        }
        // The platform also sets internal bits (window configuration, asset paths)
        // on every activity, so only the flags a manifest can declare are listed.
        String declared = configChanges(a.configChanges);
        if (!declared.isEmpty()) {
            s.append(" configChanges=").append(declared);
        }
        if (a.windowLayout != null && a.windowLayout.minWidth > 0) {
            float density = android.content.res.Resources.getSystem().getDisplayMetrics().density;
            s.append(" minSize=").append(Math.round(a.windowLayout.minWidth / density)).append('x')
                    .append(Math.round(a.windowLayout.minHeight / density)).append("dp");
        }
        return s.toString();
    }

    private static String configChanges(int bits) {
        List<String> names = new ArrayList<>();
        if ((bits & ActivityInfo.CONFIG_ORIENTATION) != 0) {
            names.add("orientation");
        }
        if ((bits & ActivityInfo.CONFIG_SCREEN_SIZE) != 0) {
            names.add("screenSize");
        }
        if ((bits & ActivityInfo.CONFIG_SMALLEST_SCREEN_SIZE) != 0) {
            names.add("smallestScreenSize");
        }
        if ((bits & ActivityInfo.CONFIG_SCREEN_LAYOUT) != 0) {
            names.add("screenLayout");
        }
        if ((bits & ActivityInfo.CONFIG_KEYBOARD_HIDDEN) != 0) {
            names.add("keyboardHidden");
        }
        return String.join("|", names);
    }

    private static String serviceAttributes(ServiceInfo s) {
        StringBuilder out = new StringBuilder();
        out.append(s.exported ? " exported" : "");
        if (s.processName != null && s.processName.contains(":")) {
            out.append(" process=").append(s.processName.substring(s.processName.indexOf(':')));
        }
        if ((s.flags & ServiceInfo.FLAG_ISOLATED_PROCESS) != 0) {
            out.append(" isolatedProcess");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && s.getForegroundServiceType() != 0) {
            out.append(" fgsType=").append(foregroundServiceType(s.getForegroundServiceType()));
        }
        if (s.permission != null) {
            out.append(" permission=").append(shortName(s.permission));
        }
        return out.toString();
    }

    public static String foregroundServiceType(int type) {
        String[] names = {"dataSync", "mediaPlayback", "phoneCall", "location", "connectedDevice",
                "mediaProjection", "camera", "microphone"};
        List<String> set = new ArrayList<>();
        for (int bit = 0; bit < names.length; bit++) {
            if ((type & (1 << bit)) != 0) {
                set.add(names[bit]);
            }
        }
        return String.join("|", set);
    }

    private static String providerAttributes(ProviderInfo p) {
        StringBuilder out = new StringBuilder();
        out.append(" authority=").append(p.authority.replace(NAMESPACE, "~"));
        out.append(p.exported ? " exported" : "");
        if (p.readPermission != null) {
            out.append(" read=").append(shortName(p.readPermission));
        }
        if (p.writePermission != null) {
            out.append(" write=").append(shortName(p.writePermission));
        }
        if (p.uriPermissionPatterns != null) {
            // The patterns, not the grantUriPermissions flag (which the parser sets
            // whenever patterns exist), decide what may be granted.
            for (PatternMatcher pattern : p.uriPermissionPatterns) {
                out.append(" grantOnly=").append(pattern.getPath()).append('*');
            }
        } else {
            out.append(" grantUri=").append(p.grantUriPermissions);
        }
        if (p.pathPermissions != null) {
            for (PathPermission path : p.pathPermissions) {
                out.append(" path:").append(path.getPath());
            }
        }
        return out.toString();
    }

    private static void section(Context context, StringBuilder out, @StringRes int title, int count) {
        out.append("\n== ").append(context.getString(title)).append(" (").append(count).append(") ==\n");
    }

    /** Drops the namespace and the android.permission prefix so lines fit on a phone screen. */
    static String shortName(String name) {
        if (name == null) {
            return "null";
        }
        if (name.startsWith("android.permission.")) {
            return name.substring("android.permission.".length());
        }
        return name.startsWith(NAMESPACE + ".") ? name.substring(NAMESPACE.length()) : name;
    }
}
