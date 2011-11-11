package com.jwetherell.augmented_reality.ui.objects;

import android.graphics.Canvas;


/**
 * This class extends PaintableObject to draw a circle with a given radius and a stroke width.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintableGps extends PaintableObject {
    private float radius = 0;
    private float strokeWidth = 0;
    private boolean fill = false;
    private int color = 0;
    
    public PaintableGps(float radius, float strokeWidth, boolean fill, int color) {
    	set(radius, strokeWidth, fill, color);
    }
    
    /**
     * Set this objects parameters. This should be used instead of creating new objects.
     * @param radius Radius of the circle representing the GPS position.
     * @param strokeWidth Stroke width of the text representing the GPS position.
     * @param fill Fill color of the circle representing the GPS position.
     * @param color Color of the circle representing the GPS position.
     */
    public void set(float radius, float strokeWidth, boolean fill, int color) {
        this.radius = radius;
        this.strokeWidth = strokeWidth;
        this.fill = fill;
        this.color = color;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void paint(Canvas canvas) {
    	if (canvas==null) throw new NullPointerException();
    	
        setStrokeWidth(strokeWidth);
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
