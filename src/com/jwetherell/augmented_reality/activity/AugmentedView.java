package com.jwetherell.augmented_reality.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import com.jwetherell.augmented_reality.data.ARData;
import com.jwetherell.augmented_reality.ui.Marker;
import com.jwetherell.augmented_reality.ui.Radar;
import com.jwetherell.augmented_reality.ui.objects.PaintableBoxedText;
import com.jwetherell.augmented_reality.ui.objects.PaintablePosition;

public class AugmentedView extends View {
    private static Radar radar = null;
    
    private static PaintablePosition startTxtContainter = null;
    private static PaintablePosition endTxtContainter = null;
    private static PaintablePosition currentTxtContainter = null;
    private static int lastZoom = 0;
    
    private static final int fontSize = 14;
    private static final int startLabelX = 4;
    private static final int endLabelX = 90;
    private static final int labelY = 85;
    private static final String startKM = "0km";
    private static final String endKM = "80km";
    
    private static final int leftBound = 11;
    private static final int rightBound = 83;
    private static final int conflictHeight = 74;

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
        PaintablePosition container  = new PaintablePosition(currentTxtBlock, 
                                                               x, 
                                                               y, 
                                                               0, 
                                                               1);
        return container;
    }

    @Override
    protected void onDraw(Canvas canvas) {
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
            
            currentTxtContainter = generateCurrentZoom(canvas);
        }
        endTxtContainter.paint(canvas);
        
        //Re-factor zoom text, if it has changed.
        if (lastZoom != ARData.getZoomProgress()) currentTxtContainter = generateCurrentZoom(canvas);
        currentTxtContainter.paint(canvas);

        //Draw AR markers
	    for (int i=0; i<ARData.getMarkerCount(); i++) {
	        Marker marker = ARData.getMarker(i);
	        marker.draw(canvas);
	    }
        
        //Radar circle and radar markers
        radar.draw(canvas);
    }
}
