package com.jwetherell.augmented_reality.data;

import com.jwetherell.augmented_reality.common.MixVector;

import android.location.Location;

/**
 * This class is used to represent a physical locations which have a latitude, longitude, and alitude.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PhysicalLocation {
	private double latitude = 0.0;
	private double longitude = 0.0;
	private double altitude = 0.0;

	public PhysicalLocation() { }

	public PhysicalLocation(PhysicalLocation pl) {
		this(pl.latitude, pl.longitude, pl.altitude);
	}

	public PhysicalLocation(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
	}

	@Override
	public String toString() {
		return "(lat=" + latitude + ", lng=" + longitude + ", alt=" + altitude + ")";
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	
	public static void calcDestination(double lat1Deg, double lon1Deg, double bear, double d, PhysicalLocation dest) {
		double brng = Math.toRadians(bear);
		double lat1 = Math.toRadians(lat1Deg);
		double lon1 = Math.toRadians(lon1Deg);
		double R = 6371.0 * 1000.0; 

		double lat2 = Math.asin(Math.sin(lat1) * Math.cos(d / R)
				+ Math.cos(lat1) * Math.sin(d / R) * Math.cos(brng));
		double lon2 = lon1
				+ Math.atan2(Math.sin(brng) * Math.sin(d / R) * Math.cos(lat1),
				Math.cos(d / R) - Math.sin(lat1) * Math.sin(lat2));

		dest.setLatitude(Math.toDegrees(lat2));
		dest.setLongitude(Math.toDegrees(lon2));
	}

	public static void convLocToVec(Location org, PhysicalLocation gp, MixVector v) {
		float[] z = new float[1];
		z[0] = 0;
		Location.distanceBetween(org.getLatitude(), org.getLongitude(), gp
				.getLatitude(), org.getLongitude(), z);
		float[] x = new float[1];
		Location.distanceBetween(org.getLatitude(), org.getLongitude(), org
				.getLatitude(), gp.getLongitude(), x);
		double y = gp.getAltitude() - org.getAltitude();
		if (org.getLatitude() < gp.getLatitude())
			z[0] *= -1;
		if (org.getLongitude() > gp.getLongitude())
			x[0] *= -1;

		v.set(x[0], (float) y, z[0]);
	}

	public static void convertVecToLoc(MixVector v, Location org, Location gp) {
		double brngNS = 0, brngEW = 90;
		if (v.z > 0)
			brngNS = 180;
		if (v.x < 0)
			brngEW = 270;

		PhysicalLocation tmp1Loc = new PhysicalLocation();
		PhysicalLocation tmp2Loc = new PhysicalLocation();
		PhysicalLocation.calcDestination(org.getLatitude(), org.getLongitude(), brngNS,
				Math.abs(v.z), tmp1Loc);
		PhysicalLocation.calcDestination(tmp1Loc.getLatitude(), tmp1Loc.getLongitude(),
				brngEW, Math.abs(v.x), tmp2Loc);

		gp.setLatitude(tmp2Loc.getLatitude());
		gp.setLongitude(tmp2Loc.getLongitude());
		gp.setAltitude(org.getAltitude() + v.y);
	}
}
