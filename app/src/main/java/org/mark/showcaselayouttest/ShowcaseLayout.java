package org.mark.showcaselayouttest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import static android.view.MotionEvent.ACTION_UP;

public class ShowcaseLayout extends FrameLayout {
    private Context mContext;
    private View mTarget;
    private boolean mDisplay;
    private StatusBarHelper mStatusBarHelper;
    private HintShowcaseDrawer mShowcaseDrawer;
    private Bitmap mBitmapBuffer;

    private int id;

    public ShowcaseLayout(Context context) {
        this(context, null);
    }

    public ShowcaseLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowcaseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        long start = System.currentTimeMillis();
        mContext = context;
        mDisplay = true;
        mStatusBarHelper = new StatusBarHelper(context);

        mShowcaseDrawer = new HintShowcaseDrawer(context,
                R.string.content_2,
                HintShowcaseDrawer.ABOVE_SHOWCASE,
                R.dimen.hint_bg_width,
                R.dimen.hint_text_size,
                R.dimen.btn_width,
                R.dimen.btn_width);

        id = R.id.btn;
        Log.v("ShowcaseLayout", "pass:" + (System.currentTimeMillis() - start));
    }


    public void setDisplay(boolean display) {
        if (display == mDisplay) {
            return;
        }
        mDisplay = display;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.v("ShowcaseLayout", "onLayout bottom:" + bottom);
        if (mTarget == null) {
            mTarget = findViewById(id);
        }
        if (mBitmapBuffer == null) {
            mBitmapBuffer = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_4444);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.v("ShowcaseLayout", "onMeasure getMeasuredHeight:" + getMeasuredHeight());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mDisplay) {
            return super.dispatchTouchEvent(ev);
        }

        if (mTarget != null) {
            if (containsView(mTarget, ev)) {
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

    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (!isInEditMode()) {
            if (mDisplay) {
                canvas.save();
                Rect targetRect = getRect();
                mShowcaseDrawer.erase(mBitmapBuffer);
                mShowcaseDrawer.drawShowcase(mBitmapBuffer, targetRect);
                mShowcaseDrawer.drawToCanvas(canvas, mBitmapBuffer);
                canvas.restore();
                mStatusBarHelper.tintStatusBar(mShowcaseDrawer.getBaseColor());
            } else {
                mStatusBarHelper.unTintStatusBar();
            }
        }
    }

    @NonNull
    private Rect getRect() {
        Rect targetRect = new Rect();
        mTarget.getGlobalVisibleRect(targetRect);
        targetRect.offset(0, -mStatusBarHelper.getStatusBarHeight());

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            if (mTarget instanceof FloatingActionButton) {
                FloatingActionButton fab = (FloatingActionButton) mTarget;
                Rect rect = new Rect();
                fab.getContentRect(rect);
                rect.offset(targetRect.left, targetRect.top);
                return rect;
            }
        }

        return targetRect;
    }

    private boolean containsView(@NonNull View view, @NonNull MotionEvent ev) {
        Rect rect = new Rect();
        view.getHitRect(rect);
        float x = ev.getX();
        float y = ev.getY();
        boolean inRect = rect.contains((int) x, (int) y);
        return inRect;
    }
}
