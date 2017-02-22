package org.mark.showcaselayouttest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import static android.view.MotionEvent.ACTION_UP;

public class ShowcaseLayout extends FrameLayout {
    private Context mContext;
    private View mTarget;
    private boolean mDisplay;
    private StatusBarHelper mStatusBarHelper;

    private HintShowcaseDrawer showcaseDrawer;
    private Bitmap bitmapBuffer;

    public ShowcaseLayout(Context context) {
        this(context, null);
    }

    public ShowcaseLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowcaseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mDisplay = true;
        mStatusBarHelper = new StatusBarHelper(context);
    }


    public void setDisplay(boolean display) {
        if (display == mDisplay) {
            return;
        }
        mDisplay = display;
        invalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mDisplay) {
            return super.dispatchTouchEvent(ev);
        }

        View view = findViewById(id);
        if (view != null) {
            if (containsView(view, ev)) {
                onTouch(ev, true);
                return super.dispatchTouchEvent(ev);
            }
        }

        onTouch(ev, false);
        return true;
    }

    protected void onTouch(MotionEvent ev, boolean isHitTarget) {
        switch (ev.getAction()) {
            case ACTION_UP:
                if (isHitTarget) {
                    Log.v("ShowcaseLayout", "onTouchEvent hit mTarget");
                } else {
                    Log.v("ShowcaseLayout", "onTouchEvent not hit mTarget");
                }
                mDisplay = false;
                invalidate();
                break;
        }
    }

    private boolean containsView(@NonNull View view, @NonNull MotionEvent ev) {
        Rect rect = new Rect();
        view.getHitRect(rect);
        float x = ev.getX();
        float y = ev.getY();
        boolean inRect = rect.contains((int) x, (int) y);
        return inRect;
    }


    private int id;

    public void setShowcaseDrawer(HintShowcaseDrawer showcaseDrawer, @IdRes int id) {
        this.showcaseDrawer = showcaseDrawer;
        this.id = id;
    }


    public void setParent(ViewGroup parent, int index) {




        ViewGroup view = (ViewGroup) parent.getChildAt(0);
        ViewGroup dddd = (ViewGroup) this.getParent();
        dddd.removeView(this);
        View viewdddd = this.getChildAt(0);
        this.removeView(viewdddd);
        dddd.addView(viewdddd);
        parent.removeView(view);
        this.addView(view);
        parent.addView(this, 0);
    }


    private void updateBitmap() {
        if (bitmapBuffer == null) {
            if (bitmapBuffer != null) {
                bitmapBuffer.recycle();
            }
            bitmapBuffer = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        }
    }


    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (!isInEditMode()) {
            if (mDisplay) {
                canvas.save();
                if (mTarget == null) {
                    mTarget = findViewById(id);
                }

                Rect targetRect = new Rect();
                mTarget.getGlobalVisibleRect(targetRect);

                updateBitmap();
                showcaseDrawer.erase(bitmapBuffer);
                showcaseDrawer.drawShowcase(bitmapBuffer, targetRect.left + targetRect.width() / 2, targetRect.top, 1f);
                showcaseDrawer.drawToCanvas(canvas, bitmapBuffer);
                canvas.restore();
                mStatusBarHelper.tintStatusBar(showcaseDrawer.getBaseColor());
            } else {
                mStatusBarHelper.unTintStatusBar();
            }
        }
    }
}
