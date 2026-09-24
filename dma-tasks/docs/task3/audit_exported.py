#!/usr/bin/env python3
"""P20: flags exported components that no permission protects.

CRITICAL findings make the exit status non-zero; NOTE lines are public entry
points (launcher, deep links, system bindings) whose input must be validated.

Reads the binary manifest of a built APK through `aapt2 dump xmltree`, so it
audits exactly what the device will parse, merged libraries included.
"""
import re
import subprocess
import sys

COMPONENTS = ("activity", "activity-alias", "service", "receiver", "provider")
ATTR = re.compile(r'A: http://schemas.android.com/apk/res/android:(\w+)\(0x[0-9a-f]+\)=(?:"([^"]*)"|(\S+))')


def parse(apk):
    """Rebuilds the element tree from the indentation of the xmltree dump."""
    dump = subprocess.run(["aapt2", "dump", "xmltree", "--file", "AndroidManifest.xml", apk],
                          check=True, capture_output=True, text=True).stdout
    root = {"tag": None, "attrs": {}, "children": []}
    stack = [(-1, root)]
    for line in dump.splitlines():
        indent = len(line) - len(line.lstrip())
        text = line.strip()
        if text.startswith("E: "):
            while stack[-1][0] >= indent:
                stack.pop()
            node = {"tag": text.split()[1], "attrs": {}, "children": []}
            stack[-1][1]["children"].append(node)
            stack.append((indent, node))
        elif text.startswith("A: "):
            match = ATTR.match(text)
            if match:
                stack[-1][1]["attrs"][match[1]] = match[2] if match[2] is not None else match[3]
    return root["children"][0]


def severity(node):
    """CRITICAL when the component is internal by nature, NOTE for a public entry point."""
    if node["tag"] == "provider":
        return "CRITICAL"
    actions = [a["attrs"].get("name", "")
               for f in node["children"] if f["tag"] == "intent-filter"
               for a in f["children"] if a["tag"] == "action"]
    if not actions:
        return "CRITICAL"  # reachable only by an explicit intent, so nobody outside needs it
    if node["tag"] == "receiver" and not any(a.startswith("android.") for a in actions):
        return "CRITICAL"  # a custom broadcast any app can forge
    return "NOTE"


def findings(manifest):
    app = next(c for c in manifest["children"] if c["tag"] == "application")
    for node in app["children"]:
        if node["tag"] not in COMPONENTS:
            continue
        attrs = node["attrs"]
        filtered = any(c["tag"] == "intent-filter" for c in node["children"])
        # Before API 31 an intent filter implied exported=true; honour that default.
        exported = attrs.get("exported", str(filtered).lower()) == "true"
        if node["tag"] == "provider":
            guarded = "permission" in attrs or ("readPermission" in attrs and "writePermission" in attrs)
        else:
            guarded = "permission" in attrs
        if exported and not guarded:
            yield severity(node), node["tag"], attrs.get("name", "?")


def main():
    exit_code = 0
    for apk in sys.argv[1:]:
        issues = sorted(findings(parse(apk)))
        critical = sum(level == "CRITICAL" for level, _, _ in issues)
        print(f"{apk}: {critical} critical, {len(issues) - critical} public entry point(s) to review")
        for level, tag, name in issues:
            print(f"  {level:<8}  {tag:<14} {name}")
        exit_code |= critical > 0
    return exit_code


if __name__ == "__main__":
    sys.exit(main())
