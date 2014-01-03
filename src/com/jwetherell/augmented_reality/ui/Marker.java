package com.jwetherell.augmented_reality.ui;

import java.text.DecimalFormat;

import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

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

/**
 * This class will represent a physical location and will calculate it's
 * visibility and draw it's text and visual representation accordingly. This
 * should be extended if you want to change the way a Marker is viewed.
 * 
 * Note: This class assumes if two Markers have the same name, it is the same
 * object.
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

    private final StringBuilder textStr = new StringBuilder();

    private float initialY = 0.0f;

    private static CameraModel cam = null;

    // Container for the circle or icon symbol
    protected PaintableObject gpsSymbol = null;
    private PaintablePosition symbolContainer = null;

    // Container for text
    private PaintableBoxedText textBox = null;
    private PaintablePosition textContainer = null;

    // Unique identifier of Marker
    private String name = null;
    // Marker's physical location (Lat, Lon, Alt)
    private final PhysicalLocation physicalLocation = new PhysicalLocation();
    // Distance from camera to PhysicalLocation in meters
    private double distance = 0.0;
    // Is within the radar
    private boolean isOnRadar = false;
    // Is in the camera's view
    private boolean isInView = false;
    // Physical location's X, Y, Z relative to the camera's location
    private final Vector locationXyzRelativeToPhysicalLocation = new Vector();
    // Marker's default color
    private int color = Color.WHITE;
    // For tracking Markers which have no altitude
    private boolean noAltitude = false;

    // Used to show exact GPS position
    private static boolean debugGpsPosition = false;
    private PaintablePoint positionPoint = null;
    private PaintablePosition positionContainer = null;

    // Used to debug the touching mechanism
    private static boolean debugTouchZone = true;
    private PaintableBox touchBox = null;
    private PaintablePosition touchPosition = null;

    public Marker(String name, double latitude, double longitude, double altitude, int color) {
        set(name, latitude, longitude, altitude, color);
    }

    /**
     * Set the objects parameters. This should be used instead of creating new
     * objects.
     * 
     * @param name
     *            String representing the Marker.
     * @param latitude
     *            Latitude of the Marker in decimal format (example 39.931269).
     * @param longitude
     *            Longitude of the Marker in decimal format (example -75.051261).
     * @param altitude
     *            Altitude of the Marker in meters (>0 is above sea level).
     * @param color
     *            Color of the Marker.
     */
    public synchronized void set(String name, double latitude, double longitude, double altitude, int color) {
        if (name == null)
            throw new NullPointerException();

        this.name = name;
        this.physicalLocation.set(latitude, longitude, altitude);
        this.color = color;
        this.isOnRadar = false;
        this.isInView = false;
        this.locationXyzRelativeToPhysicalLocation.set(0, 0, 0);
        this.initialY = 0.0f;
        if (altitude == 0.0d)
            this.noAltitude = true;
        else
            this.noAltitude = false;
    }

    /**
     * Get the name of the Marker.
     * 
     * @return String representing the new of the Marker.
     */
    public synchronized String getName() {
        return this.name;
    }

    /**
     * Get the color of this Marker.
     * 
     * @return int representing the Color of this Marker.
     */
    public synchronized int getColor() {
        return this.color;
    }

    /**
     * Get the distance of this Marker from the current GPS position.
     * 
     * @return double representing the distance of this Marker from the current
     *         GPS position.
     */
    public synchronized double getDistance() {
        return this.distance;
    }

    /**
     * Get the initial Y coordinate of this Marker. Used to reset after
     * collision detection.
     * 
     * @return float representing the initial Y coordinate of this Marker.
     */
    public synchronized float getInitialY() {
        return this.initialY;
    }

    /**
     * Get the whether the Marker is inside the range (relative to slider on
     * view)
     * 
     * @return True if Marker is inside the range.
     */
    public synchronized boolean isOnRadar() {
        return this.isOnRadar;
    }

    /**
     * Get the whether the Marker is inside the camera's view
     * 
     * @return True if Marker is inside the camera's view.
     */
    public synchronized boolean isInView() {
        return this.isInView;
    }

    /**
     * Get the position of the Marker in XYZ.
     * 
     * @return Vector representing the position of the Marker.
     */
    public synchronized Vector getScreenPosition() {
        screenPositionVector.set(locationXyzRelativeToCameraView);
        return screenPositionVector;
    }

    /**
     * Get the the location of the Marker in XYZ.
     * 
     * @return Vector representing the location of the Marker.
     */
    public synchronized Vector getLocation() {
        return this.locationXyzRelativeToPhysicalLocation;
    }

    public synchronized float getHeight() {
        if (symbolContainer == null || textContainer == null)
            return 0f;
        return symbolContainer.getHeight() + textContainer.getHeight();
    }

    public synchronized float getWidth() {
        if (symbolContainer == null || textContainer == null)
            return 0f;
        float symbolWidth = symbolContainer.getWidth();
        float textWidth = textContainer.getWidth();
        return (textWidth > symbolWidth) ? textWidth : symbolWidth;
    }

    /**
     * Update the matrices and visibility of the Marker.
     * 
     * @param canvas
     *            Canvas to use in the CameraModel.
     * @param addX
     *            Adder to the X position.
     * @param addY
     *            Adder to the Y position.
     */
    public synchronized void update(Canvas canvas, float addX, float addY) {
        if (canvas == null)
            throw new NullPointerException();

        if (cam == null)
            cam = new CameraModel(canvas.getWidth(), canvas.getHeight(), true);
        cam.set(canvas.getWidth(), canvas.getHeight(), false);
        cam.setViewAngle(CameraModel.DEFAULT_VIEW_ANGLE);
        populateMatrices(cam, addX, addY);
        updateRadar();
        updateView();
    }

    private synchronized void populateMatrices(CameraModel cam, float addX, float addY) {
        if (cam == null)
            throw new NullPointerException();

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
        locationXyzRelativeToPhysicalLocation.get(locationArray);
        float x = locationArray[0] / scale;
        float y = locationArray[2] / scale; // z==y Switched on purpose
        if ((x * x + y * y) < (Radar.RADIUS * Radar.RADIUS))
            isOnRadar = true;
    }

    private synchronized void updateView() {
        isInView = false;

        // If it's not on the radar, can't be in view3
        if (!isOnRadar)
            return;

        // If it's not in the same side as our viewing angle
        locationXyzRelativeToCameraView.get(locationArray);
        if (locationArray[2] >= -1f)
            return;

        locationXyzRelativeToCameraView.get(locationArray);
        float x = locationArray[0];
        float y = locationArray[1];

        // Adjust the center point because symbol my not be same size as text, making it off center
        if (touchBox!=null)  {
            float adjX = touchBox.getX();
            float adjY = touchBox.getY();
            x += adjX;
            y += adjY;
        }

        float width = getWidth()+10;
        float height = getHeight()+10;
        x -= width / 2;
        y -= height / 2;

        float ulX = x;
        float ulY = y;

        float lrX = x;
        float lrY = y;
        lrX += width;
        lrY += height;

        if (lrX >= -1 && ulX <= cam.getWidth() && lrY >= -1 && ulY <= cam.getHeight())
            isInView = true;

        /*
         * Log.w("updateView", "name "+this.name); Log.w("updateView",
         * "ul (x="+(ulX)+" y="+(ulY)+")"); Log.w("updateView",
         * "lr (x="+(lrX)+" y="+(lrY)+")"); Log.w("updateView",
         * "cam (w="+(cam.getWidth())+" h="+(cam.getHeight())+")"); if
         * (!isInView) Log.w("updateView", "isInView "+isInView); else
         * Log.e("updateView", "isInView "+isInView);
         */
    }

    /**
     * Calculate the relative position of this Marker from the given Location.
     * 
     * @param location
     *            Location to use in the relative position.
     * @throws NullPointerException
     *             if Location is NULL.
     */
    public synchronized void calcRelativePosition(Location location) {
        if (location == null)
            throw new NullPointerException();

        // Update the markers distance based on the new location.
        updateDistance(location);

        // noAltitude means that the elevation of the POI is not known
        // and should be set to the users GPS altitude
        if (noAltitude)
            physicalLocation.setAltitude(location.getAltitude());

        // Compute the relative position vector from user position to POI
        // location
        PhysicalLocation.convLocationToVector(location, 
                                              physicalLocation, 
                                              locationXyzRelativeToPhysicalLocation);
        this.initialY = locationXyzRelativeToPhysicalLocation.getY();
        updateRadar();
    }

    private synchronized void updateDistance(Location location) {
        if (location == null)
            throw new NullPointerException();

        Location.distanceBetween(physicalLocation.getLatitude(), 
                                 physicalLocation.getLongitude(), 
                                 location.getLatitude(),
                                 location.getLongitude(), 
                                 distanceArray);
        distance = distanceArray[0];
    }

    /**
     * Tell if the x/y position is on this marker (if the marker is visible)
     * 
     * @param x
     *            float x value.
     * @param y
     *            float y value.
     * @return True if Marker is visible and x/y is on the marker.
     */
    public synchronized boolean handleClick(float x, float y) {
        if (!isOnRadar || !isInView)
            return false;

        boolean result = isPointOnMarker(x, y, this);
        Log.e("handleClick", "point (x="+x+" y="+y+") isPointOnMarker="+result);
        return result;
    }

    /**
     * Determines if the marker is on this Marker.
     * 
     * @param marker
     *            Marker to test for overlap.
     * @return True if the marker is on Marker.
     */
    public synchronized boolean isMarkerOnMarker(Marker marker) {
        return isMarkerOnMarker(marker, true);
    }

    /**
     * Determines if the marker is on this Marker.
     * 
     * @param marker
     *            Marker to test for overlap.
     * @param reflect
     *            if True the Marker will call it's self recursively with the
     *            opposite arguments.
     * @return True if the marker is on Marker.
     */
    private synchronized boolean isMarkerOnMarker(Marker marker, boolean reflect) {
        if (marker == null)
            return false;

        marker.getScreenPosition().get(locationArray);
        float x = locationArray[0];
        float y = locationArray[1];

        float width = marker.getWidth();
        float height = marker.getHeight();
        x -= width / 2;
        y -= height / 2;

        float middleX = 0;
        float middleY = 0;
        middleX = x + (width / 2);
        middleY = y + (height / 2);
        boolean middleOfMarker = isPointOnMarker(middleX, middleY, this);
        if (middleOfMarker)
            return true;

        float ulX = x;
        float ulY = y;

        float urX = x;
        float urY = y;
        urX += width;

        float llX = x;
        float llY = y;
        llY += height;

        float lrX = x;
        float lrY = y;
        lrX += width;
        lrY += height;

        /*
         * Log.w("isMarkerOnMarker", "name "+this.name);
         * Log.w("isMarkerOnMarker", "ul (x="+(ulX)+" y="+(ulY)+")");
         * Log.w("isMarkerOnMarker", "ur (x="+(urX)+" y="+(urY)+")");
         * Log.w("isMarkerOnMarker", "ll (x="+(llX)+" y="+(llY)+")");
         * Log.w("isMarkerOnMarker", "lr (x="+(lrX)+" y="+(lrY)+")");
         */
 
        boolean upperLeftOfMarker = isPointOnMarker(ulX, ulY, this);
        if (upperLeftOfMarker)
            return true;

        boolean upperRightOfMarker = isPointOnMarker(urX, urY, this);
        if (upperRightOfMarker)
            return true;

        boolean lowerLeftOfMarker = isPointOnMarker(llX, llY, this);
        if (lowerLeftOfMarker)
            return true;

        boolean lowerRightOfMarker = isPointOnMarker(lrX, lrY, this);
        if (lowerRightOfMarker)
            return true;

        // If reflect is True then reverse the arguments and see if this Marker
        // is on the other marker.
        return (reflect) ? marker.isMarkerOnMarker(this, false) : false;
    }

    /*
     * Determines what side of a line a point lies.
     */
    private static final byte side(float Ax, float Ay, float Bx, float By, float X, float Y) {
        float result = (Bx-Ax)*(Y-Ay) - (By-Ay)*(X-Ax);
        if (result<0) {
            // below or right
            return -1;
        }
        if (result>0) {
            // above or left
            return 1;
        }
        // on
        return 0;
    }

    /*
     * Determines if point is between two lines
     */
    private static final boolean between(float Ax, float Ay, 
                                         float Bx, float By, 
                                         float Cx, float Cy, 
                                         float Dx, float Dy, 
                                         float X, float Y) {
        byte first = side(Ax, Ay, Bx, By, X, Y);
        byte second = side(Cx, Cy, Dx, Dy, X, Y);
        if (first==(second*-1)) return true;
        if (first==0 && second>0) return true;
        if (second==0 && first<0) return true;
        return false;
    }

    private float[] temp = new float[]{0,0};

    /**
     * Determines if the point is on this Marker.
     * 
     * @param xPoint
     *            X point.
     * @param yPoint
     *            Y point.
     * @param marker
     *            Marker to determine if the point is on.
     * @return True if the point is on Marker.
     */
    private synchronized boolean isPointOnMarker(float xPoint, float yPoint, Marker marker) {
        if (marker == null)
            return false;

        if (touchBox == null)
            return false;

        // UL
        temp[0] = 0;
        temp[1] = 0;
        touchBox.matrix.mapPoints(temp);
        Log.v("TAG", "UL x="+temp[0]+" y="+temp[1]);
        float ulX = temp[0];
        float ulY = temp[1];

        // UR
        temp[0] = touchBox.getWidth();
        temp[1] = 0;
        touchBox.matrix.mapPoints(temp);
        Log.v("TAG", "UR x="+temp[0]+" y="+temp[1]);
        float urX = temp[0];
        float urY = temp[1];

        // LL
        temp[0] = 0;
        temp[1] = touchBox.getHeight();
        touchBox.matrix.mapPoints(temp);
        Log.v("TAG", "LL x="+temp[0]+" y="+temp[1]);
        float llX = temp[0];
        float llY = temp[1];

        // LR
        temp[0] = touchBox.getWidth();
        temp[1] = touchBox.getHeight();
        touchBox.matrix.mapPoints(temp);
        Log.v("TAG", "LR x="+temp[0]+" y="+temp[1]);
        float lrX = temp[0];
        float lrY = temp[1];
 
        // Is the point between the top and bottom lines
        boolean betweenTB = between(ulX, ulY, urX, urY, llX, llY, lrX, lrY, xPoint, yPoint);
        // Is the point between the left and right lines
        boolean betweenLR = between(ulX, ulY, llX, llY, urX, urY, lrX, lrY, xPoint, yPoint);
        Log.v("TAG", "betweenTB="+betweenTB+" betweenLR="+betweenLR);
        if (betweenTB && betweenLR) return true;
        return false;
    }

    /**
     * Draw this Marker on the Canvas
     * 
     * @param canvas
     *            Canvas to draw on.
     * @throws NullPointerException
     *             if the Canvas is NULL.
     */
    public synchronized void draw(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();

        // If not visible then do nothing
        if (!isOnRadar || !isInView)
            return;

        if (debugTouchZone)
            drawTouchZone(canvas);

        // Draw the Icon and Text
        drawIcon(canvas);

        drawText(canvas);

        // Draw the exact position
        if (debugGpsPosition)
            drawPosition(canvas);
    }

    private synchronized void drawTouchZone(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();

        if (gpsSymbol == null)
            return;

        if (touchBox == null)
            touchBox = new PaintableBox(getWidth(), getHeight(), Color.WHITE, Color.GREEN);
        else
            touchBox.set(getWidth(), getHeight());

        getScreenPosition().get(locationArray);
        float x = locationArray[0];
        float y = locationArray[1];

        // Adjust the center point of the touch box
        float gpsHeight = (gpsSymbol!=null)?gpsSymbol.getHeight():Float.MAX_VALUE;
        float textHeight = (textBox!=null)?textBox.getHeight():Float.MAX_VALUE;
        float touchHeight = touchBox.getHeight();
        boolean textBoxIsSmaller = false;
        float minHeight = gpsHeight;
        if (gpsHeight>textHeight) {
            minHeight = textHeight;
            textBoxIsSmaller = true;
        }
        float adjY = (touchHeight/2) - minHeight;
        if (textBoxIsSmaller) adjY *= -1;
        touchBox.setCoordinates(0, adjY);

        float currentAngle = ARData.getOrientationAngle()+90;
        currentAngle = 360 - currentAngle;
        if (touchPosition == null)
            touchPosition = new PaintablePosition(touchBox, x, y, currentAngle, 1);
        else
            touchPosition.set(touchBox, x, y, currentAngle, 1);

        touchPosition.paint(canvas);
    }

    protected synchronized void drawIcon(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();

        if (gpsSymbol == null)
            gpsSymbol = new PaintableGps(36, 8, true, getColor());

        getScreenPosition().get(locationArray);
        float x = locationArray[0];
        float y = locationArray[1];

        // Adjust the symbol to be above
        gpsSymbol.setCoordinates(0, -gpsSymbol.getHeight()/2);

        float currentAngle = ARData.getOrientationAngle()+90;
        currentAngle = 360 - currentAngle;
        if (symbolContainer == null)
            symbolContainer = new PaintablePosition(gpsSymbol, x, y, currentAngle, 1);
        else
            symbolContainer.set(gpsSymbol, x, y, currentAngle, 1);

        symbolContainer.paint(canvas);
    }

    private synchronized void drawText(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();

        textStr.setLength(0);
        if (distance < 1000.0) {
            textStr.append(name).append(" (").append(DECIMAL_FORMAT.format(distance)).append("m)");
        } else {
            double d = distance / 1000.0;
            textStr.append(name).append(" (").append(DECIMAL_FORMAT.format(d)).append("km)");
        }
        float maxHeight = Math.round(canvas.getHeight() / 10f) + 1;

        if (textBox == null)
            textBox = new PaintableBoxedText(textStr.toString(), Math.round(maxHeight / 2f) + 1, 300);
        else
            textBox.set(textStr.toString(), Math.round(maxHeight / 2f) + 1, 300);

        getScreenPosition().get(locationArray);
        float x = locationArray[0];
        float y = locationArray[1];

        // Adjust the text to be below
        textBox.setCoordinates(0, textBox.getHeight()/2);

        float currentAngle = ARData.getOrientationAngle()+90;
        currentAngle = 360 - currentAngle;
        if (textContainer == null)
            textContainer = new PaintablePosition(textBox, x, y, currentAngle, 1);
        else
            textContainer.set(textBox, x, y, currentAngle, 1);

        textContainer.paint(canvas);
    }

    private synchronized void drawPosition(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();

        if (positionPoint == null)
            positionPoint = new PaintablePoint(Color.MAGENTA, true);

        getScreenPosition().get(locationArray);
        float x = locationArray[0];
        float y = locationArray[1];

        float currentAngle = ARData.getOrientationAngle()+90;
        currentAngle = 360 - currentAngle;
        if (positionContainer == null)
            positionContainer = new PaintablePosition(positionPoint, x, y, currentAngle, 1);
        else
            positionContainer.set(positionPoint, x, y, currentAngle, 1);

        positionContainer.paint(canvas);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int compareTo(Marker another) {
        if (another == null)
            throw new NullPointerException();

        return name.compareTo(another.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean equals(Object marker) {
        if (marker == null || name == null)
            throw new NullPointerException();

        return name.equals(((Marker)marker).getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int hashCode() {
        return name.hashCode();
    }
}
