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

//import dalvik.system.TemporaryDirectory;

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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LandSattle extends Activity
{
    private View footer;
    private boolean isStop;

    private Button VideoButton;

    private BaseInfo info;

    private String BaseUrl;
    private String TimeTerm;
    private String DateString;
    private String FileType;
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
    private boolean isFirst = true;
    
    private boolean isPaused = false;

    private boolean TimeFlag = false;
    private boolean DateFlag = false;

    private LinearLayout topLayer;
    private boolean isHorizontal = true;

    private Resources res;

    private boolean isCityList;
    private String From;
    private String ThemeAreaID = "";
    private int ThemeID;
    private static SattleTask task = null;
    
//    private static View main_view = null;
    
    private ProgressDialog dialog;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sattle);
        topLayer = (LinearLayout) findViewById(R.id.sattle_top);

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
	    }

        TimeFlag = Util.getTimeFlag(getApplicationContext());
        DateFlag = Util.getDateFlag(getApplicationContext());

        isStop = false;
        res = getApplication().getResources();
        nPressedTabNum = 0;
        isPressed = false;
        
        if (res == null)
	    res = getApplication().getResources();

        Calendar cl = Calendar.getInstance();
        Util.setFooter(footer, cl, TimeFlag, DateFlag);

//        findViewById(R.id.main_loadinglogo).setVisibility(View.INVISIBLE);
//        imageview = (ImageView) findViewById(R.id.sattle_loadinglogo);
//        Loading = (AnimationDrawable) imageview.getBackground();
        findViewById(R.id.progress_small_title).setVisibility(View.INVISIBLE);
        progress = (ProgressBar) findViewById(R.id.sattle_loadinglogo);

        VideoButton = (Button) findViewById(R.id.sattle_video);
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
	    }, 100);

        animation = new AnimationDrawable();
//        initThreading();

