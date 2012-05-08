package com.jwetherell.augmented_reality.ui;

import java.text.DecimalFormat;

import com.jwetherell.augmented_reality.activity.AugmentedReality;
import com.jwetherell.augmented_reality.camera.CameraModel;
import com.jwetherell.augmented_reality.common.Vector;
import com.jwetherell.augmented_reality.data.ARData;
import com.jwetherell.augmented_reality.data.PhysicalLocation;
import com.jwetherell.augmented_reality.ui.objects.PaintableBox;
import com.jwetherell.augmented_reality.ui.objects.PaintableBoxedText;
import com.jwetherell.augmented_reality.ui.objects.PaintableGps;
import com.jwetherell.augmented_reality.ui.objects.PaintableObject;
import com.jwetherell.augmented_reality.ui.objects.PaintablePoint;
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

    private static final Vector locationVector = new Vector(0, 0, 0);

    private final Vector screenPositionVector = new Vector();
    private final Vector tmpVector = new Vector();
    private final Vector tmpLocationVector = new Vector();
    private final Vector locationXyzRelativeToCameraView = new Vector();
    private final float[] distanceArray = new float[1];
    private final float[] locationArray = new float[3];

    private float initialY = 0.0f;
    
    private volatile static CameraModel cam = null;

    //Container for the circle or icon symbol
    protected PaintableObject gpsSymbol = null;
    private volatile PaintablePosition symbolContainer = null;

    //Container for text
    private PaintableBoxedText textBox = null;
    private volatile PaintablePosition textContainer = null;

    //Unique identifier of Marker
    private String name = null;
	//Marker's physical location (Lat, Lon, Alt)
    private final PhysicalLocation physicalLocation = new PhysicalLocation();
	//Distance from camera to PhysicalLocation in meters
    private double distance = 0.0;
	//Is within the radar
    private volatile boolean isOnRadar = false;
    //Is in the camera's view
    private volatile boolean isInView = false;
    //Physical location's X, Y, Z relative to the camera's location
    private final Vector locationXyzRelativeToPhysicalLocation = new Vector();
    //Marker's default color
    private int color = Color.WHITE;

    //Used to show exact GPS position
    private static boolean debugGpsPosition = false;
    private PaintablePoint positionPoint = null;
    private volatile PaintablePosition positionContainer = null;
    
    //Used to debug the touching mechanism
    private static boolean debugTouchZone = false;
    private PaintableBox touchBox = null;
    private volatile PaintablePosition touchPosition = null;

	public Marker(String name, double latitude, double longitude, double altitude, int color) {
		set(name, latitude, longitude, altitude, color);
	}
	
	/**
	 * Set the objects parameters. This should be used instead of creating new objects.
	 * @param name String representing the Marker.
	 * @param latitude Latitude of the Marker in decimal format (example 39.931269).
	 * @param longitude Longitude of the Marker in decimal format (example -75.051261). 
	 * @param altitude Altitude of the Marker in meters (>0 is above sea level). 
	 * @param color Color of the Marker.
	 */
	public synchronized void set(String name, double latitude, double longitude, double altitude, int color) {
		if (name==null) throw new NullPointerException();

		this.name = name;
		this.physicalLocation.set(latitude,longitude,altitude);
		this.color = color;
		this.isOnRadar = false;
		this.isInView = false;
		this.locationXyzRelativeToPhysicalLocation.set(0, 0, 0);
		this.initialY = 0.0f;
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
     * Get the distance of this Marker from the current GPS position.
     * @return double representing the distance of this Marker from the current GPS position.
     */
    public synchronized double getDistance() {
        return this.distance;
    }

    /**
     * Get the initial Y coordinate of this Marker. Used to reset after collision detection.
     * @return float representing the initial Y coordinate of this Marker.
     */
    public synchronized float getInitialY() {
        return this.initialY;
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
        screenPositionVector.set(locationXyzRelativeToCameraView);
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
        float symbolWidth = symbolContainer.getWidth();
        float textWidth = textContainer.getWidth();
        return (textWidth>symbolWidth)?textWidth:symbolWidth;
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
        populateMatrices(cam, addX, addY);
        updateRadar();
        if (isOnRadar) updateView();
    }

	private synchronized void populateMatrices(CameraModel cam, float addX, float addY) {
		if (cam==null) throw new NullPointerException();

	    // Find the location given the rotation matrix
        tmpLocationVector.set(locationVector);
        tmpLocationVector.add(locationXyzRelativeToPhysicalLocation);        
        tmpLocationVector.prod(ARData.getRotationMatrix());
        cam.projectPoint(tmpLocationVector, tmpVector, addX, addY);
        locationXyzRelativeToCameraView.set(tmpVector);
	}

    private synchronized void updateRadar() {
        isOnRadar = false;

        float range = ARData.getRadius() * 1000;
        float scale = range / Radar.RADIUS;
        locationXyzRelativeToCameraView.get(locationArray);
        float x = locationArray[0] / scale;
        float y = locationArray[2] / scale; // z==y Switched on purpose 
        if ((locationArray[2] <= -1f) && (x*x+y*y)<(Radar.RADIUS*Radar.RADIUS)) {
            isOnRadar = true;
        }
    }

    private synchronized void updateView() {
        isInView = false;

        locationXyzRelativeToCameraView.get(locationArray);
        float x = locationArray[0];
        float y = locationArray[1];

        float width = getWidth();
        float height = getHeight();

        if (AugmentedReality.portrait) {
            x -= height/2;
            y += width/2;
        } else {
            x -= width/2;
            y -= height/2;
        }

        float ulX = x;
        float ulY = y;

        float lrX = x;
        float lrY = y;
        if (AugmentedReality.portrait) {
            lrX += height;
            lrY -= width;
        } else {
            lrX += width;
            lrY += height;
        }

        if (AugmentedReality.portrait && (lrX>=-1 && ulX<=cam.getWidth() && ulY>=-1 && lrY<=cam.getHeight())) {
            isInView = true;
        } else if (lrX>=-1 && ulX<=cam.getWidth() && lrY>=-1 && ulY<=cam.getHeight()) {
            isInView = true;
        }
/*
        Log.w("updateView", "name "+this.name);
        Log.w("updateView", "ul (x="+(ulX)+" y="+(ulY)+")");
        Log.w("updateView", "lr (x="+(lrX)+" y="+(lrY)+")");
        Log.w("updateView", "cam (w="+(cam.getWidth())+" h="+(cam.getHeight())+")");
        if (!isInView) Log.w("updateView", "isInView "+isInView);
        else Log.e("updateView", "isInView "+isInView);
 */
    }

    /**
     * Calculate the relative position of this Marker from the given Location.
     * @param location Location to use in the relative position.
     * @throws NullPointerException if Location is NULL.
     */
    public synchronized void calcRelativePosition(Location location) {
		if (location==null) throw new NullPointerException();
		
		// Update the markers distance based on the new location.
	    updateDistance(location);
	    
		// An elevation of 0.0 probably means that the elevation of the
		// POI is not known and should be set to the users GPS height
		if (physicalLocation.getAltitude()==0.0) physicalLocation.setAltitude(location.getAltitude());
		 
		// Compute the relative position vector from user position to POI location
		PhysicalLocation.convLocationToVector(location, physicalLocation, locationXyzRelativeToPhysicalLocation);
		this.initialY = locationXyzRelativeToPhysicalLocation.getY();
		updateRadar();
    }
    
    private synchronized void updateDistance(Location location) {
        if (location==null) throw new NullPointerException();

        Location.distanceBetween(physicalLocation.getLatitude(), physicalLocation.getLongitude(), location.getLatitude(), location.getLongitude(), distanceArray);
        distance = distanceArray[0];
    }

    /**
     * Tell if the x/y position is on this marker (if the marker is visible)
     * @param x float x value.
     * @param y float y value.
     * @return True if Marker is visible and x/y is on the marker.
     */
    public synchronized boolean handleClick(float x, float y) {
        if (!isOnRadar || !isInView) return false;
        //Log.e("handleClick", "point (x="+x+" y="+y+")");
        boolean result = isPointOnMarker(x,y,this); 
        return result;
    }

    /**
     * Determines if the marker is on this Marker.
     * @param marker Marker to test for overlap.
     * @return True if the marker is on Marker.
     */
    public synchronized boolean isMarkerOnMarker(Marker marker) {
        return isMarkerOnMarker(marker,true);
    }

    /**
     * Determines if the marker is on this Marker.
     * @param marker Marker to test for overlap.
     * @param reflect if True the Marker will call it's self recursively with the opposite arguments.
     * @return True if the marker is on Marker.
     */
    private synchronized boolean isMarkerOnMarker(Marker marker, boolean reflect) {
        if (marker==null) return false;

        marker.getScreenPosition().get(locationArray);
        float x = locationArray[0];
        float y = locationArray[1];

        float width = marker.getWidth();
        float height = marker.getHeight();

        if (AugmentedReality.portrait) {
            x -= height/2;
            y += width/2;
        } else {
            x -= width/2;
            y -= height/2;
        }

        float middleX = 0;
        float middleY = 0;
        if (AugmentedReality.portrait) {
            middleX = x + (height/2);
            middleY = y - (width/2);
        } else {
            middleX = x + (width/2);
            middleY = y + (height/2);
        }
        boolean middleOfMarker = isPointOnMarker(middleX,middleY,this);
        if (middleOfMarker) return true;

        float ulX = x;
        float ulY = y;

        float urX = x;
        float urY = y;
        if (AugmentedReality.portrait) {
            urX += height;
        } else {
            urX += width;
        }

        float llX = x;
        float llY = y;
        if (AugmentedReality.portrait) {
            llY -= width;
        } else {
            llY += height;
        }

        float lrX = x;
        float lrY = y;
        if (AugmentedReality.portrait) {
            lrX += height;
            lrY -= width;
        } else {
            lrX += width;
            lrY += height;
        }
/*
        Log.w("isMarkerOnMarker", "name "+this.name);
        Log.w("isMarkerOnMarker", "ul (x="+(ulX)+" y="+(ulY)+")");
        Log.w("isMarkerOnMarker", "ur (x="+(urX)+" y="+(urY)+")");
        Log.w("isMarkerOnMarker", "ll (x="+(llX)+" y="+(llY)+")");
        Log.w("isMarkerOnMarker", "lr (x="+(lrX)+" y="+(lrY)+")");
*/
        boolean upperLeftOfMarker = isPointOnMarker(ulX,ulY,this);
        if (upperLeftOfMarker) return true;

        boolean upperRightOfMarker = isPointOnMarker(urX,urY,this);
        if (upperRightOfMarker) return true;

        boolean lowerLeftOfMarker = isPointOnMarker(llX,llY,this);
        if (lowerLeftOfMarker) return true;

        boolean lowerRightOfMarker = isPointOnMarker(lrX,lrY,this);
        if (lowerRightOfMarker) return true;

        //If reflect is True then reverse the arguments and see if this Marker is on the marker.
        return (reflect)?marker.isMarkerOnMarker(this,false):false;
    }

    /**
     * Determines if the point is on this Marker.
     * @param xPoint X point.
     * @param yPoint Y point.
     * @param marker Marker to determine if the point is on.
     * @return True if the point is on Marker.
     */
    private synchronized boolean isPointOnMarker(float xPoint, float yPoint, Marker marker) {
        if (marker==null) return false;

        marker.getScreenPosition().get(locationArray);
        float x = locationArray[0];
        float y = locationArray[1];

        float width = marker.getWidth();
        float height = marker.getHeight();

        if (AugmentedReality.portrait) {
            x -= height/2;
            y += width/2;
        } else {
            x -= width/2;
            y -= height/2;
        }

        float ulX = x;
        float ulY = y;

        float lrX = x;
        float lrY = y;
        if (AugmentedReality.portrait) {
            lrX += height;
            lrY -= width;
        } else {
            lrX += width;
            lrY += height;
        }
/*
        Log.w("isPointOnMarker", "xPoint="+(xPoint)+" yPoint="+(yPoint));
        Log.w("isPointOnMarker", "name "+this.name);
        Log.w("isPointOnMarker", "ul (x="+(ulX)+" y="+(ulY)+")");
        Log.w("isPointOnMarker", "lr (x="+(lrX)+" y="+(lrY)+")");
*/
        if (AugmentedReality.portrait) {
            if (xPoint>=ulX && xPoint<=lrX && yPoint<=ulY && yPoint>=lrY) return true;
        } else {
            if (xPoint>=ulX && xPoint<=lrX && yPoint>=ulY && yPoint<=lrY) return true;
        }

        return false;
    }

    /**
     * Draw this Marker on the Canvas
     * @param canvas Canvas to draw on.
     * @throws NullPointerException if the Canvas is NULL.
     */
    public synchronized void draw(Canvas canvas) {
        if (canvas==null) throw new NullPointerException();

        //If not visible then do nothing
        if (!isOnRadar || !isInView) return;

        //Draw the Icon and Text
        if (debugTouchZone) drawTouchZone(canvas);
        drawIcon(canvas);
        drawText(canvas);

        //Draw the exact position
        if (debugGpsPosition) drawPosition(canvas);
    }

    private synchronized void drawPosition(Canvas canvas) {
        if (canvas==null) throw new NullPointerException();

        if (positionPoint==null) positionPoint = new PaintablePoint(Color.MAGENTA,true);
        
        getScreenPosition().get(locationArray);
        float currentAngle = 0;
        if (AugmentedReality.portrait) currentAngle = -90;

        if (positionContainer==null) positionContainer = new PaintablePosition(positionPoint, locationArray[0], locationArray[1], currentAngle, 1);
        else positionContainer.set(positionPoint, locationArray[0], locationArray[1], currentAngle, 1);

        positionContainer.paint(canvas);
    }

    private synchronized void drawTouchZone(Canvas canvas) {
        if (canvas==null) throw new NullPointerException();

        if (gpsSymbol==null) return;

        if (touchBox==null) touchBox = new PaintableBox(getWidth(),getHeight(),Color.WHITE,Color.GREEN);
        else touchBox.set(getWidth(),getHeight());

        getScreenPosition().get(locationArray);
        float x = locationArray[0];
        float y = locationArray[1];
        if (AugmentedReality.portrait) {
            x -= textBox.getWidth()/2;
            y -= textBox.getWidth()/2;
            y += gpsSymbol.getHeight()/2;
        } else {
            x -= textBox.getWidth()/2;
            y -= gpsSymbol.getHeight();
        }
        float currentAngle = 0;
        if (AugmentedReality.portrait) currentAngle = -90;

        if (touchPosition==null) touchPosition = new PaintablePosition(touchBox, x, y, currentAngle, 1);
        else touchPosition.set(touchBox, x, y, currentAngle, 1);
        touchPosition.paint(canvas);
    }

    protected synchronized void drawIcon(Canvas canvas) {
        if (canvas==null) throw new NullPointerException();

        if (gpsSymbol==null) gpsSymbol = new PaintableGps(36, 8, true, getColor());

        getScreenPosition().get(locationArray);
        float x = locationArray[0];
        float y = locationArray[1];
        if (AugmentedReality.portrait) {
            x -= gpsSymbol.getWidth()/2;
            y -= gpsSymbol.getHeight();
        } else {
            y -= gpsSymbol.getHeight()/2;
        }
        float currentAngle = 0;
        if (AugmentedReality.portrait) currentAngle = -90;

        if (symbolContainer==null) symbolContainer = new PaintablePosition(gpsSymbol, x, y, currentAngle, 1);
        else symbolContainer.set(gpsSymbol, x, y, currentAngle, 1);
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
        float maxHeight = Math.round(canvas.getHeight() / 10f) + 1;

        if (textBox==null) textBox = new PaintableBoxedText(textStr, Math.round(maxHeight / 2f) + 1, 300);
        else textBox.set(textStr, Math.round(maxHeight / 2f) + 1, 300);

        getScreenPosition().get(locationArray);
        float x = locationArray[0];
        float y = locationArray[1];
        if (AugmentedReality.portrait) {
            x -= textBox.getWidth()/2;
            x += textBox.getHeight()/2;
            y -= textBox.getHeight()/2;
        } else {
            x -= textBox.getWidth()/2;
        }
        float currentAngle = 0;
        if (AugmentedReality.portrait) currentAngle = -90;

        if (textContainer==null) textContainer = new PaintablePosition(textBox, x, y, currentAngle, 1);
        else textContainer.set(textBox, x, y, currentAngle, 1);
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
