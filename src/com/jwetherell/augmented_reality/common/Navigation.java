/*
 * Copyright (C) 2010- Peer internet solutions
 * 
 * This file was an original part of mixare.
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>
 */
package com.jwetherell.augmented_reality.common;

import com.jwetherell.augmented_reality.data.ARData;

/**
 * A static class used to calculate azimuth, pitch, and roll given a rotation
 * matrix.
 * 
 * This file was adapted from Mixare <http://www.mixare.org/>
 * 
 * @author Daniele Gobbetti <info@mixare.org>
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Navigation {

    private static final Vector looking = new Vector();
    private static final float[] lookingArray = new float[3];
    private static final Matrix tempMatrix = new Matrix();
    private static final float unitPerDegree = 1f/90f;

    private static float azimuth = 0;
    private static float pitch = 0;
    private static float roll = 0;

    private Navigation() { }

    /**
     * Get angle in degrees between two points.
     * 
     * @param center_x Lesser point's X
     * @param center_y Lesser point's Y
     * @param post_x Greater point's X
     * @param post_y Greater point's Y
     * @return Angle in degrees
     */
    public static final float getAngle(float center_x, float center_y, float post_x, float post_y) {
        float delta_x = post_x - center_x;
        float delta_y = post_y - center_y;
        return (float)(Math.atan2(delta_y, delta_x) * 180 / Math.PI);
    }

    /**
     * Azimuth the phone's camera is pointing. From 0 to 360 with magnetic north
     * compensation.
     * 
     * @return float representing the azimuth the phone's camera is pointing
     */
    public static float getAzimuth() {
        return azimuth;
    }

    /**
     * Pitch of the phone's camera. From -90 to 90, where negative is pointing
     * down and zero is level.
     * 
     * @return float representing the pitch of the phone's camera.
     */
    public static float getPitch() {
        return pitch;
    }

    /**
     * Roll of the phone's camera. From -90 to 90, where negative is rolled left
     * and zero is level.
     * 
     * @return float representing the roll of the phone's camera.
     */
    public static float getRoll() {
        return roll;
    }

    /**
     * Calculate and populate the Azimuth, Pitch, and Roll.
     * 
     * @param rotationMatrix Rotation matrix used in calculations.
     */
    public static void calcPitchBearing(Matrix rotationMatrix) {
        if (rotationMatrix == null) return;

        tempMatrix.set(rotationMatrix);
        tempMatrix.transpose();

        float x = 0;
        float y = 0;
        int angle = ARData.getDeviceOrientationAngle();
        if (angle>=0 && angle<90) {
            x = (angle*unitPerDegree)-1;
            y = 1-(angle*unitPerDegree);
        } else if (angle>=90 && angle<180) {
            angle -= 90;
            x = (angle*unitPerDegree)-1;
            y = (angle*unitPerDegree)-1;
        } else if (angle>=180 && angle<270) {
            angle -= 180;
            x = 1-(angle*unitPerDegree);
            y = (angle*unitPerDegree)-1;
        } else {
            // >= 270 && < 360
            angle -= 270;
            x = 1-(angle*unitPerDegree);
            y = 1-(angle*unitPerDegree);
        }
        looking.set(x, y, 0);
        looking.prod(tempMatrix);
        looking.get(lookingArray);

        azimuth = ((getAngle(0, 0, lookingArray[0], lookingArray[2]) + 360) % 360);

        roll = -(90 - Math.abs(getAngle(0, 0, lookingArray[1], lookingArray[2])));

        looking.set(0, 0, 1);
        looking.prod(tempMatrix);
        looking.get(lookingArray);
        pitch = -(90 - Math.abs(getAngle(0, 0, lookingArray[1], lookingArray[2])));
    }
}
