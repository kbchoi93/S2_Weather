package com.weathernews.Weather;

import java.util.Date;
import java.net.URL;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

import android.os.AsyncTask;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;


class LiveCamParser extends DefaultHandler {
    // State objects
    protected char[] stch;
    protected int ststart = 0;
    protected int stend = 0;
    protected Date stdate = null;

    protected LiveCamData data;

    public LiveCamData getData() { return data; }

    public enum Tagnames {
	address, basedate, stride, images, video, temp, prec, windspd, winddir
    }

    protected int getInt() { try { return Integer.parseInt(new String(stch, ststart, stend)); } catch (Exception e) { return Integer.MIN_VALUE; } }
    protected float getFloat() { try { return Float.parseFloat(new String(stch, ststart, stend)); } catch (Exception e) { return Float.NaN; } }
    protected String getString() { return new String(stch, ststart, stend); }
    protected Date getDate() { try { return new Date(1000L * Integer.parseInt(new String(stch, ststart, stend))); } catch (Exception e) { return null ; } }

    //    @Override
    public void startDocument() {
	//Log.i("WeatherWidget", "Livecamera parsing starts");
	data = new LiveCamData();
    }

    // @Override
    public void startElement(String a, String localName, String b, Attributes att) {
    }

    //    @Override
    public void characters(char[] ch, int start, int end) {
	stch = ch;
	ststart = start;
	stend = end;
    }

    //    @Override
    public void endElement(String uri, String localName, String qName) {
	try {
	    switch (Tagnames.valueOf(localName)) {
	    case address  : data.address   = getString(); break;
	    case basedate : data.basedate  = getDate();   break;
	    case stride   : data.stride    = getInt();    break;
	    case images   : data.imageURLs = getString().split(","); break;
	    case video    : data.videoURL  = getString(); break;
	    case temp     : data.temp      = getFloat();  break;
	    case prec     : data.prec      = getFloat();  break;
	    case windspd  : data.windspd   = getFloat();  break;
	    case winddir  : data.winddir   = getFloat();  break;
	    default : // This cannot happen
	    }
	} catch (Exception e) {
	    // Unknown tag name, do nothing
	}
    }

    //    @Override
    public void endDocument() {
	//Log.i("WeatherWidget", "Livecamera data parsing ends");
    }

}

interface ImageSetterListener extends ImageLoaderListener {
    public void onImageLoaded(Drawable d, int i);
}
class ImageSetter extends ImageLoader {
    int index;
    ImageSetter(int index, ImageSetterListener whence) { super(whence); this.index = index; }
    protected void onPostExecute(Drawable result) { ((ImageSetterListener)listener).onImageLoaded(result, index); }
}
public class LiveCamDataLoader extends AsyncTask<String, Void, LiveCamData> {

    protected ViewLiveCam activity;
    protected boolean isRun = true;

    LiveCamDataLoader(ViewLiveCam activity) {
	this.activity = activity;
    }

    //    @Override
    protected void onPostExecute(LiveCamData data) {
    	if(data != null)
    		activity.setData(data);
    }

    //    @Override
    protected LiveCamData doInBackground(String... pointid) {
	String url = activity.getString(R.string.livecamroot) + pointid[0] + "/desc.xml";
	try {
	    LiveCamParser parser = new LiveCamParser();
	    SAXParserFactory.newInstance().newSAXParser().parse(url, parser);
	    final LiveCamData data = parser.getData();
	    data.images = new Drawable[data.imageURLs.length];

	    ImageSetterListener gotImage = new ImageSetterListener() {
		    public void onImageLoaded(Drawable d, int i) {
			data.images[i] = (null == d ? new ShapeDrawable() : d);
			activity.setAnimationImage(i, data);
		    }
		    public void onImageLoaded(Drawable d) {}
		};

	    for (int i = 0; i < data.imageURLs.length && isRun; ++i)
		new ImageSetter(i, gotImage).execute(activity.getString(R.string.livecamdataroot) + data.imageURLs[i]);
	    return data;
	} catch (Exception e) {
	    return null;
	}
    }

    @Override
    protected void finalize() {
    	try {
			super.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
    protected void onCancelled() {
    	super.onCancelled();
    }
}
