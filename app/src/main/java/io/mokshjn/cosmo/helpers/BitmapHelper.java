package io.mokshjn.cosmo.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

/**
 * Created by moksh on 19/3/17.
 */

public class BitmapHelper {

    private static final String TAG = LogHelper.makeLogTag(BitmapHelper.class);

    // Max read limit that we allow our input stream to mark/reset.

    public static Bitmap scaleBitmap(Bitmap src, int maxWidth, int maxHeight) {
        double scaleFactor = Math.min(
                ((double) maxWidth)/src.getWidth(), ((double) maxHeight)/src.getHeight());
        return Bitmap.createScaledBitmap(src,
                (int) (src.getWidth() * scaleFactor), (int) (src.getHeight() * scaleFactor), false);
    }

    @SuppressWarnings("SameParameterValue")
    public static Bitmap fetchAndRescaleBitmap(String uri, Context context, int width, int height) throws IOException {
        return MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(uri));
    }
}
