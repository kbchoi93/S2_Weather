package com.weathernews.Weather;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LandRadar extends Activity
{
    private View footer;
    private boolean isStop;

    private Button VideoButton;

    private BaseInfo info;

    private String BaseUrl;
    private String[] FileName;
    private Runnable animTask;
    private ExecutorService ServiceThread;
    private Future AnimPending;
    private AnimationDrawable animation;
//    private ImageView imageview;
//    private AnimationDrawable Loading;
    private ProgressBar progress;

    private int nPressedTabNum;
    private boolean isPressed;

    private boolean TimeFlag = false;
    private boolean DateFlag = false;
    private boolean isHorizontal = true;
    private boolean isFirst = true;
    
    private boolean isPaused = false;

    private LinearLayout topLayer;

    private Resources res;

    private boolean isCityList;
    private String From;
    private String ThemeAreaID = "";
    private int ThemeID;
    
    private static RadarTask task = null;
    
//    private static View main_view = null;

    private ProgressDialog dialog;
    
    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.radar);
        topLayer = (LinearLayout) findViewById(R.id.radar_top);

        footer = findViewById(R.id.footer);

        Intent intent = getIntent();
        info = new BaseInfo(intent.getStringExtra("CityID"), intent.getStringExtra("CityName"),
			    intent.getStringExtra("TimeZone"), intent.getIntExtra("Index", 0));

        From = intent.getStringExtra("From");
        if (From != null && From.equals("CITY"))
	    isCityList = true;
        else
	    {
        	isCityList = false;
        	ThemeAreaID = intent.getStringExtra("THEMEAREA");
        	ThemeID = intent.getIntExtra("ThemeID", 0);
        	//Log.d("myTag", "ThemeAreaID=" + ThemeAreaID);
	    }

        TimeFlag = Util.getTimeFlag(getApplicationContext());
        DateFlag = Util.getDateFlag(getApplicationContext());

        res = getApplication().getResources();

        nPressedTabNum = 0;
        isPressed = false;
        
        if (res == null)
	    res = getApplication().getResources();
        Calendar cl = Calendar.getInstance();
        Util.setFooter(footer, cl, TimeFlag, DateFlag);

        isStop = false;

        VideoButton = (Button) findViewById(R.id.radar_video);
//        imageview = (ImageView) findViewById(R.id.radar_loadinglogo);
//        Loading = (AnimationDrawable) imageview.getBackground();
//        findViewById(R.id.main_loadinglogo).setVisibility(View.INVISIBLE);
        findViewById(R.id.progress_small_title).setVisibility(View.INVISIBLE);
        progress = (ProgressBar)findViewById(R.id.radar_loadinglogo);

		VideoButton.setVisibility(View.INVISIBLE);
		
        new Handler().postDelayed(new Runnable()
	    {
        	public void run()
		{
    			ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
    			boolean network = Util.checkNetwork(manager);
	    	
    			if(network) {
    				progress.setVisibility(View.VISIBLE);
//    				Loading.start();
    			}
        	}
	    }, 1000);

        animation = new AnimationDrawable();
//        initThreading();

//        if (isHorizontal)
//	    AnimThread.postDelayed(animTask, 10);

        VideoButton.setOnClickListener(new OnClickListener()
	    {
		@Override public void onClick(View v)
		{
		    if (!animation.isRunning())
			{
			    animation.start();
			    isStop = false;
			}
		    else
			{
			    animation.stop();
			    isStop = true;
			}
		}

	    });
    }

