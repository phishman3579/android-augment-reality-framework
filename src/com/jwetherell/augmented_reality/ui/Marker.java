package com.jwetherell.augmented_reality.ui;

import java.text.DecimalFormat;

import com.jwetherell.augmented_reality.camera.CameraModel;
import com.jwetherell.augmented_reality.common.MixUtils;
import com.jwetherell.augmented_reality.common.MixVector;
import com.jwetherell.augmented_reality.data.ARData;
import com.jwetherell.augmented_reality.data.PhysicalLocation;
import com.jwetherell.augmented_reality.ui.objects.PaintableBoxedText;
import com.jwetherell.augmented_reality.ui.objects.PaintablePosition;

import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;

public abstract class Marker implements Comparable<Marker> {
    private static final int MAX_OBJECTS = 100;
    
    //Unique identifier of Marker
    protected String name = null;
	//Marker's physical location
    protected PhysicalLocation physicalLocation = null;
	// distance from user to PhysicalLocation in meters
    protected double distance = 0.0;

	// Draw properties
    protected boolean isVisible = false;
    protected MixVector circleVector = new MixVector();
    protected MixVector signVector = new MixVector();
    protected MixVector locationVector = new MixVector();
    protected MixVector originVector = new MixVector(0, 0, 0);
    protected MixVector upVector = new MixVector(0, 1, 0);
	
	public Marker(String name, double latitude, double longitude, double altitude) {
		this.name = name;
		this.physicalLocation = new PhysicalLocation(latitude,longitude,altitude);
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

    public int getColor() {
    	return Color.WHITE;
    }
    
    @Override
    public int compareTo(Marker another) {
        return Double.compare(this.getDistance(), another.getDistance());
    }

    @Override
    public boolean equals (Object marker) {
        return this.name.equals(((Marker)marker).getName());
    }

    private void update(Canvas canvas, float addX, float addY) {
        CameraModel cam = new CameraModel(canvas.getWidth(), canvas.getHeight(), true);
        cam.setViewAngle(CameraModel.DEFAULT_VIEW_ANGLE);
        cam.transform = ARData.getRotationMatrix();
        populateMatrices(originVector, cam, addX, addY);
        calcVisibility();
    }

	private void populateMatrices(MixVector originalPoint, CameraModel cam, float addX, float addY) {
		// Temp properties
		MixVector tmpa = new MixVector(originalPoint);
		MixVector tmpc = new MixVector(upVector);
		tmpa.add(locationVector); //3 
		tmpc.add(locationVector); //3
		tmpa.sub(cam.lco); //4
		tmpc.sub(cam.lco); //4
		tmpa.prod(cam.transform); //5
		tmpc.prod(cam.transform); //5

		MixVector tmpb = new MixVector();
		cam.projectPoint(tmpa, tmpb, addX, addY); //6
		circleVector.set(tmpb); //7
		cam.projectPoint(tmpc, tmpb, addX, addY); //6
		signVector.set(tmpb); //7
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
        float[] dist=new float[3];
        Location.distanceBetween(getLatitude(), getLongitude(), location.getLatitude(), location.getLongitude(), dist);
        distance = dist[0];
    }

	public void calcRelativePosition(Location location) {
	    updateDistance(location);
		// An elevation of 0.0 probably means that the elevation of the
		// POI is not known and should be set to the users GPS height
		if(physicalLocation.getAltitude()==0.0) physicalLocation.setAltitude(location.getAltitude());
		 
		// compute the relative position vector from user position to POI location
		PhysicalLocation.convLocToVec(location, physicalLocation, locationVector);
	}

	public void draw(Canvas canvas) {
	    update(canvas,0,0);
	    
	    if (!isVisible) return;
	    
	    drawIcon(canvas);
	    drawText(canvas);
	}
	
	public abstract void drawIcon(Canvas canvas);

	public void drawText(Canvas canvas) {
	    String textStr="";
	    DecimalFormat df = new DecimalFormat("@#");
	    if (distance<1000.0) {
	        textStr = name + " ("+ df.format(distance) + "m)";          
	    } else {
	        double d=distance/1000.0;
	        textStr = name + " (" + df.format(d) + "km)";
	    }

	    float maxHeight = Math.round(canvas.getHeight() / 10f) + 1;
	    PaintableBoxedText textBlock = new PaintableBoxedText(textStr, Math.round(maxHeight / 2f) + 1, 300);
	    float x = signVector.x - textBlock.getWidth() / 2;
	    float y = signVector.y + maxHeight;
	    float currentAngle = MixUtils.getAngle(circleVector.x, circleVector.y, signVector.x, signVector.y);
	    float angle = currentAngle + 90;
	    PaintablePosition txtContainter = new PaintablePosition(textBlock, x, y, angle, 1);
	    txtContainter.paint(canvas);
	}
}