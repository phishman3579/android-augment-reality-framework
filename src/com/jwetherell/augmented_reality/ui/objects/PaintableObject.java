package com.jwetherell.augmented_reality.ui.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * This abstract class provides many methods paint objects on a given Canvas.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public abstract class PaintableObject {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF rect = new RectF();

    protected float x = 0;
    protected float y = 0;

    public Matrix matrix = new Matrix();

    public PaintableObject() {
        if (paint == null) {
            paint = new Paint();
            paint.setTextSize(16);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.STROKE);
        }
    }

    /**
     * Set the x,y coordinates for this object
     * 
     * @param x float value
     * @param y float value
     */
    public void setCoordinates(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the X coordinate of the paintable object.
     * 
     * @return float x coordinate
     */
    public float getX() {
        return x;
    }

    /**
     * Get the Y coordinate of the paintable object.
     * 
     * @return float y coordinate
     */
    public float getY() {
        return y;
    }

    /**
     * Get the width of the paintable object.
     * 
     * @return float width
     */
    public abstract float getWidth();

    /**
     * Get the height of the paintable object.
     * 
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
        if (fill) paint.setStyle(Paint.Style.FILL);
        else paint.setStyle(Paint.Style.STROKE);
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
     * 
     * @param txt
     *            CharSequence to get the width of.
     * @param start
     *            Start of the text.
     * @param end
     *            End of the text.
     * @return float width of the text String.
     * @throws NullPointerException
     *             if the String param is NULL.
     */
    public float getTextWidth(CharSequence txt, int start, int end) {
        if (txt == null) throw new NullPointerException();
        return paint.measureText(txt, start, end);
    }

    /**
     * Get the ascent of the paint element.
     * 
     * @return float ascent of the text.
     */
    public float getTextAsc() {
        return -paint.ascent();
    }

    /**
     * Get the decent of the paint element.
     * 
     * @return float decent of the text.
     */
    public float getTextDesc() {
        return paint.descent();
    }

    /**
     * Set the font size of the paint object.
     * 
     * @param size
     *            to set the font.
     */
    public void setFontSize(float size) {
        paint.setTextSize(size);
    }

    /**
     * Paint a line on the given Canvas.
     * 
     * @param canvas
     *            Canvas to paint on.
     * @param x1
     *            Beginning X to draw line.
     * @param y1
     *            Beginning Y to draw line.
     * @param x2
     *            Ending X to draw line.
     * @param y2
     *            Ending Y to draw line.
     * @throws NullPointerException
     *             if Canvas is NULL.
     */
    public void paintLine(Canvas canvas, float x1, float y1, float x2, float y2) {
        if (canvas == null) throw new NullPointerException();

        canvas.getMatrix(matrix);

        canvas.drawLine(x1, y1, x2, y2, paint);
    }

    /**
     * Paint a rectangle on the given Canvas.
     * 
     * @param canvas
     *            Canvas to paint on.
     * @param x
     *            X location of the rectangle.
     * @param y
     *            Y location of the rectangle.
     * @param width
     *            Width of the rectangle.
     * @param height
     *            Height of the rectangle.
     * @throws NullPointerException
     *             if Canvas is NULL.
     */
    public void paintRect(Canvas canvas, float x, float y, float width, float height) {
        if (canvas == null) throw new NullPointerException();

        canvas.getMatrix(matrix);

        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    /**
     * Paint a rectangle with round corners on the given Canvas.
     * 
     * @param canvas
     *            Canvas to paint on.
     * @param x
     *            X location of the rectangle.
     * @param y
     *            Y location of the rectangle.
     * @param width
     *            Width of the rectangle.
     * @param height
     *            Height of the rectangle.
     * @throws NullPointerException
     *             if Canvas is NULL.
     */
    public void paintRoundedRect(Canvas canvas, float x, float y, float width, float height) {
        if (canvas == null) throw new NullPointerException();

        canvas.getMatrix(matrix);

        rect.set(x, y, x + width, y + height);
        canvas.drawRoundRect(rect, 15F, 15F, paint);
    }

    /**
     * Paint a bitmap on the given Canvas.
     * 
     * @param canvas
     *            Canvas to paint on.
     * @param bitmap
     *            Bitmap to paint.
     * @param left
     *            Left location to draw the bitmap.
     * @param top
     *            Top location to draw the bitmap.
     * @throws NullPointerException
     *             if Canvas or Bitmap is NULL.
     */
    public void paintBitmap(Canvas canvas, Bitmap bitmap, float left, float top) {
        if (canvas == null || bitmap == null) throw new NullPointerException();

        canvas.getMatrix(matrix);

        canvas.drawBitmap(bitmap, left, top, paint);
    }

    /**
     * Paint a circle on the given Canvas.
     * 
     * @param canvas
     *            Canvas to paint on.
     * @param x
     *            Center X coordinate of the circle.
     * @param y
     *            Center Y coordinate of the circle.
     * @param radius
     *            Radius of the circle.
     * @throws NullPointerException
     *             if Canvas is NULL.
     */
    public void paintCircle(Canvas canvas, float x, float y, float radius) {
        if (canvas == null) throw new NullPointerException();

        canvas.save();
        canvas.translate(radius,radius);

        canvas.getMatrix(matrix);

        canvas.drawCircle(x, y, radius, paint);
        canvas.restore();
    }

    /**
     * Paint text on the given Canvas.
     * 
     * @param canvas
     *            Canvas to paint on.
     * @param x
     *            X Coordinate of the text.
     * @param y
     *            Y coordinate of the text.
     * @param text
     *            CharSequence to paint on the Canvas.
     * @param start
     *            Start of the text.
     * @param end
     *            End of the text.
     * @throws NullPointerException
     *             if Canvas or String param is NULL.
     */
    public void paintText(Canvas canvas, float x, float y, CharSequence text, int start, int end) {
        if (canvas == null || text == null) throw new NullPointerException();

        canvas.getMatrix(matrix);

        canvas.drawText(text, start, end, x, y, paint);
    }

    /**
     * Paint generic object on the Canvas.
     * 
     * @param canvas
     *            Canvas to paint on.
     * @param obj
     *            Object to paint on the Canvas.
     * @param x
     *            X coordinate of the object.
     * @param y
     *            Y coordinate of the object.
     * @param rotation
     *            Rotation of the object.
     * @param scale
     *            Scale of the object.
     * @throws NullPointerException
     *             if Canvas or Object is NULL.
     */
    public void paintObj(Canvas canvas, PaintableObject obj, float x, float y, float rotation, float scale) {
        if (canvas == null || obj == null) throw new NullPointerException();

        canvas.save();
        canvas.translate(x,y);
        canvas.rotate(rotation);
        canvas.scale(scale, scale);
        obj.paint(canvas);
        matrix.set(obj.matrix);
        canvas.restore();
    }
}
