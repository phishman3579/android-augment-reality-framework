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
 * Some static utility functions.
 * 
 * This file was adapted from Mixare <http://www.mixare.org/>
 * 
 * @author Daniele Gobbetti <info@mixare.org>
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Utilities {

	public static float getAngle(float center_x, float center_y, float post_x, float post_y) {
		float tmpv_x = post_x - center_x;
		float tmpv_y = post_y - center_y;
		float d = (float) Math.sqrt(tmpv_x * tmpv_x + tmpv_y * tmpv_y);
		float cos = tmpv_x / d;
		float angle = (float) Math.toDegrees(Math.acos(cos));

		angle = (tmpv_y < 0) ? angle * -1 : angle;

		return angle;
	}
}
