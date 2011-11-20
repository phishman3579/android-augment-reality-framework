package com.jwetherell.augmented_reality.ui;

import java.text.DecimalFormat;

import com.jwetherell.augmented_reality.camera.CameraModel;
import com.jwetherell.augmented_reality.common.MixUtils;
import com.jwetherell.augmented_reality.common.MixVector;
import com.jwetherell.augmented_reality.data.ARData;
import com.jwetherell.augmented_reality.data.PhysicalLocation;
import com.jwetherell.augmented_reality.data.ScreenPosition;
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
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("@#");
        
    private static final MixVector originVector = new MixVector(0, 0, 0);
    private static final MixVector upVector = new MixVector(0, 1, 0);

    private static CameraModel cam = null;
    
    private final MixVector tmpa = new MixVector();
    private final MixVector tmpb = new MixVector();
    private final MixVector tmpc = new MixVector();

    private PaintableBoxedText textBlock = null;
    private PaintablePosition textContainer = null;    
    private PaintableGps gps = null;
    private ScreenPosition screenPosition = new ScreenPosition();
    
    private float[] dist = new float[1];
    
    protected PaintablePosition symbolContainer = null;
    
    //Unique identifier of Marker
    protected String name = null;
	//Marker's physical location (Lat, Lon, Alt)
    protected PhysicalLocation physicalLocation = new PhysicalLocation();
	//Distance from camera to PhysicalLocation in meters
    protected double distance = 0.0;
	//Is within the radar
    protected boolean onRadar = false;
    //Is in the camera's view
    protected boolean inView = false;
    //Symbol's (circle in this case) X, Y, Z position relative to the camera's view
    //X is left/right, Y is up/down, Z is In/Out (unused)
    protected MixVector symbolXyzRelativeToCameraView = new MixVector();
    //Text box's X, Y, Z position relative to the camera's view
    //X is left/right, Y is up/down, Z is In/Out (unused)
    protected MixVector textXyzRelativeToCameraView = new MixVector();
    //Physical location's X, Y, Z relative to the camera's location
    protected MixVector locationXyzRelativeToPhysicalLocation = new MixVector();
    
    protected int color = Color.WHITE;

	public Marker(String name, double latitude, double longitude, double altitude, int color) {
		set(name, latitude, longitude, altitude, color);
	}
	
	/**
	 * Set the objects parameters. This should be used instead of creating new objects.
	 * @param name String representing the Marker.
	 * @param latitude Latitude of the Marker.
	 * @param longitude Longitude of the Marker.
	 * @param altitude Altitude of the Marker.
	 * @param color Color of the Marker.
	 */
	public void set(String name, double latitude, double longitude, double altitude, int color) {
		if (name==null) throw new NullPointerException();
		
		this.name = name;
		this.physicalLocation.set(latitude,longitude,altitude);
		this.color = color;
		onRadar = false;
		inView = false;
		symbolXyzRelativeToCameraView.set(0, 0, 0);
		textXyzRelativeToCameraView.set(0, 0, 0);
		locationXyzRelativeToPhysicalLocation.set(0, 0, 0);
	}
	
	/**
	 * Get the name of the Marker.
	 * @return String representing the new of the Marker.
	 */
	public String getName(){
		return name;
	}

    /**
     * Get the color of this Marker.
     * @return int representing the Color of this Marker.
     */
    public int getColor() {
    	return color;
    }

    /**
     * Get the whether the Marker is inside the range (relative to slider on view)
     * @return True if Marker is inside the range.
     */
    public boolean onRadar() {
        return onRadar;
    }

    /**
     * Get the whether the Marker is inside the camera's view
     * @return True if Marker is inside the camera's view.
     */
    public boolean inView() {
        return inView;
    }
    
    /**
     * Get the position of the Marker in XYZ.
     * @return MixVector representing the prosition of the Marker.
     */
    public MixVector getScreenPosition() {
        return symbolXyzRelativeToCameraView;
    }

    /**
     * Get the the location of the Marker in XYZ.
     * @return MixVector representing the location of the Marker.
     */
    public MixVector getLocation() {
        return locationXyzRelativeToPhysicalLocation;
    }

    public float getHeight() {
        if (symbolContainer==null || textContainer==null) return 0f;
        return symbolContainer.getHeight()+textContainer.getHeight();
    }
    
    public float getWidth() {
        if (symbolContainer==null || textContainer==null) return 0f;
        float symbol = symbolContainer.getWidth();
        float text = textContainer.getWidth();
        return (symbol>text)?symbol:text;
    }
    
	/**
	 * Update the matrices and visibility of the Marker.
	 * 
	 * @param canvas Canvas to use in the CameraModel.
	 * @param addX Adder to the X position.
	 * @param addY Adder to the Y position.
	 */
    public void update(Canvas canvas, float addX, float addY) {
    	if (canvas==null) throw new NullPointerException();
    	
    	if (cam==null) cam = new CameraModel(canvas.getWidth(), canvas.getHeight(), true);
    	cam.set(canvas.getWidth(), canvas.getHeight(), false);
        cam.setViewAngle(CameraModel.DEFAULT_VIEW_ANGLE);
        cam.setTransform(ARData.getRotationMatrix());
        populateMatrices(originVector, cam, addX, addY);
        updateRadar();
        updateView();
    }

	private void populateMatrices(MixVector originalPoint, CameraModel cam, float addX, float addY) {
		if (originalPoint==null || cam==null) throw new NullPointerException();
		
		// Temp properties
		tmpa.set(originalPoint.x, originalPoint.y, originalPoint.z);
		tmpc.set(upVector.x, upVector.y, upVector.z);
		tmpa.add(locationXyzRelativeToPhysicalLocation); //3 
		tmpc.add(locationXyzRelativeToPhysicalLocation); //3
		tmpa.sub(cam.getLco()); //4
		tmpc.sub(cam.getLco()); //4
		tmpa.prod(cam.getTransform()); //5
		tmpc.prod(cam.getTransform()); //5

		tmpb.set(0, 0, 0);
		cam.projectPoint(tmpa, tmpb, addX, addY); //6
		symbolXyzRelativeToCameraView.set(tmpb.x, tmpb.y, tmpb.z); //7
		cam.projectPoint(tmpc, tmpb, addX, addY); //6
		textXyzRelativeToCameraView.set(tmpb.x, tmpb.y, tmpb.z); //7
	}

	private void updateRadar() {
		onRadar = false;

		float range = ARData.getRadius() * 1000;
		float scale = range / Radar.RADIUS;
        float x = locationXyzRelativeToPhysicalLocation.x / scale;
        float y = locationXyzRelativeToPhysicalLocation.z / scale;
		if ( (symbolXyzRelativeToCameraView.z < -1f) && ((x*x+y*y)<(Radar.RADIUS*Radar.RADIUS)) ) {
			onRadar = true;
		}
	}

    private void updateView() {
        inView = false;

        float x1 = symbolXyzRelativeToCameraView.x + (getWidth()/2);
        float y1 = symbolXyzRelativeToCameraView.y + (getHeight()/2);
        float x2 = symbolXyzRelativeToCameraView.x - (getWidth()/2);
        float y2 = symbolXyzRelativeToCameraView.y - (getHeight()/2);
        if (x1>=0 && 
            x2<=cam.getWidth() &&
            y1>=0 &&
            y2<=cam.getHeight()
        ) {
            inView = true;
        }
    }
    
    private void updateDistance(Location location) {
    	if (location==null) throw new NullPointerException();

        Location.distanceBetween(physicalLocation.getLatitude(), physicalLocation.getLongitude(), location.getLatitude(), location.getLongitude(), dist);
        distance = dist[0];
    }

    /**
     * Calculate the relative position of this Marker from the given Location.
     * @param location Location to use in the relative position.
     * @throws NullPointerException if Location is NULL.
     */
    public void calcRelativePosition(Location location) {
		if (location==null) throw new NullPointerException();
		
	    updateDistance(location);
		// An elevation of 0.0 probably means that the elevation of the
		// POI is not known and should be set to the users GPS height
		if (physicalLocation.getAltitude()==0.0) physicalLocation.setAltitude(location.getAltitude());
		 
		// compute the relative position vector from user position to POI location
		PhysicalLocation.convLocationToMixVector(location, physicalLocation, locationXyzRelativeToPhysicalLocation);
		//Log.i("Location", "locationRelativeToPhysicalLocation="+locationRelativeToPhysicalLocation.toString());
		updateRadar();
    }

    /**
     * Draw this Marker on the Canvas
     * @param canvas Canvas to draw on.
     * @throws NullPointerException if the Canvas is NULL.
     */
    public void draw(Canvas canvas) {
		if (canvas==null) throw new NullPointerException();

		//Calculate the visibility of this Marker
	    update(canvas,0,0);
	    
	    //If not visible then do nothing
	    if (!onRadar || !inView) return;
	    
	    //Draw the Icon and Text
	    drawIcon(canvas);
	    drawText(canvas);
	}

    public boolean handleClick(float x, float y) {
    	if (!onRadar || !inView) return false;
    	return isPointOnMarker(x,y);
    }
	
    /**
     * Determines if the point is on this Marker.
     * @param x X point.
     * @param y Y point.
     * @return True if the point is on this Marker.
     */
	public boolean isPointOnMarker(float x, float y) {
	    if (textContainer==null) return false;
	    
		float currentAngle = MixUtils.getAngle(symbolXyzRelativeToCameraView.x, symbolXyzRelativeToCameraView.y, textXyzRelativeToCameraView.x, textXyzRelativeToCameraView.y);
		
		screenPosition.setX(x - symbolXyzRelativeToCameraView.x);
		screenPosition.setY(y - symbolXyzRelativeToCameraView.y);
		screenPosition.rotate(Math.toRadians(-(currentAngle + 90)));
		screenPosition.setX(screenPosition.getX() + symbolContainer.getX());
		screenPosition.setY(screenPosition.getY() + symbolContainer.getY());

		float objX = symbolContainer.getX() - (symbolContainer.getWidth() / 2);
		float objY = symbolContainer.getY() - (symbolContainer.getHeight() / 2);
		float objW = symbolContainer.getWidth();
		float objH = symbolContainer.getHeight();

		if (screenPosition.getX() > objX && 
			screenPosition.getX() < (objX + objW) && 
			screenPosition.getY() > objY && 
			screenPosition.getY() < (objY + objH)) 
		{
			return true;
		}
		return false;
	}
    
    protected void drawIcon(Canvas canvas) {
    	if (canvas==null) throw new NullPointerException();
    	
        float maxHeight = Math.round(canvas.getHeight() / 10f) + 1;
        if (gps==null) gps = new PaintableGps((maxHeight / 1.5f), (maxHeight / 10f), true, getColor());
        
        if (symbolContainer==null) symbolContainer = new PaintablePosition(gps, symbolXyzRelativeToCameraView.x, symbolXyzRelativeToCameraView.y, 0, 1);
        else symbolContainer.set(gps, symbolXyzRelativeToCameraView.x, symbolXyzRelativeToCameraView.y, 0, 1);
        symbolContainer.paint(canvas);
    }

    private void drawText(Canvas canvas) {
		if (canvas==null) throw new NullPointerException();
		
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
	    float x = textXyzRelativeToCameraView.x - textBlock.getWidth() / 2;
	    float y = textXyzRelativeToCameraView.y + maxHeight;
	    float currentAngle = MixUtils.getAngle(symbolXyzRelativeToCameraView.x, symbolXyzRelativeToCameraView.y, textXyzRelativeToCameraView.x, textXyzRelativeToCameraView.y);
	    float angle = currentAngle + 90;
	    if (textContainer==null) textContainer = new PaintablePosition(textBlock, x, y, angle, 1);
	    else textContainer.set(textBlock, x, y, angle, 1);
	    textContainer.paint(canvas);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Marker another) {
        if (another==null) throw new NullPointerException();
        
        return name.compareTo(another.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object marker) {
        if(marker==null || name==null) throw new NullPointerException();
        
        return name.equals(((Marker)marker).getName());
    }
}
