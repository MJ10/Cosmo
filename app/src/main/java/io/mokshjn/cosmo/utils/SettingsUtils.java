package io.mokshjn.cosmo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by moksh on 3/5/17.
 */

public class SettingsUtils {

    public static final String REPEAT_MODE = "PLAYBACK_REPEAT";

    public static void toggleRepeat(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(StorageUtils.STORAGE, Context.MODE_PRIVATE);
        boolean currentValue = preferences.getBoolean(REPEAT_MODE, false);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(REPEAT_MODE, !currentValue);
        editor.apply();
    }

    public static boolean isRepeatEnabled(Context context) {
        return context.getSharedPreferences(StorageUtils.STORAGE, Context.MODE_PRIVATE).getBoolean(REPEAT_MODE, false);
    }
}
