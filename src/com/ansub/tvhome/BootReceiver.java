package com.ansub.tvhome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.UserManager;

// Android can line Home up at boot without starting it until the TV is less busy, and on MediaTek TVs
// Live TV takes over first (see MainActivity.BOOT_WINDOW_MS), so Home's own check never runs. So open
// Home as early as apps can start: LOCKED_BOOT_COMPLETED comes as the boot animation ends, then wait for
// the user to unlock (immediate on a TV). BOOT_COMPLETED, up to half a minute later, is the fallback,
// unless a key was pressed on Home since.
public class BootReceiver extends BroadcastReceiver {
    private static final long UNLOCK_POLL_MS = 500, UNLOCK_WAIT_MS = 30_000;

    @Override
    public void onReceive(Context c, Intent i) {
        String action = i.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            if (!MainActivity.keyPressed) openHome(c);
        } else if ("android.intent.action.LOCKED_BOOT_COMPLETED".equals(action)
                && android.os.Build.VERSION.SDK_INT >= 24) {
            PendingResult result = goAsync();
            Context app = c.getApplicationContext();
            Handler h = new Handler(Looper.getMainLooper());
            long until = SystemClock.elapsedRealtime() + UNLOCK_WAIT_MS;
            h.post(new Runnable() {
                @Override public void run() {
                    UserManager um = (UserManager) app.getSystemService(Context.USER_SERVICE);
                    if (um != null && um.isUserUnlocked()) openHome(app);
                    else if (SystemClock.elapsedRealtime() < until) { h.postDelayed(this, UNLOCK_POLL_MS); return; }
                    result.finish();
                }
            });
        }
    }

    private static void openHome(Context c) {
        Intent home = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
                .setPackage(c.getPackageName()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try { c.startActivity(home); } catch (Exception ignored) {}
    }
}
