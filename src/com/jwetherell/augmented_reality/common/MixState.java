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

/**
 * A state class used to calculate bearing and pitch given a Matrix.
 * 
 * This file was adapted from Mixare <http://www.mixare.org/>
 * 
 * @author Daniele Gobbetti <info@mixare.org>
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class MixState {
	private MixVector looking = new MixVector();
	
	public float bearing = 0;
	public float pitch = 0;

	public void calcPitchBearing(Matrix rotationM) {
		assert(rotationM!=null);
		
		looking.set(0, 0, 0);
	    rotationM.transpose();
		looking.set(1, 0, 0);
		looking.prod(rotationM);
		bearing = ((int) (MixUtils.getAngle(0, 0, looking.x, looking.z)  + 360 ) % 360);

		rotationM.transpose();
		looking.set(0, 1, 0);
		looking.prod(rotationM);
		pitch = -MixUtils.getAngle(0, 0, looking.y, looking.z);
	}
}
