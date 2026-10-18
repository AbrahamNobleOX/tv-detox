#!/bin/sh
# Installs the home screen on your TV and makes it the default.
# Usage: sh install.sh <TV_IP>
set -e
[ -n "$1" ] || { echo "Usage: sh install.sh <TV_IP>" >&2; exit 1; }
cd "$(dirname "$0")"
TV="$1:5555"
[ -f out/tvhome.apk ] || sh build.sh
adb connect "$TV"
adb -s "$TV" install -r out/tvhome.apk
adb -s "$TV" shell cmd package set-home-activity com.ansub.tvhome/.MainActivity
adb -s "$TV" shell input keyevent KEYCODE_HOME
echo "Done. Press Home on your remote to see it."
