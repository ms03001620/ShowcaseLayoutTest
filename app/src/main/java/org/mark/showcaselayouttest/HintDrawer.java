package org.mark.showcaselayouttest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.MetricAffectingSpan;

import static org.mark.showcaselayouttest.HintShowcaseDrawer.ABOVE_SHOWCASE;
import static org.mark.showcaselayouttest.HintShowcaseDrawer.BELOW_SHOWCASE;

public class HintDrawer {
    private static final int INDEX_TEXT_START_X = 0;
    private static final int INDEX_TEXT_START_Y = 1;
    private static final int INDEX_TEXT_WIDTH = 2;
    private static final int INDEX_ARROW_OFFSET = 3;

    private SpannableString mText;
    private final TextPaint mTextPaint;
    private DynamicLayout mTextLayout;
    private MetricAffectingSpan mTextSpan;
    private Layout.Alignment mTextAlignment;

    private float[] mBestTextPosition = new float[4];
    @HintShowcaseDrawer.TextPosition
    private int mForcedTextPosition;
    private int mArrowSize;
    private int mBgMarginToTarget;
    private int mBgMarginToScreen;
    private int mBgPadding;
    private int mBgWidth;
    private int mBgRadius;

    public HintDrawer(int bgWidth, int bgMarginToTarget, int bgRadius, int bgPadding, int arrowSize, int bgMarginToScreen) {
        this.mBgWidth = bgWidth;
        this.mBgMarginToTarget = bgMarginToTarget;
        this.mBgRadius = bgRadius;
        this.mBgPadding = bgPadding;
        this.mArrowSize = arrowSize;
        this.mBgMarginToScreen = bgMarginToScreen;

        mTextAlignment = Layout.Alignment.ALIGN_NORMAL;
        mTextPaint = new TextPaint();
    }

    public void calculateTextPosition(int canvasW, RectF showcase) {
        mTextLayout = new DynamicLayout(mText, mTextPaint, mBgWidth - mBgPadding * 2, mTextAlignment, 1.2f, 1.0f, true);
        switch (mForcedTextPosition) {
            case ABOVE_SHOWCASE:
                //bg x
                mBestTextPosition[INDEX_TEXT_START_X] = showcase.centerX() - mBgWidth / 2;
                //bg y
                mBestTextPosition[INDEX_TEXT_START_Y] = showcase.top - mTextLayout.getHeight() - mBgPadding * 2 - calcHeight(mArrowSize) - this.mBgMarginToTarget;
                mBestTextPosition[INDEX_TEXT_WIDTH] = mBgWidth;

                //calc arrow offset
                if (mBestTextPosition[INDEX_TEXT_START_X] < mBgMarginToScreen) {
                    mBestTextPosition[INDEX_ARROW_OFFSET] = mBestTextPosition[INDEX_TEXT_START_X] - mBgMarginToScreen;
                    mBestTextPosition[INDEX_TEXT_START_X] = mBgMarginToScreen;
                } else if (mBestTextPosition[INDEX_TEXT_START_X] + mBgWidth > (canvasW - mBgMarginToScreen)) {
                    mBestTextPosition[INDEX_ARROW_OFFSET] = mBestTextPosition[INDEX_TEXT_START_X] + mBgWidth - (canvasW - mBgMarginToScreen);
                    mBestTextPosition[INDEX_TEXT_START_X] = (canvasW - mBgMarginToScreen) - mBgWidth;
                } else {
                    mBestTextPosition[INDEX_ARROW_OFFSET] = 0;
                }

                break;
            case BELOW_SHOWCASE:
                //bg x
                mBestTextPosition[INDEX_TEXT_START_X] = showcase.centerX() - mBgWidth / 2;
                //bg y
                mBestTextPosition[INDEX_TEXT_START_Y] = showcase.bottom + calcHeight(mArrowSize) + this.mBgMarginToTarget;
                mBestTextPosition[INDEX_TEXT_WIDTH] = mBgWidth;

                //calc arrow offset
                if (mBestTextPosition[INDEX_TEXT_START_X] < mBgMarginToScreen) {
                    mBestTextPosition[INDEX_ARROW_OFFSET] = mBestTextPosition[INDEX_TEXT_START_X] - mBgMarginToScreen;
                    mBestTextPosition[INDEX_TEXT_START_X] = mBgMarginToScreen;
                } else if (mBestTextPosition[INDEX_TEXT_START_X] + mBgWidth > (canvasW - mBgMarginToScreen)) {
                    mBestTextPosition[INDEX_ARROW_OFFSET] = mBestTextPosition[INDEX_TEXT_START_X] + mBgWidth - (canvasW - mBgMarginToScreen);
                    mBestTextPosition[INDEX_TEXT_START_X] = (canvasW - mBgMarginToScreen) - mBgWidth;
                } else {
                    mBestTextPosition[INDEX_ARROW_OFFSET] = 0;
                }
                break;
        }
    }

