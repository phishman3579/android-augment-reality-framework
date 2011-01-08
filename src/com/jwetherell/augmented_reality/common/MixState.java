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

public class MixState {
    public float bearing;
    public float pitch;

	public void calcPitchBearing(Matrix rotationM) {
	    MixVector looking = new MixVector();
		rotationM.transpose();
		looking.set(1, 0, 0);
		looking.prod(rotationM);
		this.bearing = (int) (MixUtils.getAngle(0, 0, looking.x, looking.z)  + 360 ) % 360;

		rotationM.transpose();
		looking.set(0, 1, 0);
		looking.prod(rotationM);
		this.pitch = -MixUtils.getAngle(0, 0, looking.y, looking.z);
	}
}
