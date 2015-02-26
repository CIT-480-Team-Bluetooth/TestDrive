package edu.oakland.secs.testdrive;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by jeffq on 2/26/2015.
 */
public class CustomViewPager extends ViewPager {

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * canScroll really means "should we not process touch events for the ViewPager"
     * Since we want to be able to dismiss the data entries with swipes, we need to
     * do this when we're touching a CardView
     */
    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if(v instanceof CardView)
            return true;
        return super.canScroll(v, checkV, dx, x, y);
    }

}
