package io.mokshjn.cosmo.transitions;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Keep;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import io.mokshjn.cosmo.utils.ViewUtils;

/**
 * Created by moksh on 22/3/17.
 */

class BackgroundFade extends Visibility {

    public BackgroundFade() {
        super();
    }

    @Keep
    public BackgroundFade(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Animator onAppear(ViewGroup sceneRoot, View view,
                             TransitionValues startValues, TransitionValues endValues) {
        if (view == null || view.getBackground() == null) return null;
        Drawable background = view.getBackground();
        background.setAlpha(0);
        return ObjectAnimator.ofInt(background, ViewUtils.DRAWABLE_ALPHA, 0, 255);
    }

    @Override
    public Animator onDisappear(ViewGroup sceneRoot, View view,
                                TransitionValues startValues, TransitionValues endValues) {
        if (view == null || view.getBackground() == null) return null;
        return ObjectAnimator.ofInt(view.getBackground(), ViewUtils.DRAWABLE_ALPHA, 0);
    }

}