//        if (isHorizontal) AnimThread.postDelayed(animTask, 10);


        VideoButton.setOnClickListener(new OnClickListener()
	    {
		@Override
		    public void onClick(View v) {
		    if (!animation.isRunning()) {
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

//    @Override public void onWindowFocusChanged(boolean hasFocus)
//    {
//    	if (hasFocus && isFirst)
//	    {
//			ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
//			boolean network = Util.checkNetwork(manager);
//    	
//			if(network) {
//				VideoButton.setVisibility(View.INVISIBLE);
//				imageview.setVisibility(View.VISIBLE);
//				Loading.start();
//			}
//		isFirst = false;
//	    }
//    }

    final Handler AnimThread = new Handler()
	{
	    public void handleMessage(Message msg)
	    {
        	VideoButton.setBackgroundDrawable(animation);
//        	Loading.stop();
    		progress.setVisibility(View.INVISIBLE);
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
    	animTask = new Runnable()
    	{
    		@Override public void run()
    		{
    			// TODO Auto-generated method stub
    			ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
    			boolean network = Util.checkNetwork(manager);
	    	
    			if(!network) {
    				progress.setVisibility(View.INVISIBLE);
    				return;
    			}

    			Thread background=new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						getXmlData();
					}
    				
    			});
    			background.start();
//    			getXmlData();
    			if(BaseUrl != null && DateString != null && BaseUrl.length() > 0 && DateString.length() > 0) {
    				task = new SattleTask(LandSattle.this, BaseUrl, DateString, TimeTerm, FileType);
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

    private void getXmlData()
    {
    	String URL;
    	InputStream fis = null;

    	URL = getString(R.string.sattle_url);

    	try {
			fis = Util.getByteArrayFromURL(URL);
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	
    	if(fis == null)
    		return;

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
    	MyHandlerXML mySample  = new MyHandlerXML(this);
    	myReader.setContentHandler(mySample);
    	try {
    		myReader.parse(new InputSource(fis));
    	} catch (IOException e1) {
    		// TODO Auto-generated catch block
    		e1.printStackTrace();
    		return;
    	} catch (SAXException e1) {
    		// TODO Auto-generated catch block
    		e1.printStackTrace();
    		return;
    	}
    	BaseUrl = mySample.getBaseURL();
    	TimeTerm = mySample.getTerm();
    	DateString = mySample.getTimeStr();
    	FileType = mySample.getType();
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

    @Override
	protected void onPause()
    {
    	super.onPause();
//    	Log.d("myTag", "Sattle onPause");
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
    	if(animTask != null)
    		animTask = null;

    	res = null;
    	try{
    	for(int i = 0 ; i < animation.getNumberOfFrames() ; i ++) {
    		if(null != animation.getFrame(i))
    			animation.getFrame(i).setCallback(null);
    	}
    	} catch (NullPointerException e){}
	
    	if(null != animation) {
    		animation.setCallback(null);
    		animation = null;
    	}
    	if(null != VideoButton) {
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

    @Override
	protected void onDestroy()
    {
    	super.onDestroy();
    	if (AnimPending != null)
    	{
    		AnimPending.cancel(true);
    		AnimPending = null;
	    }
    	if (AnimThread != null)
    		AnimThread.removeCallbacks(animTask);
    	if(animTask != null)
    		animTask = null;

    	res = null;
    	try{
    	for(int i = 0 ; i < animation.getNumberOfFrames() ; i ++) {
    		if(null != animation.getFrame(i))
    			animation.getFrame(i).setCallback(null);
    	}
    	} catch (NullPointerException e){}
	
    	if(null != animation) {
    		animation.setCallback(null);
    		animation = null;
    	}
    	if(null != VideoButton) {
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

    @Override
	public boolean onOptionsItemSelected(MenuItem item){

	switch (item.getItemId()) {
        case 0:
	    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
	    	boolean network = Util.checkNetwork(manager);
	    	
	    	if(!network) {
	    		
//	    		Loading.stop();
	    	    progress.setVisibility(View.INVISIBLE);
	    	    
				AlertDialog.Builder alert = null;
//		    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//		    		alert = new AlertDialog.Builder(LandSattle.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//		    	else
		    		alert = new AlertDialog.Builder(LandSattle.this);
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
	        	return true;
	    	}

        	
//        	Loading.start();
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
	    		    if(BaseUrl != null && DateString != null && BaseUrl.length() > 0 && DateString.length() > 0) {
	    		    	task = new SattleTask(LandSattle.this, BaseUrl, DateString, TimeTerm, FileType);
	    		    	AnimPending = ServiceThread.submit(task);
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
//        	final ProgressDialog dialog;

        	dialog = new ProgressDialog(LandSattle.this);
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
//        		    		alert = new AlertDialog.Builder(LandSattle.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//        		    	else
        		    		alert = new AlertDialog.Builder(LandSattle.this);
        		    	alert.setTitle("SD카드 오류");
        		    	alert.setIcon(R.drawable.ic_dialog_menu_generic);
        		    	alert.setMessage("이미지를 저장할 공간이 없습니다. SD카드를 확인해 주십시오.");
        		    	
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
					boolean isOk = Util.setCaptureAndSend(LandSattle.this, (View)topLayer, bmp);
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

    public boolean onCreateOptionsMenu(Menu menu) {
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
    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
    	boolean network = Util.checkNetwork(manager);
    	
    	if(null == animation) animation = new AnimationDrawable();
    	
    	if(!network) {
    		
//    		Loading.stop();
    	    progress.setVisibility(View.INVISIBLE);
    	    
			AlertDialog.Builder alert = null;
//	    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//	    		alert = new AlertDialog.Builder(LandSattle.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//	    	else
	    		alert = new AlertDialog.Builder(LandSattle.this);
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
//   			AnimThread.removeCallbacks(animTask);
   	    	if (AnimPending != null) {
   	    		AnimPending.cancel(true);
   		    }
   	    	if (AnimThread != null)
   	    		AnimThread.removeCallbacks(animTask);

   	    	for(int i = 0 ; i < animation.getNumberOfFrames() ; i ++) {
   	    		if(null != animation.getFrame(i))
   	    			animation.getFrame(i).setCallback(null);
   	    	}
   		
   	    	if(null != animation) {
   	    		animation.setCallback(null);
   	    	}
   	    	if(null != VideoButton) {
   	    		VideoButton.setBackgroundDrawable(null);
   	    	}
   			
   		}
   		
   		initThreading();

   		AnimThread.postDelayed(animTask, 700);
    }
}
class MyHandlerXML<XmlParser>  extends DefaultHandler {
    private XmlParser xp;
    private String Path;
    private String Year;
    private String Mon;
    private String Day;
    private String Hour;
    private String Min;
    private String Term;
    private String Type;

    private boolean image;
    private boolean path;
    private boolean year;
    private boolean mon;
    private boolean day;
    private boolean hour;
    private boolean min;
    private boolean term;
    private boolean type;

    public MyHandlerXML(XmlParser xp){
	this.xp	= xp;
    }
    public void startElement(String uri, String localName, String qName, Attributes atts){
	if (localName.equals("image"))
	    image = true;
	if (localName.equals("path"))
	    path = true;
	if (localName.equals("year"))
	    year = true;
	if (localName.equals("month"))
	    mon = true;
	if (localName.equals("date"))
	    day = true;
	if (localName.equals("hour"))
	    hour = true;
	if (localName.equals("minute"))
	    min = true;
	if (localName.equals("term"))
	    term = true;
	if (localName.equals("type"))
	    type = true;
    }

    public void endElement(String uri, String localName, String qName){
	if (localName.equals("image"))
	    image = false;
	if (localName.equals("path"))
	    path = false;
	if (localName.equals("year"))
	    year = false;
	if (localName.equals("month"))
	    mon = false;
	if (localName.equals("date"))
	    day = false;
	if (localName.equals("hour"))
	    hour = false;
	if (localName.equals("minute"))
	    min = false;
	if (localName.equals("term"))
	    term = false;
	if (localName.equals("type"))
	    type = false;
    }

    public void characters(char[] chars, int start, int leng){
	if (image && path)
	    Path = new String(chars, start, leng);
	if (image && year)
	    Year = new String(chars, start, leng);
	if (image && mon)
	    Mon = new String(chars, start, leng);
	if (image && day)
	    Day = new String(chars, start, leng);
	if (image && hour)
	    Hour = new String(chars, start, leng);
	if (image && min)
	    Min = new String(chars, start, leng);
	if (image && term)
	    Term = new String(chars, start, leng);
	if (image && type)
	    Type = new String(chars, start, leng);

    }

    public String getTimeStr()
    {
	String temp;
	try{
	    temp = String.format("%04d/%02d/%02d/%02d/%02d",
				 Integer.parseInt(Year), Integer.parseInt(Mon), Integer.parseInt(Day), Integer.parseInt(Hour), Integer.parseInt(Min));
	}
	catch(NumberFormatException e) { temp = ""; }
	return temp;
    }

    public String getBaseURL() { return Path; }
    public String getTerm()    { return Term; }
    public String getType()    { return Type; }
}
