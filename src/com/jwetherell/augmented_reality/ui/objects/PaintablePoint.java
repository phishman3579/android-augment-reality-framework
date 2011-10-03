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
    
    /**
     * Set this objects parameters. This should be used instead of creating new objects.
     * @param color Color to set the rectangle representing this Point.
     * @param fill Fill color to set the rectangle representing this Point.
     */
    public void set(int color, boolean fill) {
        this.color = color;
        this.fill = fill;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void paint(Canvas canvas) {
    	if (canvas==null) throw new NullPointerException();
    	
        setFill(fill);
        setColor(color);
        paintRect(canvas, -1, -1, width, height);
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public float getWidth() {
        return width;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public float getHeight() {
        return height;
    }
}
