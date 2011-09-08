package com.jwetherell.augmented_reality.ui.objects;

import android.graphics.Canvas;

/**
 * This class extends PaintableObject and adds the ability to rotate and scale.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintablePosition extends PaintableObject {
    private float myX=0, myY=0, width=0, height=0;
    private PaintableObject obj = null;
    private float objX=0, objY=0, objRotation=0, objScale=0;

    public PaintablePosition(PaintableObject drawObj, float x, float y, float rotation, float scale) {
    	set(drawObj, x, y, rotation, scale);
    }

    public void set(PaintableObject drawObj, float x, float y, float rotation, float scale) {
        obj = drawObj;
        objX = x;
        objY = y;
        objRotation = rotation;
        objScale = scale;
        float w = obj.getWidth();
        float h = obj.getHeight();

        myX = w / 2;
        myY = 0;

        width = w * 2;
        height = h * 2;
    }
    
    public void move(float x, float y) {
        objX = x;
        objY = y;
    }

    public void paint(Canvas canvas) {
    	assert(canvas!=null && obj!=null);
    	
        paintObj(canvas, obj, objX, objY, objRotation, objScale);
    }
    
    public float getX() {
        return myX;
    }
    
    public float getY() {
        return myY;
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
