package com.weathernews.Weather;

import java.util.Date;
import android.graphics.drawable.Drawable;

public class LiveCamData {
    public String address;
    public float temp;
    public float prec;
    public float windspd;
    public float winddir;
    public Date basedate;
    public int stride;
    public Drawable[] images;
    public String[] imageURLs;
    public String videoURL;
    LiveCamData() { address = null; temp = prec = windspd = winddir = 0.0f; basedate = null; images = null; imageURLs = null; videoURL = null; }

    public String toString() {
	return "Address = " + address + "\n" +
	    "Temp = " + Float.toString(temp) + "\n" +
	    "Prec = " + Float.toString(prec) + "\n" +
	    "Windspd = " + Float.toString(windspd) + "\n" +
	    "Winddir = " + Float.toString(winddir) + "\n" +
	    "Basedate = " + basedate.toString() + "\n" +
	    "Stride = " + Integer.toString(stride) + "\n" +
	    "Image URLs = " + imageURLs.toString() + "\n" +
	    "Video URL = " + videoURL;
    }
}