    public int calcHeight(int lineWidth) {
        double aMiddleLine = Math.sqrt(3) / 2 * lineWidth;
        return (int) Math.round(aMiddleLine);
    }

    public void draw(Canvas canvas) {
        if (shouldDrawText()) {
            float[] textPosition = getBestTextPosition();
            //content
            if (!TextUtils.isEmpty(mText)) {
                if (mTextLayout != null) {
                    //draw bg
                    canvas.save();
                    canvas.translate(textPosition[INDEX_TEXT_START_X], textPosition[INDEX_TEXT_START_Y]);
                    Rect bgRect = new Rect();
                    //bgHeight = textHeight + mBgPadding * 2;
                    bgRect.set(0, 0, (int) textPosition[INDEX_TEXT_WIDTH], mTextLayout.getHeight() + mBgPadding * 2);
                    drawBg(bgRect, canvas, (int) textPosition[INDEX_ARROW_OFFSET], mBgRadius);
                    canvas.restore();

                    //draw text
                    canvas.save();
                    canvas.translate(textPosition[INDEX_TEXT_START_X] + mBgPadding, textPosition[INDEX_TEXT_START_Y] + mBgPadding);
                    mTextLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    private void drawBg(Rect rect, Canvas canvas, int arrowOffset, int bgRadius) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        Path path = new Path();

        path.moveTo(rect.left, rect.top + bgRadius);
        path.quadTo(rect.left, rect.top, rect.left + bgRadius, rect.left);

        int round = mArrowSize / 3;
        int keep = mArrowSize / 2;

        if (mForcedTextPosition == BELOW_SHOWCASE) {
            Point start = new Point();
            start.set(rect.centerX() - mArrowSize + arrowOffset, rect.top);
            path.lineTo(start.x, start.y);//start

            path.cubicTo(
                    start.x + keep, start.y - keep,
                    start.x + mArrowSize - round, start.y - calcHeight(mArrowSize),
                    start.x + mArrowSize, start.y - calcHeight(mArrowSize)
            );

            path.cubicTo(
                    start.x + mArrowSize + round, start.y - calcHeight(mArrowSize),
                    start.x + mArrowSize * 2 - keep, start.y - keep,
                    start.x + mArrowSize * 2, start.y
            );
        }

        path.lineTo(rect.right - bgRadius, rect.left);
        path.quadTo(rect.right, rect.top, rect.right, rect.top + bgRadius);
        path.lineTo(rect.right, rect.bottom - bgRadius);
        path.quadTo(rect.right, rect.bottom, rect.right - bgRadius, rect.bottom);

        if (mForcedTextPosition == ABOVE_SHOWCASE) {
            Point start = new Point();
            start.set(rect.centerX() + mArrowSize + arrowOffset, rect.bottom);
            path.lineTo(start.x, start.y);//start

            path.cubicTo(
                    start.x - keep, start.y + keep,
                    start.x - mArrowSize + round, start.y + calcHeight(mArrowSize),
                    start.x - mArrowSize, start.y + calcHeight(mArrowSize)
            );

            path.cubicTo(
                    start.x - mArrowSize - round, start.y + calcHeight(mArrowSize),
                    start.x - mArrowSize * 2 + keep, start.y + keep,
                    start.x - mArrowSize * 2, start.y
            );
        }
        path.lineTo(rect.left + bgRadius, rect.bottom);
        path.quadTo(rect.left, rect.bottom, rect.left, rect.bottom - bgRadius);

        path.close();
        canvas.drawPath(path, paint);
    }

    public void setContentText(CharSequence details) {
        if (details != null) {
            SpannableString ssbDetail = new SpannableString(details);
            ssbDetail.setSpan(mTextSpan, 0, ssbDetail.length(), 0);
            mText = ssbDetail;
        }
    }

    public float[] getBestTextPosition() {
        return mBestTextPosition;
    }

    public boolean shouldDrawText() {
        return !TextUtils.isEmpty(mText);
    }

    public void setContentPaint(TextPaint contentPaint) {
        mTextPaint.set(contentPaint);
        if (mText != null) {
            mText.removeSpan(mTextSpan);
        }
        mTextSpan = new NoOpSpan();
        setContentText(mText);
    }

    public void forceTextPosition(@HintShowcaseDrawer.TextPosition int forcedTextPosition) {
        mForcedTextPosition = forcedTextPosition;
    }

    private static class NoOpSpan extends MetricAffectingSpan {
        @Override
        public void updateDrawState(TextPaint tp) {
        }

        @Override
        public void updateMeasureState(TextPaint p) {
        }
    }
}
