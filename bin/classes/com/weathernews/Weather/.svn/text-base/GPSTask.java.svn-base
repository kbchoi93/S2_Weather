package com.weathernews.Weather;

import java.util.Locale;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.GpsStatus.Listener;
import android.os.Bundle;

public class GPSTask implements Runnable {
	showTodayWeather today;
	Location loc;
	
	GPSTask(showTodayWeather showtoday) {
		today = showtoday;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//Log.d("myTag", "GPSThread Started");
		LocationManager locationManager;
    	locationManager = (LocationManager)today.getSystemService(Context.LOCATION_SERVICE);
    	
    	Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);           
        String provider = locationManager.getBestProvider(criteria, true);
        
        loc = locationManager.getLastKnownLocation(provider);
        
        //Log.d("myTag", "provider=" + provider);
        
        LocationListener loclistener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				loc = location;
				//today.setLocation(location);
			}
		};
        
        locationManager.requestLocationUpdates(provider, 1000, 0, loclistener);
        
        if(loc != null) {
//        	/today.setLocation(loc);
        	locationManager.removeGpsStatusListener((Listener)loclistener);
        	return;
        }
        //Log.d("myTag", "provider=" + provider + loc.toString());
	}

}
