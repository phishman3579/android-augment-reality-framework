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
    	set(color, x, y);
    }
    
    /**
     * Set this objects parameters. This should be used instead of creating new objects.
     * @param color Color of the line.
     * @param x X coordinate of the line.
     * @param y Y coordinate of the line.
     */
    public void set(int color, float x, float y) {
        this.color = color;
        this.x = x;
        this.y = y;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void paint(Canvas canvas) {
    	if (canvas==null) throw new NullPointerException();
    	
        setFill(false);
        setColor(color); 
        paintLine(canvas, 0, 0, x, y);
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public float getWidth() {
        return x;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public float getHeight() {
        return y;
    }
}
