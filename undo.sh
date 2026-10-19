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

# Turn back on whatever install.sh turned off, before removing this home screen.
for p in $(adb -s "$TV" shell cat /data/local/tmp/tvhome-disabled.txt 2>/dev/null | tr -d '\r' | sort -u); do
  adb -s "$TV" shell pm enable "$p" >/dev/null && echo "Turned $p back on"
done
adb -s "$TV" shell rm -f /data/local/tmp/tvhome-disabled.txt
# Turn off the remote-buttons service if install.sh turned it on, and put accessibility back as it was.
was=$(adb -s "$TV" shell cat /data/local/tmp/tvhome-keys.txt 2>/dev/null | tr -d '\r')
if [ -n "$was" ]; then
  on=$(adb -s "$TV" shell settings get secure enabled_accessibility_services | tr -d '\r' \
    | tr ':' '\n' | grep -vx com.ansub.tvhome/com.ansub.tvhome.KeyService | paste -sd: -)
  if [ -n "$on" ] && [ "$on" != null ]; then adb -s "$TV" shell settings put secure enabled_accessibility_services "$on"
  else adb -s "$TV" shell settings delete secure enabled_accessibility_services >/dev/null; fi
  case "$was" in
    0|1) adb -s "$TV" shell settings put secure accessibility_enabled "$was" ;;
    *)   adb -s "$TV" shell settings delete secure accessibility_enabled >/dev/null ;;
  esac
  adb -s "$TV" shell rm -f /data/local/tmp/tvhome-keys.txt
fi
adb -s "$TV" uninstall com.ansub.tvhome >/dev/null 2>&1 || true
adb -s "$TV" shell input keyevent KEYCODE_HOME
echo "Removed. Your TV is back to its own home screen."