//    @Override
//	public void onWindowFocusChanged(boolean hasFocus) {
//    	if (hasFocus && isFirst) {
//			ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
//			boolean network = Util.checkNetwork(manager);
//    	
//			if(network) {
//				VideoButton.setVisibility(View.INVISIBLE);
//				imageview.setVisibility(View.VISIBLE);
//				Loading.start();
//			}
//	    isFirst = false;
//    	}
//    }

    final Handler AnimThread = new Handler() {
	    public void handleMessage(Message msg) {
        	VideoButton.setBackgroundDrawable(animation);
    		progress.setVisibility(View.INVISIBLE);
//        	Loading.stop();
    		if(animation != null) {
    			animation.start();
    			VideoButton.setVisibility(View.VISIBLE);
    		}
	    }
	};

    private void initThreading() {
    	if(ServiceThread != null)
    		ServiceThread.shutdown();
	ServiceThread = Executors.newSingleThreadExecutor();
	animTask = new Runnable() {

		@Override
		    public void run() {
		    // TODO Auto-generated method stub
	    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
	    	boolean network = Util.checkNetwork(manager);
	    	
	    	if(!network) {
//	    		Loading.stop();
	    	    progress.setVisibility(View.INVISIBLE);
	        	return;
	    	}

	    	Thread background = new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					getXmlData();
				}
			});
	    	
	    	background.start();
		    
	    	if(BaseUrl != null && FileName != null && BaseUrl.length() > 0 && FileName.length > 0) {
		    task = new RadarTask(LandRadar.this, BaseUrl, FileName);
//		    if (null != ServiceThread) ServiceThread.shutdown();
		    if(AnimPending == null || AnimPending.isDone() || AnimPending.isCancelled()) {
		    	try {
	    			AnimPending = ServiceThread.submit(task);
	    		} catch(RejectedExecutionException e) {
	    			ServiceThread.shutdown();
	    			return;
	    		}
		    }
			} else {
				if (null != AnimThread && null != animTask) AnimThread.removeCallbacks(animTask);
				AnimThread.postDelayed(animTask, 10);
			}
		}
	    };
    }

    private void getXmlData(){
    	String URL;
	String szCityList = "";
	InputStream fis = null;
	byte[] XmlData = null;
	StringBuffer sbTemp = null;
	String[] szTemp;

	//URL = "http://weathernews.co.kr/radar/xml/KRW.xml";
	URL = getString(R.string.radar_url);
	//URL = Const.URL_LIST + "cnt="+ 3 + "&region=11B10101|TOK|ALTN1";

	try {
		fis = Util.getByteArrayFromURL(URL);
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
	MySampleHandlerXML mySample  = new MySampleHandlerXML(this);
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
	BaseUrl = mySample.getBaseURL();
	FileName = mySample.getFileName();
	if(fis != null) {
		try {
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		fis = null;
	}
    }

    public void SetButtonBackImage(Drawable[] d){
    	for(int i = 0 ; i < d.length ; i++){
    		if (d[i] != null){
    			try{
    				animation.addFrame(d[i], 500);
    			} catch (Exception e){
    			}
    		}
    	}
    	animation.setOneShot(false);
    	Message msg = AnimThread.obtainMessage();
    	AnimThread.sendMessage(msg);
    	AnimThread.removeCallbacks(animTask);
    	if (null != ServiceThread) ServiceThread.shutdown();
    	task.clearmemory();
    	for(int i = 0 ; i < d.length ; i++){
    		if (d[i] != null){
    			d[i].setCallback(null);
    		}
    	}
    	
    	animTask = null;
    }

    @Override protected void onPause()
    {
    	super.onPause();
    	isPaused = true;
    	if (null != AnimPending) AnimPending.cancel(true);
    	if (null != ServiceThread) ServiceThread.shutdown();
    	if (null != AnimThread && null != animTask) AnimThread.removeCallbacks(animTask);
    	if (null != animTask ) animTask = null;

    	if(dialog != null && dialog.isShowing())
    		dialog.dismiss();
    	
    	animation.stop();

    	if (AnimPending != null)
	    {
		AnimPending.cancel(true);
		AnimPending = null;
	    }
	if (AnimThread != null)
	    AnimThread.removeCallbacks(animTask);
    	res = null;

    	try{
    	for(int i = 0 ; i < animation.getNumberOfFrames() ; i ++) {
    		if(null != animation.getFrame(i))
    			animation.getFrame(i).setCallback(null);
    	}
    	} catch (NullPointerException e){}
	
    	if(null != animation){
    		animation.setCallback(null);
    		animation = null;
    	}
    	if(null != VideoButton){
    		VideoButton.setBackgroundDrawable(null);
    		VideoButton.clearAnimation();
    	}
    	
    	if(null != ServiceThread)
    		ServiceThread.shutdown();
    	
    	topLayer.setBackgroundDrawable(null);
    	
		if(task != null) {
			task.clearmemory();
			task = null;
		}
    }

    @Override protected void onDestroy()
    {
	super.onDestroy();
	if (AnimPending != null)
	    {
		AnimPending.cancel(true);
		AnimPending = null;
	    }
	if (AnimThread != null)
	    AnimThread.removeCallbacks(animTask);
    	res = null;

    	try{
    	for(int i = 0 ; i < animation.getNumberOfFrames() ; i ++) {
    		if(null != animation.getFrame(i))
    			animation.getFrame(i).setCallback(null);
    	}
    	} catch (NullPointerException e){}
	
    	if(null != animation){
    		animation.setCallback(null);
    		animation = null;
    	}
    	if(null != VideoButton){
    		VideoButton.setBackgroundDrawable(null);
    		VideoButton.clearAnimation();
    	}
    	
    	if(null != ServiceThread)
    		ServiceThread.shutdown();
    	
    	topLayer.setBackgroundDrawable(null);
    	
		if(task != null) {
			task.clearmemory();
			task = null;
		}
    	System.gc();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
	switch (item.getItemId())
	    {
	    case 0:
	    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
	    	boolean network = Util.checkNetwork(manager);
	    	
	    	if(!network) {
	    		
//	    		Loading.stop();
	    	    progress.setVisibility(View.INVISIBLE);
	    	    
				AlertDialog.Builder alert = null;
//		    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//		    		alert = new AlertDialog.Builder(LandRadar.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//		    	else
		    		alert = new AlertDialog.Builder(LandRadar.this);
	        	alert.setTitle("네트워크 오류");
	        	alert.setIcon(R.drawable.ic_dialog_menu_generic);
	        	alert.setMessage("네트워크 상태를 확인해 주십시오.");
	        	
//			    Context mContext = LandRadar.this;
//		        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
//		        LayoutInflater inflater1 = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
//		        View layout = inflater.inflate(R.layout.custom_dialog_center,(ViewGroup) findViewById(R.id.layout_root));
//		        View custom_title = inflater1.inflate(R.layout.custom_dialog_top, (ViewGroup) findViewById(R.id.dialog_top));
//		        final TextView title = (TextView)custom_title.findViewById(R.id.dialog_title);
//		        final ImageView icon = (ImageView)custom_title.findViewById(R.id.left_icon);
//		        final TextView content_text = (TextView)layout.findViewById(R.id.dialog_center_text);
//		        
//		    	icon.setImageResource(R.drawable.ic_dialog_menu_generic);
//		    	title.setText("네트워크 오류");
//		    	content_text.setText("네트워크 상태를 확인해 주십시오.");
//
//		    	alert.setCustomTitle(custom_title);
//		    	alert.setView(layout);

	        	alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
	        		@Override
	    		    public void onClick(DialogInterface dialog, int which) {
	        			dialog.dismiss();
	        		}
	        	});

	        	alert.show();
	        	return true;
	    	}

//	    Loading.start();
		VideoButton.setVisibility(View.INVISIBLE);
		progress.setVisibility(View.VISIBLE);
		
        Calendar cl = Calendar.getInstance();
        Util.setFooter(footer, cl, TimeFlag, DateFlag);

		
	    if(animTask == null) {
	    	animTask = new Runnable()
		    {
	    		@Override public void run()
	    		{
	    		    // TODO Auto-generated method stub
	    			ServiceThread = Executors.newSingleThreadExecutor();
	    			Thread background = new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							getXmlData();		
						}
					});
	    			background.start();
	    	    	if(BaseUrl != null && FileName != null && BaseUrl.length() > 0 && FileName.length > 0) {
	    			    task = new RadarTask(LandRadar.this, BaseUrl, FileName);
//	    			    if (null != ServiceThread) ServiceThread.shutdown();
	    			    if(AnimPending == null || AnimPending.isDone() || AnimPending.isCancelled()) {
	    			    	try {
	    		    			AnimPending = ServiceThread.submit(task);
	    		    		} catch(RejectedExecutionException e) {
	    		    			ServiceThread.shutdown();
	    		    			return;
	    		    		}
	    			    }
	    				} else {
	    					if (null != AnimThread && null != animTask) AnimThread.removeCallbacks(animTask);
	    					AnimThread.postDelayed(animTask, 10);
	    				}
	    		}
		    };
	    }
	    AnimThread.removeCallbacks(animTask);
	    AnimThread.postDelayed(animTask, 10);
		return true;
	    case 1:
