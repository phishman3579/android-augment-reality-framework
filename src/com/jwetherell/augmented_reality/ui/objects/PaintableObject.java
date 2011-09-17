package com.jwetherell.augmented_reality.ui.objects;

import java.util.logging.Logger;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * This abstract class provides many methods paint objects on a given Canvas.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public abstract class PaintableObject {
    private static final Logger logger = Logger.getLogger(PaintableObject.class.getSimpleName());
    private static final boolean DEBUG = false;
    
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

    public abstract float getWidth();

    public abstract float getHeight();

    public abstract void paint(Canvas canvas);
    
    public void setFill(boolean fill) {
        if (fill)
            paint.setStyle(Paint.Style.FILL);
        else
            paint.setStyle(Paint.Style.STROKE);
    }

    public void setColor(int c) {
        paint.setColor(c);
    }

    public void setStrokeWidth(float w) {
        paint.setStrokeWidth(w);
    }

    public float getTextWidth(String txt) {
        return paint.measureText(txt);
    }

    public float getTextAsc() {
        return -paint.ascent();
    }

    public float getTextDesc() {
        return paint.descent();
    }

    public void setFontSize(float size) {
        paint.setTextSize(size);
    }

    public void paintLine(Canvas canvas, float x1, float y1, float x2, float y2) {
    	if (canvas==null) return;
    	
        if (DEBUG) logger.severe("paintLine: x1="+x1+" y1="+y1+" x2="+x2+" y2="+y2+" paint="+paint.toString());
        canvas.drawLine(x1, y1, x2, y2, paint);
    }

    public void paintRect(Canvas canvas, float x, float y, float width, float height) {
    	if (canvas==null) return;
    	
        if (DEBUG) logger.severe("paintRect: x="+x+" y="+y+" width="+(x + width)+" height="+(y + height)+" paint="+paint.toString());
        canvas.drawRect(x, y, x + width, y + height, paint);
    }
    
    public void paintBitmap(Canvas canvas, Bitmap bitmap, float left, float top) {
    	if (canvas==null) return;
    	
        if (DEBUG) logger.severe("paintBitmap: left="+left+" top="+top+" bitmap="+bitmap.toString());
        canvas.drawBitmap(bitmap, left, top, paint);
    }

    public void paintCircle(Canvas canvas, float x, float y, float radius) {
    	if (canvas==null) return;
    	
        if (DEBUG) logger.severe("paintCircle: x="+x+" y="+y+" radius="+radius);
        canvas.drawCircle(x, y, radius, paint);
    }

    public void paintText(Canvas canvas, float x, float y, String text) {
    	if (canvas==null && text==null) return;
    	
        if (DEBUG) logger.severe("paintText: x="+x+" y="+y+" text="+text);
        canvas.drawText(text, x, y, paint);
    }

    public void paintObj(	Canvas canvas, PaintableObject obj, 
    						float x, float y, 
    						float rotation, float scale) 
    {
    	if (canvas==null || obj==null) return;
    	
        if (DEBUG) logger.severe("paintObj: x="+x+" y="+y+" rotation="+rotation+" scale="+scale);
        canvas.save();
        canvas.translate(x + obj.getWidth() / 2, y + obj.getHeight() / 2);
        canvas.rotate(rotation);
        canvas.scale(scale, scale);
        canvas.translate(-(obj.getWidth() / 2), -(obj.getHeight() / 2));
        obj.paint(canvas);
        canvas.restore();
    }
    
    public void paintPath(	Canvas canvas, Path path, 
    						float x, float y, float width, 
    						float height, float rotation, float scale) 
    {
    	if (canvas==null || path==null) return;
    	
    	if (DEBUG) logger.severe("paintPath: x="+x+" y="+y+" rotation="+rotation+" scale="+scale);
        canvas.save();
        canvas.translate(x + width / 2, y + height / 2);
        canvas.rotate(rotation);
        canvas.scale(scale, scale);
        canvas.translate(-(width / 2), -(height / 2));
        canvas.drawPath(path, paint);
        canvas.restore();
    }
}
