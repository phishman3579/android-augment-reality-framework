package com.jwetherell.augmented_reality.ui.objects;

import android.graphics.Canvas;

/**
 * This class extends PaintableObject and adds the ability to rotate and scale.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintablePosition extends PaintableObject {
    private float width=0, height=0;
    private float objX=0, objY=0, objRotation=0, objScale=0;
    private PaintableObject obj = null;
    
    public PaintablePosition(PaintableObject drawObj, float x, float y, float rotation, float scale) {
    	set(drawObj, x, y, rotation, scale);
    }

    /**
     * Set this objects parameters. This should be used instead of creating new objects.
     * @param drawObj Object to set for this Position. 
     * @param x X coordinate of the Position.
     * @param y Y coordinate of the Position.
     * @param rotation Rotation of the Position.
     * @param scale Scale of the Position.
     * @throws NullPointerException if PaintableObject is NULL.
     */
    public void set(PaintableObject drawObj, float x, float y, float rotation, float scale) {
    	if (drawObj==null) throw new NullPointerException();
    	
        this.obj = drawObj;
        this.objX = x;
        this.objY = y;
        this.objRotation = rotation;
        this.objScale = scale;
        this.width = obj.getWidth();
        this.height = obj.getHeight();
    }
    
    /**
     * Move the object.
     * @param x New X coordinate of the Position.
     * @param y New Y coordinate of the Position.
     */
    public void move(float x, float y) {
        objX = x;
        objY = y;
    }

    /**
     * X coordinate of the Object.
     * @return float X coordinate.
     */
    public float getObjectsX() {
        return objX;
    }
    
    /**
     * Y coordinate of the Object.
     * @return float Y coordinate.
     */
    public float getObjectsY() {
        return objY;
    }
    
	/**
	 * {@inheritDoc}
	 */
	@Override
    public void paint(Canvas canvas) {
    	if (canvas==null || obj==null) throw new NullPointerException();
    	
        paintObj(canvas, obj, objX, objY, objRotation, objScale);
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

    /**
     * {@inheritDoc}
     */
    @Override
	public String toString() {
	    return "< objX="+objX+" objY="+objY+" width="+width+" height="+height+" >";
	}
}