//	    	final ProgressDialog dialog;

        	dialog = new ProgressDialog(LandRadar.this);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setMessage("이미지 저장중..");
            dialog.closeOptionsMenu();
            dialog.show();

            final Handler sendhandler = new Handler() {
            	public void handleMessage(Message msg) {
                	if (msg.what == -1) {
                		if(isPaused == true)
                			return;
        				AlertDialog.Builder alert = null;
//        		    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//        		    		alert = new AlertDialog.Builder(LandRadar.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//        		    	else
        		    		alert = new AlertDialog.Builder(LandRadar.this);
        		    	alert.setTitle("SD카드 오류");
        		    	alert.setIcon(R.drawable.ic_dialog_menu_generic);
        		    	alert.setMessage("이미지를 저장할 공간이 없습니다. SD카드를 확인해 주십시오.");
        		    	
//        			    Context mContext = LandRadar.this;
//        		        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
//        		        LayoutInflater inflater1 = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
//        		        View layout = inflater.inflate(R.layout.custom_dialog_center,(ViewGroup) findViewById(R.id.layout_root));
//        		        View custom_title = inflater1.inflate(R.layout.custom_dialog_top, (ViewGroup) findViewById(R.id.dialog_top));
//        		        final TextView title = (TextView)custom_title.findViewById(R.id.dialog_title);
//        		        final ImageView icon = (ImageView)custom_title.findViewById(R.id.left_icon);
//        		        final TextView content_text = (TextView)layout.findViewById(R.id.dialog_center_text);
//        		        
//        		    	icon.setImageResource(R.drawable.ic_dialog_menu_generic);
//        		    	title.setText("SD카드 오류");
//        		    	content_text.setText("이미지를 저장할 공간이 없습니다. SD카드를 확인해 주십시오.");
//
//        		    	alert.setCustomTitle(custom_title);
//        		    	alert.setView(layout);

        		    	alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
        		    		@Override
        				    public void onClick(DialogInterface dialog, int which) {
        		    			dialog.dismiss();
        		    		}
        		    	});

        		    	alert.show();
                	}
        	    }
           };

        	Thread send = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Bitmap bmp = null;
					
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					boolean isOk = Util.setCaptureAndSend(LandRadar.this, (View)topLayer, bmp);
					dialog.dismiss();
					if(!isOk) {
						Message msg = sendhandler.obtainMessage(-1);
						sendhandler.sendMessage(msg);
					}
				}
			});
        	send.start();
		return true;
	    case 2:
		return true;
	    case 3:
		return true;
	    case 4:
		return true;
	    case 5:
		return true;
	    }
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "업데이트").setIcon(R.drawable.ic_menu_update);
        menu.add(0, 1, 0, "공유하기").setIcon(R.drawable.ic_menu_share);
        return true;
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
    	isPaused = false;
