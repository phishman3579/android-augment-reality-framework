package com.jwetherell.augmented_reality.ui;

import android.graphics.Canvas;
import android.graphics.Color;

import com.jwetherell.augmented_reality.camera.CameraModel;
import com.jwetherell.augmented_reality.common.MixState;
import com.jwetherell.augmented_reality.common.MixUtils;
import com.jwetherell.augmented_reality.data.ARData;
import com.jwetherell.augmented_reality.ui.objects.PaintableCircle;
import com.jwetherell.augmented_reality.ui.objects.PaintableLine;
import com.jwetherell.augmented_reality.ui.objects.PaintablePosition;
import com.jwetherell.augmented_reality.ui.objects.PaintableRadarPoints;
import com.jwetherell.augmented_reality.ui.objects.PaintableText;


/**
 * This class will visually represent a radar screen with a radar radius and blips on the screen in their appropriate
 * locations. 
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Radar {
    public static final float RADIUS = 40;
    
    private static final int LINE_COLOR = Color.argb(150,0,0,220);
    private static final float PAD_X = 10;
    private static final float PAD_Y = 20;
    private static final int RADAR_COLOR = Color.argb(100, 0, 0, 200);
    private static final int TEXT_COLOR = Color.rgb(255,255,255);
    private static final int TEXT_SIZE = 12;

    private static MixState state = null;
    private static ScreenLine leftRadarLine = null;
    private static ScreenLine rightRadarLine = null;
    private static PaintablePosition leftLineContainer = null;
    private static PaintablePosition rightLineContainer = null;
    private static PaintablePosition circleContainer = null;
    
    private static PaintableRadarPoints radarPoints = null;
    private static PaintablePosition pointsContainer = null;
    
    private static PaintableText paintableText = null;
    private static PaintablePosition paintedContainer = null;

    public Radar() {
        if (state==null) state = new MixState();
        if (leftRadarLine==null) leftRadarLine = new ScreenLine();
        if (rightRadarLine==null) rightRadarLine = new ScreenLine();
    }

    public void draw(Canvas canvas) {
    	if (canvas==null) return;

    	//Update the pitch and bearing using the phone's rotation matrix
        state.calcPitchBearing(ARData.getRotationMatrix());

        //Update the radar graphics and text based upon the new pitch and bearing
        drawRadarCircle(canvas);
        drawRadarPoints(canvas);
        drawRadarLines(canvas);
        drawRadarText(canvas);
    }
    
    private void drawRadarCircle(Canvas canvas) {
    	if (canvas==null) return;
    	
        if (circleContainer==null) {
            PaintableCircle paintableCircle = new PaintableCircle(RADAR_COLOR,RADIUS,true);
            circleContainer = new PaintablePosition(paintableCircle,PAD_X+RADIUS,PAD_Y+RADIUS,0,1);
        }
        circleContainer.paint(canvas);
    }
    
    private void drawRadarPoints(Canvas canvas) {
    	if (canvas==null) return;
    	
        if (radarPoints==null) radarPoints = new PaintableRadarPoints();
        
        if (pointsContainer==null) 
        	pointsContainer = new PaintablePosition( radarPoints, 
                                                     PAD_X, 
                                                     PAD_Y, 
                                                     -state.bearing, 
                                                     1);
        else 
        	pointsContainer.set(radarPoints, 
                    			PAD_X, 
                    			PAD_Y, 
                    			-state.bearing, 
                    			1);
        
        pointsContainer.paint(canvas);
    }
    
    private void drawRadarLines(Canvas canvas) {
    	if (canvas==null) return;
    	
        //Left line
        if (leftLineContainer==null) {
            leftRadarLine.set(0, -RADIUS);
            leftRadarLine.rotate(-CameraModel.DEFAULT_VIEW_ANGLE / 2);
            leftRadarLine.add(PAD_X+RADIUS, PAD_Y+RADIUS);

            float leftX = leftRadarLine.x-(PAD_X+RADIUS);
            float leftY = leftRadarLine.y-(PAD_Y+RADIUS);
            PaintableLine leftLine = new PaintableLine(LINE_COLOR, leftX, leftY);
            leftLineContainer = new PaintablePosition(  leftLine, 
                                                        PAD_X+RADIUS, 
                                                        PAD_Y+RADIUS, 
                                                        0, 
                                                        1);
        }
        leftLineContainer.paint(canvas);
        
        //Right line
        if (rightLineContainer==null) {
            rightRadarLine.set(0, -RADIUS);
            rightRadarLine.rotate(CameraModel.DEFAULT_VIEW_ANGLE / 2);
            rightRadarLine.add(PAD_X+RADIUS, PAD_Y+RADIUS);
            
            float rightX = rightRadarLine.x-(PAD_X+RADIUS);
            float rightY = rightRadarLine.y-(PAD_Y+RADIUS);
            PaintableLine rightLine = new PaintableLine(LINE_COLOR, rightX, rightY);
            rightLineContainer = new PaintablePosition( rightLine, 
                                                        PAD_X+RADIUS, 
                                                        PAD_Y+RADIUS, 
                                                        0, 
                                                        1);
        }
        rightLineContainer.paint(canvas);
    }

    private void drawRadarText(Canvas canvas) {
    	if (canvas==null) return;
    	
        //Direction text
        int range = (int) (state.bearing / (360f / 16f)); 
        String  dirTxt = "";
        if (range == 15 || range == 0) dirTxt = "N"; 
        else if (range == 1 || range == 2) dirTxt = "NE"; 
        else if (range == 3 || range == 4) dirTxt = "E"; 
        else if (range == 5 || range == 6) dirTxt = "SE";
        else if (range == 7 || range == 8) dirTxt= "S"; 
        else if (range == 9 || range == 10) dirTxt = "SW"; 
        else if (range == 11 || range == 12) dirTxt = "W"; 
        else if (range == 13 || range == 14) dirTxt = "NW";
        int bearing = (int) state.bearing; 
        radarText(  canvas, 
                    ""+bearing+((char)176)+" "+dirTxt, 
                    (PAD_X + RADIUS), 
                    (PAD_Y - 5), 
                    true
                 );
        
        //Zoom text
        radarText(  canvas, 
                    MixUtils.formatDist(ARData.getRadius() * 1000), 
                    (PAD_X + RADIUS), 
                    (PAD_Y + RADIUS*2 -10), 
                    false
                 );
    }
    
    private void radarText(Canvas canvas, String txt, float x, float y, boolean bg) {
    	if (canvas==null || txt==null) return;
    	
        if (paintableText==null) paintableText = new PaintableText(txt,TEXT_COLOR,TEXT_SIZE,bg);
        else paintableText.set(txt,TEXT_COLOR,TEXT_SIZE,bg);
        
        if (paintedContainer==null) paintedContainer = new PaintablePosition(paintableText,x,y,0,1);
        else paintedContainer.set(paintableText,x,y,0,1);
        
        paintedContainer.paint(canvas);
    }

    private class ScreenLine {
        public float x, y;

        public ScreenLine() {
            set(0, 0);
        }

        public void set(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public void rotate(double t) {
            float xp = (float) Math.cos(t) * x - (float) Math.sin(t) * y;
            float yp = (float) Math.sin(t) * x + (float) Math.cos(t) * y;

            x = xp;
            y = yp;
        }

        public void add(float x, float y) {
            this.x += x;
            this.y += y;
        }
    }
}
