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
 * A state class used to calculate bearing and pitch given a Matrix.
 * 
 * This file was adapted from Mixare <http://www.mixare.org/>
 * 
 * @author Daniele Gobbetti <info@mixare.org>
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class AzimuthCalculator {
    private static final Vector looking = new Vector();
    private static final float[] lookingArray = new float[3];

    private static volatile float azimuth = 0;

    private AzimuthCalculator() {};

    private static final float getAngle(float center_x, float center_y, float post_x, float post_y) {
        float tmpv_x = post_x - center_x;
        float tmpv_y = post_y - center_y;
        float d = (float) Math.sqrt(tmpv_x * tmpv_x + tmpv_y * tmpv_y);
        float cos = tmpv_x / d;
        float angle = (float) Math.toDegrees(Math.acos(cos));

        angle = (tmpv_y < 0) ? angle * -1 : angle;

        return angle;
    }

    public static synchronized float getAzimuth() {
        return AzimuthCalculator.azimuth;
    }

    public static synchronized void calcPitchBearing(Matrix rotationM) {
        if (rotationM==null) return;

        looking.set(0, 0, 0);
        rotationM.transpose();
        if (AugmentedReality.portrait) {
        	looking.set(0, 1, 0);
        } else {
        	looking.set(1, 0, 0);
        }
        looking.prod(rotationM);
        looking.get(lookingArray);
        AzimuthCalculator.azimuth = ((getAngle(0, 0, lookingArray[0], lookingArray[2])  + 360 ) % 360);
    }
}

