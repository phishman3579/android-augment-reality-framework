package com.jwetherell.augmented_reality.ui.objects;

import android.graphics.Canvas;

/**
 * This class extends PaintableObject to draw a line.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintableLine extends PaintableObject {
    private int color = 0;
    private float x = 0;
    private float y = 0;
    
    public PaintableLine(int color, float x, float y) {
        this.color = color;
        this.x = x;
        this.y = y;
    }
    
    @Override
    public float getWidth() {
        return x;
    }

    @Override
    public float getHeight() {
        return y;
    }

    @Override
    public void paint(Canvas canvas) {
        setFill(false);
        setColor(color); 
        paintLine(canvas, 0, 0, x, y);
    }
}
