
package com.android.systemui.statusbar.toggles;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;;
import android.view.View;
import com.android.internal.util.aokp.SysHelpers;

import com.android.systemui.R;

public class HaloToggle extends StatefulToggle {
   
    SettingsObserver mSettingsObserver;

    @Override
    public void init(Context c, int style) {
        super.init(c, style);
        mSettingsObserver = new SettingsObserver(new Handler());
        scheduleViewUpdate();
    }

    @Override
    protected void doEnable() {
        Settings.System.putInt(mContext.getContentResolver(),
                Settings.System.HALO_ENABLED, 1);
                
        Settings.System.putInt(mContext.getContentResolver(),
                Settings.System.HALO_ACTIVE, 1);                
    }

    @Override
    protected void doDisable() {
        Settings.System.putInt(mContext.getContentResolver(),
                Settings.System.HALO_ENABLED, 0);
                
        Settings.System.putInt(mContext.getContentResolver(),
                Settings.System.HALO_ACTIVE, 0);
    }

    @Override
    public boolean onLongClick(View v) {
    Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClassName("com.android.settings", "com.android.settings.Settings$HaloActivity");
        intent.addCategory("android.intent.category.LAUNCHER");
        startActivity(intent);
        return super.onLongClick(v);
    }

    @Override
    protected void updateView() {
        boolean enabled = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.HALO_ENABLED, 0) == 1;
        setEnabledState(enabled);
        setIcon(enabled ? R.drawable.ic_notify_halo_pressed : R.drawable.ic_notify_halo_normal);
        setLabel(enabled ? R.string.quick_settings_halo_on_label
                : R.string.quick_settings_halo_off_label);
        super.updateView();
    }

    class SettingsObserver extends ContentObserver {
        SettingsObserver(Handler handler) {
            super(handler);
            observe();
        }

        void observe() {
            ContentResolver resolver = mContext.getContentResolver();
            resolver.registerContentObserver(Settings.System
                    .getUriFor(Settings.System.HALO_ENABLED), false,
                    this);
        }

        @Override
        public void onChange(boolean selfChange) {
            SysHelpers.restartSystemUI();
        }
    }
}
