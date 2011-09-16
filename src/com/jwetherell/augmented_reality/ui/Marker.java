package com.jwetherell.augmented_reality.ui;

import java.text.DecimalFormat;

import com.jwetherell.augmented_reality.camera.CameraModel;
import com.jwetherell.augmented_reality.common.MixUtils;
import com.jwetherell.augmented_reality.common.MixVector;
import com.jwetherell.augmented_reality.data.ARData;
import com.jwetherell.augmented_reality.data.PhysicalLocation;
import com.jwetherell.augmented_reality.ui.objects.PaintableBoxedText;
import com.jwetherell.augmented_reality.ui.objects.PaintableGps;
import com.jwetherell.augmented_reality.ui.objects.PaintablePosition;

import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;


/**
 * This class will represent a physical location and will calculate it's visibility and draw it's text and 
 * visual representation accordingly. This should be extended if you want to change the way a Marker is viewed.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Marker implements Comparable<Marker> {
    private static final int MAX_OBJECTS = 50;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("@#");
        
    private static final MixVector originVector = new MixVector(0, 0, 0);
    private static final MixVector upVector = new MixVector(0, 1, 0);

    private static CameraModel cam = null;
    
    private final MixVector tmpa = new MixVector();
    private final MixVector tmpb = new MixVector();
    private final MixVector tmpc = new MixVector();

    private PaintableBoxedText textBlock = null;
    private PaintablePosition txtContainter = null;
    
    private PaintableGps gps = null;
    private PaintablePosition gpsContainter = null;
    
    //Unique identifier of Marker
    protected String name = null;
	//Marker's physical location
    protected PhysicalLocation physicalLocation = new PhysicalLocation();
	// distance from user to PhysicalLocation in meters
    protected double distance = 0.0;

	// Draw properties
    protected boolean isVisible = false;
    protected MixVector circleVector = new MixVector();
    protected MixVector signVector = new MixVector();
    protected MixVector locationVector = new MixVector();
    
    protected int color = Color.WHITE;
	
	public Marker(String name, double latitude, double longitude, double altitude, int color) {
		set(name, latitude, longitude, altitude, color);
	}
	
	public void set(String name, double latitude, double longitude, double altitude, int color) {
		this.name = name;
		this.physicalLocation.set(latitude,longitude,altitude);
		this.color = color;
		isVisible = false;
		circleVector.set(0, 0, 0);
		signVector.set(0, 0, 0);
		locationVector.set(0, 0, 0);
	}
	
	public String getName(){
		return name;
	}

	public double getLatitude() {
		return physicalLocation.getLatitude();
	}
	
	public double getLongitude() {
		return physicalLocation.getLongitude();
	}
	
	public double getAltitude() {
		return physicalLocation.getAltitude();
	}
	
	public MixVector getLocationVector() {
		return locationVector;
	}

    public double getDistance() {
        return distance;
    }

    public int getMaxObjects() {
        return MAX_OBJECTS;
    }
    
    public void setColor(int color) {
    	this.color = color;
    }
    
    public int getColor() {
    	return color;
    }
    
    @Override
    public int compareTo(Marker another) {
    	if (another==null) throw new NullPointerException();
    	
        return Double.compare(this.getDistance(), another.getDistance());
    }

    @Override
    public boolean equals (Object marker) {
    	if(marker==null || name==null) return false;
    	
        return name.equals(((Marker)marker).getName());
    }

    private void update(Canvas canvas, float addX, float addY) {
    	if (canvas==null) return;
    	
    	if (cam==null) cam = new CameraModel(canvas.getWidth(), canvas.getHeight(), true);
        cam.setViewAngle(CameraModel.DEFAULT_VIEW_ANGLE);
        cam.setTransform(ARData.getRotationMatrix());
        populateMatrices(originVector, cam, addX, addY);
        calcVisibility();
    }

	private void populateMatrices(MixVector originalPoint, CameraModel cam, float addX, float addY) {
		if (originalPoint==null || cam==null) return;
		
		// Temp properties
		tmpa.set(originalPoint.x, originalPoint.y, originalPoint.z);
		tmpc.set(upVector.x, upVector.y, upVector.z);
		tmpa.add(locationVector); //3 
		tmpc.add(locationVector); //3
		tmpa.sub(cam.getLco()); //4
		tmpc.sub(cam.getLco()); //4
		tmpa.prod(cam.getTransform()); //5
		tmpc.prod(cam.getTransform()); //5

		tmpb.set(0, 0, 0);
		cam.projectPoint(tmpa, tmpb, addX, addY); //6
		circleVector.set(tmpb.x, tmpb.y, tmpb.z); //7
		cam.projectPoint(tmpc, tmpb, addX, addY); //6
		signVector.set(tmpb.x, tmpb.y, tmpb.z); //7
	}

	private void calcVisibility() {
		isVisible = false;
		
		float range = ARData.getRadius() * 1000;
		float scale = range / Radar.RADIUS;
        float x = getLocationVector().x / scale;
        float y = getLocationVector().z / scale;
		if ( (circleVector.z < -1f) && ((x*x+y*y)<(Radar.RADIUS*Radar.RADIUS)) ) {
			isVisible = true;
		}
	}

    private void updateDistance(Location location) {
    	if (location==null) return;
    	
        float[] dist=new float[1];
        Location.distanceBetween(getLatitude(), getLongitude(), location.getLatitude(), location.getLongitude(), dist);
        distance = dist[0];
    }

	public void calcRelativePosition(Location location) {
		if (location==null) return;
		
	    updateDistance(location);
		// An elevation of 0.0 probably means that the elevation of the
		// POI is not known and should be set to the users GPS height
		if (physicalLocation.getAltitude()==0.0) physicalLocation.setAltitude(location.getAltitude());
		 
		// compute the relative position vector from user position to POI location
		PhysicalLocation.convLocToVec(location, physicalLocation, locationVector);
	}

	public void draw(Canvas canvas) {
		if (canvas==null) return;

		//Calculate the visibility of this Marker
	    update(canvas,0,0);
	    
	    //If not visible then do nothing
	    if (!isVisible) return;
	    
	    //Draw the Icon and Text
	    drawIcon(canvas);
	    drawText(canvas);
	}

    public void drawIcon(Canvas canvas) {
    	if (canvas==null) return;
    	
        float maxHeight = Math.round(canvas.getHeight() / 10f) + 1;
        if (gps==null) gps = new PaintableGps((maxHeight / 1.5f), (maxHeight / 10f), true, getColor());
        
        if (gpsContainter==null) gpsContainter = new PaintablePosition(gps, circleVector.x, circleVector.y, 0, 1);
        else gpsContainter.set(gps, circleVector.x, circleVector.y, 0, 1);
        gpsContainter.paint(canvas);
    }

	public void drawText(Canvas canvas) {
		if (canvas==null) return;
		
	    String textStr = null;
	    if (distance<1000.0) {
	        textStr = name + " ("+ DECIMAL_FORMAT.format(distance) + "m)";          
	    } else {
	        double d=distance/1000.0;
	        textStr = name + " (" + DECIMAL_FORMAT.format(d) + "km)";
	    }

	    float maxHeight = Math.round(canvas.getHeight() / 10f) + 1;
	    if (textBlock==null) textBlock = new PaintableBoxedText(textStr, Math.round(maxHeight / 2f) + 1, 300);
	    else textBlock.set(textStr, Math.round(maxHeight / 2f) + 1, 300);
	    float x = signVector.x - textBlock.getWidth() / 2;
	    float y = signVector.y + maxHeight;
	    float currentAngle = MixUtils.getAngle(circleVector.x, circleVector.y, signVector.x, signVector.y);
	    float angle = currentAngle + 90;
	    if (txtContainter==null) txtContainter = new PaintablePosition(textBlock, x, y, angle, 1);
	    else txtContainter.set(textBlock, x, y, angle, 1);
	    txtContainter.paint(canvas);
	}
}
