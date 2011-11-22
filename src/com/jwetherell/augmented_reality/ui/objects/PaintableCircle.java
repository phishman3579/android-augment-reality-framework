package com.jwetherell.augmented_reality.ui.objects;

import android.graphics.Canvas;

/**
 * This class extends PaintableObject to draw a circle with a given radius.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintableCircle extends PaintableObject {
    private int color = 0;
    private float radius = 0;
    private boolean fill = false;
    
    public PaintableCircle(int color, float radius, boolean fill) {
    	set(color, radius, fill);
    }
    
    /**
     * Set the objects parameters. This should be used instead of creating new objects.
     * @param color Color of the circle.
     * @param radius Radius of the circle.
     * @param fill Fill color of the circle.
     */
    public void set(int color, float radius, boolean fill) {
        this.color = color;
        this.radius = radius;
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
        paintCircle(canvas, 0, 0, radius);
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
    public float getWidth() {
        return radius*2;
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
    public float getHeight() {
        return radius*2;
    }
}
