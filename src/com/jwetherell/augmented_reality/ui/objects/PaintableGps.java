package com.jwetherell.augmented_reality.ui.objects;

import android.graphics.Canvas;

public class PaintableGps extends PaintableObject {
    private float radius = 0;
    private float strokeWidth = 0;
    private boolean fill = false;
    private int color = 0;
    
    public PaintableGps(float radius, float strokeWidth, boolean fill, int color) {
        this.radius = radius;
        this.strokeWidth = strokeWidth;
        this.fill = fill;
        this.color = color;
    }

    @Override
    public float getWidth() {
        return radius;
    }

    @Override
    public float getHeight() {
        return radius;
    }

    @Override
    public void paint(Canvas canvas) {
        setStrokeWidth(strokeWidth);
        setFill(fill);
        setColor(color);
        paintCircle(canvas, 0, 0, radius);
    }
}
