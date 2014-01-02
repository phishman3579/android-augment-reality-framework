package com.jwetherell.augmented_reality.common;


/**
 * This class detects the orientation of the device given the values from the
 * accelerometer.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Orientation {

    private static ORIENTATION currentOrientation = ORIENTATION.UNKNOWN;
    private static int orientationAngle = -1;

    private Orientation() { }

    public static ORIENTATION getDeviceOrientation() {
        return currentOrientation;
    }

    public static int getDeviceAngle() {
        return orientationAngle;
    }

    public static enum ORIENTATION {
        UNKNOWN, PORTRAIT, PORTRAIT_UPSIDE_DOWN, LANDSCAPE, LANDSCAPE_UPSIDE_DOWN
    };

    public static void calcOrientation(float[] accel_values) {
        int tempOrientation = -1;
        float X = -accel_values[0];
        float Y = -accel_values[1];
        float Z = -accel_values[2];
        float magnitude = X * X + Y * Y;
        // Don't trust the angle if the magnitude is small compared to the y
        // value
        if (magnitude * 4 >= Z * Z) {
            float OneEightyOverPi = 57.29577957855f;
            float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
            tempOrientation = 90 - Math.round(angle);
            // normalize to 0 - 359 range
            while (tempOrientation >= 360) {
                tempOrientation -= 360;
            }
            while (tempOrientation < 0) {
                tempOrientation += 360;
            }
        }
        // ^^ thanks to google for that code

        // now we must figure out which orientation based on the degrees
        ORIENTATION tempOrientRounded = ORIENTATION.UNKNOWN;
        // figure out actual orientation
        if (tempOrientation <= 45 || tempOrientation > 315) { // round to 0
            tempOrientRounded = ORIENTATION.PORTRAIT;// portrait
        } else if (tempOrientation > 45 && tempOrientation <= 135) { // round to 90
            tempOrientRounded = ORIENTATION.LANDSCAPE_UPSIDE_DOWN; // landscape left
        } else if (tempOrientation > 135 && tempOrientation <= 225) { // round to 180
            tempOrientRounded = ORIENTATION.PORTRAIT_UPSIDE_DOWN; // portrait upside down
        } else if (tempOrientation > 225 && tempOrientation <= 315) { // round to 270
            tempOrientRounded = ORIENTATION.LANDSCAPE;// landscape right
        }

        orientationAngle = tempOrientation;
        currentOrientation = tempOrientRounded;
    }
}
