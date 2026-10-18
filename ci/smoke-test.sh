#!/bin/sh
# Installs the home screen on an Android TV (real or emulator) exactly like a user would,
# then checks it works. Usage: sh ci/smoke-test.sh <adb serial or TV_IP> [screenshot.png]
set -e
cd "$(dirname "$0")/.."
TV="$1"; SHOT="${2:-smoke.png}"
fail() { echo "FAIL: $*" >&2; exit 1; }
ui() { adb -s "$TV" shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1; adb -s "$TV" shell cat /sdcard/ui.xml; }
focus() { adb -s "$TV" shell dumpsys window | grep -m1 mCurrentFocus; }

echo "== Android $(adb -s "$TV" shell getprop ro.build.version.release | tr -d '\r'), $(adb -s "$TV" shell wm size | tail -1 | tr -d '\r')"

echo "== install"
YES=1 sh install.sh "$TV"
sleep 3
home=$(adb -s "$TV" shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME | tail -1 | tr -d '\r')
[ "$home" = "com.ansub.tvhome/.MainActivity" ] || fail "default home is $home"
adb -s "$TV" shell input keyevent KEYCODE_HOME; sleep 3
focus | grep -q com.ansub.tvhome || fail "home screen not in front: $(focus)"

echo "== home screen shows a clock and Settings"
x=$(ui)
echo "$x" | grep -q 'text="Settings"' || fail "no Settings tile"
echo "$x" | grep -qE 'text="[0-9]{1,2}:[0-9]{2}"' || fail "no clock"
tiles=$(echo "$x" | grep -o 'focusable="true"' | wc -l | tr -d ' ')
echo "   $tiles tiles"

echo "== remote: move right, open Settings, come back with Home"
adb -s "$TV" shell input keyevent KEYCODE_DPAD_RIGHT
adb -s "$TV" shell am start -a android.settings.SETTINGS >/dev/null; sleep 3
adb -s "$TV" shell input keyevent KEYCODE_HOME; sleep 3
focus | grep -q com.ansub.tvhome || fail "Home didn't come back: $(focus)"

echo "== custom app list"
# Use whatever TV app this device has (test images may have none).
app=$(adb -s "$TV" shell cmd package query-activities --brief -a android.intent.action.MAIN -c android.intent.category.LEANBACK_LAUNCHER \
  | tr -d '\r' | grep / | sed 's|/.*||; s/^ *//' | grep -vx com.ansub.tvhome | head -1)
if [ -n "$app" ]; then
  YES=1 sh install.sh "$TV" "$app" >/dev/null
  sleep 3
  focus | grep -q com.ansub.tvhome || fail "home screen not in front after custom list"
  n=$(ui | grep -o 'focusable="true"' | wc -l | tr -d ' ')
  [ "$n" -ge 2 ] || fail "custom app $app didn't show up"
  echo "   $app shows up"
else
  echo "   skipped: this device has no TV apps"
fi

adb -s "$TV" exec-out screencap -p > "$SHOT"

echo "== undo puts the TV back"
sh undo.sh "$TV" >/dev/null
sleep 3
home=$(adb -s "$TV" shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME | tail -1 | tr -d '\r')
case "$home" in *com.ansub.tvhome*|"") fail "after undo, home is '$home'" ;; esac
echo "   home is back to $home"
echo "PASS ($SHOT)"
