package com.example.vitaly.yandexapplication;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Vitaly on 15.04.2018.
 */
public class RotatedDrawerLayout extends DrawerLayout {
    private boolean m_disallowIntercept;



    public RotatedDrawerLayout (Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        // as the drawer intercepts all touches when it is opened
        // we need this to let the content beneath the drawer to be touchable
        return !m_disallowIntercept && super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setDrawerLockMode(int lockMode) {
        super.setDrawerLockMode(lockMode);
        // if the drawer is locked, then disallow interception
        m_disallowIntercept = (lockMode == LOCK_MODE_LOCKED_OPEN);
    }
}
