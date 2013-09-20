package com.Utility;

import android.location.Location;

import com.npcompete.chennaiautometer.Distance;

public class Util {
	public static double getVelocity(Location locationA,Location locationB)
	{
		//MLog.i("Util","Getting Velocity");
		double distance=getDistance(locationA,locationB); // in KiloMeters
		double time=getTime(locationA.getTime(),locationB.getTime());
		double velocity=distance/time;
		//MLog.i("Setting Velocity",velocity+"");
		return velocity;
	}
	public static double getDistance(Location locationA,Location locationB)
	{
		//MLog.i("Util","Getting Distance");
	    double R = 6373.00d;
	    double lon1 =locationA.getLongitude();
	    double lat1 =locationA.getLatitude();
	    
	    double lon2 =locationB.getLongitude();
	    double lat2 =locationB.getLatitude();
	    	
	    double dlon = lon2 - lon1;
	    dlon=Math.toRadians(dlon);
	    double dlat = lat2 - lat1;
	    dlat=Math.toRadians(dlat);
	    double a = Math.sin(dlat/2) * Math.sin(dlat/2) +
        Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * 
        Math.sin(dlon/2) * Math.sin(dlon/2); 
	    double c = 2 * Math.atan2((Math.sqrt(a)),Math.sqrt((1-a)));
	    double d = R * c;
	    //d/=1000; //in km
	   // MLog.i("Setting Distance",d+"");
	    return d;
	}
	public static double getTime(double previous,double next)
	{
		double time=((next-previous))/1000; //in seconds
		time/=60.0; // in minutes
		time/=60.0; // in hrs
		//MLog.i("Util","Setting Time"+time+"");
		return time;
	}
}
