package com.jwetherell.augmented_reality.activity;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import com.jwetherell.augmented_reality.data.ARData;
import com.jwetherell.augmented_reality.ui.Marker;
import com.jwetherell.augmented_reality.ui.Radar;
import com.jwetherell.augmented_reality.ui.objects.PaintableBoxedText;
import com.jwetherell.augmented_reality.ui.objects.PaintablePosition;

/**
 * This class extends the View class and is designed draw the zoom bar, radar circle, and markers on the View.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class AugmentedView extends View {
    private static final int fontSize = 14;
    private static final int startLabelX = 4;
    private static final int endLabelX = 95;
    private static final int labelY = 85;
    private static final String startKM = "0km";
    private static final String endKM = "80km";
    private static final int leftBound = 11;
    private static final int rightBound = 88;
    private static final int conflictHeight = 74;
    
    private static Radar radar = null;
    private static PaintablePosition startTxtContainter = null;
    private static PaintablePosition endTxtContainter = null;
    private static PaintablePosition currentTxtContainter = null;
    private static int lastZoom = 0;
    
    private static AtomicBoolean drawing = new AtomicBoolean(false);
    
    public AugmentedView(Context context) {
        super(context);

        if (radar==null) radar = new Radar();
    }

    private static PaintablePosition generateCurrentZoom(Canvas canvas) {
        lastZoom = ARData.getZoomProgress();
        PaintableBoxedText currentTxtBlock = new PaintableBoxedText(ARData.getZoomLevel(), fontSize, 30);
        int x = canvas.getWidth()/100*lastZoom;
        int y = canvas.getHeight()/100*labelY;
        if (lastZoom < leftBound || lastZoom > rightBound) {
            y = canvas.getHeight()/100*conflictHeight;
            if (lastZoom < leftBound)
                x = canvas.getWidth()/100*startLabelX;
            else
                x = canvas.getWidth()/100*endLabelX;
        }
        PaintablePosition container  = new PaintablePosition(currentTxtBlock, x, y, 0, 1);
        return container;
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	assert(canvas!=null);
        
        Log.d("JUSTIN", "onDraw");
        if (drawing.compareAndSet(false, true)) { 
        	Log.d("JUSTIN", "drawing..");
        	
	        if (startTxtContainter==null) {
	            PaintableBoxedText startTextBlock = new PaintableBoxedText(startKM, fontSize, 30);
	            startTxtContainter = new PaintablePosition( startTextBlock, 
	                                                         (canvas.getWidth()/100*startLabelX), 
	                                                         (canvas.getHeight()/100*labelY), 
	                                                         0, 
	                                                         1);
	        }
	        startTxtContainter.paint(canvas);
	        
	        if (endTxtContainter==null) {
	            PaintableBoxedText endTextBlock = new PaintableBoxedText(endKM, fontSize, 30);
	            endTxtContainter = new PaintablePosition( endTextBlock, 
	                                                       (canvas.getWidth()/100*endLabelX), 
	                                                       (canvas.getHeight()/100*labelY), 
	                                                       0, 
	                                                       1);
	        }
	        endTxtContainter.paint(canvas);
        	
	        //Re-factor zoom text, if it has changed.
	        if (lastZoom != ARData.getZoomProgress()) currentTxtContainter = generateCurrentZoom(canvas);
	        currentTxtContainter.paint(canvas);
	
	        //Draw AR markers
		    for (Marker marker : ARData.getMarkers()) {
		        marker.draw(canvas);
		    }
	        
	        //Radar circle and radar markers
	        radar.draw(canvas);
	        drawing.set(false);
        }
    }
}
