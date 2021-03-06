package com.weathernews.Weather;

import java.net.URL;
import java.io.InputStream;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class ImageLoader extends AsyncTask<String, Void, Drawable> {
    protected ImageLoaderListener listener;

    ImageLoader(ImageLoaderListener whence) {
	listener = whence;
    }

//  override def onPreExecute {}
//    @Override
    protected void onPostExecute(Drawable result) { listener.onImageLoaded(result); }
    //    @Override
    protected Drawable doInBackground(String... url) {
	try {
	    //Log.i("WeatherWidget", "ImageLoader loading " + url[0]);
	    Drawable image = Drawable.createFromStream((InputStream)(new URL(url[0]).getContent()), "src");
	    return image;
	} catch (Exception e) {
	    //Log.e("WeatherWidget", "Couldn't load image " + url[0]);
	    return null;
	} catch (OutOfMemoryError e) {
		System.gc();
		return null;
	}
    }
}
