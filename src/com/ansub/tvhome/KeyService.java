package com.ansub.tvhome;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

// Some remotes send their YouTube button to Google TV's home screen, which install.sh turns off,
// so the button stops working. Accessibility services see keys before that happens, so this one
// catches the button and opens SmartTube (YouTube without ads), or YouTube when SmartTube isn't
// installed. install.sh switches it on. Gamepads also have a "button 3", so they're left alone.
public class KeyService extends AccessibilityService {
    private static final String[] YOUTUBE = {"org.smarttube.stable", "org.smarttube.beta",
            "com.teamsmart.videomanager.tv", "com.google.android.youtube.tv"};

    @Override
    protected boolean onKeyEvent(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.KEYCODE_BUTTON_3 || e.isFromSource(InputDevice.SOURCE_GAMEPAD)) return false;
        if (e.getAction() == KeyEvent.ACTION_UP) {
            for (String p : YOUTUBE) {
                Intent i = MainActivity.launchIntent(getPackageManager(), p);
                if (i == null) continue;
                try { startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); } catch (Exception ignored) {}
                break;
            }
        }
        return true;
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent e) {}
    @Override public void onInterrupt() {}
}
