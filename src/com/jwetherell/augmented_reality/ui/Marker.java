package com.jwetherell.augmented_reality.ui;

import java.text.DecimalFormat;

import com.jwetherell.augmented_reality.camera.CameraModel;
import com.jwetherell.augmented_reality.common.Utilities;
import com.jwetherell.augmented_reality.common.Vector;
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
        
    private static final Vector originVector = new Vector(0, 0, 0);
    private static final Vector upVector = new Vector(0, 1, 0);

    private final Vector screenPositionVector = new Vector();
    private final Vector tmpVector1 = new Vector();
    private final Vector tmpVector2 = new Vector();
    private final Vector tmpVector3 = new Vector();
    private final float[] distanceArray = new float[1];
    private final float[] symbolArray = new float[3];
    private final float[] textArray = new float[3];
    
    private volatile static CameraModel cam = null;
    
    private volatile PaintableBoxedText textBlock = null;
    private volatile PaintablePosition textContainer = null;    
    private volatile PaintableGps gps = null;
    
    private final ScreenPosition screenPosition = new ScreenPosition();

    //Container for the circle or icon symbol
    protected volatile PaintablePosition symbolContainer = null;
    //Unique identifier of Marker
    protected String name = null;
	//Marker's physical location (Lat, Lon, Alt)
    protected volatile PhysicalLocation physicalLocation = new PhysicalLocation();
	//Distance from camera to PhysicalLocation in meters
    protected volatile double distance = 0.0;
	//Is within the radar
    protected volatile boolean isOnRadar = false;
    //Is in the camera's view
    protected volatile boolean isInView = false;
    //Symbol's (circle in this case) X, Y, Z position relative to the camera's view
    //X is left/right, Y is up/down, Z is In/Out (unused)
    protected final Vector symbolXyzRelativeToCameraView = new Vector();
    //Text box's X, Y, Z position relative to the camera's view
    //X is left/right, Y is up/down, Z is In/Out (unused)
    protected final Vector textXyzRelativeToCameraView = new Vector();
    //Physical location's X, Y, Z relative to the camera's location
    protected final Vector locationXyzRelativeToPhysicalLocation = new Vector();
    //Marker's default color
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
	public synchronized void set(String name, double latitude, double longitude, double altitude, int color) {
		if (name==null) throw new NullPointerException();
		
		this.name = name;
		this.physicalLocation.set(latitude,longitude,altitude);
		this.color = color;
		this.isOnRadar = false;
		this.isInView = false;
		this.symbolXyzRelativeToCameraView.set(0, 0, 0);
		this.textXyzRelativeToCameraView.set(0, 0, 0);
		this.locationXyzRelativeToPhysicalLocation.set(0, 0, 0);
	}
	
	/**
	 * Get the name of the Marker.
	 * @return String representing the new of the Marker.
	 */
	public synchronized String getName(){
		return this.name;
	}

    /**
     * Get the color of this Marker.
     * @return int representing the Color of this Marker.
     */
    public synchronized int getColor() {
    	return this.color;
    }

    /**
     * Get the whether the Marker is inside the range (relative to slider on view)
     * @return True if Marker is inside the range.
     */
    public synchronized boolean isOnRadar() {
        return this.isOnRadar;
    }

    /**
     * Get the whether the Marker is inside the camera's view
     * @return True if Marker is inside the camera's view.
     */
    public synchronized boolean isInView() {
        return this.isInView;
    }

    /**
     * Get the position of the Marker in XYZ.
     * @return Vector representing the position of the Marker.
     */
    public synchronized Vector getScreenPosition() {
        symbolXyzRelativeToCameraView.get(symbolArray);
        textXyzRelativeToCameraView.get(textArray);
        float x = (symbolArray[0] + textArray[0])/2;
        float y = (symbolArray[1] + textArray[1])/2;
        float z = (symbolArray[2] + textArray[2])/2;
        screenPositionVector.set(x, y, z);
        return screenPositionVector;
    }

    /**
     * Get the the location of the Marker in XYZ.
     * @return Vector representing the location of the Marker.
     */
    public synchronized Vector getLocation() {
        return this.locationXyzRelativeToPhysicalLocation;
    }

    public synchronized float getHeight() {
        if (symbolContainer==null || textContainer==null) return 0f;
        return symbolContainer.getHeight()+textContainer.getHeight();
    }
    
    public synchronized float getWidth() {
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
    public synchronized void update(Canvas canvas, float addX, float addY) {
    	if (canvas==null) throw new NullPointerException();
    	
    	if (cam==null) cam = new CameraModel(canvas.getWidth(), canvas.getHeight(), true);
    	cam.set(canvas.getWidth(), canvas.getHeight(), false);
        cam.setViewAngle(CameraModel.DEFAULT_VIEW_ANGLE);
        cam.setTransform(ARData.getRotationMatrix());
        populateMatrices(originVector, cam, addX, addY);
        updateRadar();
        updateView();
    }

	private synchronized void populateMatrices(Vector original, CameraModel cam, float addX, float addY) {
		if (original==null || cam==null) throw new NullPointerException();
		
		// Temp properties
		tmpVector1.set(original);
		tmpVector3.set(upVector);
		tmpVector1.add(locationXyzRelativeToPhysicalLocation);
		tmpVector3.add(locationXyzRelativeToPhysicalLocation);
		tmpVector1.sub(cam.getLco());
		tmpVector3.sub(cam.getLco());
		tmpVector1.prod(cam.getTransform());
		tmpVector3.prod(cam.getTransform());

		tmpVector2.set(0, 0, 0);
		cam.projectPoint(tmpVector1, tmpVector2, addX, addY);
		symbolXyzRelativeToCameraView.set(tmpVector2);
		cam.projectPoint(tmpVector3, tmpVector2, addX, addY);
		textXyzRelativeToCameraView.set(tmpVector2);
	}

    private final float[] locationArray1 = new float[3];
    private final float[] symbolArray2 = new float[3];
	private synchronized void updateRadar() {
		isOnRadar = false;

		float range = ARData.getRadius() * 1000;
		float scale = range / Radar.RADIUS;
		locationXyzRelativeToPhysicalLocation.get(locationArray1);
        float x = locationArray1[0] / scale;
        float y = locationArray1[2] / scale;
        symbolXyzRelativeToCameraView.get(symbolArray2);
		if ((symbolArray2[2] < -1f) && ((x*x+y*y)<(Radar.RADIUS*Radar.RADIUS))) {
			isOnRadar = true;
		}
	}

	private final float[] symbolArray3 = new float[3];
    private synchronized void updateView() {
        isInView = false;

        symbolXyzRelativeToCameraView.get(symbolArray3);
        float x1 = symbolArray3[0] + (getWidth()/2);
        float y1 = symbolArray3[1] + (getHeight()/2);
        float x2 = symbolArray3[0] - (getWidth()/2);
        float y2 = symbolArray3[1] - (getHeight()/2);
        if (x1>=0 && 
            x2<=cam.getWidth() &&
            y1>=0 &&
            y2<=cam.getHeight()
        ) {
            isInView = true;
        }
    }

    /**
     * Calculate the relative position of this Marker from the given Location.
     * @param location Location to use in the relative position.
     * @throws NullPointerException if Location is NULL.
     */
    public synchronized void calcRelativePosition(Location location) {
		if (location==null) throw new NullPointerException();
		
	    updateDistance(location);
		// An elevation of 0.0 probably means that the elevation of the
		// POI is not known and should be set to the users GPS height
		if (physicalLocation.getAltitude()==0.0) physicalLocation.setAltitude(location.getAltitude());
		 
		// compute the relative position vector from user position to POI location
		PhysicalLocation.convLocationToVector(location, physicalLocation, locationXyzRelativeToPhysicalLocation);
		updateRadar();
    }
    
    private synchronized void updateDistance(Location location) {
        if (location==null) throw new NullPointerException();

        Location.distanceBetween(physicalLocation.getLatitude(), physicalLocation.getLongitude(), location.getLatitude(), location.getLongitude(), distanceArray);
        distance = distanceArray[0];
    }

    /**
     * Draw this Marker on the Canvas
     * @param canvas Canvas to draw on.
     * @throws NullPointerException if the Canvas is NULL.
     */
    public synchronized void draw(Canvas canvas) {
		if (canvas==null) throw new NullPointerException();

		//Calculate the visibility of this Marker
	    update(canvas,0,0);
	    
	    //If not visible then do nothing
	    if (!isOnRadar || !isInView) return;
	    
	    //Draw the Icon and Text
	    drawIcon(canvas);
	    drawText(canvas);
	}

    /**
     * Tell if the x/y position is on this marker (if the marker is visible)
     * @param x float x value.
     * @param y float y value.
     * @return True if Marker is visible and x/y is on the marker.
     */
    public synchronized boolean handleClick(float x, float y) {
    	if (!isOnRadar || !isInView) return false;
    	return isPointOnMarker(x,y);
    }

    /**
     * Determines if the point is on this Marker.
     * @param x X point.
     * @param y Y point.
     * @return True if the point is on Marker.
     */
	public synchronized boolean isPointOnMarker(float x, float y) {
        if (symbolContainer==null || textContainer==null) return false;
        
        symbolXyzRelativeToCameraView.get(symbolArray);
        textXyzRelativeToCameraView.get(textArray);
        float currentAngle = Utilities.getAngle(symbolArray[0], symbolArray[1], textArray[0], textArray[1]);
        
        float x1 = (symbolArray[0] + textArray[0])/2;
        float y1 = (symbolArray[1] + textArray[1])/2;
        
        float x2 = (symbolContainer.getX() + textContainer.getX())/2;
        float y2 = (symbolContainer.getY() + textContainer.getY())/2;
        
        screenPosition.setX(x - x1);
        screenPosition.setY(y - y1);
        screenPosition.rotate(Math.toRadians(-(currentAngle + 90)));
        screenPosition.setX(screenPosition.getX() + x2);
        screenPosition.setY(screenPosition.getY() + y2);

        float objX = x2 - (getWidth() / 2);
        float objY = y2 - (getHeight() / 2);
        float objW = getWidth();
        float objH = getHeight();

        if (screenPosition.getX() > objX && 
            screenPosition.getX() < (objX + objW) && 
            screenPosition.getY() > objY && 
            screenPosition.getY() < (objY + objH)) 
        {
            return true;
        }
        return false;
	}

    protected synchronized void drawIcon(Canvas canvas) {
    	if (canvas==null) throw new NullPointerException();
    	
        float maxHeight = Math.round(canvas.getHeight() / 10f) + 1;
        if (gps==null) gps = new PaintableGps((maxHeight / 1.5f), (maxHeight / 10f), true, getColor());
        
        symbolXyzRelativeToCameraView.get(symbolArray);
        if (symbolContainer==null) symbolContainer = new PaintablePosition(gps, symbolArray[0], symbolArray[1], 0, 1);
        else symbolContainer.set(gps, symbolArray[0], symbolArray[1], 0, 1);
        symbolContainer.paint(canvas);
    }

    private synchronized void drawText(Canvas canvas) {
		if (canvas==null) throw new NullPointerException();
		
	    String textStr = null;
	    if (distance<1000.0) {
	        textStr = name + " ("+ DECIMAL_FORMAT.format(distance) + "m)";          
	    } else {
	        double d=distance/1000.0;
	        textStr = name + " (" + DECIMAL_FORMAT.format(d) + "km)";
	    }

	    textXyzRelativeToCameraView.get(textArray);
	    symbolXyzRelativeToCameraView.get(symbolArray);
	    float maxHeight = Math.round(canvas.getHeight() / 10f) + 1;
	    if (textBlock==null) textBlock = new PaintableBoxedText(textStr, Math.round(maxHeight / 2f) + 1, 300);
	    else textBlock.set(textStr, Math.round(maxHeight / 2f) + 1, 300);
	    float x = textArray[0] - textBlock.getWidth() / 2;
	    float y = textArray[1] + maxHeight;
	    float currentAngle = Utilities.getAngle(symbolArray[0], symbolArray[1], textArray[0], textArray[1]);
	    float angle = currentAngle + 90;
	    if (textContainer==null) textContainer = new PaintablePosition(textBlock, x, y, angle, 1);
	    else textContainer.set(textBlock, x, y, angle, 1);
	    textContainer.paint(canvas);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int compareTo(Marker another) {
        if (another==null) throw new NullPointerException();
        
        return name.compareTo(another.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean equals(Object marker) {
        if(marker==null || name==null) throw new NullPointerException();
        
        return name.equals(((Marker)marker).getName());
    }
}
