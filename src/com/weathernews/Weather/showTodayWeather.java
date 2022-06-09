package com.weathernews.Weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.weathernews.Weather.Const.WEATHER_FOCUS_BTN;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.graphics.drawable.NinePatchDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

class showTodayUpdater implements Runnable {
    showTodayWeather parent;
    showTodayUpdater(showTodayWeather parent) { this.parent = parent; }
    public void run() {
	try {
	    Thread.sleep(100);
	    parent.Update();
	    Message msg;
	    if (parent.saveData())
		msg = parent.handler.obtainMessage();
	    else
		msg = parent.handler.obtainMessage(-1);
	    parent.handler.sendMessage(msg);
	}
	catch (Throwable t) {
	    // just end the background thread
	    //Log.e("myTag", "error = " + t.toString());
	    Message msg = parent.handler.obtainMessage(-1);
	    parent.handler.sendMessage(msg);
	}
	parent = null;
    }
}


public class showTodayWeather extends Activity {

    private BaseInfo info;
    //private DrawWeather weather = null;
    private TodayData today;
    private HourlyData hourly;
    private WeeklyData weekly;
    private long updateTime;
    private ImageView imgContent;
//    private ImageView imgWeekly;
    private String cityname;

    private BaseInfo tempinfo;
    private boolean isStop = false;
    
    private int x = 0;
    private int y = 0;
    private Bitmap[] iconw = new Bitmap[10];
    private static Bitmap[] icon = new Bitmap[5];
    private Bitmap[] Hourly = new Bitmap[4];
    private Bitmap CityNameBG;
    private Bitmap Today;
    private Bitmap Week_BG;
    private Paint paint = new Paint();
    private int [][] WeatherIcon;

    final static int nWeekX = 4;
    final static int nWeekY = 37;
    final static int nWidth = 100;
    final static int nHeight = 100;
    final static int nGap = 22;
    private Resources rw;
    private WEATHER_FOCUS_BTN nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_NONE;

    private Rect[] rc = new Rect[5];
    private Rect rc_Week = new Rect(0, 570, 480, 724);
    private Rect rc_Icon_btn = new Rect(0, 0, 720, 850);
    private Rect rc_Weather = new Rect(0, 0, 160, 96);
    private int nWeeklyCnt = 7;


    private String TempSymbol = "℃";


//    private AnimationDrawable LoadingAnimation;
//    private ImageView LoadingLogo;
    private ProgressBar progress;
    private LinearLayout topLayer;

//    private Handler SearchThread;
//    private Runnable searchTask;
//    private ExecutorService ServiceThread;
//    private Future SearchPending;
    private String provider;
    private LocationManager locationManager;
    private static Location Loc;

    private boolean isLiveCam = false;
    private String LiveCamID = "";
    private boolean DateFlag;
    private boolean TimeFlag;
    private boolean TempFlag;

    private DispLocListener loclistener;

    private View footer;
    private Bitmap bm;
    private Bitmap bmWeek;
    private ImageButton livebtn;
    private ImageButton gpsbtn;

    private boolean isKorea;

//    private void initThreading() {
//	SearchThread = new Handler();
//	ServiceThread = Executors.newSingleThreadExecutor();
//
//	searchTask = new Runnable() {
//
//		@Override
//		    public void run() {
//		    // TODO Auto-generated method stub
//		    if (SearchPending != null)
//			SearchPending.cancel(true);
//
//		    GPSTask task = new GPSTask(showTodayWeather.this);
//		    SearchPending = ServiceThread.submit(task);
//		}
//	    };
//    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.showtodayweather);
        
        initValues();

        Draw();

        progress.setVisibility(View.INVISIBLE);

        showTodayUpdater background = new showTodayUpdater(this);
        Calendar cl = Calendar.getInstance();
        if (cl.getTimeInMillis() - updateTime > 1800000l) {
        	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        	boolean network = Util.checkNetwork(manager);
        	
        	if(!network) {
        	    progress.setVisibility(View.INVISIBLE);
        	    
				AlertDialog.Builder alert = null;
//		    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//		    		alert = new AlertDialog.Builder(showTodayWeather.this, AlertDialog.THEME_HOLO_DARK);
//		    	else
		    		alert = new AlertDialog.Builder(showTodayWeather.this);
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
        	} else {
        		new Thread(background).start();
        		new Handler().postDelayed(new Runnable() { public void run() { /*LoadingAnimation.start();*/ progress.setVisibility(View.VISIBLE); } }, 100);
        	}
        }

    }
    
    public void initValues() {
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
        	try{
        		info = new BaseInfo(CityID[0][0], CityID[0][1], CityID[0][2], 0);
        	} catch (ArrayIndexOutOfBoundsException e) {
        		info = new BaseInfo("11B10101", "서울", "Asia/Seoul", 0);
        	}
        }
        
        isKorea = false;
        
		if(info.getTimeZone()!= null && info.getTimeZone().equals("Asia/Seoul"))
			isKorea = true;
		else
		    isKorea = false;


        DateFlag = Util.getDateFlag(getApplicationContext());
        TimeFlag = Util.getTimeFlag(getApplicationContext());
    	TempFlag = Util.getTempFlag(getApplicationContext());

        if (TempFlag)
	    TempSymbol = "℃";
     	else
	    TempSymbol = "℉";

        topLayer = (LinearLayout) findViewById(R.id.showtodayweather);
        imgContent= (ImageView) findViewById(R.id.showtoday_content);
        imgContent.setSoundEffectsEnabled(true);
