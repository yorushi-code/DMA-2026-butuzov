#!/usr/bin/env bash
# Checks the merged manifests of the built APKs and the standalone manifests in
# docs/task3. Run from dma-tasks/ after:
#   ./gradlew assembleMobileDebug assembleMobileRelease assembleWearDebug
# Needs ANDROID_HOME; apkanalyzer (cmdline-tools) and aapt2 (build-tools) are
# looked up there. Every command is echoed, prefixed with "+", before its output.
set -euo pipefail

sdk=${ANDROID_HOME:?ANDROID_HOME is not set}
build_tools=$(ls -d "$sdk"/build-tools/* | sort -V | tail -1)
platform=$(ls -d "$sdk"/platforms/android-* | sort -V | tail -1)
export PATH="$sdk/cmdline-tools/latest/bin:$build_tools:$PATH"
apks=app/build/outputs/apk
out=build/task3-manifests
mkdir -p "$out"

run() {
    printf '\n+ %s\n' "$*"
    eval "$@"
}

echo "== Merged manifests (T1.2, T1.3, P01, P15, P42)"
for apk in "$apks"/mobile/debug/app-mobile-debug.apk "$apks"/mobile/release/app-mobile-release.apk \
        "$apks"/wear/debug/app-wear-debug.apk; do
    printf '%-24s id=%-20s minSdk=%s debuggable=%-5s allowBackup=%s READ_PHONE_STATE=%s type.watch=%s\n' \
        "$(basename "$apk")" \
        "$(apkanalyzer manifest application-id "$apk")" \
        "$(apkanalyzer manifest min-sdk "$apk")" \
        "$(apkanalyzer manifest debuggable "$apk")" \
        "$(apkanalyzer manifest print "$apk" | grep -o 'allowBackup="[a-z]*"' | cut -d'"' -f2)" \
        "$(apkanalyzer manifest permissions "$apk" | grep -c READ_PHONE_STATE || true)" \
        "$(apkanalyzer manifest print "$apk" | grep -c 'android.hardware.type.watch' || true)"
done

echo
echo "== Declared-only items, read back from the release APK (T6.3, P18, P41)"
release="$apks"/mobile/release/app-mobile-release.apk
apkanalyzer manifest print "$release" | python3 -c '
import re, subprocess, sys
xml = sys.stdin.read()
queries = re.search(r"<queries>(.*?)</queries>", xml, re.S).group(1)
print("queries      packages:", ", ".join(re.findall(r"<package\s+android:name=\"([^\"]+)", queries)),
      "| schemes:", ", ".join(re.findall(r"android:scheme=\"([^\"]+)", queries)))
dump = subprocess.run(["aapt2", "dump", "resources", sys.argv[1]], capture_output=True, text=True).stdout
def resource(ref):
    m = re.search(r"resource " + ref.split("/")[1] + r" (\S+)", dump)
    return m.group(1) if m else ref
print("backup      ", " ".join(f"{a}=@{resource(v)}" for a, v in
      re.findall(r"android:(fullBackupContent|dataExtractionRules)=\"([^\"]+)", xml)))
foldable = next(e for e in xml.split("<activity") if "FoldableActivity" in e.split(">")[0])
print("foldable    ", " ".join(re.findall(r"android:(?:resizeableActivity|minAspectRatio|maxAspectRatio)=\"[^\"]+\"", foldable)))
' "$release"

echo
echo "== T1.5: minimal manifest"
run aapt2 link -I "${platform/#$sdk/\$ANDROID_HOME}/android.jar" \
    --manifest docs/task3/minimal/AndroidManifest.xml -o "$out"/minimal.apk
run "aapt2 dump badging $out/minimal.apk | head -3"

echo
echo "== P20: exported-component audit"
for variant in p20-vulnerable p20-fixed; do
    aapt2 link -I "$platform/android.jar" --manifest docs/task3/$variant/AndroidManifest.xml -o "$out/$variant.apk"
done
run "docs/task3/audit_exported.py $out/p20-vulnerable.apk || echo \"exit status \$?\""
run docs/task3/audit_exported.py "$out"/p20-fixed.apk
run "docs/task3/audit_exported.py $apks/mobile/release/app-mobile-release.apk | head -4"
