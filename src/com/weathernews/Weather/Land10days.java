package com.weathernews.Weather;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class Land10days extends Activity
{
    private View footer;
    private ImageView imgContent;
    private BaseInfo info;

    private TodayData today = new TodayData();;
    private HourlyData hourly = new HourlyData();
    private WeeklyData weekly = new WeeklyData();

    private long updateTime;

    private String TempSymbol = "℃";

    private boolean DateFlag;
    private boolean TimeFlag;
    private boolean TempFlag;
    private LinearLayout topLayer;
    private static View main_view = null;

//    private AnimationDrawable LoadingAnimation;
//    private ImageView LoadingLogo;
    
    private ProgressBar progress;
    private Context context;

    private boolean isFirst = true;

    private Resources res;
    private Bitmap bm;

    private boolean isCityList;
    private String From;
    private String ThemeAreaID = "";

    private ProgressDialog dialog;
    
    private final int MSG_CODE = 100;
    private String ThemeCode = "";
    
    private Handler msgHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
		    try {
		    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
		    	boolean network = Util.checkNetwork(manager);
		    	
		    	if(!network) {
		    		progress.setVisibility(View.INVISIBLE);
		    	    
					AlertDialog.Builder alert = null;
//			    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//			    		alert = new AlertDialog.Builder(Land10days.this, AlertDialog.THEME_HOLO_DARK);
//			    	else
			    		alert = new AlertDialog.Builder(Land10days.this);
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

			Thread.sleep(100);
			Update();
			saveData();
			Message mesg = handler.obtainMessage();
			handler.sendMessage(mesg);
		    }
		    catch (Throwable t) {
			// just end the background thread
			//Log.e("myTag", "error = " + t.toString());
			Message mesg = handler.obtainMessage();
			handler.sendMessage(mesg);
		    }
        }
    };


    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(main_view == null) {
        	LayoutInflater layout = getLayoutInflater();
        	main_view = layout.inflate(R.layout.land48h, null);
        }
        setContentView(main_view);

        Intent intent = getIntent();
        if(intent.getStringExtra("CityID") != null)
        	info = new BaseInfo(intent.getStringExtra("CityID"), intent.getStringExtra("CityName"),
        			intent.getStringExtra("TimeZone"), intent.getIntExtra("Index", 0));
        else {
        	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);

        	if (prefs == null){
        		return;
        	}
        	int nCityCnt = prefs.getInt(Const.CITY_CNT, 0);

        	String[][] CityID = new String[nCityCnt][8];
        	String Temp;
        	String[] szTemp;
        	for (int i = 0 ; i < nCityCnt ; i++){
        	    Temp = prefs.getString(Const.CITY_LIST + i, "");
        	    //Log.i("myTag", "SaveValue =" + Temp);
        	    szTemp = Temp.split("\t");
        	    for (int j = 0 ; j < szTemp.length ; j++) {
        	    	if (szTemp[j].equals("") || szTemp[j] == null || szTemp[j].equals("\n"))
        	    		CityID[i][j] = "--";
        	    	else
        	    		CityID[i][j] = szTemp[j];
        	    }
        	}
        	info = new BaseInfo(CityID[0][0], CityID[0][1], CityID[0][2], 0);
        }
        

        From = intent.getStringExtra("From");
        if (From != null && From.equals("CITY"))
	    isCityList = true;
        else {
	    isCityList = false;
	    ThemeAreaID = intent.getStringExtra("THEMEAREA");
	    ThemeCode = intent.getStringExtra("ThemeCode");
	    //Log.d("myTag", "ThemeAreaID=" + ThemeAreaID);
        }

        topLayer = (LinearLayout) findViewById(R.id.land48htoplayer);
        footer = findViewById(R.id.footer);

