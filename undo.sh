#!/bin/sh
# Removes the home screen and turns your TV's own home screen back on.
# Usage: sh undo.sh <TV_IP>
set -e
[ -n "$1" ] || { echo "Usage: sh undo.sh <TV_IP>" >&2; exit 1; }
case "$1" in
  *.*.*.*:*) TV=$1 ;;
  *.*.*.*)   TV=$1:5555 ;;
  *)         TV=$1 ;;
esac
case "$TV" in *:*) adb connect "$TV" >/dev/null || true ;; esac
[ "$(adb -s "$TV" get-state 2>/dev/null)" = device ] || { echo "Can't reach $TV." >&2; exit 1; }

failed=
is_off() { adb -s "$TV" shell pm list packages -d "$1" | tr -d '\r' | grep -Fqx "package:$1"; }
# Turns an app back on and checks it worked. An app that's no longer installed counts as done.
turn_on() {
  adb -s "$TV" shell pm enable "$1" >/dev/null 2>&1 || true
  if is_off "$1"; then echo "Couldn't turn $1 back on." >&2; failed=1; else echo "Turned $1 back on"; fi
}

# Turn back on whatever install.sh turned off, before removing this home screen.
for p in $(adb -s "$TV" shell cat /data/local/tmp/tvhome-disabled.txt 2>/dev/null | tr -d '\r' | sort -u); do
  turn_on "$p"
done
# Google TV's setup app may have been turned off by hand (see the README), so turn it back on too.
W=com.google.android.tungsten.setupwraith
if is_off "$W"; then turn_on "$W"; fi

# If something stayed off, keep this home screen and the list, so Home still works and
# undo.sh can try again.
if [ -n "$failed" ]; then
  echo "Kept this home screen so your TV still has one. Run undo.sh again, or turn the app on with: adb shell pm enable <package>" >&2
  exit 1
fi
adb -s "$TV" shell rm -f /data/local/tmp/tvhome-disabled.txt
adb -s "$TV" uninstall com.ansub.tvhome >/dev/null 2>&1 || true
adb -s "$TV" shell input keyevent KEYCODE_HOME
echo "Removed. Your TV is back to its own home screen."
