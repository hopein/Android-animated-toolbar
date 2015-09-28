package com.kassisdion.animatedtoolbar.lib.toolbar;

import com.kassisdion.animatedtoolbar.lib.toolbarAnimator.ToolbarAnimator;

import android.content.Context;
import android.util.AttributeSet;

public class AnimatedToolbar extends android.support.v7.widget.Toolbar {

    private final static String TAG = AnimatedToolbar.class.getSimpleName();
    private final Context mContext;
    private ToolbarAnimator mToolbarAnimator;

    /*
    ** Constructor
     */
    public AnimatedToolbar(Context context) {
        this(context, null);
    }

    public AnimatedToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.toolbarStyle);
    }

    public AnimatedToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mToolbarAnimator = new ToolbarAnimator(mContext, this);
    }

    /*
    ** Public method
     */
    public ToolbarAnimator getAnimator() {
        return new ToolbarAnimator(mContext, this);
    }
}