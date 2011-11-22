package com.jwetherell.augmented_reality.ui.objects;

import android.graphics.Canvas;
import android.graphics.Color;

/**
 * This class extends PaintableObject to draw text.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintableText extends PaintableObject {
    private static final float WIDTH_PAD = 4;
    private static final float HEIGHT_PAD = 2;
    
    private String text = null;
    private int color = 0;
    private int size = 0;
    private float width = 0;
    private float height = 0;
    private boolean bg = false;
    
    public PaintableText(String text, int color, int size, boolean paintBackground) {
    	set(text, color, size, paintBackground);
    }
    
    /**
     * Set this objects parameters. This should be used instead of creating new objects.
     * @param text String representing this object.
     * @param color Color of the object.
     * @param size Size of the object.
     * @param paintBackground Should the background get rendered.
     * @throws NullPointerException if String param is NULL.
     */
    public void set(String text, int color, int size, boolean paintBackground) {
    	if (text==null) throw new NullPointerException();
    	
        this.text = text;
        this.bg = paintBackground;
        this.color = color;
        this.size = size;
        this.width = getTextWidth(text) + WIDTH_PAD * 2;
        this.height = getTextAsc() + getTextDesc() + HEIGHT_PAD * 2;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void paint(Canvas canvas) {
    	if (canvas==null || text==null) throw new NullPointerException();
    	
        setColor(color);
        setFontSize(size);
        if (bg) {
            setColor(Color.rgb(0, 0, 0));
            setFill(true);
            paintRect(canvas, -(width/2), -(height/2), width, height);
            setColor(Color.rgb(255, 255, 255));
            setFill(false);
            paintRect(canvas, -(width/2), -(height/2), width, height);
        }
        paintText(canvas, (WIDTH_PAD - width/2), (HEIGHT_PAD + getTextAsc() - height/2), text);
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
