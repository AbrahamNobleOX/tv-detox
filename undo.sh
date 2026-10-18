#!/bin/sh
# Removes the home screen. Usage: sh undo.sh <TV_IP>
# If you turned off your TV's original launcher, turn it back on first
# (adb shell pm enable <package>), or the TV will have no home screen.
set -e
[ -n "$1" ] || { echo "Usage: sh undo.sh <TV_IP>" >&2; exit 1; }
TV="$1:5555"
adb connect "$TV"
adb -s "$TV" uninstall com.ansub.tvhome
echo "Removed. Press Home on your remote; the TV goes back to its other launcher."
