package io.mokshjn.cosmo.transitions;

import android.content.Context;
import android.graphics.Rect;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by moksh on 22/3/17.
 */

class AlbumArtSharedEnter extends ChangeBounds {

    private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";
    private static final String PROPNAME_PARENT = "android:changeBounds:parent";

    public AlbumArtSharedEnter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        int width = ((View) transitionValues.values.get(PROPNAME_PARENT)).getWidth();
        Rect bounds = (Rect) transitionValues.values.get(PROPNAME_BOUNDS);
        bounds.right = width;
        bounds.bottom = width * 3 / 4;
        transitionValues.values.put(PROPNAME_BOUNDS, bounds);
    }

}
