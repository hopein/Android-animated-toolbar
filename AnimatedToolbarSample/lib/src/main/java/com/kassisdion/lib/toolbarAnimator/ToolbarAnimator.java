package com.kassisdion.lib.toolbarAnimator;

import com.kassisdion.lib.R;
import com.kassisdion.utils.LogHelper;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Simple class for animate a toolbar (fadeIn & fadeOut)
 */
public final class ToolbarAnimator {

    private final static String TAG = ToolbarAnimator.class.getSimpleName();

    //private static field
    private final int ALPHA_MAX = 255;//just look at the documentation
    private final int NUMBER_OF_TICK = 255;//can go from 1 to 255, it's the number of tick

    //private field we'll change under the thread
    private volatile int mCurrentAlpha;
    private volatile Timer mTimer;
    private volatile ToolbarAnimatorCallback mCallback;
    private volatile int mAlphaPerTick;//alpha we'll remove/add on every tick

    //private field
    private final Toolbar mActionBar;
    private final Context mContext;
    private long mPeriod;
    private long mDuration;
    private long mDelay;//amount of time in milliseconds before animation execution.
    private final int mActionBarBackgroundColor;

    //public field
    public enum AnimationType {
        FADE_IN,
        FADE_OUT
    }

    /*
    ** Constructor
     */
    public ToolbarAnimator(@NonNull final Context context, @NonNull final Toolbar actionBar, final int actionBarBackgroundColor) {
        mContext = context;
        mActionBar = actionBar;
        mActionBarBackgroundColor = actionBarBackgroundColor;
    }

    public ToolbarAnimator(@NonNull final Context context, @NonNull final Toolbar actionBar) {
        this(context, actionBar, getThemeAccentColor(context));
    }

    /*
    ** Public method
     */
    public ToolbarAnimator setCallback(@NonNull final ToolbarAnimatorCallback callback) {
        mCallback = callback;
        return this;
    }

    public ToolbarAnimator setDelay(final int delay) {
        mDelay = delay;
        return this;
    }

    public void startAnimation(final long duration, @NonNull final AnimationType animationType) {
        mDuration = duration;

        if (mTimer == null) {
            mTimer = new Timer();
        }

        switch (animationType) {
            case FADE_IN:
                mAlphaPerTick = ALPHA_MAX / NUMBER_OF_TICK;
                mCurrentAlpha = 0;
                break;
            case FADE_OUT:
                mAlphaPerTick = -1 * ALPHA_MAX / NUMBER_OF_TICK;
                mCurrentAlpha = 255;
                break;
        }
        initTimer();
    }

    /*
    ** Private method
     */
    private void initTimer() {
        //calculation of the time between 2 run() call
        mPeriod = mDuration / NUMBER_OF_TICK;

        //init a timer which will updateActionBarColor on every each period
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //update the actionBar
                updateActionBar();
            }
        }, mDelay, mPeriod);
    }

    private void updateActionBar() {
        //We have to go to the main thread for updating the interface.
        ((Activity) mContext).runOnUiThread(new TimerTask() {
            @Override
            public void run() {
                //check if the animation is ended
                if (mCurrentAlpha > 255 || mCurrentAlpha < 0) {
                    LogHelper.d(TAG, "cancel timer");
                    finishAnimation();
                    return;
                }

                //create the new backgroundColorDrawable
                final Drawable backgroundDrawable = new ColorDrawable(mActionBarBackgroundColor);
                backgroundDrawable.setAlpha(mCurrentAlpha);

                //apply the new drawable on the actionBar
                updateUi(backgroundDrawable);

                //upgrade alpha
                mCurrentAlpha += mAlphaPerTick;
            }
        });
    }

    private void updateUi(final Drawable backgroundDrawable) {
        //We have to go to the main thread for updating the interface.
        ((Activity) mContext).runOnUiThread(new TimerTask() {
            @Override
            public void run() {
                //apply the new color
                mActionBar.setBackgroundDrawable(backgroundDrawable);
            }
        });
    }

    private void finishAnimation() {
        if (mTimer == null) {
            return;
        }
        mTimer.cancel();
        mTimer.purge();
        mTimer = null;

        if (mCallback != null) {
            mCallback.hasEnded();
        }
    }

    /*
    ** Utils
     */
    private static int getThemeAccentColor(final Context context) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int color = a.getColor(0, Color.BLUE);
        a.recycle();

        return color;
    }
}