//        LoadingLogo = (ImageView) findViewById(R.id.main_loadinglogo);
//    	LoadingAnimation = (AnimationDrawable) LoadingLogo.getBackground();
    	progress = (ProgressBar) findViewById(R.id.progress_small_title);

    	context = getApplicationContext();
        DateFlag = Util.getDateFlag(context);
        TimeFlag = Util.getTimeFlag(context);
    	TempFlag = Util.getTempFlag(context);

    	res = getResources();

        if (TempFlag)
	    TempSymbol = "℃";
     	else
	    TempSymbol = "℉";

        getSavedData();

        imgContent = (ImageView) findViewById(R.id.land48h_content);
        bm = getContentImage();
        imgContent.setImageBitmap(bm);

//        LoadingAnimation.stop();
//        LoadingLogo.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.INVISIBLE);

//        Thread background=new Thread(new Runnable() {
//		public void run() {
//		    //Log.d("myTag", "Start Thread~~~~");
//		    try {
//		    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
//		    	boolean network = Util.checkNetwork(manager);
//		    	
//		    	if(!network) {
//		    		progress.setVisibility(View.INVISIBLE);
//		    	    
//		    		AlertDialog.Builder alert = new AlertDialog.Builder(Land10days.this);
//		        	alert.setTitle("네트워크 오류");
//		        	alert.setIcon(R.drawable.ic_dialog_menu_generic);
//		        	alert.setMessage("네트워크 상태를 확인해 주십시오.");
//
//		        	alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//		        		@Override
//		    		    public void onClick(DialogInterface dialog, int which) {
//		        			dialog.dismiss();
//		        		}
//		        	});
//
//		        	alert.show();
//		        	return;
//		    	}
//
//			Thread.sleep(100);
//			Update();
//			saveData();
//			Message msg = handler.obtainMessage();
//			handler.sendMessage(msg);
//		    }
//		    catch (Throwable t) {
//			// just end the background thread
//			//Log.e("myTag", "error = " + t.toString());
//			Message msg = handler.obtainMessage();
//			handler.sendMessage(msg);
//		    }
//		}
//	    });
        Calendar cl;
        cl = Calendar.getInstance();
