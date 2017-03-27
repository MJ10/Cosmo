package io.mokshjn.cosmo.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by moksh on 27/3/17.
 */

public class FontUtil {
    private static final Map<String, Typeface> sTypefaceCache = new HashMap<>();

    private FontUtil() {
    }

    public static Typeface get(Context context, String font) {
        synchronized (sTypefaceCache) {
            if (!sTypefaceCache.containsKey(font)) {
                Typeface tf = Typeface.createFromAsset(
                        context.getApplicationContext().getAssets(), "fonts/" + font + ".ttf");
                sTypefaceCache.put(font, tf);
            }
            return sTypefaceCache.get(font);
        }
    }

    public static String getName(@NonNull Typeface typeface) {
        for (Map.Entry<String, Typeface> entry : sTypefaceCache.entrySet()) {
            if (entry.getValue() == typeface) {
                return entry.getKey();
            }
        }
        return null;
    }
}
