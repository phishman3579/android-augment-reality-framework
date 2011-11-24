package com.jwetherell.augmented_reality.ui.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;


/**
 * This abstract class provides many methods paint objects on a given Canvas.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public abstract class PaintableObject {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public PaintableObject() {
        if (paint==null) {
            paint = new Paint();
            paint.setTextSize(16);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.STROKE);
        }
    }

    /**
     * Get the width of the paintable object.
     * @return float width
     */
    public abstract float getWidth();

    /**
     * Get the height of the paintable object.
     * @return float height
     */
    public abstract float getHeight();

    /**
     * Paints this object on the given canvas.
     */
    public abstract void paint(Canvas canvas);
    
    /**
     * Should we fill this paintable object.
     */
    public void setFill(boolean fill) {
        if (fill)
            paint.setStyle(Paint.Style.FILL);
        else
            paint.setStyle(Paint.Style.STROKE);
    }
    
    /**
     * Set the color of the paintable object.
     */
    public void setColor(int c) {
        paint.setColor(c);
    }
    
    /**
     * Set the stroke with of the paint used to render this object.
     */
    public void setStrokeWidth(float w) {
        paint.setStrokeWidth(w);
    }
    
    /**
     * Get the width of the text String.
     * @param txt String to get the width of.
     * @return float width of the text String.
     * @throws NullPointerException if the String param is NULL.
     */
    public float getTextWidth(String txt) {
    	if (txt==null) throw new NullPointerException();
        return paint.measureText(txt);
    }

    /**
     * Get the ascent of the paint element.
     * @return float ascent of the text.
     */
    public float getTextAsc() {
        return -paint.ascent();
    }

    /**
     * Get the decent of the paint element.
     * @return float decent of the text.
     */
    public float getTextDesc() {
        return paint.descent();
    }

    /**
     * Set the font size of the paint object.
     * @param size to set the font.
     */
    public void setFontSize(float size) {
        paint.setTextSize(size);
    }

    /**
     * Paint a line on the given Canvas.
     * @param canvas Canvas to paint on.
     * @param x1 Beginning X to draw line.
     * @param y1 Beginning Y to draw line.
     * @param x2 Ending X to draw line.
     * @param y2 Ending Y to draw line.
     * @throws NullPointerException if Canvas is NULL.
     */
    public void paintLine(Canvas canvas, float x1, float y1, float x2, float y2) {
    	if (canvas==null) throw new NullPointerException();
    	
        canvas.drawLine(x1, y1, x2, y2, paint);
    }

    /**
     * Paint a rectangle on the given Canvas.
     * @param canvas Canvas to paint on.
     * @param x X location of the rectangle.
     * @param y Y location of the rectangle.
     * @param width Width of the rectangle.
     * @param height Height of the rectangle.
     * @throws NullPointerException if Canvas is NULL.
     */
    public void paintRect(Canvas canvas, float x, float y, float width, float height) {
    	if (canvas==null) throw new NullPointerException();
    	
        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    /**
     * Paint a bitmap on the given Canvas.
     * @param canvas Canvas to paint on.
     * @param bitmap Bitmap to paint.
     * @param src Source rectangle.
     * @param dst Destination rectangle.
     * @throws NullPointerException if Canvas or Bitmap is NULL.
     */
    public void paintBitmap(Canvas canvas, Bitmap bitmap, Rect src, Rect dst) {
        if (canvas==null || bitmap==null) throw new NullPointerException();
        
        canvas.drawBitmap(bitmap, src, dst, paint);
    }
    
    /**
     * Paint a bitmap on the given Canvas.
     * @param canvas Canvas to paint on.
     * @param bitmap Bitmap to paint.
     * @param left Left location to draw the bitmap.
     * @param top Top location to draw the bitmap.
     * @throws NullPointerException if Canvas or Bitmap is NULL.
     */
    public void paintBitmap(Canvas canvas, Bitmap bitmap, float left, float top) {
    	if (canvas==null || bitmap==null) throw new NullPointerException();
    	
        canvas.drawBitmap(bitmap, left, top, paint);
    }

    /**
     * Paint a circle on the given Canvas.
     * @param canvas Canvas to paint on.
     * @param x Center X coordinate of the circle.
     * @param y Center Y coordinate of the circle.
     * @param radius Radius of the circle.
     * @throws NullPointerException if Canvas is NULL.
     */
    public void paintCircle(Canvas canvas, float x, float y, float radius) {
    	if (canvas==null) throw new NullPointerException();
    	
        canvas.drawCircle(x, y, radius, paint);
    }

    /**
     * Paint text on the given Canvas.
     * @param canvas Canvas to paint on.
     * @param x X Coordinate of the text.
     * @param y Y coordinate of the text.
     * @param text String to paint on the Canvas.
     * @throws NullPointerException if Canvas or String param is NULL.
     */
    public void paintText(Canvas canvas, float x, float y, String text) {
    	if (canvas==null || text==null) throw new NullPointerException();
    	
        canvas.drawText(text, x, y, paint);
    }

    /**
     * Paint generic object on the Canvas.
     * @param canvas Canvas to paint on.
     * @param obj Object to paint on the Canvas.
     * @param x X coordinate of the object.
     * @param y Y coordinate of the object.
     * @param rotation Rotation of the object.
     * @param scale Scale of the object.
     * @throws NullPointerException if Canvas or Object is NULL.
     */
    public void paintObj(	Canvas canvas, PaintableObject obj, 
    						float x, float y, 
    						float rotation, float scale) 
    {
    	if (canvas==null || obj==null) throw new NullPointerException();
    	
        canvas.save();
        canvas.translate(x+obj.getWidth()/2, y+obj.getHeight()/2);
        canvas.rotate(rotation);
        canvas.scale(scale,scale);
        canvas.translate(-(obj.getWidth()/2), -(obj.getHeight()/2));
        obj.paint(canvas);
        canvas.restore();
    }
    
    /**
     * Paint path on the given Canvas.
     * @param canvas Canvas to paint on.
     * @param path Path to paint on the Canvas.
     * @param x X coordinate of the path.
     * @param y Y coordinate of the path.
     * @param width Width of the path.
     * @param height Height of the path.
     * @param rotation Rotation of the path.
     * @param scale Scale of the path.
     * @throws NullPointerException if Canvas or Path is NULL.
     */
    public void paintPath(	Canvas canvas, Path path, 
    						float x, float y, float width, 
    						float height, float rotation, float scale) 
    {
    	if (canvas==null || path==null) throw new NullPointerException();
    	
    	canvas.save();
        canvas.translate(x + width / 2, y + height / 2);
        canvas.rotate(rotation);
        canvas.scale(scale, scale);
        canvas.translate(-(width / 2), -(height / 2));
        canvas.drawPath(path, paint);
        canvas.restore();
    }
}