//    	initThreading();
//    	Log.d("myTag", "Radar onResume");
    	
    	if(null == animation) animation = new AnimationDrawable();
    	
    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
    	boolean network = Util.checkNetwork(manager);
    	
    	if(!network) {
    		
//    		Loading.stop();
    	    progress.setVisibility(View.INVISIBLE);
    	    
			AlertDialog.Builder alert = null;
//	    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//	    		alert = new AlertDialog.Builder(LandRadar.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//	    	else
	    		alert = new AlertDialog.Builder(LandRadar.this);
        	alert.setTitle("네트워크 오류");
        	alert.setIcon(R.drawable.ic_dialog_menu_generic);
        	alert.setMessage("네트워크 상태를 확인해 주십시오.");
        	

        	alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
        		@Override
    		    public void onClick(DialogInterface dialog, int which) {
        			dialog.dismiss();
        		}
        	});

        	alert.show();
        	return;
    	}

	    VideoButton.setVisibility(View.INVISIBLE);
	    progress.setVisibility(View.VISIBLE);

	    if (null != AnimThread && null != animTask) {
//			AnimThread.removeCallbacks(animTask);
			if (AnimPending != null) {
				AnimPending.cancel(true);
		    }
			if (AnimThread != null)
				AnimThread.removeCallbacks(animTask);

	    	for(int i = 0 ; i < animation.getNumberOfFrames() ; i ++) {
	    		if(null != animation.getFrame(i))
	    			animation.getFrame(i).setCallback(null);
	    	}
		
	    	if(null != animation)
	    		animation.setCallback(null);
	    	if(null != VideoButton)
	    		VideoButton.setBackgroundDrawable(null);

		}
	    
	    initThreading();

		AnimThread.postDelayed(animTask, 700);
    }
}

