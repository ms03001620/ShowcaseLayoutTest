package org.mark.showcaselayouttest;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.graphics.ColorUtils;
import android.view.Window;

public class StatusBarHelper {

    private Window mWindow;
    private int mColor;

    public StatusBarHelper(Context context) {
        if (context instanceof Activity) {
            mWindow = ((Activity) context).getWindow();
            if (hasStatusBar()) {
                mColor = mWindow.getStatusBarColor();
            }
        }else {
            throw new IllegalArgumentException("The context is not an activity");
        }
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
}
