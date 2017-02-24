package org.mark.showcase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import static android.view.MotionEvent.ACTION_UP;

public class ShowcaseLayout extends FrameLayout {
    private boolean mDisplay;
    private Rect mTargetRect;
    private StatusBarHelper mStatusBarHelper;
    private HintShowcaseDrawer mShowcaseDrawer;
    private Bitmap mBitmapBuffer;
    private boolean mCanceledOnTouchOutside;
    private boolean mCanceledOnTouchTarget;

    private final int mTargetId;

    public ShowcaseLayout(Context context) {
        this(context, null);
    }

    public ShowcaseLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowcaseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mStatusBarHelper = new StatusBarHelper(context);

        final TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.Showcase, defStyleAttr, 0);

        mTargetId = a.getResourceId(R.styleable.Showcase_targetId, Integer.MIN_VALUE);
        mTargetRect = new Rect();
        if (mTargetId == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Please set targetId");
        }

        mDisplay = a.getBoolean(R.styleable.Showcase_display, true);

        final String text = a.getString(R.styleable.Showcase_text);

        final float textSize = a.getDimension(R.styleable.Showcase_textSize, 32);

        final int textBoxMaxWidth = a.getDimensionPixelSize(R.styleable.Showcase_textBoxMaxWidth, 0);

        final int targetWidth = a.getDimensionPixelSize(R.styleable.Showcase_targetWidth, 100);
        final int targetHeight = a.getDimensionPixelSize(R.styleable.Showcase_targetWidth, 100);
        mCanceledOnTouchOutside = a.getBoolean(R.styleable.Showcase_canceledOnTouchOutside, true);
        mCanceledOnTouchTarget = a.getBoolean(R.styleable.Showcase_canceledOnTouchTarget, true);


        mShowcaseDrawer = new HintShowcaseDrawer(context,
                text == null ? "" : text,
                parseDirection(a.getInt(R.styleable.Showcase_direction, 3)),
                textBoxMaxWidth,
                parseShape(a.getInt(R.styleable.Showcase_shape, 1)),
                textSize,
                targetWidth,
                targetHeight);

        a.recycle();
    }


    public void setDisplay(boolean display) {
        if (display == mDisplay) {
            return;
        }
        mDisplay = display;
        invalidate();
    }

    public void setEnabled(boolean enabled) {
        setDisplay(enabled);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mTargetRect.isEmpty()) {
            View view = findViewById(mTargetId);
            if (view != null) {
                mTargetRect.set(getRect(view));
            }
        }
        if (mBitmapBuffer == null) {
            mBitmapBuffer = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_4444);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mDisplay) {
            return super.dispatchTouchEvent(ev);
        }
        if (containsView(ev)) {
            if (mCanceledOnTouchTarget) {
                onTouch(ev);
            }
            return super.dispatchTouchEvent(ev);
        }

        if (mCanceledOnTouchOutside) {
            onTouch(ev);
        }
        return true;
    }

    protected void onTouch(MotionEvent ev) {
        switch (ev.getAction()) {
            case ACTION_UP:
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
                mShowcaseDrawer.erase(mBitmapBuffer);
                if (!mTargetRect.isEmpty()) {
                    mShowcaseDrawer.drawShowcase(mBitmapBuffer, mTargetRect);
                }
                mShowcaseDrawer.drawToCanvas(canvas, mBitmapBuffer);
                canvas.restore();
                mStatusBarHelper.tintStatusBar(mShowcaseDrawer.getBaseColor());
            } else {
                mStatusBarHelper.unTintStatusBar();
            }
        }
    }

    @NonNull
    private Rect getRect(@NonNull View view) {
        Rect targetRect = new Rect();
        view.getGlobalVisibleRect(targetRect);
        targetRect.offset(0, -mStatusBarHelper.getStatusBarHeight());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (view instanceof FloatingActionButton) {
                FloatingActionButton fab = (FloatingActionButton) view;
                Rect rect = new Rect();
                fab.getContentRect(rect);
                rect.offset(targetRect.left, targetRect.top);
                return rect;
            }
        }

        return targetRect;
    }

    private boolean containsView(@NonNull MotionEvent ev) {
        if (!mTargetRect.isEmpty()) {
            float x = ev.getX();
            float y = ev.getY();
            boolean inRect = mTargetRect.contains((int) x, (int) y);
            return inRect;
        }
        return false;
    }

    @HintShowcaseDrawer.TextPosition
    protected int parseDirection(int value) {
        switch (value) {
            case 0:
                return HintShowcaseDrawer.LEFT_OF_SHOWCASE;
            case 1:
                return HintShowcaseDrawer.ABOVE_SHOWCASE;
            case 2:
                return HintShowcaseDrawer.RIGHT_OF_SHOWCASE;
            case 3:
                return HintShowcaseDrawer.BELOW_SHOWCASE;
            default:
                return HintShowcaseDrawer.BELOW_SHOWCASE;
        }
    }

    @HintShowcaseDrawer.TargetShape
    protected int parseShape(int value) {
        switch (value) {
            case 1:
                return HintShowcaseDrawer.RECT;
            case 2:
                return HintShowcaseDrawer.OVAL;
            case 3:
                return HintShowcaseDrawer.LEAF;
            default:
                return HintShowcaseDrawer.RECT;
        }
    }

    public void setText(@StringRes int id) {
        mShowcaseDrawer.setText(id);
        invalidate();
    }
}
