package org.mark.showcase;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.TintTypedArray;
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
    private boolean mCanceledOnTouchOutside;

    private final int id;

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
        Resources resources = context.getResources();
        mStatusBarHelper = new StatusBarHelper(context);


        final TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.Showcase, defStyleAttr, 0);

        id = a.getResourceId(R.styleable.Showcase_targetId, Integer.MIN_VALUE);
        if (id == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Please set targetId");
        }

        mDisplay = a.getBoolean(R.styleable.Showcase_display, true);

        final String text = a.getString(R.styleable.Showcase_text);

        final float textSize = a.getDimension(R.styleable.Showcase_textSize, 32);

        final int textBoxMaxWidth = a.getDimensionPixelSize(R.styleable.Showcase_textBoxMaxWidth, 0);

        final int targetWidth = a.getDimensionPixelSize(R.styleable.Showcase_targetWidth, 100);
        final int targetHeight = a.getDimensionPixelSize(R.styleable.Showcase_targetWidth, 100);
        mCanceledOnTouchOutside = a.getBoolean(R.styleable.Showcase_canceledOnTouchOutside, true);

        mShowcaseDrawer = new HintShowcaseDrawer(context,
                text == null ? "" : text,
                parseDirection(a.getInt(R.styleable.Showcase_direction, 3)),
                textBoxMaxWidth,
                parseShape(a.getInt(R.styleable.Showcase_shape, 1)),
                textSize,
                targetWidth,
                targetHeight);

        a.recycle();


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
                onTouch(ev);
                Log.v("ShowcaseLayout", "onTouchEvent hit mTarget");
                return super.dispatchTouchEvent(ev);
            }
        }

        if(mCanceledOnTouchOutside){
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
        Rect rect = getRect();
        float x = ev.getX();
        float y = ev.getY();
        boolean inRect = rect.contains((int) x, (int) y);
        return inRect;
    }

    static @HintShowcaseDrawer.TextPosition int parseDirection(int value) {
        switch (value){
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

    static @HintShowcaseDrawer.TargetShape int parseShape(int value) {
        switch (value){
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
}