class MySampleHandlerXML<XmlParser>  extends DefaultHandler
{
    private XmlParser xp;
    private String BaseURL;
    private String Year;
    private String Mon;
    private String Day;
    private String Hour;
    private String Min;

    private StringBuffer ImageUrl = new StringBuffer();

    private boolean last;
    private boolean year;
    private boolean mon;
    private boolean day;
    private boolean hour;
    private boolean min;
    private boolean commonpath;
    private boolean image;
    private boolean filename;

    public MySampleHandlerXML(XmlParser xp) { this.xp = xp; }
    public void startElement(String uri, String localName, String qName, Attributes atts)
    {
	if (localName.equals("last"))
	    last = true;
	if (localName.equals("year"))
	    year = true;
	if (localName.equals("month"))
	    mon = true;
	if (localName.equals("day"))
	    day = true;
	if (localName.equals("hour"))
	    hour = true;
	if (localName.equals("min"))
	    min = true;
	if (localName.equals("commonPath"))
	    commonpath = true;
	if (localName.equals("image"))
	    {
		String attrValue = atts.getValue("index");
		if (attrValue.equals("0")) // duration 10 min
		    image = true;
	    }
	if (localName.equals("fileName"))
	    filename = true;
    }

    public void endElement(String uri, String localName, String qName)
    {
	if (localName.equals("last"))
	    last = false;
	if (localName.equals("image"))
	    image = false;
	if (localName.equals("year"))
	    year = false;
	if (localName.equals("month"))
	    mon = false;
	if (localName.equals("day"))
	    day = false;
	if (localName.equals("hour"))
	    hour = false;
	if (localName.equals("min"))
	    min = false;
	if (localName.equals("commonPath"))
	    commonpath = false;
	if (localName.equals("image"))
	    image = false;
	if (localName.equals("fileName"))
	    filename = false;
    }

    public void characters(char[] chars, int start, int leng)
    {
	if (last && year)
	    Year = new String(chars, start, leng);
	if (last && mon)
	    Mon = new String(chars, start, leng);
	if (last && day)
	    Day = new String(chars, start, leng);
	if (last && hour)
	    Hour = new String(chars, start, leng);
	if (last && min)
	    Min = new String(chars, start, leng);

	if (commonpath)
	    BaseURL = new String(chars, start, leng);
	if (image && filename)
	    {
		//Log.d("myTag", "[" + new String(chars, start, leng).trim() + "]");
		ImageUrl.append(new String(chars, start, leng).trim());
		ImageUrl.append("\t");
	    }
    }

    public String getTimeStr()
    {
	String temp;
	temp = String.format("%04d.%02d.%02d %02d:%02d",
			     Integer.parseInt(Year), Integer.parseInt(Mon), Integer.parseInt(Day), Integer.parseInt(Hour), Integer.parseInt(Min));
	return temp;
    }

    public String[] getFileName()
    {
	String[] Temp;
	Temp = ImageUrl.toString().split("\t");
	return Temp;
    }

    public String getBaseURL() { return BaseURL; }
}
