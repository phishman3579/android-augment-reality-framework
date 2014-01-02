package com.jwetherell.augmented_reality.widget;

import com.jwetherell.augmented_reality.activity.AugmentedReality;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * This class extends the TextView class and is designed to work vertically and
 * horizontally.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class VerticalTextView extends TextView {

    public VerticalTextView(Context context) {
        super(context);
    }

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (AugmentedReality.ui_portrait) {
            super.onMeasure(heightMeasureSpec, widthMeasureSpec);
            setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (AugmentedReality.ui_portrait) {
            TextPaint textPaint = getPaint();
            textPaint.setColor(getCurrentTextColor());
            textPaint.drawableState = getDrawableState();

            canvas.save();
            canvas.translate(0, getHeight());
            canvas.rotate(-90);
            canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());
            getLayout().draw(canvas);
            canvas.restore();
        } else {
            super.onDraw(canvas);
        }
    }
}