//        imgWeekly = (ImageView) findViewById(R.id.showtoday_week);

        livebtn = (ImageButton) findViewById(R.id.livecam);
        livebtn.setClickable(false);
        livebtn.setBackgroundResource(R.drawable.weather_livecam_btn_dimmed);
        gpsbtn = (ImageButton) findViewById(R.id.gpsbtn);

        footer = findViewById(R.id.footer);

        gpsbtn.setOnClickListener(new OnClickListener()
	    {
		@Override public void onClick(View v)
		{
		    if (!checkUseGPS()) showDialog();
		    else startUseGPS();
		}
	    });

        //initThreading();

        today = new TodayData();
        hourly = new HourlyData();
        weekly = new WeeklyData();

	rw = getResources();
        paint.setAntiAlias(true);
        
        footer = findViewById(R.id.footer);


    	progress = (ProgressBar) findViewById(R.id.progress_small_title);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        provider = locationManager.getBestProvider(criteria, true);
        loclistener = new DispLocListener() ;
    }

    public final Handler handler = new Handler()
	{
	    public void handleMessage(Message msg)
	    {
	    	if (msg.what != -1) {
	    		showTodayWeather.this.Draw();
	    	}
	    	else if(isStop == false)
	    		ShowUpdateAgain();
//	    	LoadingAnimation.stop();
	    	progress.setVisibility(View.INVISIBLE);
	    }
	};
	

    @Override
	protected void onResume(){
    	isStop = false;
//    	Log.e("myTag", "Draw in onResume");
//	Draw();
	super.onResume();
    }

    @Override
	protected void onPause() {
    	isStop = true;
//    	LoadingAnimation.stop();
    	if (loclistener != null) {
//    		Log.d("myTag", "Stop Location Listener");
    		locationManager.removeUpdates(loclistener);
    		loclistener = null;
    	}

    	super.onPause();
    }

    @Override
	protected void onDestroy(){
    	super.onDestroy();
    	for (int i = 0 ; i < WeatherIcon.length ; i ++) {
    		if (icon[i] != null){
    			icon[i].recycle();
    			icon[i] = null;
    		}
    	}

    	if (bm != null){
	    bm.recycle();
	    bm = null;
    	}
    	if (bmWeek != null){
	    bmWeek.recycle();
	    bmWeek = null;
    	}
    	for(int i = 0 ; i < 4 ; i++) {
    		if(Hourly[i] != null) {
    			Hourly[i].recycle();
    			Hourly[i] = null;
    		}
    	}
    	if(Week_BG != null) {
    		Week_BG.recycle();
    		Week_BG = null;
    	}
    	rw = null;
    }

    @Override
	public void onWindowFocusChanged(boolean hasFocus) {
//    	if (hasFocus) LoadingAnimation.start();
    }

    void Draw() {
//    	setTabImage();
        setContentImage();
        setWeekImage();
        setFooterImage();
    }

    void setTabImage()
    {
    	if (null == rw) rw = getResources();
    }

    void setContentImage() {
    	TextView tvCityName1 = (TextView) findViewById(R.id.TextView01);
    	TextView tvCityName2 = (TextView) findViewById(R.id.TextView08);
    	
    	TextView tvTempMax = (TextView) findViewById(R.id.TextView02);
    	TextView tvTempMin = (TextView) findViewById(R.id.TextView06);
    	TextView tvTempCur = (TextView) findViewById(R.id.TextView03);
    	TextView tvTempFeel = (TextView) findViewById(R.id.TextView11);
    	
    	TextView tvTempSym1 = (TextView) findViewById(R.id.TextView04);
    	TextView tvTempSym2 = (TextView) findViewById(R.id.TextView07);
    	TextView tvTempSym3 = (TextView) findViewById(R.id.TextView09);
    	TextView tvTempSym4 = (TextView) findViewById(R.id.TextView12);
    	
    	TextView tvSym1 = (TextView) findViewById(R.id.TextView05);
    	TextView tvSym2 = (TextView) findViewById(R.id.TextView10);
    	
    	TextView tvDate = (TextView) findViewById(R.id.TextView13);
    	
    	TextView tvHourlyDate1 = (TextView) findViewById(R.id.Hourlytext_1_1);
    	TextView tvHourlyDate2 = (TextView) findViewById(R.id.HourlyText_2_1);
    	TextView tvHourlyDate3 = (TextView) findViewById(R.id.HourlyText_3_1);
    	TextView tvHourlyDate4 = (TextView) findViewById(R.id.HourlyText_4_1);
    	
    	TextView tvHourlyTemp1 = (TextView) findViewById(R.id.HourlyText_1_2);
    	TextView tvHourlyTemp2 = (TextView) findViewById(R.id.HourlyText_2_2);
    	TextView tvHourlyTemp3 = (TextView) findViewById(R.id.HourlyText_3_2);
    	TextView tvHourlyTemp4 = (TextView) findViewById(R.id.HourlyText_4_2);
    	
    	ImageView ivHourly1 = (ImageView) findViewById(R.id.Hourly_1);
    	ImageView ivHourly2 = (ImageView) findViewById(R.id.Hourly_2);
    	ImageView ivHourly3 = (ImageView) findViewById(R.id.Hourly_3);
    	ImageView ivHourly4 = (ImageView) findViewById(R.id.Hourly_4);
    	if(bm != null){
    		bm.recycle();
    		bm = null;
    	}
    	bm = Bitmap.createBitmap(480, 475, Config.RGB_565);

    	Canvas canvas = new Canvas(bm);
    	boolean isDayTime = false;

    	String CountryName;

    	if (rw == null)
    		rw = getResources();
    	
    	Week_BG = BitmapFactory.decodeResource(rw, R.drawable.weekend_weather_bg_01);
        CityNameBG = BitmapFactory.decodeResource(rw, R.drawable.location_name_bg_01);

        Today = BitmapFactory.decodeResource(rw, R.drawable.today_weather_text);

    	paint.setAntiAlias(true);
        paint.setColor(Const.FONT_COLOR_WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(20);
        
        getSavedData();

        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(updateTime);
        
        if(info != null && info.getTimeZone() != null && info.getTimeZone().length() > 0 )
        	cl.setTimeZone(TimeZone.getTimeZone(info.getTimeZone()));
        
        isDayTime = Util.getIsDayTime(today.getTodayIcon(), cl.get(Calendar.HOUR_OF_DAY));
        
    	int nColor = Color.rgb(5, 37, 102);

        if(isDayTime) {
        	tvTempMax.setTextColor(nColor);
        	tvTempMin.setTextColor(nColor);
        	tvTempCur.setTextColor(nColor);
        	tvTempFeel.setTextColor(nColor);
        	tvTempSym1.setTextColor(nColor);
            tvTempSym2.setTextColor(nColor);
            tvTempSym3.setTextColor(nColor);
            tvTempSym4.setTextColor(nColor);
            tvHourlyDate1.setTextColor(nColor);
            tvHourlyDate2.setTextColor(nColor);
            tvHourlyDate3.setTextColor(nColor);
            tvHourlyDate4.setTextColor(nColor);
            tvHourlyTemp1.setTextColor(nColor);
            tvHourlyTemp2.setTextColor(nColor);
            tvHourlyTemp3.setTextColor(nColor);
            tvHourlyTemp4.setTextColor(nColor);
            tvDate.setTextColor(nColor);
            tvSym1.setTextColor(nColor);
            tvSym2.setTextColor(nColor);
            tvTempCur.setShadowLayer(0, 0, 0, nColor);
            tvTempFeel.setShadowLayer(0, 0, 0, nColor);
            tvSym2.setShadowLayer(0, 0, 0, nColor);
            tvTempSym3.setShadowLayer(0, 0, 0, nColor);
            tvTempSym4.setShadowLayer(0, 0, 0, nColor);
        } else {
        	tvTempMax.setTextColor(Color.WHITE);
        	tvTempMin.setTextColor(Color.WHITE);
        	tvTempCur.setTextColor(Color.WHITE);
        	tvTempFeel.setTextColor(Color.WHITE);
        	tvTempSym1.setTextColor(Color.WHITE);
            tvTempSym2.setTextColor(Color.WHITE);
            tvTempSym3.setTextColor(Color.WHITE);
            tvTempSym4.setTextColor(Color.WHITE);
            tvHourlyDate1.setTextColor(Color.WHITE);
            tvHourlyDate2.setTextColor(Color.WHITE);
            tvHourlyDate3.setTextColor(Color.WHITE);
            tvHourlyDate4.setTextColor(Color.WHITE);
            tvHourlyTemp1.setTextColor(Color.WHITE);
            tvHourlyTemp2.setTextColor(Color.WHITE);
            tvHourlyTemp3.setTextColor(Color.WHITE);
            tvHourlyTemp4.setTextColor(Color.WHITE);
            tvDate.setTextColor(Color.WHITE);
            tvSym1.setTextColor(Color.WHITE);
            tvSym2.setTextColor(Color.WHITE);
            tvTempCur.setShadowLayer(1, 1, 1, Color.BLACK);
            tvTempFeel.setShadowLayer(1, 1, 1, Color.BLACK);
            tvSym2.setShadowLayer(1, 1, 1, Color.BLACK);
            tvTempSym3.setShadowLayer(1, 1, 1, Color.BLACK);
            tvTempSym4.setShadowLayer(1, 1, 1, Color.BLACK);
        }
        tvTempSym1.setText(TempSymbol);
        tvTempSym2.setText(TempSymbol);
        tvTempSym3.setText(TempSymbol);
        tvTempSym4.setText(TempSymbol);
        
        Paint paint1 = new Paint();
        Paint paint2 = new Paint();
        Paint paint3 = new Paint();

        paint1.setAntiAlias(true);
        paint1.setColor(nColor);
        paint1.setTextAlign(Paint.Align.LEFT);
        paint1.setTextSize(40);

        paint2.setAntiAlias(true);
        paint2.setColor(nColor);
        paint2.setTextAlign(Paint.Align.LEFT);
        paint2.setTextSize(32);

        paint3.setAntiAlias(true);
        paint3.setColor(Color.BLACK);
        paint3.setTextAlign(Paint.Align.LEFT);

       
        

    	// 날씨 아이콘 그리기
        WeatherIcon = Util.getTodayIconID(today.getTodayIcon(), cl.get(Calendar.HOUR_OF_DAY));
        for (int i = 0 ; i < WeatherIcon.length ; i ++) {
        	icon[i] = BitmapFactory.decodeResource(rw, WeatherIcon[i][0]);
        	rc[i] = new Rect(WeatherIcon[i][1], WeatherIcon[i][2], WeatherIcon[i][3], WeatherIcon[i][4]);
        	canvas.drawBitmap(icon[i], null, rc[i], null);
        }
        

    	CountryName = getCountryName(info.getCityName());
    	
    	tvCityName1.setTextColor(Const.FONT_COLOR_WHITE);
    	tvCityName2.setTextColor(Const.FONT_COLOR_WHITE);
    	

        paint.setTextAlign(Paint.Align.LEFT);
    	paint.setTextSize(40);
        paint.setColor(Const.FONT_COLOR_WHITE);
    	if (CountryName != null && !isKorea) {
    		String tempCityName = info.getCityName();
    		if(tempCityName.length() > 8) {
				tvCityName1.setTextSize(20);
    		} else {
    			tvCityName2.setText("(" + CountryName + ")");
    		}
    		tvCityName1.setText(tempCityName);
    	} else {
    		int nIndex = 0;
    		if ((nIndex = info.getCityName().indexOf("(")) > 0) {
    			String tempCityName = info.getCityName().substring(0, nIndex);
    			String tempDoName = info.getCityName().substring(nIndex);

    			paint.setTextSize(22);
    			tvCityName1.setText(tempCityName);
    			tvCityName2.setText(tempDoName);
    		} else {
    			tvCityName1.setText(info.getCityName());
    		}
    	}
    	paint.setTextSize(50);
        paint.setColor(nColor);

        if (!isDayTime){
	    paint.setColor(Const.FONT_COLOR_WHITE);
	    paint1.setColor(Const.FONT_COLOR_WHITE);
	    paint2.setColor(Const.FONT_COLOR_WHITE);
        }

        String szlTempMax = today.getTodayTempMax() == null || today.getTodayTempMax().length() == 0 ? "--" : today.getTodayTempMax();
        String szlTempMin = today.getTodayTempMin() == null || today.getTodayTempMin().length() == 0 ? "--" : today.getTodayTempMin();
        String szlTempCur = today.getTodayTempCur() == null || today.getTodayTempCur().length() == 0 ? "--" : today.getTodayTempCur();
        String szlTempFeel = today.getTodayTempFeel() == null || today.getTodayTempFeel().length() == 0 ? "--" : today.getTodayTempFeel();

        if (!TempFlag) {
	    szlTempMax = Util.getFahrenheit(szlTempMax);
	    szlTempMin = Util.getFahrenheit(szlTempMin);
	    szlTempCur = Util.getFahrenheit(szlTempCur);
	    szlTempFeel = Util.getFahrenheit(szlTempFeel);
        }

        tvTempMax.setText(szlTempMax);
        tvTempMin.setText(szlTempMin);
        tvTempCur.setText("현재기온" + szlTempCur);
        tvTempFeel.setText("체감기온" + szlTempFeel);
        


    	// 시간대별 날씨 아이콘
        for (int i = 0 ; i < 4 ; i++) {
        	try{
        		Hourly[i] = BitmapFactory.decodeResource(rw, Util.getHourIconID(rw, hourly.getHourlyIcons(i), today.getTodayIcon(), cl.get(Calendar.HOUR_OF_DAY)));
        	} catch(ArrayIndexOutOfBoundsException e){
        		Hourly[i] = BitmapFactory.decodeResource(rw, Util.getHourIconID(rw, "01", today.getTodayIcon(), cl.get(Calendar.HOUR_OF_DAY)));
        	}
        }

        ivHourly1.setImageBitmap(Hourly[0]);
    	ivHourly2.setImageBitmap(Hourly[1]);
    	ivHourly3.setImageBitmap(Hourly[2]);
    	ivHourly4.setImageBitmap(Hourly[3]);

    	paint.setTextSize(20);
    	if (isDayTime)
	    paint.setColor(Color.rgb(15, 52, 126));
    	else
	    paint.setColor(Color.rgb(136, 136, 136));
    	paint.setTextAlign(Paint.Align.CENTER);
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
    	
    	tvHourlyDate1.setText((nTextIndex / 4) == 1 || nTextIndex == 0 ? "내일 " + Const.HOURLY_TXT[nTextIndex++%4] : "오늘 " + Const.HOURLY_TXT[nTextIndex++%4]);
    	tvHourlyDate2.setText((nTextIndex / 4) == 1 || nTextIndex == 1 ? "내일 " + Const.HOURLY_TXT[nTextIndex++%4] : "오늘 " + Const.HOURLY_TXT[nTextIndex++%4]);
    	tvHourlyDate3.setText((nTextIndex / 4) == 1 || nTextIndex == 2 ? "내일 " + Const.HOURLY_TXT[nTextIndex++%4] : "오늘 " + Const.HOURLY_TXT[nTextIndex++%4]);
    	tvHourlyDate4.setText((nTextIndex / 4) == 1 || nTextIndex == 3 ? "내일 " + Const.HOURLY_TXT[nTextIndex++%4] : "오늘 " + Const.HOURLY_TXT[nTextIndex++%4]);
    	if (!TempFlag) {
	    	tvHourlyTemp1.setText(Util.getFahrenheit(hourly.getHourlyTemp(0)) + TempSymbol + "/" + hourly.getHourlyPop(0)+ "%");
	    	tvHourlyTemp2.setText(Util.getFahrenheit(hourly.getHourlyTemp(1)) + TempSymbol + "/" + hourly.getHourlyPop(1)+ "%");
	    	tvHourlyTemp3.setText(Util.getFahrenheit(hourly.getHourlyTemp(2)) + TempSymbol + "/" + hourly.getHourlyPop(2)+ "%");
	    	tvHourlyTemp4.setText(Util.getFahrenheit(hourly.getHourlyTemp(3)) + TempSymbol + "/" + hourly.getHourlyPop(3)+ "%");
    	} else {
	    	tvHourlyTemp1.setText(hourly.getHourlyTemp(0) + TempSymbol + "/" + hourly.getHourlyPop(0)+ "%");
	    	tvHourlyTemp2.setText(hourly.getHourlyTemp(1) + TempSymbol + "/" + hourly.getHourlyPop(1)+ "%");
	    	tvHourlyTemp3.setText(hourly.getHourlyTemp(2) + TempSymbol + "/" + hourly.getHourlyPop(2)+ "%");
	    	tvHourlyTemp4.setText(hourly.getHourlyTemp(3) + TempSymbol + "/" + hourly.getHourlyPop(3)+ "%");
    	}

        String DateStr;
        if (updateTime > 0){
	    if (DateFlag)
		DateStr = String.format("%02d", (cl.get(Calendar.MONTH) + 1)) + "/" +
		    String.format("%02d", cl.get(Calendar.DAY_OF_MONTH)) + "(" + Const.Week_day[cl.get(Calendar.DAY_OF_WEEK)-1] + ")";
	    else
		DateStr = String.format("%02d", cl.get(Calendar.DAY_OF_MONTH)) + "/" +
		    String.format("%02d", (cl.get(Calendar.MONTH) + 1)) + "(" + Const.Week_day[cl.get(Calendar.DAY_OF_WEEK)-1] + ")";
        } else {
	    DateStr = "01/01(목)";
        }
        
        tvDate.setText(DateStr);


    	imgContent.setImageBitmap(bm);
    	
    	new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
		    	rc_Icon_btn = new Rect(0, 0, imgContent.getWidth(), imgContent.getHeight());
			}
		}, 300);
    	

    	//Log.d("myTag", "LiveCamID=" + LiveCamID);
    	if (LiveCamID != null && LiveCamID.length() > 0)
    		isLiveCam = true;
        else
        	isLiveCam = false;

        if (isLiveCam) {
	    livebtn.setEnabled(true);
	    livebtn.setBackgroundResource(R.drawable.livebtn_selector);
	    livebtn.setOnClickListener(new OnClickListener() {

		    @Override
			public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent;
			intent = new Intent(showTodayWeather.this, ViewLiveCam.class);
			intent.putExtra("CityID", info.getCityID());
			intent.putExtra("CityName", info.getCityName());
			intent.putExtra("TimeZone", info.getTimeZone());
			intent.putExtra("Index", info.getIndex());
			intent.putExtra("CamID", LiveCamID);
			((TabRoot)getParent()).setCurrentTab(intent);
		    }
		});
        } else {
	    livebtn.setEnabled(true);
	    livebtn.setClickable(true);
	    livebtn.setBackgroundResource(R.drawable.weather_livecam_btn_dimmed);
        }

    	Today.recycle();
    	Today = null;
    	CityNameBG.recycle();
    	CityNameBG = null;
    }

    private String getCountryName(String cityName2) {
	// TODO Auto-generated method stub
    	String StartString = HangulUtils.getHangulInitialSound(cityName2.substring(0, 1));
    	String country = null;
	CityListClass[] current = null;
	if (StartString.equals("ㄱ") || StartString.equals("ㄲ")) current = GA.ga;
	else if (StartString.equals("ㄴ")) current = NA.na;
	else if (StartString.equals("ㄷ") || StartString.equals("ㄸ")) current = DA.da;
	else if (StartString.equals("ㄹ")) current = RA.ra;
	else if (StartString.equals("ㅁ")) current = MA.ma;
	else if (StartString.equals("ㅂ") || StartString.equals("ㅃ")) current = BA.ba;
	else if (StartString.equals("ㅅ") || StartString.equals("ㅆ")) current = SA.sa;
	else if (StartString.equals("ㅇ")) current = AA.aa;
	else if (StartString.equals("ㅈ") || StartString.equals("ㅉ")) current = JA.ja;
	else if (StartString.equals("ㅊ")) current = CHA.cha;
	else if (StartString.equals("ㅋ")) current = KA.ka;
	else if (StartString.equals("ㅌ")) current = TA.ta;
	else if (StartString.equals("ㅍ")) current = PA.pa;
	else if (StartString.equals("ㅎ")) current = HA.ha;
	else current = GA.ga;
	for(int i = 0 ; i < current.length; i++) {
	    if ( current[i].getCityName().equals(cityName2) && current[i].getCityID().equals(info.getCityID())) {
		country = current[i].getCountry();
		break;
	    }
	}
	return country;
    }

    void setWeekImage() {
    	int daily_date_id[] = {R.id.daily_date01, R.id.daily_date02, R.id.daily_date03, R.id.daily_date04, R.id.daily_date05, R.id.daily_date06, R.id.daily_date07};
    	int daily_icon_id[] = {R.id.daily_icon01, R.id.daily_icon02, R.id.daily_icon03, R.id.daily_icon04, R.id.daily_icon05, R.id.daily_icon06, R.id.daily_icon07};
    	int daily_min_id[] = {R.id.daily_temp_min01, R.id.daily_temp_min02, R.id.daily_temp_min03, R.id.daily_temp_min04, R.id.daily_temp_min05, R.id.daily_temp_min06, R.id.daily_temp_min07};
    	int daily_max_id[] = {R.id.daily_temp_max01, R.id.daily_temp_max02, R.id.daily_temp_max03, R.id.daily_temp_max04, R.id.daily_temp_max05, R.id.daily_temp_max06, R.id.daily_temp_max07};
    	int daiyl_symbol_id[] = {R.id.daily_symbol01, R.id.daily_symbol02, R.id.daily_symbol03, R.id.daily_symbol04, R.id.daily_symbol05, R.id.daily_symbol06, R.id.daily_symbol07};
    	int daiyl_unit_id[] = {R.id.daily_unit01, R.id.daily_unit02, R.id.daily_unit03, R.id.daily_unit04, R.id.daily_unit05, R.id.daily_unit06, R.id.daily_unit07};

        for (int i = 0 ; i < nWeeklyCnt; ++i) {
        	TextView daily_date = (TextView)findViewById(daily_date_id[i]);
        	ImageView daily_icon = (ImageView)findViewById(daily_icon_id[i]);
        	TextView daily_min = (TextView)findViewById(daily_min_id[i]);
        	TextView daily_max = (TextView)findViewById(daily_max_id[i]);
        	TextView daily_symbol = (TextView)findViewById(daiyl_symbol_id[i]);
        	TextView daily_unit = (TextView)findViewById(daiyl_unit_id[i]);
        	
    	    String szlWeekMax = weekly.getWeeklyTempMax(i);
    	    String szlWeekMin = weekly.getWeeklyTempMin(i);

    	    if(szlWeekMax == null) szlWeekMax = "--";
    	    if(szlWeekMin == null) szlWeekMin = "--";
    	    if (!TempFlag) {
    	    	szlWeekMax = Util.getFahrenheit(szlWeekMax);
    	    	szlWeekMin = Util.getFahrenheit(szlWeekMin);
    	    }

    	    daily_symbol.setText(TempSymbol + "/");
    	    daily_unit.setText(TempSymbol);
    	    
    	    if (weekly.getWeeklyDayOfWeek(i) == 6)
    	    	daily_date.setTextColor(Const.FONT_COLOR_BLUE);
    	    else if (weekly.getWeeklyDayOfWeek(i) == 0)
    	    	daily_date.setTextColor(Const.FONT_COLOR_RED);
    	    else
    	    	daily_date.setTextColor(Const.FONT_COLOR_WHITE);

    	    if (i != 0) {
    			if (DateFlag) {
    				daily_date.setText(weekly.getWeeklyDate(i) + "(" + Const.Week_day[weekly.getWeeklyDayOfWeek(i)] + ")");
    			} else {
    			    String[] datetemp;
    			    datetemp = weekly.getWeeklyDate(i).split("/");
    			    if (datetemp.length == 2) {
    			    	daily_date.setText(datetemp[1] + "/" + datetemp[0] + "(" + Const.Week_day[weekly.getWeeklyDayOfWeek(i)] + ")");
    			    } else {
    			    	daily_date.setText(weekly.getWeeklyDate(i) + "(" + Const.Week_day[weekly.getWeeklyDayOfWeek(i)] + ")");
    			    }
    			}
    	    }
    	    daily_icon.setImageResource(Util.getWeekIconID(rw, weekly.getWeeklyIcon(i)));
    	    daily_min.setText(szlWeekMin);
    	    daily_max.setText(szlWeekMax);
        }
        
        /*        
    	bmWeek = Bitmap.createBitmap(nWeeklyCnt * nWidth + (nWeeklyCnt - 1) * nGap + 2*nWeekX, 153, Config.RGB_565);

    	Canvas canvas = new Canvas(bmWeek);
    	int nColor = Color.rgb(5, 37, 102);
    	if (rw == null)
	    rw = getResources();

    	paint.setAntiAlias(true);
        paint.setColor(Const.FONT_COLOR_WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(20);

        Paint paint1 = new Paint();
        Paint paint2 = new Paint();

        paint1.setAntiAlias(true);
        paint1.setColor(nColor);
        paint1.setTextAlign(Paint.Align.LEFT);
        paint1.setTextSize(40);

        paint2.setAntiAlias(true);
        paint1.setColor(nColor);
        paint2.setTextAlign(Paint.Align.LEFT);
        paint2.setTextSize(32);

        float t1;

    	// 이동에 따라 주간 날씨 아이콘 다시 그리기
        for (int i = 0 ; i < nWeeklyCnt; ++i)
	    iconw[i] = BitmapFactory.decodeResource(rw, Util.getWeekIconID(rw, weekly.getWeeklyIcon(i)));

        canvas.drawBitmap(Week_BG, null, rc_Week, null);
        String szlWeekMax;
        String szlWeekMin;
        int posX = nWeekX;
        Rect rcw = new Rect(posX , nWeekY,
			    posX + nWidth, nWeekY + nHeight);

        for (int i = 0 ; i < nWeeklyCnt ; ++i){
	    szlWeekMax = weekly.getWeeklyTempMax(i);
	    szlWeekMin = weekly.getWeeklyTempMin(i);

	    if(szlWeekMax == null) szlWeekMax = "--";
	    if(szlWeekMin == null) szlWeekMin = "--";
	    if (!TempFlag) {
		szlWeekMax = Util.getFahrenheit(szlWeekMax);
		szlWeekMin = Util.getFahrenheit(szlWeekMin);
	    }
	    float t2 = 0, t3 = 0, t4 = 0, t5 = 0, startT = 0;
	    iconw[i] = BitmapFactory.decodeResource(rw, Util.getWeekIconID(rw, weekly.getWeeklyIcon(i)));
	    canvas.drawBitmap(iconw[i], null, rcw, null);
	    if (iconw[i] != null){
	    	iconw[i].recycle();
	    	iconw[i] = null;
	    }
	    rcw.left += nWidth + nGap;
	    rcw.right += nWidth + nGap;
	    paint.setTextSize(25);
	    paint.setTextAlign(Paint.Align.CENTER);
	    if (weekly.getWeeklyDayOfWeek(i) == 6)
		paint.setColor(Const.FONT_COLOR_BLUE);
	    else if (weekly.getWeeklyDayOfWeek(i) == 0)
		paint.setColor(Const.FONT_COLOR_RED);
	    else
		paint.setColor(Const.FONT_COLOR_WHITE);
	    if (0 == i)
		canvas.drawText("내일", 54 + i * 122, 37, paint);
	    else {
		if (DateFlag)
		    canvas.drawText(weekly.getWeeklyDate(i) + "(" + Const.Week_day[weekly.getWeeklyDayOfWeek(i)] + ")", 54 + i * 122, 37, paint);
		else {
		    String[] datetemp;
		    datetemp = weekly.getWeeklyDate(i).split("/");
		    if (datetemp.length == 2)
			canvas.drawText(datetemp[1] + "/" + datetemp[0] + "(" + Const.Week_day[weekly.getWeeklyDayOfWeek(i)] + ")", 54 + i * 122, 37, paint);
		    else
			canvas.drawText(weekly.getWeeklyDate(i) + "(" + Const.Week_day[weekly.getWeeklyDayOfWeek(i)] + ")", 54 + i * 122, 37, paint);
		}
	    }
	    paint.setTextSize(20);
	    paint.setTextAlign(Paint.Align.LEFT);
	    paint.setColor(Const.FONT_COLOR_WHITE);
	    paint1.setTextSize(16);
	    paint1.setColor(Const.FONT_COLOR_WHITE);
	    paint1.setTextAlign(Paint.Align.LEFT);
	    t2 = paint.measureText(szlWeekMax);
	    t3 = paint1.measureText(TempSymbol + "/");
	    t4 = paint.measureText(szlWeekMin);
	    t5 = paint1.measureText(TempSymbol);
	    t1 = t2 + t3 + t4 + t5;
	    startT = (55 + i * 122) - (t1 / 2);
	    canvas.drawText(szlWeekMax, startT, 148, paint);
	    canvas.drawText(TempSymbol + "/", startT + t2, 148, paint1);
	    canvas.drawText(szlWeekMin, startT + t2 + t3, 148, paint);
	    canvas.drawText(TempSymbol, startT + t2 + t3 + t4, 148, paint1);
	    //canvas.drawText(szWeekTempMin[i], 54 + i * 122, 719, paint);
        }
        imgWeekly.setImageBitmap(bmWeek);
       */
    }

    void setFooterImage() {
    	Calendar cl = Calendar.getInstance();
    	if (updateTime != 0)
	    cl.setTimeInMillis(updateTime);
        if (rw == null)
	    rw = getResources();
        Util.setFooter(footer, cl, TimeFlag, DateFlag);
    }

    private DialogInterface.OnDismissListener listener = new DialogInterface.OnDismissListener() {
	    public void onDismiss(DialogInterface dialog) {
    		Draw();
	    }
	};
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item){

	switch (item.getItemId()) {
        case 0:
        	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        	boolean network = Util.checkNetwork(manager);
        	
        	if(!network) {
        		
//        		LoadingAnimation.stop();
        	    progress.setVisibility(View.INVISIBLE);
        	    
				AlertDialog.Builder alert = null;
//		    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//		    		alert = new AlertDialog.Builder(showTodayWeather.this, AlertDialog.THEME_HOLO_DARK);
//		    	else
		    		alert = new AlertDialog.Builder(showTodayWeather.this);
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
	    UpdateData();
            return true;
        case 1:
	    if (!checkUseGPS())
		showDialog();
	    else {
		startUseGPS();
	    }
            return true;
        case 2:
        	if(/*LoadingAnimation.isRunning() ||*/ progress.getVisibility() == View.VISIBLE && isStop == false) {
    		    Toast.makeText(showTodayWeather.this,
    					   "업데이트 완료후 이용가능합니다.",
    					   Toast.LENGTH_SHORT).show();
    		    return true;
        	}
        	final ProgressDialog dialog;

        	dialog = new ProgressDialog(showTodayWeather.this);
        	dialog.setIndeterminate(true);
        	dialog.setCancelable(false);
        	dialog.setMessage("이미지 저장중..");
            dialog.closeOptionsMenu();
            dialog.show();
            
            final Handler sendhandler = new Handler() {
            	public void handleMessage(Message msg) {
            		if (msg.what == -1) {
        				AlertDialog.Builder alert = null;
//        		    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//        		    		alert = new AlertDialog.Builder(showTodayWeather.this, AlertDialog.THEME_HOLO_DARK);
//        		    	else
        		    		alert = new AlertDialog.Builder(showTodayWeather.this);
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
            		boolean isOk = Util.setCaptureAndSend(showTodayWeather.this, (View)topLayer, bmp);
            		dialog.dismiss();
            		if(!isOk) {
            			Message msg = sendhandler.obtainMessage(-1);
            			sendhandler.sendMessage(msg);
            		}
            	}
            });
            send.start();
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

    public void UpdateData() {
    	Thread background=new Thread(new Runnable() {
		public void run() {
		    try {
			Thread.sleep(100);
			Update();
			Message msg;
			if (saveData())
			    msg = handler.obtainMessage();
			else {
			    // try again
			    Update();
			    if (saveData())
				msg = handler.obtainMessage();
			    else {
				msg = handler.obtainMessage(-1);
			    }
			}
			handler.sendMessage(msg);
		    }
		    catch (Throwable t) {
			// just end the background thread
			Message msg = handler.obtainMessage(-1);
			handler.sendMessage(msg);
		    }
		}
	    });
	background.start();
//	LoadingAnimation.start();
	progress.setVisibility(View.VISIBLE);
    }

    public boolean checkUseGPS(){
    	boolean usegps = false;
    	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
    	usegps = prefs.getBoolean("USE_GPS", false);

    	return usegps;
    }

    public void startUseGPS() {
    	if (provider != null) {
    		if(loclistener == null)
    			loclistener = new DispLocListener() ;
    		locationManager.requestLocationUpdates(provider, 1000, 0, loclistener);
    	}

        final ProgressDialog dialog;

    	dialog = new ProgressDialog(showTodayWeather.this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setMessage("현재 위치의 날씨 정보를 확인중입니다.");
        dialog.closeOptionsMenu();
        dialog.show();

        Thread background2 = new Thread(new Runnable() {
		Message msg;
		public void run() {
			int retryCnt = 7;
		    try {
		    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
		    	boolean isMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
		    	boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		    	if(!isMobile && !isWifi) {
		    		dialog.dismiss();
		    		msg = toasthandler.obtainMessage(2);
					toasthandler.sendMessage(msg);
					return;
		    	}
		    	if(loclistener == null) {
		    		toasthandler.sendMessage(toasthandler.obtainMessage(4));
		    		return;
		    	}
		    	while(retryCnt >= 0 && dialog.isShowing()) {
					Thread.sleep(1000);
					if(dialog.isShowing())
						cityname = getCurrentPostion();
					if(cityname != null && cityname.length() > 0)
						break;
					else
						retryCnt --;
					
		    	}
			dialog.dismiss();
			msg = toasthandler.obtainMessage(-1);
			if (cityname != null && cityname.length() > 0) {
			    tempinfo = addNewCity(cityname);
			    if (tempinfo != null){
			    	Intent newintent = new Intent(showTodayWeather.this, showTodayWeather.class);
			    	newintent.putExtra("CityID", tempinfo.getCityID());
			    	newintent.putExtra("CityName", tempinfo.getCityName());
			    	newintent.putExtra("TimeZone", tempinfo.getTimeZone());
			    	newintent.putExtra("Index", tempinfo.getIndex());
//			    	Can't setCurrentTab in Thread.
//			    	((TabRoot)getParent()).setCurrentTab(newintent);
			    	Message msg1 = intenthandler.obtainMessage(100, newintent);
			    	intenthandler.sendMessage(msg1);
			    	locationManager.removeUpdates(loclistener);
			    } else {
			    	msg = toasthandler.obtainMessage(0);
			    	locationManager.removeUpdates(loclistener);
			    }
			} else
			    msg = toasthandler.obtainMessage(1);
				toasthandler.sendMessage(msg);
		    }
		    catch (Throwable t) {
			// just end the background thread
			dialog.dismiss();
			Message msg = handler.obtainMessage(1);
			handler.sendMessage(msg);
		    }
		    if(loclistener!= null)
		    	locationManager.removeUpdates(loclistener);
		}
	    });
	background2.start();
    }
    
	public final Handler intenthandler = new Handler()
	{
	    public void handleMessage(Message msg)
	    {
	    	if(msg.what == 100)
	    		((TabRoot)getParent()).setCurrentTab((Intent)msg.obj);
	    }
	};


    private static class DispLocListener implements LocationListener {
    	@Override public void onLocationChanged(Location location) {
	    // TextView를 업데이트 한다.
	    Loc = location;
        }
    	@Override public void onProviderDisabled(String provider) {}
    	@Override public void onProviderEnabled(String provider)  {}
    	@Override public void onStatusChanged(String provider, int status, Bundle extras) {}
    }


    public String getCurrentPostion(){
    	Geocoder gc = new Geocoder(getApplicationContext(), Locale.KOREAN);

        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null)
	    ;//Log.i("myTag", "location=" + location.toString());
        else {
	    if (Loc != null)
		location = Loc;
	    else
		;//Log.e("myTag", "location= NULL");
        }

        Double lat, lon;
        String addressString = "";
        String CityName = "";

        try{
	    lat = location.getLatitude();
	    lon = location.getLongitude();
	    //Log.d("myTag", "lat = " + lat + " long= " + lon);

	    List<Address>  addresses = gc.getFromLocation(lat, lon, 1);
	    //List<Address>  addresses = gc.getFromLocation(37.530704, 126.880898, 1); // 순천 지역
	    StringBuilder sb = new StringBuilder();

	    if (addresses.size() > 0) {
		Address address = addresses.get(0);

                for (int i = 0; i < address.getMaxAddressLineIndex(); ++i)
                    sb.append(address.getAddressLine(i)).append(", ");

                sb.append(address.getLocality()).append(",");
                sb.append(address.getCountryName());

                addressString = sb.toString();

                CityName = addressString;
	    }

        } catch (Exception e) {
	    // TODO: handle exception
	    //Log.e("myTag", "getCurrentPostion error=" + e.toString());
	}

        return CityName;
    }


    public void showDialog(){
		AlertDialog.Builder alert = null;
//    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    		alert = new AlertDialog.Builder(showTodayWeather.this, AlertDialog.THEME_HOLO_DARK);
//    	else
    		alert = new AlertDialog.Builder(showTodayWeather.this);
    	alert.setTitle("현재위치검색(GPS)");
    	alert.setMessage("위치 사용을 승인하시겠습니까?");

    	alert.setIcon(R.drawable.ic_dialog_menu_generic);

    	alert.setPositiveButton("예", new DialogInterface.OnClickListener() {
    		@Override
		    public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		    SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
		    prefs1.putBoolean("USE_GPS", true);
		    prefs1.commit();
		    startUseGPS();
		}
	    });

    	alert.setNegativeButton("아니요", new DialogInterface.OnClickListener() {

		@Override public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		}
	    });

    	alert.show();
    }

    final Handler toasthandler = new Handler() {
	    int msgid;
	    public void handleMessage(Message msg) {
        	msgid = msg.what;
        	//Log.d("myTag", "Message=" + msgid);
        	if(loclistener == null) {
//        		Log.d("myTag", "loclistener is null");
        		return;
        	}
        	if (msgid == 0) {
		    Toast.makeText(showTodayWeather.this,
				   "현재 위치의 날씨 정보가 없습니다.",
				   Toast.LENGTH_LONG).show();
        	} else if (msgid == 1) {
		    Toast.makeText(showTodayWeather.this,
				   "일시적으로 위치정보를 확인할 수 없습니다.",
				   Toast.LENGTH_LONG).show();
        	} else if (msgid == 2) {
    		    Toast.makeText(showTodayWeather.this,
    					   "네트워크에 연결할 수 없습니다.",
    					   Toast.LENGTH_LONG).show();
        	} else if (msgid == 4) {
    		    Toast.makeText(showTodayWeather.this,
    					   "위치정보를 확인할 수 없습니다.",
    					   Toast.LENGTH_LONG).show();
        	}
//		LoadingAnimation.stop();
		progress.setVisibility(View.INVISIBLE);
	    }
	};

    private BaseInfo addNewCity(String cityname) {
    	BaseInfo baseinfo;
    	String name = "";
    	String id = "";
    	String tz = "Asia/Seoul";
    	long idx;
    	int nCityCnt = 0;
    	String[] CityID;
    	CityID = new String[8];

    	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
    	nCityCnt = prefs.getInt(Const.CITY_CNT, 0);

    	String strTemp;
    	String[] szTemp;



    	idx = nCityCnt;

    	for (int i = 0 ; i < Const.CITY_VALUE.length ; i++)
	    if (cityname.matches(".*" + Const.CITY_VALUE[i][0] + ".*")) {
		name = Const.CITY_VALUE[i][0];
		id = Const.CITY_VALUE[i][1];
	    }

    	for (int i = 0 ; i < nCityCnt ; i ++){
	    strTemp = prefs.getString(Const.CITY_LIST + i, "");

	    szTemp = strTemp.split("\t");

	    if (szTemp[0].equals(id)) {
		baseinfo = new BaseInfo(id, name, tz, i);
		//Log.d("myTag", "Already in List");
		return baseinfo;
	    }
    	}

    	if (id == null || id.length() == 0)
	    return null;

    	//Log.d("myTag", "id=" + id + " name=" + name);

    	CityID[0] = id;
    	CityID[1] = name;
    	CityID[2] = tz;
    	CityID[3] = "--";
    	CityID[4] = "01";
    	CityID[5] = "-999";
    	CityID[6] = "-999";
	CityID[7] = "-999";

	SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
	String Temp = "";
	for (int j = 0 ; j < 8 ; ++j)
	    Temp = Temp + CityID[j] + "\t";

	prefs1.putString(Const.CITY_LIST + Long.toString(idx), Temp);

	prefs1.putInt(Const.CITY_CNT, (int)idx + 1);
	prefs1.commit();

	prefs = getSharedPreferences(Const.PREFS_NAME, 0);
	nCityCnt = prefs.getInt(Const.CITY_CNT, 0);

	String [][] CityID2 = new String[nCityCnt][8];
	for (int i = 0 ; i < nCityCnt ; i++){
	    Temp = prefs.getString(Const.CITY_LIST + i, "");
	    //Log.d("myTag", "[" + Temp + "]");
	    szTemp = Temp.split("\t");
	    for (int j = 0 ; j < 8 ; j++){
		//Log.d("myTag", "szTemp[" + j + "]=" + szTemp[j]);
		if (szTemp[j].equals("") || szTemp[j] == null || szTemp[j].equals("\n"))
		    CityID2[i][j] = "--";
		else
		    CityID2[i][j] = szTemp[j];
	    }
	}

	baseinfo = new BaseInfo(id, name, tz, (int)idx);

	return baseinfo;
    }


    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0,0, "업데이트").setIcon(R.drawable.ic_menu_update);
        menu.add(0, 1,0, "현재위치검색").setIcon(R.drawable.more_panel_icon_gps);
        menu.add(0, 2,0, "공유하기").setIcon(R.drawable.ic_menu_share);
        return true;
    }

    @Override public boolean onTouchEvent(MotionEvent event)
    {
    	int nsx = (int) event.getX();
    	int nsy = (int) event.getY();
    	Intent intent;

    	switch(event.getAction())
	    {
	    case MotionEvent.ACTION_MOVE :

		if (Math.abs(nsx - x) < 40 )
		    return true;
		if (nButtonState == Const.WEATHER_FOCUS_BTN.WEATHER_ICON_AREA) {
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_NONE;
		    if (rc_Icon_btn.contains(nsx, nsy - 38)){
			SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
			int nCityCnt;
			int nNextID;
			String NextCityID;
			String NextCityName;
			String NextTimeZone;
			String Temp;
			String[] TempArray;
			int direction;
			nCityCnt = prefs.getInt(Const.CITY_CNT, 0);
			if (nsx - x > 0)
			    {	// Prev
				if ((int)info.getIndex() == 0)
				    nNextID = nCityCnt - 1;
				else
				    nNextID = (int)info.getIndex() - 1;
				direction = TabHostHide.DIRECTION_RIGHT;
			    }
			else
			    {	// Next
				if ((int)info.getIndex() == nCityCnt - 1)
				    nNextID = 0;
				else
				    nNextID = (int)info.getIndex() + 1;
				direction = TabHostHide.DIRECTION_LEFT;
			    }
			Temp = prefs.getString(Const.CITY_LIST + nNextID, "");

//			Log.d("myTag", "Index=" + nNextID + " data=" + Temp);

			if(nCityCnt > 1) {
				Intent intent1 = new Intent(showTodayWeather.this, showTodayWeather.class);
				TempArray = Temp.split("\t");
	
				intent1.putExtra("CityID", TempArray[0]);
				intent1.putExtra("CityName", TempArray[1]);
				intent1.putExtra("TimeZone", TempArray[2]);
				intent1.putExtra("Index", nNextID);
	
				((TabRoot)getParent()).setCurrentTab(intent1, direction);
			}

//			if (nsx - x > 0)
//			    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		    }
		}

		break;
	    case MotionEvent.ACTION_DOWN:
//		if (rc_GPS.contains(nsx, nsy - 38 - 96)){
////		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_GPS_BTN;
////		    setContentImage();
//		} else if (rc_LiveCam.contains(nsx, nsy - 38 - 96)){
////		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_LIVECAM_BTN;
////		    if (isLiveCam)
////			setContentImage();
//		} else if (rc_Week.contains(nsx, nsy - 38)) {
////		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_WEEK_AREA;
////		    x = nsx;
////		    y = nsy;
//		} else if (rc_Weather.contains(nsx, nsy -38))
//		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_WEATHER_BTN;
//		else 
		if (rc_Icon_btn.contains(nsx, nsy - 38)) {
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_ICON_AREA;
		    x = nsx;
		    y = nsy;
		}
		break;

	    case MotionEvent.ACTION_UP:
		if (nButtonState == Const.WEATHER_FOCUS_BTN.WEATHER_WEATHER_BTN){
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_NONE;
		    if (rc_Weather.contains(nsx, nsy - 38)){
			// 다른사이트로 이동?
		    }
		} else if (nButtonState == Const.WEATHER_FOCUS_BTN.WEATHER_THEME_BTN){
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_NONE;
//		    setTabImage();
		} else if (nButtonState == Const.WEATHER_FOCUS_BTN.WEATHER_LIVECAM_TAB){
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_NONE;
//		    setTabImage();
		} else if (nButtonState == Const.WEATHER_FOCUS_BTN.WEATHER_GPS_BTN){
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_NONE;
//		    setContentImage();
//		    if (rc_GPS.contains(nsx, nsy - 38 - 96)){
//			if (!checkUseGPS())
//			    showDialog();
//			else
//			    startUseGPS();
//		    }
		} else if (nButtonState == Const.WEATHER_FOCUS_BTN.WEATHER_LIVECAM_BTN){
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_NONE;
//		    setContentImage();
//		    if (rc_LiveCam.contains(nsx, nsy - 38 - 96) && isLiveCam){
//			intent = new Intent(showTodayWeather.this, ViewLiveCam.class);
//			intent.putExtra("CityID", info.getCityID());
//			intent.putExtra("CityName", info.getCityName());
//			intent.putExtra("TimeZone", info.getTimeZone());
//			intent.putExtra("Index", info.getIndex());
//			intent.putExtra("CamID", LiveCamID);
//			((TabRoot)getParent()).setCurrentTab(intent);
//		    }
		} else if (nButtonState == Const.WEATHER_FOCUS_BTN.WEATHER_ICON_AREA) {
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_NONE;
		    if (rc_Icon_btn.contains(nsx, nsy - 38) && isKorea){
		    	imgContent.playSoundEffect(0);
			intent = new Intent(showTodayWeather.this, CustomDialog.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
			intent.putExtra("CityID", info.getCityID());
			intent.putExtra("CityName", info.getCityName());
			intent.putExtra("TimeZone", info.getTimeZone());
			intent.putExtra("Index", info.getIndex());
			intent.putExtra("IsToday", true);
			startActivity(intent);
			//((TabRoot)getParent()).setCurrentTab(intent);
		    }
		} else if (nButtonState == Const.WEATHER_FOCUS_BTN.WEATHER_WEEK_AREA) {
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_NONE;
		} else if (nButtonState == Const.WEATHER_FOCUS_BTN.WEATHER_ICON_AREA)
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_NONE;
		break;
	    }
    	return true;
    }


    public void getSavedData() {
    	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
    	String[] TempArray;
    	String Temp;



    	Temp = prefs.getString(Const.Today_Today + info.getCityID(), "");
    	//Log.d("myTag", "Data=" + Temp);

    	if (Temp.equals("") || Temp.length() <= 0){
	    today.setTodayIcon("01");
	    today.setTodayTempMax("--");
	    today.setTodayTempMin("--");
	    today.setTodayPop("--");
	    today.setTodayVisi("--");
	    today.setTodayTempCur("--");
	    today.setTodayHum("--");
	    today.setTodayWindDir("--");
	    today.setTodayWindSpeed("--");
	    today.setTodayTempFeel("--");
	    today.setTodayPress("--");
	    today.setTodayIndex1("--");
	    today.setTodayIndex2("--");
	    today.setTodayExplain("--");
	    today.setTodayUpdateTime("0");
    	} else {
	    TempArray = Temp.split("\t");
	    today.setTodayIcon(TempArray[0]);
	    today.setTodayTempMax(TempArray[1]);
	    today.setTodayTempMin(TempArray[2]);
	    today.setTodayPop(TempArray[3]);
	    today.setTodayVisi(TempArray[4]);
	    today.setTodayTempCur(TempArray[5]);
	    today.setTodayHum(TempArray[6]);
	    today.setTodayWindDir(TempArray[7]);
	    today.setTodayWindSpeed(TempArray[8]);
	    today.setTodayTempFeel(TempArray[9]);
	    today.setTodayPress(TempArray[10]);
	    today.setTodayIndex1(TempArray[11]);
	    today.setTodayIndex2(TempArray[12]);
	    today.setTodayExplain(TempArray[13]);
	    today.setTodayUpdateTime(TempArray[14]);
    	}

    	try{
	    updateTime = Long.parseLong(today.getTodayUpdateTime());
    	}catch(NumberFormatException e) {
	    updateTime = Calendar.getInstance().getTimeInMillis();
    	}

    	Temp = prefs.getString(Const.Today_Hourly + info.getCityID(), "");
    	if (Temp.equals("") || Temp.length() <= 0){
	    for (int i = 0, j = 0 ; i < 8 ; i++, j++)
		hourly.setHourlyIcon("01", i);
	    for (int i = 0, j = 8 ; i < 8 ; i++, j++)
		hourly.setHourlyTemp("--", i);
	    for (int i = 0, j = 16 ; i < 8 ; i++, j++)
		hourly.setHourlyPop("--", i);
    	} else {
	    TempArray = Temp.split("\t");
	    for (int i = 0, j = 0 ; i < 8 ; i++, j++)
		try{
		    hourly.setHourlyIcon(TempArray[j], i);
		} catch(ArrayIndexOutOfBoundsException e) {
		    //hourly.setHourlyIcon("01", i);
		}
	    for (int i = 0, j = 8 ; i < 8 ; i++, j++)
		try{
		    hourly.setHourlyTemp(TempArray[j], i);
		} catch(ArrayIndexOutOfBoundsException e) {
		    //hourly.setHourlyTemp("--", i);
		}
	    for (int i = 0, j = 16 ; i < 8 ; i++, j++)
		try{
		    hourly.setHourlyPop(TempArray[j], i);
		} catch(ArrayIndexOutOfBoundsException e) {
		    //hourly.setHourlyPop("--", i);
		}
    	}

    	Temp = prefs.getString(Const.Today_Weekly + info.getCityID(), "");
    	if (Temp.equals("") || Temp.length() <= 0){
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
    	} else {
	    TempArray = Temp.split("\t");
	    for (int i = 0, j = 0 ; i < 10 ; i++, j++)
		try{
		    weekly.setWeeklyDate(TempArray[j], i);
		} catch(ArrayIndexOutOfBoundsException e) {
		    //weekly.setWeeklyDate("--", i);
		}
	    for (int i = 0, j = 10 ; i < 10 ; i++, j++)
		try{
		    weekly.setWeeklyDayOfWeek(Integer.parseInt(TempArray[j]), i);
		} catch(ArrayIndexOutOfBoundsException e) {
		    //weekly.setWeeklyDayOfWeek(1, i);
		}
	    for (int i = 0, j = 20 ; i < 10 ; i++, j++)
		try{
		    weekly.setWeeklyIcon(TempArray[j], i);
		} catch(ArrayIndexOutOfBoundsException e) {
		    //weekly.setWeeklyIcon("01", i);
		}
	    for (int i = 0, j = 30 ; i < 10 ; i++, j++)
		try{
		    weekly.setWeeklyTempMax(TempArray[j], i);
		} catch(ArrayIndexOutOfBoundsException e) {
		    //weekly.setWeeklyTempMax("--", i);
		}
	    for (int i = 0, j = 40 ; i < 10 ; i++, j++)
		try{
		    weekly.setWeeklyTempMin(TempArray[j], i);
		} catch(ArrayIndexOutOfBoundsException e) {
		    //weekly.setWeeklyTempMin("--", i);
		}
    	}
    	LiveCamID = prefs.getString(Const.LIVECAMID + info.getCityID(), "");
    }

    public boolean saveData(){
    	//Log.d("myTag", "Start SaveData");
    	Calendar cl = Calendar.getInstance();
    	String Temp = "";
    	//cl.setTimeZone(TimeZone.getTimeZone(info.getTimeZone()));

    	SharedPreferences.Editor prefs = getSharedPreferences(Const.PREFS_NAME, 0).edit();

    	//prefs.putLong(Const.UPDATE_TIME_TODAY + info.getCityID(), cl.getTimeInMillis());
    	if (today.getTodayIcon() == null || today.getTodayIcon().length() <= 0){
	    //Log.e("myTag", "Error Save TodayIcon");
	    return false;
    	}
    	Temp = today.getTodayIcon() + "\t" + today.getTodayTempMax() + "\t" + today.getTodayTempMin() + "\t" +
	    today.getTodayPop() + "\t" + today.getTodayVisi() + "\t" + today.getTodayTempCur() + "\t" +
	    today.getTodayHum() + "\t" + today.getTodayWindDir() + "\t" + today.getTodayWindSpeed() + "\t" +
	    today.getTodayTempFeel() + "\t" + today.getTodayPress() + "\t" + today.getTodayIndex1() + "\t" +
	    today.getTodayIndex2() + "\t" + today.getTodayExplain() + "\t" + cl.getTimeInMillis() + "\t";
    	prefs.putString(Const.Today_Today + info.getCityID(), Temp);

    	// Save 6Hourly Data
    	Temp = "";
    	// Icon
    	if (hourly.getHourlyIcons(0) == null || hourly.getHourlyIcons(0).length() <= 0) {
	    //Log.e("myTag", "Error Save HourlyIcon");
	    return false;
    	}
    	for (int i = 0 ; i < 8 ; ++i)
	    Temp = Temp + hourly.getHourlyIcons(i) + "\t";
    	// Temp
    	for (int i = 0 ; i < 8 ; ++i)
	    Temp = Temp + hourly.getHourlyTemp(i) + "\t";
    	// 강수확률
    	for (int i = 0 ; i < 8 ; ++i)
	    Temp = Temp + hourly.getHourlyPop(i) + "\t";
    	prefs.putString(Const.Today_Hourly + info.getCityID(), Temp);

    	// Save Weekly Data
    	Temp = "";
    	// date
    	if (weekly.getWeeklyDate(0) == null || weekly.getWeeklyDate(0).length() <= 0) {
	    //Log.e("myTag", "Error Save Weeklyate");
	    return false;
    	}
    	for (int i = 0 ; i < 10 ; ++i)
	    Temp = Temp + weekly.getWeeklyDate(i) + "\t";
    	// day of week
    	for (int i = 0 ; i < 10 ; ++i)
	    Temp = Temp + weekly.getWeeklyDayOfWeek(i) + "\t";
    	// WX_Icon
    	for (int i = 0 ; i < 10 ; ++i)
	    Temp = Temp + weekly.getWeeklyIcon(i) + "\t";
    	// TempMax
    	for (int i = 0 ; i < 10 ; ++i)
	    Temp = Temp + weekly.getWeeklyTempMax(i) + "\t";
    	// TempMin
    	for (int i = 0 ; i < 10 ; ++i)
	    Temp = Temp + weekly.getWeeklyTempMin(i) + "\t";
    	prefs.putString(Const.Today_Weekly + info.getCityID(), Temp);

    	prefs.putString(Const.LIVECAMID + info.getCityID(), LiveCamID);

    	prefs.commit();

    	//Log.d("myTag", "End SaveData");
    	return true;
    }

    public void ShowUpdateAgain() {
		AlertDialog.Builder alert = null;
//    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    		alert = new AlertDialog.Builder(getParent(), AlertDialog.THEME_HOLO_DARK);
//    	else
    		alert = new AlertDialog.Builder(getParent());
    	if(alert == null)
    		return;
    	alert.setTitle("날씨정보 업데이트 실패");
    	alert.setIcon(R.drawable.ic_dialog_menu_generic);
    	//alert.setMessage("날씨 정보 업데이트에 실패하였습니다. 다시 시도해 주십시오.");
    	Context mContext = getApplicationContext();
//    	Context mContext = this;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.today_popup,(ViewGroup) findViewById(R.id.showtoday_root));

        alert.setView(layout);

    	alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
    		@Override
		    public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		}
	    });
    	alert.setNegativeButton("재시도", new DialogInterface.OnClickListener() {
    		@Override
		    public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		    UpdateData();
		}
	    });

    	alert.show();
    }

    public void Update() {
	// TODO Auto-generated method stub
	String URL;
	InputStream fis = null;
	String[] Temp;
	int[] nDayOfWeek = new int[10];
	
	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
	boolean network = Util.checkNetwork(manager);
	
	if(!network) {
		
//		LoadingAnimation.stop();
	    progress.setVisibility(View.INVISIBLE);
	    
		AlertDialog.Builder alert = null;
//    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    		alert = new AlertDialog.Builder(showTodayWeather.this, AlertDialog.THEME_HOLO_DARK);
//    	else
    		alert = new AlertDialog.Builder(showTodayWeather.this);
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

	//Log.d("myTag", "Start Update");
	URL = Const.URL_TODAY + "region=" + info.getCityID();
	try {
		fis = Util.getByteArrayFromURL(URL);
	} catch (UnknownHostException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}

	if (fis == null) {
	    //Log.e("myTag", "Url = " + URL);
	    today.setTodayIcon(null);
	    today.setTodayTempMax(null);
	    today.setTodayTempMin(null);
	    today.setTodayPop(null);
	    today.setTodayVisi(null);
	    today.setTodayTempCur(null);
	    today.setTodayHum(null);
	    today.setTodayWindDir(null);
	    today.setTodayWindSpeed(null);
	    today.setTodayTempFeel(null);
	    today.setTodayPress(null);
	    today.setTodayIndex1(null);
	    today.setTodayIndex2(null);
	    today.setTodayExplain(null);
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

	Calendar cl = Calendar.getInstance();

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
    	today.setTodayUpdateTime(Long.toBinaryString(cl.getTimeInMillis()));

	URL = Const.URL_HOURLY + "region=" + info.getCityID();
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
	    //Log.e("myTag", "Url = " + URL);
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

	URL = Const.URL_WEEK + "region=" + info.getCityID();
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
	    //Log.e("myTag", "Url = " + URL);
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
	for (int i = 0 ; i < 10 ; ++i)
	    try {
		nDayOfWeek[i] = Integer.parseInt(Temp[i]);
	    } catch(Exception e){
		nDayOfWeek[i] = 0;
	    }
	weekly.setWeeklyDayOfWeek(nDayOfWeek);

	Temp = Weekly.geticon().toString().split("\t");
	weekly.setWeeklyIcon(Temp);

	Temp = Weekly.gettempmax().toString().split("\t");
	weekly.setWeeklyTempMax(Temp);

	Temp = Weekly.gettempmin().toString().split("\t");
	weekly.setWeeklyTempMin(Temp);
	
	Weekly.ClearMemory();

	URL = getString(R.string.livecamalive) + "?region=" + info.getCityID();

	InputStream fis2 = null;

	BufferedReader in;
	String data = "";

	try {
	    fis2 = Util.getByteArrayFromURL(URL);
	    in = new BufferedReader(new InputStreamReader(fis2));
	    if (in == null)
	    	data = null;
	    data = in.readLine();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    //Log.e("myTag", e.toString());
	    data = null;
	}

	//Log.d("myTag", "LiveCamID, data = " + data);
	LiveCamID = data;
	if(fis != null) {
		try {
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		fis = null;
	}

	if(fis2 != null) {
		try {
			fis2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		fis2 = null;
	}
	//Log.d("myTag", "End Update");
    }

    @Override public void onBackPressed()
    {
	((TabRoot)getParent()).onBackPressed();
    }
}

//class ParseTodayWeather<XmlParser> extends DefaultHandler {
//    private String szRegion_id;
//    private String szPointName;
//    private String szIcon;
//    private String szTempMax;
//    private String szTempMin;
//    private String szPop;
//    private String szVisi;
//    private String szTempCur;
//    private String szHum;
//    private String szWindDir;
//    private String szWindSpeed;
//    private String szTempFeel;
//    private String szPress;
//    private String szIndex1;
//    private String szIndex2;
//    private String szExplain;
//
//    private boolean region_id = false;
//    private boolean pointname = false;
//    private boolean icon = false;
//    private boolean tempmin = false;
//    private boolean tempmax = false;
//    private boolean pop = false;
//    private boolean visi = false;
//    private boolean tempcur = false;
//    private boolean hum = false;
//    private boolean winddir = false;
//    private boolean windspeed = false;
//    private boolean tempfeel = false;
//    private boolean press = false;
//    private boolean index1 = false;
//    private boolean index2 = false;
//    private boolean explain = false;
//
//    private XmlParser xp;
//
//    public ParseTodayWeather(XmlParser xp)
//	{
//	    this.xp = xp;
//	}
//    public void startElement(String uri, String localName, String qName, Attributes atts)
//    {
//	if (localName.equals("region_id"))
//	    region_id = true;
//	else if (localName.equals("PointName"))
//	    pointname = true;
//	else if (localName.equals("WX_icon"))
//	    icon = true;
//	else if (localName.equals("TempMax"))
//	    tempmax = true;
//	else if (localName.equals("TempMin"))
//	    tempmin = true;
//	else if (localName.equals("PopAM"))
//	    pop = true;
//	else if (localName.equals("Visi"))
//	    visi = true;
//	else if (localName.equals("TempCur"))
//	    tempcur = true;
//	else if (localName.equals("Hum"))
//	    hum = true;
//	else if (localName.equals("Wnddir"))
//	    winddir = true;
//	else if (localName.equals("Wndspd"))
//	    windspeed = true;
//	else if (localName.equals("TempFeel"))
//	    tempfeel = true;
//	else if (localName.equals("press"))
//	    press = true;
//	else if (localName.equals("index1"))
//	    index1 = true;
//	else if (localName.equals("index2"))
//	    index2 = true;
//	else if (localName.equals("explain"))
//	    explain = true;
//    }
//    /*
//      public void endElement(String uri, String localName, String qName)
//      {
//      if (localName.equals("person"))
//      {
//      xp.updateTextView(names.toString()+"\n"+comps.toString()+"\n"+departs.toString());
//      }
//      }
//    */
//    public void characters(char[] chars, int start, int leng)
//    {
//	if (region_id)
//	    {
//		region_id = false;
//		szRegion_id = new String(chars, start, leng);
//	    }
//	else if (pointname)
//	    {
//		pointname = false;
//		szPointName = new String(chars, start, leng);
//	    }
//	else if (icon)
//	    {
//		icon = false;
//		szIcon = new String(chars, start, leng);
//	    }
//	else if (tempmax)
//	    {
//		tempmax = false;
//		szTempMax = new String(chars, start, leng);
//	    }
//	else if (tempmin)
//	    {
//		tempmin = false;
//		szTempMin = new String(chars, start, leng);
//	    }
//	else if (pop)
//	    {
//		pop = false;
//		szPop = new String(chars, start, leng);
//	    }
//	else if (visi)
//	    {
//		visi = false;
//		szVisi = new String(chars, start, leng);
//	    }
//	else if (hum)
//	    {
//		hum = false;
//		szHum = new String(chars, start, leng);
//	    }
//	else if (winddir)
//	    {
//		winddir = false;
//		szWindDir = new String(chars, start, leng);
//	    }
//	else if (windspeed)
//	    {
//		windspeed = false;
//		szWindSpeed = new String(chars, start, leng);
//	    }
//	else if (tempcur)
//	    {
//		tempcur = false;
//		szTempCur = new String(chars, start, leng);
//	    }
//	else if (tempfeel)
//	    {
//		tempfeel = false;
//		szTempFeel = new String(chars, start, leng);
//	    }
//	else if (press)
//	    {
//		press = false;
//		szPress = new String(chars, start, leng);
//	    }
//	else if (index1)
//	    {
//		index1 = false;
//		szIndex1 = new String(chars, start, leng);
//	    }
//	else if (index2)
//	    {
//		index2 = false;
//		szIndex2 = new String(chars, start, leng);
//	    }
//	else if (explain)
//	    {
//		explain = false;
//		szExplain = new String(chars, start, leng);
//	    }
//    }
//
//    public String getregion_id() { return (null == szRegion_id ? "" : szRegion_id); }
//    public String getpointname() { return (null == szPointName ? "" : szPointName); }
//    public String geticon()      { return (null == szIcon ?      "" : szIcon); }
//    public String gettempmax()   { return (null == szTempMax ?   "" : szTempMax); }
//    public String gettempmin()   { return (null == szTempMin ?   "" : szTempMin); }
//    public String getpop()       { return (null == szPop ?       "" : szPop); }
//    public String getvisi()      { return (null == szVisi ?      "" : szVisi); }
//    public String gettempcur()   { return (null == szTempCur ?   "" : szTempCur); }
//    public String gethum()       { return (null == szHum ?       "" : szHum); }
//    public String getwnddir()    { return (null == szWindDir ?   "" : szWindDir); }
//    public String getwndspd()    { return (null == szWindSpeed ? "" : szWindSpeed); }
//    public String gettempfeel()  { return (null == szTempFeel ?  "" : szTempFeel); }
//    public String getpress()     { return (null == szPress ?     "" : szPress); }
//    public String getindex1()    { return (null == szIndex1 ?    "" : szIndex1); }
//    public String getindex2()    { return (null == szIndex2 ?    "" : szIndex2); }
//    public String getexplain()   { return (null == szExplain ?   "" : szExplain); }
//}

//class Parse6HourlyWeather<XmlParser> extends DefaultHandler {
//    private StringBuffer sbRegion_id	= new StringBuffer();
//    private StringBuffer sbIcon = new StringBuffer();
//    private StringBuffer sbTemp = new StringBuffer();
//    private StringBuffer sbPop = new StringBuffer();
//
//    private boolean region_id = false;
//    private boolean icon = false;
//    private boolean temp = false;
//    private boolean pop = false;
//
//    private XmlParser xp;
//
//    public Parse6HourlyWeather(XmlParser xp)
//	{
//	    this.xp		=		xp;
//	}
//    public void startElement(String uri, String localName, String qName, Attributes atts)
//    {
//	if (localName.equals("region_id"))
//	    region_id = true;
//	else if (localName.equals("icon"))
//	    icon = true;
//	else if (localName.equals("temp"))
//	    temp = true;
//	else if (localName.equals("pop"))
//	    pop = true;
//    }
//    /*
//      public void endElement(String uri, String localName, String qName)
//      {
//      if (localName.equals("person"))
//      {
//      xp.updateTextView(names.toString()+"\n"+comps.toString()+"\n"+departs.toString());
//      }
//      }
//    */
//    public void characters(char[] chars, int start, int leng)
//    {
//	if (region_id)
//	    {
//		region_id = false;
//		sbRegion_id.append(chars, start, leng);
//		sbRegion_id.append('\t');
//	    }
//	else if (icon)
//	    {
//		icon = false;
//		sbIcon.append(chars, start, leng);
//		sbIcon.append('\t');
//	    }
//	else if (temp)
//	    {
//		temp = false;
//		sbTemp.append(chars, start, leng);
//		sbTemp.append('\t');
//	    }
//	else if (pop)
//	    {
//		pop = false;
//		sbPop.append(chars, start, leng);
//		sbPop.append('\t');
//	    }
//    }
//
//    public StringBuffer getregion_id() { return sbRegion_id; }
//    public StringBuffer geticon()      { return sbIcon; }
//    public StringBuffer gettemp()      { return sbTemp; }
//    public StringBuffer getpop()       { return sbPop; }
//}

//class ParseWeeklyWeather<XmlParser> extends DefaultHandler {
//    private StringBuffer sbRegion_id = new StringBuffer();
//    private StringBuffer sbIcon = new StringBuffer();
//    private StringBuffer sbDate = new StringBuffer();
//    private StringBuffer sbDayofWeek = new StringBuffer();
//    private StringBuffer sbTempMin = new StringBuffer();
//    private StringBuffer sbTempMax = new StringBuffer();
//
//
//    private boolean region_id = false;
//    private boolean date = false;
//    private boolean dayofweek = false;
//    private boolean icon = false;
//    private boolean tempmin = false;
//    private boolean tempmax = false;
//    private XmlParser xp;
//
//    public ParseWeeklyWeather(XmlParser xp)
//	{
//	    this.xp		=		xp;
//	}
//    public void startElement(String uri, String localName, String qName, Attributes atts)
//    {
//	if (localName.equals("id"))
//	    region_id = true;
//	else if (localName.equals("wd"))
//	    date = true;
//	else if (localName.equals("dow"))
//	    dayofweek = true;
//	else if (localName.equals("icon"))
//	    icon = true;
//	else if (localName.equals("TempMax"))
//	    tempmax = true;
//	else if (localName.equals("TempMin"))
//	    tempmin = true;
//    }
//
//    public void characters(char[] chars, int start, int leng)
//    {
//	if (region_id)
//	    {
//		region_id = false;
//		sbRegion_id.append(chars, start, leng);
//		sbRegion_id.append('\t');
//	    }
//	else if (date)
//	    {
//		date = false;
//		sbDate.append(chars, start, leng);
//		sbDate.append('\t');
//	    }
//	else if (dayofweek)
//	    {
//		dayofweek = false;
//		sbDayofWeek.append(chars, start, leng);
//		sbDayofWeek.append('\t');
//	    }
//	else if (icon)
//	    {
//		icon = false;
//		sbIcon.append(chars, start, leng);
//		sbIcon.append('\t');
//	    }
//	else if (tempmin)
//	    {
//		tempmin = false;
//		sbTempMin.append(chars, start, leng);
//		sbTempMin.append('\t');
//	    }
//	else if (tempmax)
//	    {
//		tempmax = false;
//		sbTempMax.append(chars, start, leng);
//		sbTempMax.append('\t');
//	    }
//    }
//
//    public StringBuffer getregion_id() { return sbRegion_id; }
//    public StringBuffer getdate()      { return sbDate; }
//    public StringBuffer getdayofweek() { return sbDayofWeek; }
//    public StringBuffer geticon()      { return sbIcon; }
//    public StringBuffer gettempmin()   { return sbTempMin; }
//    public StringBuffer gettempmax()   { return sbTempMax; }
//    
//    public void ClearMemory() {
//        sbRegion_id = null;
//        sbIcon = null;
//        sbDate = null;
//        sbDayofWeek = null;
//        sbTempMin = null;
//        sbTempMax = null;
//    	
//    }
//}
