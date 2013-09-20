package com.npcompete.chennaiautometer;



import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class Tracker implements LocationListener{
	
	private static final String TAG = Tracker.class.getCanonicalName();
	private final Context mContext;
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;
	Location location = null; 
	double latitude; 
	double longitude; 

	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	protected LocationManager locationManager;
	private Location m_Location;
	 public Tracker(Context context) {
	    this.mContext = context;
	    m_Location = getLocation();
	    if(m_Location != null){
	    System.out.println("location Latitude:"+m_Location.getLatitude());
	    System.out.println("location Longitude:"+m_Location.getLongitude());
	    }
	    System.out.println("getLocation():"+getLocation());
	    
	    }

	public Location getLocation() {
	    try {
	        locationManager = (LocationManager) mContext
	                .getSystemService(Context.LOCATION_SERVICE);

	        isGPSEnabled = locationManager
	                .isProviderEnabled(LocationManager.GPS_PROVIDER);

	        isNetworkEnabled = locationManager
	                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

	        if (!isGPSEnabled && !isNetworkEnabled) {
	            // no network provider is enabled
	        	Log.d(TAG,"$$$ No network provider....");
	        } 
	        else {
	            this.canGetLocation = true;
	            if (isNetworkEnabled) {
	                locationManager.requestLocationUpdates(
	                        LocationManager.NETWORK_PROVIDER,
	                        MIN_TIME_BW_UPDATES,
	                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//	                Toast.makeText(mContext,"Network Enabled",Toast.LENGTH_SHORT).show();
	                if (locationManager != null) {
	                    location = locationManager
	                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	                    if (location != null) {
	                        latitude = location.getLatitude();
	                        longitude = location.getLongitude();
	                    }
	                }
	            }
	            if (isGPSEnabled) {
	                if (location == null) {
	                    locationManager.requestLocationUpdates(
	                            LocationManager.GPS_PROVIDER,
	                            MIN_TIME_BW_UPDATES,
	                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//	                    Toast.makeText(mContext,"GPS Enabled",Toast.LENGTH_SHORT).show();
	                   
	                    if (locationManager != null) {
	                        location = locationManager
	                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
	                        if (location != null) {
	                            latitude = location.getLatitude();
	                            longitude = location.getLongitude();
	                        }
	                    }
	                }
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return location;
	}

	public void stopUsingGPS() {
	    if (locationManager != null) {
	        locationManager.removeUpdates(Tracker.this);
	    }
	}

	public double getLatitude() {
	    if (location != null) {
	        latitude = location.getLatitude();
	    }

	    return latitude;
	}

	public double getLongitude() {
	    if (location != null) {
	        longitude = location.getLongitude();
	    }

	    return longitude;
	}

	public boolean canGetLocation() {
	    return this.canGetLocation;
	}

	
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onLocationChanged");
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onProviderDisabled");
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onProviderEnabled");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onStatusChanged");
	}

	
   
}
