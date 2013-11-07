package com.npcompete.chennaiautometer;

import java.util.Timer;
import java.util.TimerTask;

import com.npcompete.chennaiautometer.logger.S;
import com.npcompete.chennaiautometer.logger.Severe;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class BaseService extends Service{
	
	private Tracker gpsTracker;
    private Handler handler= new Handler();
    private Timer timer = new Timer();
    private Distance pastDistance = new Distance();
    private Distance currentDistance = new Distance();
    public static double DISTANCE;
    boolean flag = true ;
    private double totalDistance ;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		gpsTracker = new Tracker(this);
		Toast.makeText(this, "Service started...", Toast.LENGTH_LONG).show();
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                    	S.Log(Severe.LOW, "flag is "+flag);
                        if(flag){
                            pastDistance.setLatitude(gpsTracker.getLocation().getLatitude());
                            pastDistance.setLongitude(gpsTracker.getLocation().getLongitude());
                            flag = false;
                        }else{
                            currentDistance.setLatitude(gpsTracker.getLocation().getLatitude());
                            currentDistance.setLongitude(gpsTracker.getLocation().getLongitude());
                            flag = compare_LatitudeLongitude();
                        }
//                        Toast.makeText(BaseService.this, "latitude:"+gpsTracker.getLocation().getLatitude()+" \nlongitude:"+gpsTracker.getLocation().getLongitude(), 4000).show();

                    }
                });


            }
        };

        timer.schedule(timerTask,0, 5000);
		return START_STICKY;
	}
	
	
    
	//http://www.movable-type.co.uk/scripts/latlong-vincenty.html
	// Vincenty formula to find the distance in miles
	public static double distance(double lat1, double lon1, double lat2, double lon2) {
	    double a = 6378137, b = 6356752.314245, f = 1 / 298.257223563; // WGS-84 ellipsoid params
	    double L = Math.toRadians(lon2 - lon1);
	    double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
	    double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));
	    double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
	    double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

	    double sinLambda, cosLambda, sinSigma, cosSigma, sigma, sinAlpha, cosSqAlpha, cos2SigmaM;
	    double lambda = L, lambdaP, iterLimit = 100;
	    do {
	        sinLambda = Math.sin(lambda);
	        cosLambda = Math.cos(lambda);
	        sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
	                + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
	        if (sinSigma == 0)
	            return 0; // co-incident points
	        cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
	        sigma = Math.atan2(sinSigma, cosSigma);
	        sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
	        cosSqAlpha = 1 - sinAlpha * sinAlpha;
	        cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
	        if (Double.isNaN(cos2SigmaM))
	            cos2SigmaM = 0; // equatorial line: cosSqAlpha=0 (§6)
	        double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
	        lambdaP = lambda;
	        lambda = L + (1 - C) * f * sinAlpha
	                * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
	    } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

	    if (iterLimit == 0)
	        return Double.NaN; // formula failed to converge

	    double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
	    double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
	    double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
	    double deltaSigma = B
	            * sinSigma
	            * (cos2SigmaM + B
	                    / 4
	                    * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
	                            * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
	    double dist = b * A * (sigma - deltaSigma);

	    return dist;
	}
	
//    private double distance(double lat1, double lon1, double lat2, double lon2) {
//          double theta = lon1 - lon2;
//          double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
//          dist = Math.acos(dist);
//          dist = rad2deg(dist);
//          dist = dist * 60 * 1.1515;
//           return (dist);
//        }

       private double deg2rad(double deg) {
          return (deg * Math.PI / 180.0);
        }
       private double rad2deg(double rad) {
          return (rad * 180.0 / Math.PI);
   }
       
       @Override
       public void onDestroy() {

           super.onDestroy();
           System.out.println("--------------------------------onDestroy -stop service ");
           timer.cancel();
           DISTANCE = totalDistance ;
       }
       private Distance startLocation;
       private static boolean startFlag = true;
       public boolean compare_LatitudeLongitude(){
    	   
          S.Log(Severe.LOW, "StartLocation "+startLocation+" pastDistance "+pastDistance+" currentDistance "+currentDistance);

           if(pastDistance.getLatitude() == currentDistance.getLatitude() && pastDistance.getLongitude() == currentDistance.getLongitude()){
        	   S.Log(Severe.LOW, "place is same ");
               return false;
           }else{
               if(startFlag){
            	   S.Log(Severe.LOW, "%%% inside if condition startflag "+startFlag);
            	    
            	   startLocation = new Distance();
            	   startLocation.setLatitude(pastDistance.getLatitude());
            	   startLocation.setLongitude(pastDistance.getLongitude());
            	   startFlag = false;
            	   S.Log(Severe.LOW, "Start Location "+startLocation);
            	  
               }
               final double distance = distance(pastDistance.getLatitude(),pastDistance.getLongitude(),currentDistance.getLatitude(),currentDistance.getLongitude());
               double kiloMeterDistance = distance / 1000;
               S.Log(Severe.LOW, "$$ Distance  :"+distance);
               S.Log(Severe.LOW, "$$ Distance in kilometer :"+kiloMeterDistance);
               
               double totalDistance = (distance(startLocation.getLatitude(),startLocation.getLongitude(),currentDistance.getLatitude(),currentDistance.getLongitude())/1000);
               S.Log(Severe.LOW, "$$ Total Distance in kilometer :"+totalDistance);
               
               
//               handler.post(new Runnable() {
//
//                   @Override
//                   public void run() {
//                       float kilometer=1.609344f;
//
//                       totalDistance = totalDistance +  distance * kilometer;
//                       DISTANCE = totalDistance;
//                       //Toast.makeText(HomeFragment.HOMECONTEXT, "distance in km:"+DISTANCE, 4000).show();
//
//                   }
//               });

               return true;
           }

       }

//       private States getCurrentMovementStateOfDevice(Location newLocation){
//       	double velocity = Util.getVelocity(LocationState.getLastBestLocation(),newLocation);
//   		States now;
//   		Constants.updateMotionStateUpperLimits(AppLocationListener.this);
//   		if (velocity < Constants.STATE_STATIC_UPPER_LIMIT)
//   			now = States.STATIC;
//   		else if (velocity > Constants.STATE_STATIC_UPPER_LIMIT && velocity < Constants.STATE_WALKING_UPPER_LIMIT)
//   			now = States.WALKING;
//   		else if (velocity > Constants.STATE_WALKING_UPPER_LIMIT && velocity < Constants.STATE_SLOWDRIVING_UPPER_LIMIT)
//   			now = States.SLOW_DRIVING;
//   		else
//   			now = States.FAST_DRIVING;
//   		return now;
//       }

}