//        if (cl.getTimeInMillis() - updateTime > 1800000l)
//	    background.start();
		if (cl.getTimeInMillis() - updateTime > 1800000l){
	        Message message = new Message();
	        message.what = MSG_CODE;
	        msgHandler.sendMessage(message);
		}


        if (updateTime != 0)
	    cl.setTimeInMillis(updateTime);
        if (res == null)
	    res = getResources();
        Util.setFooter(footer, cl, TimeFlag, DateFlag);
    }

    @Override
	public void onWindowFocusChanged(boolean hasFocus) {
	if (hasFocus && isFirst){
	    Calendar cl;
	    cl = Calendar.getInstance();
	    if (cl.getTimeInMillis() - updateTime > 1800000l) {
			ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
			boolean network = Util.checkNetwork(manager);
    	
			if(network) {
//				LoadingAnimation.start();
//				LoadingLogo.setVisibility(View.VISIBLE);
				progress.setVisibility(View.VISIBLE);
			}
	    }
	    isFirst = false;
	}
    }

    @Override
	protected void onDestroy(){
    	super.onDestroy();
    	if (bm != null){
	    bm.recycle();
	    bm = null;
    	}

    	res = null;
    	context = null;
    	main_view = null;

    }

    public void saveData(){
    	Calendar cl = Calendar.getInstance();
    	String Temp = "";
    	cl.setTimeZone(TimeZone.getTimeZone(info.getTimeZone()));

    	SharedPreferences.Editor prefs = getSharedPreferences(Const.PREFS_NAME, 0).edit();

    	if (isCityList)
	    prefs.putLong(Const.UPDATE_TIME_TODAY + info.getCityID(), cl.getTimeInMillis());
    	else
	    prefs.putLong(Const.THEME_TODAY_UPDATETIME + ThemeCode, cl.getTimeInMillis());
    	if (today.getTodayIcon() == null || today.getTodayIcon().length() <= 0)
	    return;
    	Temp = today.getTodayIcon() + "\t" + today.getTodayTempMax() + "\t" + today.getTodayTempMin() + "\t" +
	    today.getTodayPop() + "\t" + today.getTodayVisi() + "\t" + today.getTodayTempCur() + "\t" +
	    today.getTodayHum() + "\t" + today.getTodayWindDir() + "\t" + today.getTodayWindSpeed() + "\t" +
	    today.getTodayTempFeel() + "\t" + today.getTodayPress() + "\t" + today.getTodayIndex1() + "\t" +
	    today.getTodayIndex2() + "\t" + today.getTodayExplain() + "\t" + today.getTodayUpdateTime() + "\t";
    	//Log.d("myTag", "TodayData=[" + Temp + "]");
    	if (isCityList)
	    prefs.putString(Const.Today_Today + info.getCityID(), Temp);
    	else
	    prefs.putString(Const.THEME_TODAY_WEATHER + ThemeCode, Temp);

    	// Save 6Hourly Data
    	Temp = "";
    	// Icon
    	if (hourly.getHourlyIcons(0) == null || hourly.getHourlyIcons(0).length() <= 0)
	    return;
    	for (int i = 0 ; i < 8 ; i ++)
	    Temp = Temp + hourly.getHourlyIcons(i) + "\t";
    	// Temp
    	for (int i = 0 ; i < 8 ; i ++)
	    Temp = Temp + hourly.getHourlyTemp(i) + "\t";
    	// 강수확률
    	for (int i = 0 ; i < 8 ; i ++)
	    Temp = Temp + hourly.getHourlyPop(i) + "\t";
    	if (isCityList)
	    prefs.putString(Const.Today_Hourly + info.getCityID(), Temp);
    	else
	    prefs.putString(Const.THEME_HOURLY_WEATHER + ThemeCode, Temp);

    	// Save Weekly Data
    	Temp = "";
    	// date
    	if (weekly.getWeeklyDate(0) == null || weekly.getWeeklyDate(0).length() <= 0)
	    return;
    	for (int i = 0 ; i < 10 ; i++)
	    Temp = Temp + weekly.getWeeklyDate(i) + "\t";
    	// day of week
    	for (int i = 0 ; i < 10 ; i++)
	    Temp = Temp + weekly.getWeeklyDayOfWeek(i) + "\t";
    	// WX_Icon
    	for (int i = 0 ; i < 10 ; i++)
	    Temp = Temp + weekly.getWeeklyIcon(i) + "\t";
    	// TempMax
    	for (int i = 0 ; i < 10 ; i++)
	    Temp = Temp + weekly.getWeeklyTempMax(i) + "\t";
    	// TempMin
    	for (int i = 0 ; i < 10 ; i++)
	    Temp = Temp + weekly.getWeeklyTempMin(i) + "\t";
    	if (isCityList)
	    prefs.putString(Const.Today_Weekly + info.getCityID(), Temp);
    	else
	    prefs.putString(Const.THEME_WEEKLY_WEATHER + ThemeCode, Temp);

    	prefs.commit();
    }

    public void Update() {
	// TODO Auto-generated method stub
	String URL;
	InputStream fis = null;
	String[] Temp;
	int[] nDayOfWeek = new int[10];

	if (isCityList)
	    URL = Const.URL_TODAY + "region=" + info.getCityID();
	else
	    URL = Const.URL_TODAY + "region=" + ThemeAreaID;
	//Log.d("myTag", "URL = " + URL);
	try {
		fis = Util.getByteArrayFromURL(URL);
	} catch (UnknownHostException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}

	if (fis == null) {
	    //Log.e("myTag", "fis is null");
	    return;
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

	ParseTodayWeather mySample  = new ParseTodayWeather(this);
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

	today.setTodayIcon(mySample.geticon());
    	today.setTodayTempMax(mySample.gettempmax());
    	today.setTodayTempMin(mySample.gettempmin());
    	today.setTodayPop(mySample.getpop());
    	today.setTodayVisi(mySample.getvisi());
    	today.setTodayTempCur(mySample.gettempcur());
    	today.setTodayHum(mySample.gethum());
    	today.setTodayWindDir(mySample.getwnddir());
    	today.setTodayWindSpeed(mySample.getwndspd());
    	today.setTodayTempFeel(mySample.gettempfeel());
    	today.setTodayPress(mySample.getpress());
    	today.setTodayIndex1(mySample.getindex1());
    	today.setTodayIndex2(mySample.getindex2());
    	today.setTodayExplain(mySample.getexplain());
    	today.setTodayUpdateTime(Long.toString(Calendar.getInstance().getTimeInMillis()));

    	if (isCityList)
	    URL = Const.URL_HOURLY + "region=" + info.getCityID();
    	else
	    URL = Const.URL_HOURLY + "region=" + ThemeAreaID;
	try {
		if(fis != null) {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			fis = null;
		}
		fis = Util.getByteArrayFromURL(URL);
	} catch (UnknownHostException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}

	if (fis == null) {
	    //Log.e("myTag", "fis is null");
	    return;
	}

	Parse6HourlyWeather Hourly  = new Parse6HourlyWeather(this);
	myReader.setContentHandler(Hourly);
	try {
	    myReader.parse(new InputSource(fis));
	} catch (IOException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	} catch (SAXException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}

	Temp = Hourly.geticon().toString().split("\t");
	hourly.setHourlyIcon(Temp);

	Temp = Hourly.gettemp().toString().split("\t");
	hourly.setHourlyTemp(Temp);

	Temp = Hourly.getpop().toString().split("\t");
	hourly.setHourlyPop(Temp);

	if (isCityList)
	    URL = Const.URL_WEEK + "region=" + info.getCityID();
	else
	    URL = Const.URL_WEEK + "region=" + ThemeAreaID;
	try {
		if(fis != null) {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			fis = null;
		}
		fis = Util.getByteArrayFromURL(URL);
	} catch (UnknownHostException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}

	if (fis == null) {
	    //Log.e("myTag", "fis is null");
	    return;
	}

	ParseWeeklyWeather Weekly  = new ParseWeeklyWeather(this);
	myReader.setContentHandler(Weekly);
	try {
	    myReader.parse(new InputSource(fis));
	} catch (IOException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	} catch (SAXException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}

	Temp = Weekly.getdate().toString().split("\t");
	weekly.setWeeklyDate(Temp);

	Temp = Weekly.getdayofweek().toString().split("\t");
	for (int i = 0 ; i < 10 ; i ++){
	    try{
		nDayOfWeek[i] = Integer.parseInt(Temp[i]);
	    } catch(Exception e){
		nDayOfWeek[i] = 0;
	    }
	}
	weekly.setWeeklyDayOfWeek(nDayOfWeek);

	Temp = Weekly.geticon().toString().split("\t");
	weekly.setWeeklyIcon(Temp);

	Temp = Weekly.gettempmax().toString().split("\t");
	weekly.setWeeklyTempMax(Temp);

	Temp = Weekly.gettempmin().toString().split("\t");
	weekly.setWeeklyTempMin(Temp);
	
	if(fis != null) {
		try {
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		fis = null;
	}

	//Log.d("myTag", "Update done");
    }

    final Handler handler = new Handler() {
	    public void handleMessage(Message msg) {
        	//Log.d("myTag", "ReDraw~~~");
        	getSavedData();
        	bm = getContentImage();
		imgContent.setImageBitmap(bm);
		Calendar cl;
		cl = Calendar.getInstance();
		if (res == null)
		    res = getResources();
		if (updateTime != 0)
		    cl.setTimeInMillis(updateTime);
		Util.setFooter(footer, cl, TimeFlag, DateFlag);
//		LoadingAnimation.stop();
//		LoadingLogo.setVisibility(View.INVISIBLE);
		progress.setVisibility(View.INVISIBLE);
	    }
	};

    @Override
	protected void onResume() {
//	if (LoadingAnimation.isRunning())
//	    LoadingAnimation.stop();
//	LoadingLogo.setVisibility(View.INVISIBLE);
    	progress.setVisibility(View.INVISIBLE);
    	super.onResume();
    }

    private Bitmap getContentImage() {
	// TODO Auto-generated method stub
	Bitmap bm = Bitmap.createBitmap(800, 324, Config.ARGB_8888);
//	bm = Bitmap.createBitmap(800, 324, Config.RGB_565);
	if (res == null)
	    res = getResources();
	Canvas canvas = new Canvas(bm);
	Bitmap bg = BitmapFactory.decodeResource(res, R.drawable.landscape_view_bg_01);
	Bitmap NameBG = BitmapFactory.decodeResource(res, R.drawable.location_name_bg_01);
	Bitmap[] Icons = new Bitmap[7];

	Rect[] rcIcons = new Rect[7];
	Rect rcBG = new Rect(0, 0, 800, 324);
	Rect rcNameBG = new Rect(20, 0, 790, 65);

	Paint paint = new Paint();
	Paint paint1 = new Paint();
	paint.setAntiAlias(true);
	paint1.setAntiAlias(true);

	Paint paintTemp1 = new Paint();
	Paint paintTemp2 = new Paint();
	Paint paintTemp3 = new Paint();
	paintTemp1.setAntiAlias(true);
	paintTemp2.setAntiAlias(true);
	paintTemp3.setAntiAlias(true);

	canvas.drawBitmap(NameBG, null, rcNameBG, null);
	canvas.drawBitmap(bg, null, rcBG, null);

	//paint.setTypeface(Const.typeFace);
	//paint1.setTypeface(Const.typeFace);
	paint.setColor(Const.FONT_COLOR_WHITE);
	paint.setTextSize(32);

	canvas.drawText(info.getCityName(), 20, 49, paint);

	paint.setTextSize(24);
	paint.setTextAlign(Paint.Align.CENTER);
	String DateString;

	paintTemp1.setColor(Color.rgb(99, 151, 255));
	paintTemp1.setTextAlign(Paint.Align.LEFT);
	paintTemp1.setTextSize(30);
	paintTemp2.setColor(Color.rgb(99, 151, 255));
	paintTemp2.setTextAlign(Paint.Align.LEFT);
	paintTemp2.setTextSize(16);
	paintTemp3.setColor(Color.rgb(99, 151, 255));
	paintTemp3.setTextAlign(Paint.Align.LEFT);
	paintTemp3.setTextSize(30);

	float fTemp = 0;
	float fStart = 0;

	String szlTempMax;
	String szlTempMin;

	for (int i = 0 ; i < 7 ; i ++){
	    Icons[i] = BitmapFactory.decodeResource(res, Util.getWeekIconID(res, weekly.getWeeklyIcon(i)));

	    if (i < 4)	rcIcons[i] = new Rect(34 + 200 * i, 80, 41 + 200 * i + 100, 180);
	    else		rcIcons[i] = new Rect(34 + 300 * (i - 4), 208, 41 + 300 * (i - 4) + 100, 308);

	    //Log.d("myTag", "rcIcon[" + i + "] left=" + rcIcons[i].left + " top=" + rcIcons[i].top +
	    //		" right=" + rcIcons[i].right + " bottom=" + rcIcons[i].bottom);

	    canvas.drawBitmap(Icons[i], null, rcIcons[i], null);

	    if (weekly.getWeeklyDayOfWeek(i) == 6)
		paint.setColor(Const.FONT_COLOR_BLUE);
	    else if (weekly.getWeeklyDayOfWeek(i) == 0)
		paint.setColor(Const.FONT_COLOR_RED);
	    else
		paint.setColor(Const.FONT_COLOR_WHITE);

	    if (DateFlag)
	    	DateString = weekly.getWeeklyDate(i);
	    else{
			String[] datetemp;
			datetemp = weekly.getWeeklyDate(i).split("/");
			if(datetemp.length == 2)
				DateString = datetemp[1] + "/" + datetemp[0];
			else
				DateString = weekly.getWeeklyDate(i);
	    }
	    if (i < 4)
		canvas.drawText(DateString + "(" + Const.Week_day[weekly.getWeeklyDayOfWeek(i)] + ")", 20 + 73 + 200 * i, 90, paint);
	    else
		canvas.drawText(DateString + "(" + Const.Week_day[weekly.getWeeklyDayOfWeek(i)] + ")", 20 + 73 + 300 * (i - 4), 217, paint);

	    szlTempMax = weekly.getWeeklyTempMax(i);
	    szlTempMin = weekly.getWeeklyTempMin(i);

	    int nlTempMax, nlTempMin;

	    try{ nlTempMax = Integer.parseInt(szlTempMax); } catch (NumberFormatException e) { nlTempMax = -999; }

	    try{ nlTempMin = Integer.parseInt(szlTempMin); } catch (NumberFormatException e) { nlTempMin = -999; }

	    paintTemp1.setColor(Color.rgb(220, 220, 0));
	    paintTemp2.setColor(Color.rgb(220, 220, 0));

	    if (!TempFlag) {
		szlTempMax = Util.getFahrenheit(szlTempMax);
		szlTempMin = Util.getFahrenheit(szlTempMin);
	    }
	    fTemp = paintTemp1.measureText(szlTempMax) + paintTemp1.measureText(szlTempMin) +
		paintTemp2.measureText(TempSymbol) * 2 + paintTemp2.measureText("/");

	    if (i < 4)
		fStart = 20 + 73 + 200 * i - ( fTemp / 2 );
	    else
		fStart = 20 + 73 + 300 * (i - 4) - ( fTemp / 2 );
	    if (i < 4){
		canvas.drawText(szlTempMax, fStart, 180, paintTemp1);
		fStart += paintTemp1.measureText(szlTempMax);
		canvas.drawText(TempSymbol + "/", fStart, 180, paintTemp2);
		fStart += paintTemp2.measureText(TempSymbol + "/");
		canvas.drawText(szlTempMin, fStart, 180, paintTemp1);
		fStart += paintTemp1.measureText(szlTempMin);
		canvas.drawText(TempSymbol, fStart, 180, paintTemp2);
	    } else {
		canvas.drawText(szlTempMax, fStart, 314, paintTemp1);
		fStart += paintTemp1.measureText(szlTempMax);
		canvas.drawText(TempSymbol + "/", fStart, 314, paintTemp2);
		fStart += paintTemp2.measureText(TempSymbol + "/");
		canvas.drawText(szlTempMin, fStart, 314, paintTemp1);
		fStart += paintTemp1.measureText(szlTempMin);
		canvas.drawText(TempSymbol, fStart, 314, paintTemp2);
	    }
	}

	bg.recycle();
	NameBG.recycle();
	for (int i = 0 ; i < 7 ; i++)
	    Icons[i].recycle();
	return bm;
    }

    private void getSavedData(){
    	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
    	String[] TempArray;
    	String Temp;

    	//updateTime = prefs.getLong(Const.UPDATE_TIME_TODAY + info.getCityID(), 0);
    	if (isCityList)
	    Temp = prefs.getString(Const.Today_Today + info.getCityID(), "");
    	else
	    Temp = prefs.getString(Const.THEME_TODAY_WEATHER + ThemeCode, "");
    	if (Temp.length() <= 0)
	    updateTime = 0;
	else {
	    TempArray = Temp.split("\t");
	    if (TempArray[14]!= null)
		try{
		    updateTime = Long.parseLong(TempArray[14]);
		} catch(NumberFormatException e){
		    updateTime = 0;
		}
    	}

    	if (isCityList)
	    Temp = prefs.getString(Const.Today_Weekly + info.getCityID(), "");
    	else
	    Temp = prefs.getString(Const.THEME_WEEKLY_WEATHER + ThemeCode, "");
    	if (Temp.equals("") || Temp.length() <= 0)
	    {
		TempArray = Temp.split("\t");
		for (int i = 0, j = 0 ; i < 10 ; i++, j++)
		    weekly.setWeeklyDate("--", i);
		for (int i = 0, j = 10 ; i < 10 ; i++, j++)
		    weekly.setWeeklyDayOfWeek(i%7, i);
		for (int i = 0, j = 20 ; i < 10 ; i++, j++)
		    weekly.setWeeklyIcon("01", i);
		for (int i = 0, j = 30 ; i < 10 ; i++, j++)
		    weekly.setWeeklyTempMax("--", i);
		for (int i = 0, j = 40 ; i < 10 ; i++, j++)
		    weekly.setWeeklyTempMin("--", i);
	    }
	else
	    {
		TempArray = Temp.split("\t");
		for (int i = 0, j = 0 ; i < 10 ; i++, j++)
		    weekly.setWeeklyDate(TempArray[j], i);
		for (int i = 0, j = 10 ; i < 10 ; i++, j++)
		    weekly.setWeeklyDayOfWeek(Integer.parseInt(TempArray[j]), i);
		for (int i = 0, j = 20 ; i < 10 ; i++, j++)
		    weekly.setWeeklyIcon(TempArray[j], i);
		for (int i = 0, j = 30 ; i < 10 ; i++, j++)
		    weekly.setWeeklyTempMax(TempArray[j], i);
		for (int i = 0, j = 40 ; i < 10 ; i++, j++)
		    weekly.setWeeklyTempMin(TempArray[j], i);
	    }
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item){

	switch (item.getItemId()) {
        case 0:
	    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
	    	boolean network = Util.checkNetwork(manager);
	    	
	    	if(!network) {
	    		
//	    		LoadingAnimation.stop();
//	    		LoadingLogo.setVisibility(View.INVISIBLE);
	    		progress.setVisibility(View.INVISIBLE);
	    	    
				AlertDialog.Builder alert = null;
//		    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//		    		alert = new AlertDialog.Builder(Land10days.this, AlertDialog.THEME_HOLO_DARK);
//		    	else
		    		alert = new AlertDialog.Builder(Land10days.this);
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

//	    LoadingAnimation.start();
//	    LoadingLogo.setVisibility(View.VISIBLE);
	    	progress.setVisibility(View.VISIBLE);
	    Thread background=new Thread(new Runnable() {
		    public void run() {
			//Log.d("myTag", "Start Thread~~~~");
			try {
			    Thread.sleep(100);
			    Update();
			    saveData();
			    Message msg = handler.obtainMessage();
			    handler.sendMessage(msg);
			}
			catch (Throwable t) {
			    // just end the background thread
			    //Log.e("myTag", "error = " + t.toString());
			    Message msg = handler.obtainMessage();
			    handler.sendMessage(msg);
			}
		    }
    		});
            background.start();
            return true;
        case 1:
        	dialog = new ProgressDialog(Land10days.this);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setMessage("이미지 저장중..");
            dialog.closeOptionsMenu();
            dialog.show();

            final Handler sendhandler = new Handler() {
            	public void handleMessage(Message msg) {
                	if (msg.what == -1) {
    					AlertDialog.Builder alert = null;
//    			    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    			    		alert = new AlertDialog.Builder(Land10days.this, AlertDialog.THEME_HOLO_DARK);
//    			    	else
    			    		alert = new AlertDialog.Builder(Land10days.this);
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
					boolean isOk = Util.setCaptureAndSend(Land10days.this, (View)topLayer, bmp);
					dialog.dismiss();
					if(!isOk) {
						Message msg = sendhandler.obtainMessage(-1);
						sendhandler.sendMessage(msg);
					}
				}
			});
        	send.start();
            return true;
        }
        return true;
    }

    @Override
	protected void onPause() {
    	super.onPause();
    	if(dialog != null && dialog.isShowing())
    		dialog.dismiss();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "업데이트").setIcon(R.drawable.ic_menu_update);
        menu.add(0, 1, 0, "공유하기").setIcon(R.drawable.ic_menu_share);
        return true;
    }
}
