package com.weathernews.Weather;

import java.io.InputStream;
import java.net.URL;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class RadarTask implements Runnable{
	private static LandRadar animation;
	private String BaseUrl;
	private String[] ArrayFiles;
	private final static Drawable[] drawable = new Drawable[10];

	RadarTask(LandRadar animation, String baseurl, String[] arrayfiles) {
    	this.animation = animation;
		this.BaseUrl = baseurl;
		this.ArrayFiles = arrayfiles;
	}
	
	public void run(){

		if(BaseUrl != null) {
			for(int i = 0 ; i < 10 ; i++){
	    		try{
	    			final String url = BaseUrl + ArrayFiles[i];
//	    			Log.e("myTag", url);
	    			int j = 0;
	    			while( j < 3) {
	    			drawable[i] = Drawable.createFromStream((InputStream)(new URL(url).getContent()), "src");
	    			j++;
	    			if(drawable[i] != null) break;
	    			}
	    		}catch (Exception e) {
	    			//Log.e("myTag", "Can't get Image From " + BaseUrl + ArrayFiles[i] + e.toString());
	    		}			
			}
		}
		animation.SetButtonBackImage(drawable);
		
		clearmemory();
	}
	
	public void clearmemory() {
		for(int i = 0 ; i < 10 ; i ++){
			if(null != drawable[i]){
				drawable[i].setCallback(null);
				drawable[i] = null;
			}
		}
	}
}
