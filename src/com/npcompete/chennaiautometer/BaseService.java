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
	
	

    private double distance(double lat1, double lon1, double lat2, double lon2) {
          double theta = lon1 - lon2;
          double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
          dist = Math.acos(dist);
          dist = rad2deg(dist);
          dist = dist * 60 * 1.1515;
           return (dist);
        }

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
       
       public boolean compare_LatitudeLongitude(){
    	   
    	   Toast.makeText(this, " pastDistance "+pastDistance+" currentDistance "+currentDistance,Toast.LENGTH_SHORT).show();

           if(pastDistance.getLatitude() == currentDistance.getLatitude() && pastDistance.getLongitude() == currentDistance.getLongitude()){
               return false;
           }else{

               final double distance = distance(pastDistance.getLatitude(),pastDistance.getLongitude(),currentDistance.getLatitude(),currentDistance.getLongitude());
               Toast.makeText(this, "Distance in mile :"+distance,Toast.LENGTH_SHORT).show();
//               System.out.println("Distance in mile :"+distance);
               handler.post(new Runnable() {

                   @Override
                   public void run() {
                       float kilometer=1.609344f;

                       totalDistance = totalDistance +  distance * kilometer;
                       DISTANCE = totalDistance;
                       //Toast.makeText(HomeFragment.HOMECONTEXT, "distance in km:"+DISTANCE, 4000).show();

                   }
               });

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
