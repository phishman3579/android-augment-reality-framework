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

import java.util.ArrayList;
import java.util.List;


/**
 * A state class used to calculate bearing and pitch given a Matrix.
 * 
 * This file was adapted from Mixare <http://www.mixare.org/>
 * 
 * @author Daniele Gobbetti <info@mixare.org>
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PitchBearingCalculator {
	private static final Vector looking = new Vector();
	
	private static final float[] lookingArray = new float[3];
	
	private static final int bearingListSize = 5;
	private static List<Float> bearingList = new ArrayList<Float>();
	private static volatile float bearing = 0;
	
	private static final int pitchListSize = 5;
	private static List<Float> pitchList = new ArrayList<Float>();
	private static volatile float pitch = 0;

	private PitchBearingCalculator() {};
	
	public static synchronized float getBearing() {
        return PitchBearingCalculator.bearing;
    }
    public static synchronized float getPitch() {
        return PitchBearingCalculator.pitch;
    }

    public static synchronized void calcPitchBearing(Matrix rotationM) {
		if (rotationM==null) return;
		
		looking.set(0, 0, 0);
	    rotationM.transpose();
		looking.set(1, 0, 0);
		looking.prod(rotationM);
		looking.get(lookingArray);
		float bearing = ((Utilities.getAngle(0, 0, lookingArray[0], lookingArray[2])  + 360 ) % 360);
		bearingList.add(bearing);
		if (bearingList.size()>bearingListSize) bearingList.remove(0);
		float adjBearing = 0;
		for (float tempBearing : bearingList) {
		    adjBearing += tempBearing;
		}
		PitchBearingCalculator.bearing = adjBearing/bearingList.size();

		rotationM.transpose();
		looking.set(0, 1, 0);
		looking.prod(rotationM);
		looking.get(lookingArray);
		float pitch = -Utilities.getAngle(0, 0, lookingArray[1], lookingArray[2]);
		pitchList.add(pitch);
	    if (pitchList.size()>pitchListSize) pitchList.remove(0);
	    float adjPitch = 0;
	    for (float tempPitch : pitchList) {
	        adjPitch += tempPitch;
	    }
	    PitchBearingCalculator.pitch = adjPitch/pitchList.size();
	}
}
