package org.mark.showcaselayouttest;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.graphics.ColorUtils;
import android.view.Window;

public class StatusBarHelper {

    private Window mWindow;
    private int mColor;
    private int mStatusBarHeight;

    public StatusBarHelper(Context context) {
        if (context instanceof Activity) {
            mStatusBarHeight = calcStatusBarHeight(context);
            mWindow = ((Activity) context).getWindow();
            if (hasStatusBar()) {
                mColor = mWindow.getStatusBarColor();
            }
        }else {
            throw new IllegalArgumentException("The context is not an activity");
        }
    }

    public int getStatusBarHeight(){
        return mStatusBarHeight;
    }

    public void tintStatusBar(int color) {
        if (hasStatusBar()) {
            mWindow.setStatusBarColor(ColorUtils.compositeColors(color, mColor));
        }
    }

    public void unTintStatusBar() {
        if (hasStatusBar()) {
            mWindow.setStatusBarColor(mColor);
        }
    }

    private boolean hasStatusBar() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    private int calcStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
