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
import android.graphics.Point;
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

public class Land48hGraph extends Activity {

    private View footer;
    private ImageView imgContent;
    private BaseInfo info;
    private HourlyData hourly = new HourlyData();
    private TodayData today = new TodayData();
    private WeeklyData weekly = new WeeklyData();

//    private AnimationDrawable LoadingAnimation;
//    private ImageView LoadingLogo;
    private ProgressBar progress;

    private long updateTime;

    private boolean DateFlag;
    private boolean TimeFlag;
    private boolean TempFlag;
    private LinearLayout topLayer;
    private static View main_view = null;

    private boolean isFirst = true;

    private static Resources res;
    private Bitmap bm;

    private boolean isCityList;
    private String From;
    private String ThemeAreaID = "";
    private ProgressDialog dialog;
    private Context context;

    
    private final int MSG_CODE = 100;
    private String ThemeCode = "";
    
    private Handler msgHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
		    try {
		    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
		    	boolean network = Util.checkNetwork(manager);
		    	
		    	if(!network) {
		    		
//		    		LoadingAnimation.stop();
		    		progress.setVisibility(View.INVISIBLE);
		    	    
					AlertDialog.Builder alert = null;
//			    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//			    		alert = new AlertDialog.Builder(Land48hGraph.this, AlertDialog.THEME_HOLO_DARK);
//			    	else
			    		alert = new AlertDialog.Builder(Land48hGraph.this);
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
        	main_view = layout.inflate(R.layout.land48hgraph, null);
        }
//        setContentView(R.layout.land48h);
        setContentView(main_view);
        footer = findViewById(R.id.footer);

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

//        LoadingLogo = (ImageView) findViewById(R.id.main_loadinglogo);
//    	LoadingAnimation = (AnimationDrawable) LoadingLogo.getBackground();
    	progress = (ProgressBar) findViewById(R.id.progress_small_title);
    	
    	context = getApplicationContext();

        DateFlag = Util.getDateFlag(context);
        TimeFlag = Util.getTimeFlag(context);
    	TempFlag = Util.getTempFlag(context);

    	res = getResources();

        getSavedData();

        imgContent = (ImageView) findViewById(R.id.land48h_content);
        bm = getContentImage();
        imgContent.setImageBitmap(bm);

//        LoadingAnimation.stop();
        progress.setVisibility(View.INVISIBLE);

//        Thread background=new Thread(new Runnable() {
//		public void run() {
//		    //Log.d("myTag", "Start Thread~~~~");
//		    try {
//		    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
//		    	boolean network = Util.checkNetwork(manager);
//		    	
//		    	if(!network) {
//		    		
////		    		LoadingAnimation.stop();
//		    		progress.setVisibility(View.INVISIBLE);
//		    	    
//		    		AlertDialog.Builder alert = new AlertDialog.Builder(Land48hGraph.this);
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

	    if (cl.getTimeInMillis() - updateTime > 1800000l){
			ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
			boolean network = Util.checkNetwork(manager);
    	
			if(network) {
//				LoadingAnimation.start();
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
		progress.setVisibility(View.INVISIBLE);
	    }
	};

    @Override
	protected void onResume() {
//	if (LoadingAnimation.isRunning())
//	    LoadingAnimation.stop();
	progress.setVisibility(View.INVISIBLE);
    	super.onResume();
    }

    private Bitmap getContentImage() {
	// TODO Auto-generated method stub
	Bitmap bm = Bitmap.createBitmap(800, 324, Config.ARGB_8888);
//	bm = Bitmap.createBitmap(800, 324, Config.RGB_565);
	Canvas canvas = new Canvas(bm);
	if (res == null)
	    res = getResources();
	Bitmap bg = BitmapFactory.decodeResource(res, R.drawable.landscape_view_bg_01);
	Bitmap NameBG = BitmapFactory.decodeResource(res, R.drawable.location_name_bg_01);
	Bitmap Line10 = BitmapFactory.decodeResource(res, R.drawable.landscape_line_02);
	Bitmap Line0 = BitmapFactory.decodeResource(res, R.drawable.landscape_line_01);
	Bitmap Dot = BitmapFactory.decodeResource(res, R.drawable.landscape_dot_01);
	Bitmap DateBG = BitmapFactory.decodeResource(res, R.drawable.landscape_view_bg_02);

	Rect rcBG = new Rect(0, 0, 800, 324);
	Rect rcNameBG = new Rect(20, 0, 790, 65);
	Rect rcLine;
	Rect rcDot;
	Rect rcDateBg = new Rect(0, 248, 800, 323);
	int[] nXpos = {36, 140, 244, 348, 452, 556, 660, 764};
	int[] nCurTmp = new int[8];

	Point[] point = new Point[8];
	String[] DateStr = {"01.28(목)", "01.29(금)", "01.30(토)"};
	String[] Hour6Str = {"새벽", "오전", "오후", "저녁"};

	for (int i = 0 ; i < 8 ; i++){
	    try{
		if (!TempFlag)
		    nCurTmp[i] = Integer.parseInt(Util.getFahrenheit(hourly.getHourlyTemp(i)));
		else
		    nCurTmp[i] = Integer.parseInt(hourly.getHourlyTemp(i));
	    } catch (NumberFormatException e){
		nCurTmp[i] = -999;
	    }
	}

	int nBaseMin = getMinTemp(nCurTmp);
	int nBaseMax = getMaxTemp(nCurTmp);
	int nStartY = 86;
	int nGab = (int)Math.round((160.0/(nBaseMax - nBaseMin)));

	//Log.d("myTag", "Min=" + nBaseMin + " Max=" + nBaseMax + " nGab=" + nGab);

	canvas.drawBitmap(NameBG, null, rcNameBG, null);
	canvas.drawBitmap(bg, null, rcBG, null);

	for (int i = nBaseMax, j = 0 ; i >= nBaseMin ; i -= 10, j++){
	    rcLine = new Rect(0, nStartY +  nGab * 10 * j, 800, nStartY + nGab * 10 * j + 2);
	    //Log.d("myTag", "rcLIne=" + rcLine.toString());
	    if (i == 0){
		if (rcLine.bottom <= 250)
		    canvas.drawBitmap(Line0, null, rcLine, null);
		//Log.d("myTag", "Line0");
	    } else{
		if (rcLine.bottom <= 250)
		    canvas.drawBitmap(Line10, null, rcLine, null);
		//Log.d("myTag", "Line10");
	    }
	}

	Paint paint = new Paint();
	paint.setAntiAlias(true);

//	paint.setTypeface(Const.typeFace);
	paint.setColor(Color.rgb(220, 255, 59));
	paint.setStyle(Paint.Style.STROKE);

	paint.setStrokeWidth(2);
	for (int i = 0 ; i < nXpos.length ; i++){
	    point[i] = new Point(nXpos[i], nStartY + (nBaseMax - nCurTmp[i]) * nGab);
	    rcDot = new Rect(nXpos[i] - 7, nStartY + (nBaseMax - nCurTmp[i]) * nGab - 7, nXpos[i] + 7, nStartY + (nBaseMax - nCurTmp[i]) * nGab + 7);
	    //Log.d("myTag", "rcDot=" + rcDot.toString());
	    canvas.drawBitmap(Dot, null, rcDot, null);
	    if (i < nXpos.length - 1)
		canvas.drawLine(nXpos[i], nStartY + (nBaseMax - nCurTmp[i] ) * nGab, nXpos[i+1], nStartY + (nBaseMax - nCurTmp[i+1]) * nGab, paint);
	}
	paint.setStrokeWidth(0);
	paint.setColor(Const.FONT_COLOR_WHITE);
	paint.setTextSize(32);

	canvas.drawText(info.getCityName(), 20, 49, paint);

	paint.setTextSize(20);
	paint.setTextAlign(Paint.Align.CENTER);


	for (int i = 0 ; i < point.length ; i++){
	    canvas.drawText(""+nCurTmp[i], point[i].x, point[i].y - 11, paint);
	}

	paint.setTextAlign(Paint.Align.LEFT);

	canvas.drawBitmap(DateBG, null, rcDateBg, null);

	Calendar cl;
        cl = Calendar.getInstance();
        cl.setTimeInMillis(updateTime);
        cl.setTimeZone(TimeZone.getTimeZone(info.getTimeZone()));

        int nTextIndex = 0;
    	int nHourOfDay = cl.get(Calendar.HOUR_OF_DAY);
    	if (nHourOfDay <6)
	    nTextIndex = 1;
    	else if (nHourOfDay >= 6 && nHourOfDay < 12)
	    nTextIndex = 2;
    	else if (nHourOfDay >= 12 && nHourOfDay < 18)
	    nTextIndex = 3;
    	else
	    nTextIndex = 0;

	for (int i = 0, j = 0 ; i < 8 ; i++, nTextIndex++){
	    canvas.drawText(Const.HOURLY_TXT[nTextIndex % 4], 18 + 103 * i, 315, paint);
	    if (nTextIndex % 4 == 0){
		cl.add(Calendar.DAY_OF_MONTH, 1);
		if (DateFlag)
		    DateStr[j] = String.format("%02d/%02d(%s)",
					       cl.get(Calendar.MONTH) + 1, cl.get(Calendar.DAY_OF_MONTH),
					       Const.Week_day[cl.get(Calendar.DAY_OF_WEEK)-1]);
		else
		    DateStr[j] = String.format("%02d/%02d(%s)",
					       cl.get(Calendar.DAY_OF_MONTH), cl.get(Calendar.MONTH) + 1,
					       Const.Week_day[cl.get(Calendar.DAY_OF_WEEK)-1]);
		if ( i != 7)
		    canvas.drawText(DateStr[j], 18+103*i, 280, paint);
		else
		    canvas.drawText(DateStr[j], 18+103*i - 25, 280, paint);
		j++;
	    }
	}

	bg.recycle();
	NameBG.recycle();
	Line10.recycle();
	Line0.recycle();
	Dot.recycle();
	DateBG.recycle();
	return bm;
    }

    private int getMinTemp(int[] nCurTmp) {
	// TODO Auto-generated method stub
	int nTemp = nCurTmp[0];
	for (int i = 1 ; i < nCurTmp.length ; i++){
	    nTemp = (nTemp - nCurTmp[i]) > 0 ? nCurTmp[i] : nTemp;
	}

	if (nTemp % 10 != 0)
	    nTemp = ( nTemp / 10 - 1 ) * 10;


	return nTemp;
    }

    private int getMaxTemp(int[] nCurTmp) {
	// TODO Auto-generated method stub
	int nTemp = nCurTmp[0];
	for (int i = 1 ; i < nCurTmp.length ; i++){
	    nTemp = (nTemp - nCurTmp[i]) > 0 ? nTemp : nCurTmp[i];
	}

	return ( nTemp / 10 + 1 ) * 10;
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
	    Temp = prefs.getString(Const.Today_Hourly + info.getCityID(), "");
    	else
	    Temp = prefs.getString(Const.THEME_HOURLY_WEATHER + ThemeCode, "");
    	if (Temp.equals("") || Temp.length() <= 0)
	    {
		for (int i = 0, j = 0 ; i < 8 ; i++, j++)
		    hourly.setHourlyIcon("01", i);
		for (int i = 0, j = 8 ; i < 8 ; i++, j++)
		    hourly.setHourlyTemp("--", i);
		for (int i = 0, j = 16 ; i < 8 ; i++, j++)
		    hourly.setHourlyPop("--", i);
	    }
	else
	    {
		TempArray = Temp.split("\t");
		for (int i = 0, j = 0 ; i < 8 ; i++, j++)
		    hourly.setHourlyIcon(TempArray[j], i);
		for (int i = 0, j = 8 ; i < 8 ; i++, j++)
		    hourly.setHourlyTemp(TempArray[j], i);
		for (int i = 0, j = 16 ; i < 8 ; i++, j++)
		    hourly.setHourlyPop(TempArray[j], i);
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
	    		progress.setVisibility(View.INVISIBLE);
	    	    
				AlertDialog.Builder alert = null;
//		    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//		    		alert = new AlertDialog.Builder(Land48hGraph.this, AlertDialog.THEME_HOLO_DARK);
//		    	else
		    		alert = new AlertDialog.Builder(Land48hGraph.this);
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
//        	final ProgressDialog dialog;

        	dialog = new ProgressDialog(Land48hGraph.this);
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
//    			    		alert = new AlertDialog.Builder(Land48hGraph.this, AlertDialog.THEME_HOLO_DARK);
//    			    	else
    			    		alert = new AlertDialog.Builder(Land48hGraph.this);
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
					boolean isOk = Util.setCaptureAndSend(Land48hGraph.this, (View)topLayer, bmp);
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
