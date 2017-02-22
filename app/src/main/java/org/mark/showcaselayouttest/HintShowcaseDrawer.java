package org.mark.showcaselayouttest;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.text.TextPaint;
import android.util.TypedValue;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class HintShowcaseDrawer {

    public static final int UNDEFINED = -1;
    public static final int LEFT_OF_SHOWCASE = 0;
    public static final int RIGHT_OF_SHOWCASE = 2;
    public static final int ABOVE_SHOWCASE = 1;
    public static final int BELOW_SHOWCASE = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UNDEFINED, LEFT_OF_SHOWCASE, RIGHT_OF_SHOWCASE, ABOVE_SHOWCASE, BELOW_SHOWCASE})
    public @interface TextPosition {
    }

    private static final float DEFAULT_LINE_PADDING = 2.5f;
    private static final float DEFAULT_LINE_STROKE_WIDTH = 2.5f;
    private static final float DEFAULT_LINE_POINT_WIDTH = 6.5f;
    private static final float DEFAULT_DRAWER_CORNERS_RADIUS = 8.0f;
    private static final float DEFAULT_DRAWER_PADDING = 12f;
    private static final int DEFAULT_DRAWER_MARGIN_TO_SCREEN = 10;
    private static final int DEFAULT_DRAWER_ARROW_SIZE = 10;
    private static final int DEFAULT_DRAWER_MARGIN_TO_TARGET = 15;

    public static final int RECT = 1;
    public static final int OVAL = 2;
    public static final int LEAF = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RECT, OVAL, LEAF})
    public @interface TargetShape {
    }

    private @TargetShape int mTargetShape;
    private final float mTargetWidth;
    private final float mTargetHeight;

    private final Paint mBasicPaint;
    private final int mBaseColour;

    private final RectF mTargetRect;
    private final Paint mTargetPaint;

    private int mLinePadding;
    private int mLineStrokeWidth;
    private int mLinePointWidth;

    private final HintDrawer mHintDrawer;
    private int mHintDrawerBgRadius;
    private int mHintDrawerPadding;
    private int mHintDrawerMarginToScreen;
    private int mHintDrawerArrowSize;
    private int mHintDrawerMarginToTarget;


    public HintShowcaseDrawer(Context context, @StringRes int hintText, @TextPosition int hintPosition,
                              @DimenRes int hintWidth,
                              @DimenRes int hintTextSize,
                              @DimenRes int targetWidth,
                              @DimenRes int targetHeight) {
        this(context, hintText, hintPosition, 0x80000000, hintWidth, OVAL, hintTextSize, targetWidth, targetHeight, false);
    }

    public HintShowcaseDrawer(Context context, @StringRes int hintText, @TextPosition int hintPosition,
                              @DimenRes int hintWidth,
                              @DimenRes int hintTextSize,
                              @DimenRes int targetWidth,
                              @DimenRes int targetHeight,
                              @TargetShape int targetShape) {
        this(context, hintText, hintPosition, 0x80000000, hintWidth, targetShape, hintTextSize, targetWidth, targetHeight, false);
    }

    public HintShowcaseDrawer(Context context, @StringRes int hintText, @TextPosition int hintPosition,
                              @DimenRes int hintWidth,
                              @DimenRes int hintTextSize,
                              int targetWidth,
                              int targetHeight,
                              @TargetShape int targetShape,
                              boolean isPixels) {
        this(context, hintText, hintPosition, 0x80000000, hintWidth, targetShape, hintTextSize, targetWidth, targetHeight, isPixels);
    }

    public HintShowcaseDrawer(Context context, @StringRes int hintText, @TextPosition int hintPosition,
                              @ColorInt int maskColorRes, @DimenRes int hintWidth, @TargetShape int targetShape, @DimenRes int hintTextSize, int targetWidth, int targetHeight, boolean isPixels) {
        Resources resources = context.getResources();
        this.mTargetWidth = isPixels ? targetWidth : resources.getDimension(targetWidth);
        this.mTargetHeight = isPixels ? targetHeight : resources.getDimension(targetHeight);
        mTargetPaint = new Paint();
        mTargetPaint.setColor(0xFFFFFF);
        mTargetPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mTargetPaint.setAntiAlias(true);

        mBaseColour = maskColorRes;
        mBasicPaint = new Paint();
        mTargetRect = new RectF();
        this.mTargetShape = targetShape;

        //虚线距离洞口距离
        mLinePadding = dpToPx(resources, DEFAULT_LINE_PADDING);
        //虚线粗细
        mLineStrokeWidth = dpToPx(resources, DEFAULT_LINE_STROKE_WIDTH);
        //虚线的点长度
        mLinePointWidth = dpToPx(resources, DEFAULT_LINE_POINT_WIDTH);
        //Drawer 背景圆角直径
        mHintDrawerBgRadius = dpToPx(resources, DEFAULT_DRAWER_CORNERS_RADIUS);
        //Drawer 内边距
        mHintDrawerPadding = dpToPx(resources, DEFAULT_DRAWER_PADDING);
        //Drawer 外边距，距离屏幕边界
        mHintDrawerMarginToScreen = dpToPx(resources, DEFAULT_DRAWER_MARGIN_TO_SCREEN);
        //Drawer 外边距，距离目标showcase
        mHintDrawerMarginToTarget = dpToPx(resources, DEFAULT_DRAWER_MARGIN_TO_TARGET);
        //箭头大小
        mHintDrawerArrowSize = dpToPx(resources, DEFAULT_DRAWER_ARROW_SIZE);

        mHintDrawer = new HintDrawer((int)resources.getDimension(hintWidth), mHintDrawerMarginToTarget, mHintDrawerBgRadius, mHintDrawerPadding, mHintDrawerArrowSize, mHintDrawerMarginToScreen);
        mHintDrawer.forceTextPosition(hintPosition);
        mHintDrawer.setContentText(resources.getString(hintText));

        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(resources.getDimension(hintTextSize));
        paint.setFakeBoldText(true);
        paint.setColor(0xFF07a7ab);
        mHintDrawer.setContentPaint(paint);
    }

    public void drawShowcase(Bitmap buffer, float x, float y, float scaleMultiplier) {
        Canvas bufferCanvas = new Canvas(buffer);
        mTargetRect.left = x - mTargetWidth / 2f;
        mTargetRect.right = x + mTargetWidth / 2f;
        mTargetRect.top = y - mTargetHeight / 2f;
        mTargetRect.bottom = y + mTargetHeight / 2f;

        mHintDrawer.calculateTextPosition(buffer.getWidth(), mTargetRect);
        mHintDrawer.draw(bufferCanvas);

        drawTarget(bufferCanvas);
    }


    private void drawTarget(Canvas bufferCanvas) {
        RectF lineRectF = new RectF(mTargetRect);
        //虚线set padding
        lineRectF.top -= mLinePadding;
        lineRectF.right += mLinePadding;
        lineRectF.bottom += mLinePadding;
        lineRectF.left -= mLinePadding;

        //绘制虚线
        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(mLineStrokeWidth);
        PathEffect effects = new DashPathEffect(new float[]{mLinePointWidth, mLinePointWidth - mLinePointWidth / 3}, mLinePointWidth);
        linePaint.setPathEffect(effects);

        switch (mTargetShape) {
            case RECT:
                bufferCanvas.drawRect(mTargetRect, mTargetPaint);
                bufferCanvas.drawRect(lineRectF, linePaint);
                break;
            case OVAL:
                bufferCanvas.drawOval(mTargetRect, mTargetPaint);
                bufferCanvas.drawOval(lineRectF, linePaint);
                break;
            case LEAF:
                bufferCanvas.drawPath(getLeafPath(mTargetRect), mTargetPaint);
                bufferCanvas.drawPath(getLeafPath(lineRectF), linePaint);
                break;
            default:
                throw new UnsupportedOperationException("Not support this type:" + mTargetShape);
        }
    }

    private Path getLeafPath(RectF rect) {
        Path path = new Path();
        path.moveTo(rect.left, rect.centerY());
        path.quadTo(rect.left, rect.top, rect.centerX(), rect.top);
        path.lineTo(rect.right, rect.top);
        path.lineTo(rect.right, rect.centerY());
        path.quadTo(rect.right, rect.bottom, rect.centerX(), rect.bottom);
        path.quadTo(rect.left, rect.bottom, rect.left, rect.centerY());
        return path;
    }

    private static int dpToPx(Resources resources, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    public void setShowcaseColour(int color) {
        mTargetPaint.setColor(color);
    }

    public int getShowcaseWidth() {
        return (int) mTargetWidth;
    }

    public RectF getTargetRect(){
        return mTargetRect;
    }

    public int getShowcaseHeight() {
        return (int) mTargetHeight;
    }

    public float getBlockedRadius() {
        return mTargetWidth;
    }

    public void setBackgroundColour(int backgroundColor) {
    }

    public void erase(Bitmap bitmapBuffer) {
        bitmapBuffer.eraseColor(mBaseColour);
    }

    public void drawToCanvas(Canvas canvas, Bitmap bitmapBuffer) {
        canvas.drawBitmap(bitmapBuffer, 0, 0, mBasicPaint);
    }

    public int getBaseColor(){
        return mBaseColour;
    }

}