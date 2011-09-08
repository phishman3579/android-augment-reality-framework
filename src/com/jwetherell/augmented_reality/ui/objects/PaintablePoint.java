package com.jwetherell.augmented_reality.ui.objects;

import android.graphics.Canvas;

/**
 * This class extends PaintableObject and draws a small rectangle.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintablePoint extends PaintableObject {
    private static int width=2;
    private static int height=2;
    
    private int color = 0;
    private boolean fill = false;
    
    public PaintablePoint(int color, boolean fill) {
    	set(color, fill);
    }
    
    public void set(int color, boolean fill) {
        this.color = color;
        this.fill = fill;
    }

    @Override
    public void paint(Canvas canvas) {
    	assert(canvas!=null);
    	
        setFill(fill);
        setColor(color);
        paintRect(canvas, -1, -1, width, height);
    }
    
    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }
}
