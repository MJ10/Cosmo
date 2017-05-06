package io.mokshjn.cosmo.utils;

/**
 * Created by moksh on 22/3/17.
 */

public class MathUtils {
    private MathUtils() {
    }

    public static float constrain(float min, float max, float v) {
        return Math.max(min, Math.min(max, v));
    }
}
