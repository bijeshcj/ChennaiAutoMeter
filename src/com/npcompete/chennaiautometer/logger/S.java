package com.npcompete.chennaiautometer.logger;

import android.util.Log;

public class S {
   private static final String TAG = "ChennaiAutoMeter";	
   private static boolean FLAG = true;
   public static void Log(Severe severe,String log){
	   switch(severe){
	   case HIGH:
		   Log.e(TAG,log);
		   break;
	   case LOW:
		   if(FLAG){
			   Log.d(TAG,log);
		   }
	   }
   }
}
