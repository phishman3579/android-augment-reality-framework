package com.jwetherell.augmented_reality.ui.objects;

import android.graphics.Canvas;
import android.graphics.Color;

public class PaintableText extends PaintableObject {
    private static final float WIDTH_PAD = 4;
    private static final float HEIGHT_PAD = 2;
    
    private String text = null;
    private int color = 0;
    private int size = 0;
    private float w = 0;
    private float h = 0;
    private boolean bg = false;
    
    public PaintableText(String text, int color, int size, boolean paintBackground) {
        this.text = text;
        this.bg = paintBackground;
        this.color = color;
        this.size = size;
        w = getTextWidth(text) + WIDTH_PAD * 2;
        h = getTextAsc() + getTextDesc() + HEIGHT_PAD * 2;
    }
    
    @Override
    public float getWidth() {
        return w;
    }

    @Override
    public float getHeight() {
        return h;
    }

    @Override
    public void paint(Canvas canvas) {
        setColor(color);
        setFontSize(size);
        if (bg) {
            setColor(Color.rgb(0, 0, 0));
            setFill(true);
            paintRect(canvas, -(w/2), -(h/2), w, h);
            setColor(Color.rgb(255, 255, 255));
            setFill(false);
            paintRect(canvas, -(w/2), -(h/2), w, h);
        }
        paintText(canvas, (WIDTH_PAD - w/2), (HEIGHT_PAD + getTextAsc() - h/2), text);
    }
}
