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

import com.jwetherell.augmented_reality.activity.AugmentedReality;


/**
 * A static class used to calculate azimuth, pitch, and roll given a rotation matrix.
 * 
 * This file was adapted from Mixare <http://www.mixare.org/>
 * 
 * @author Daniele Gobbetti <info@mixare.org>
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Calculator {
    private static final Vector looking = new Vector();
    private static final float[] lookingArray = new float[3];

    private static volatile float azimuth = 0;
    private static volatile float pitch = 0;
    private static volatile float roll = 0;


    private Calculator() {};

    public static final float getAngle(float center_x, float center_y, float post_x, float post_y) {
        float tmpv_x = post_x - center_x;
        float tmpv_y = post_y - center_y;
        float d = (float) Math.sqrt(tmpv_x * tmpv_x + tmpv_y * tmpv_y);
        float cos = tmpv_x / d;
        float angle = (float) Math.toDegrees(Math.acos(cos));

        angle = (tmpv_y < 0) ? angle * -1 : angle;

        return angle;
    }

    /**
     * Azimuth the phone's camera is pointing. From 0 to 360 with magnetic north compensation.
     * 
     * @return float representing the azimuth the phone's camera is pointing
     */
    public static synchronized float getAzimuth() {
        return Calculator.azimuth;
    }

    /**
     * Pitch of the phone's camera. From -90 to 90, where negative is pointing down and zero is level.
     * 
     * @return float representing the pitch of the phone's camera.
     */
    public static synchronized float getPitch() {
        return Calculator.pitch;
    }

    /**
     * Roll of the phone's camera. From -90 to 90, where negative is rolled left and zero is level.
     * 
     * @return float representing the roll of the phone's camera.
     */
    public static synchronized float getRoll() {
        return Calculator.roll;
    }

    public static synchronized void calcPitchBearing(Matrix rotationMatrix) {
        if (rotationMatrix==null) return;

        rotationMatrix.transpose();
        if (AugmentedReality.portrait) {
        	looking.set(0, 1, 0);
        } else {
        	looking.set(1, 0, 0);
        }
        looking.prod(rotationMatrix);
        looking.get(lookingArray);
        Calculator.azimuth = ((getAngle(0, 0, lookingArray[0], lookingArray[2])  + 360 ) % 360);
        Calculator.roll = -(90-Math.abs(getAngle(0, 0, lookingArray[1], lookingArray[2])));
        looking.set(0, 0, 1);
        looking.prod(rotationMatrix);
        looking.get(lookingArray);
        Calculator.pitch = -(90-Math.abs(getAngle(0, 0, lookingArray[1], lookingArray[2])));
    }
}

