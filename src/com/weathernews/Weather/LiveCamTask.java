package com.weathernews.Weather;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.UnknownHostException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

public class LiveCamTask implements Runnable{
	private LiveCam animation;
	private String BaseUrl;
	
	private String URLroot;
	private String[] id;
	private String[] Url;
	
	private Drawable image;
	

	LiveCamTask(LiveCam animation, String url) {
		this.animation = animation;
		this.BaseUrl = url;
	}
	
	public void run(){
		getXmlData();
		for(int i = 0 ; i < id.length ; i++){
			String url = URLroot + Url[i];
    		try{
    			//Log.d("myTag", "GetImage " + url);
    			image = Drawable.createFromStream((InputStream)(new URL(url).getContent()), "src");
    		}catch (Exception e) {
    			//Log.e("myTag", "Can't get Image From " + BaseUrl + Url[i] + e.toString());
    		}
    		//Log.d("myTag", "Get Image Success " + url);
//			animation.SetButtonBackImage(id[i], image);
			image = null;
		}
	}

	private void getXmlData(){
    	String URL;
		String szCityList = "";
		InputStream fis = null;
		byte[] XmlData = null;
		StringBuffer sbTemp = null;
		String[] szTemp;
		
		
		try {
			fis = Util.getByteArrayFromURL(BaseUrl);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SAXParserFactory parserModel = SAXParserFactory.newInstance();
        SAXParser concreteParser = null;
        try {
			concreteParser   = parserModel.newSAXParser();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		XMLReader myReader=null;
		try {
			myReader =  concreteParser.getXMLReader();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		MySampleHandlerLiveCamXML mySample  = new MySampleHandlerLiveCamXML(this);
		myReader.setContentHandler(mySample);
		try {
			myReader.parse(new InputSource(fis));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		URLroot = mySample.getBaseURL();
		id = mySample.getID();
		Url = mySample.getImageURL();
		if(fis != null) {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			fis = null;
		}
    }
}
class MySampleHandlerLiveCamXML<XmlParser>  extends DefaultHandler {
	private XmlParser xp;
	private String BaseURL;
	private StringBuffer ID = new StringBuffer();
	private StringBuffer Url = new StringBuffer();
	private StringBuffer point = new StringBuffer();
	
	private boolean baseurl;
	private boolean id;
	private boolean url;
	private boolean pos;
	
	public MySampleHandlerLiveCamXML(XmlParser xp){
		this.xp	= xp;
	}
	public void startElement(String uri, String localName, String qName, Attributes atts){
		if(localName.equals("baseurl"))
			baseurl = true;
		if(localName.equals("id"))
			id = true;
		if(localName.equals("url"))
			url = true;
		if(localName.equals("pos"))
			pos = true;
	}

	public void endElement(String uri, String localName, String qName){
		if(localName.equals("baseurl"))
			baseurl = false;
		if(localName.equals("id"))
			id = false;
		if(localName.equals("url"))
			url = false;
		if(localName.equals("pos"))
			pos = false;
	}

	public void characters(char[] chars, int start, int leng){
		if(baseurl)
			BaseURL = new String(chars, start, leng);
		if(id) {
			ID.append(chars, start, leng);
			ID.append(",");
		}
		if(url) {
			Url.append(chars, start, leng);
			Url.append(",");
		}
		if(pos) {
			point.append(chars, start, leng);
			point.append(",");
		}
	}
	
	public String getBaseURL() {
		return BaseURL;
	}
	
	public String[] getID(){
		return ID.toString().split(",");
	}
	
	public String[] getImageURL() {
		return Url.toString().split(",");
	}
	
	public String[] getPos() {
		return point.toString().split(",");
	}
}
