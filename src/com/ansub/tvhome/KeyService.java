package com.ansub.tvhome;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;

// Some remotes send their YouTube button to Google TV's home screen, which install.sh turns off,
// so the button stops working. Accessibility services see keys before that happens, so this one
// catches the button and opens SmartTube (YouTube without ads), or YouTube when SmartTube isn't
// installed. install.sh switches it on. Gamepads also have a "button 3", so they're left alone.
// While a service watches keys, Android stops repeating held keys, so holding volume moved it
// only one step. The service repeats the volume step itself until the button is let go.
public class KeyService extends AccessibilityService {
    private static final String[] YOUTUBE = {"org.smarttube.stable", "org.smarttube.beta",
            "com.teamsmart.videomanager.tv", "com.google.android.youtube.tv"};
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int heldVolumeKey;
    private final Runnable volumeRepeat = new Runnable() {
        @Override public void run() {
            getSystemService(AudioManager.class).adjustSuggestedStreamVolume(
                    heldVolumeKey == KeyEvent.KEYCODE_VOLUME_UP ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER,
                    AudioManager.USE_DEFAULT_STREAM_TYPE, AudioManager.FLAG_SHOW_UI);
            handler.postDelayed(this, ViewConfiguration.getKeyRepeatDelay());
        }
    };

    @Override
    protected boolean onKeyEvent(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.KEYCODE_VOLUME_UP || k == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // The first step is Android's own; repeats start after the usual hold delay.
            // If Android does send repeats, the timer stays off and they pass through.
            handler.removeCallbacks(volumeRepeat);
            if (e.getAction() == KeyEvent.ACTION_DOWN && e.getRepeatCount() == 0) {
                heldVolumeKey = k;
                handler.postDelayed(volumeRepeat, ViewConfiguration.getKeyRepeatTimeout());
            }
            return false;
        }
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
    @Override public void onInterrupt() { handler.removeCallbacks(volumeRepeat); }
    @Override public void onDestroy() { handler.removeCallbacks(volumeRepeat); super.onDestroy(); }
}
