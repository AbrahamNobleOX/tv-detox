package com.ansub.tvhome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// Android can line Home up at boot without starting it until the TV is less busy, and on MediaTek TVs
// Live TV takes over first (see MainActivity.BOOT_WINDOW_MS), so Home's own check never runs. Once boot
// is done, open Home: in front of Live TV if it already took over, or early enough to catch it if not.
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent i) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(i.getAction())) return;
        Intent home = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
                .setPackage(c.getPackageName()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try { c.startActivity(home); } catch (Exception ignored) {}
    }
}
