package io.mokshjn.cosmo.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by moksh on 22/3/17.
 */

public class FourThreeImageView extends ForegroundImageView {

    public FourThreeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int fourThreeHeight = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthSpec) * 3 / 4,
                View.MeasureSpec.EXACTLY);
        super.onMeasure(widthSpec, fourThreeHeight);
    }
}
