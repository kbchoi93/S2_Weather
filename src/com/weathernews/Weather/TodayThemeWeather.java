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

import com.weathernews.Weather.Const.WEATHER_FOCUS_BTN;
import com.weathernews.Weather.ThemeList.List2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TodayThemeWeather extends Activity
{
    private ThemeInfo info;
    private TodayData today;
    private HourlyData hourly;
    private WeeklyData weekly;
    private long updateTime;
    private ImageView imgContent;
//    private ImageView imgWeekly;
    private View footer;
    private boolean isStop = false;

    private int x = 0;
    private int y = 0;
    private Bitmap[] iconw = new Bitmap[10];
    private static Bitmap[] icon = new Bitmap[5];
    private Bitmap[] Hourly = new Bitmap[4];
//    private Bitmap CityNameBG;
//    private Bitmap Today;
    private Bitmap Week_BG;
//    private Bitmap PinpointBG;
//    private Bitmap Logo;
//    private Bitmap Logo_BG;
    private Paint paint = new Paint();
    private int [][] WeatherIcon;

    final static int nWeekX = 4;
    final static int nWeekY = 37;
    final static int nWidth = 100;
    final static int nHeight = 100;
    final static int nGap = 22;
    private Resources rw;
    private int nCount = 0;
    private WEATHER_FOCUS_BTN nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_NONE;

    private Rect[] rc = new Rect[5];
    private Rect rc_Week = new Rect(0, 570, 480, 724);
    private Rect rc_Icon_btn = new Rect(0, 0, 720, 850);
    private Rect rc_Weather = new Rect(0, 0, 160, 96);
    private Rect rc_LiveCam = new Rect(320, 0, 480, 96);
    private Rect rc_GPS = new Rect(400, 77, 470, 147);


    private String TempSymbol = "???";


    private ProgressBar progress;

    private boolean DateFlag;
    private boolean TimeFlag;
    private boolean TempFlag;

    private LinearLayout topLayer;

    private Bitmap bm;
    private Bitmap bmWeek;

    private ImageButton gpsbtn;
    private ImageButton livebtn;
    private int nWeeklyCnt = 7;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Log.d("myTag", "CityName=" + info.getCityName());
        setContentView(R.layout.showtodayweather);
        
        initValues();

        paint.setAntiAlias(true);

        Draw();

        progress.setVisibility(View.INVISIBLE);

        Thread background=new Thread(new Runnable() {
		public void run() {
			ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
			boolean network = Util.checkNetwork(manager);
			
			if(!network) {
		    	return;
			}
		    try {
			Thread.sleep(100);
			Update();
			Message msg;
			if (saveData())
			    msg = handler.obtainMessage();
			else {
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
			//Log.e("myTag", "error = " + t.toString());
			Message msg = handler.obtainMessage(-1);
			handler.sendMessage(msg);
		    }
		}
	    });
        if(Calendar.getInstance().getTimeInMillis() - updateTime > 1800000l) {
        	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        	boolean network = Util.checkNetwork(manager);
        	
        	if(!network) {
        		
//        		LoadingAnimation.stop();
        	    progress.setVisibility(View.INVISIBLE);
        	    
    			AlertDialog.Builder alert = null;
//    	    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    	    		alert = new AlertDialog.Builder(TodayThemeWeather.this, AlertDialog.THEME_HOLO_DARK);
//    	    	else
    	    		alert = new AlertDialog.Builder(TodayThemeWeather.this);
            	alert.setTitle("???????????? ??????");
            	alert.setIcon(R.drawable.ic_dialog_menu_generic);
            	alert.setMessage("???????????? ????????? ????????? ????????????.");

            	alert.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            		@Override
        		    public void onClick(DialogInterface dialog, int which) {
            			dialog.dismiss();
            		}
            	});

            	alert.show();
        	} else {
        		background.start();
        		new Handler().postDelayed(new Runnable() { public void run() { /*LoadingAnimation.start();*/ progress.setVisibility(View.VISIBLE); } }, 100);
        	}
        }
    }
    
    public void initValues(){
        Intent intent = getIntent();
        if(intent.getStringExtra("ThemeCode") != null)
        	info = new ThemeInfo(intent.getStringExtra("ThemeCode"), intent.getStringExtra("CityID"), intent.getStringExtra("CityName"), intent.getIntExtra("Index", 0), intent.getIntExtra("ThemeID", 0));
        else {
        	String Temp = null;
        	String[] szTemp;
        	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
        	int nCityCnt;

        	if (prefs == null){
        		nCityCnt = 0;
        		updateTime = 0;
        		return;
        	}

        	nCityCnt = prefs.getInt(Const.THEME_CNT, 0);
        	updateTime = prefs.getLong(Const.THEME_LIST_UPDATETIME, 0);


        	String[][] CityID = new String[nCityCnt][10];
        	for (int i = 0 ; i < nCityCnt ; ++i){
        		Temp = prefs.getString(Const.THEME_LIST + i, "");
        		szTemp = Temp.split("\t");

        		for (int j = 0 ; j < 10 ; j++) {
        			if (szTemp[j].equals("") || szTemp[j] == null)
        				szTemp[j] = "--";
        			CityID[i][j] = szTemp[j];
        		}
        	}
        	
        	try{
        		info = new ThemeInfo(CityID[0][0], CityID[0][1], CityID[0][2], 0, Integer.parseInt(CityID[0][9]));
        	} catch(ArrayIndexOutOfBoundsException e) {
        		e.printStackTrace();
        	}
        }
    	
        topLayer = (LinearLayout) findViewById(R.id.showtodayweather);
        imgContent= (ImageView) findViewById(R.id.showtoday_content);
        imgContent.setSoundEffectsEnabled(true);
//        imgWeekly = (ImageView) findViewById(R.id.showtoday_week);
        footer = findViewById(R.id.footer);

        gpsbtn = (ImageButton) findViewById(R.id.gpsbtn);
        livebtn = (ImageButton) findViewById(R.id.livecam);
        gpsbtn.setVisibility(View.GONE);
        livebtn.setVisibility(View.GONE);

//        LoadingLogo = (ImageView) findViewById(R.id.main_loadinglogo);
//    	LoadingAnimation = (AnimationDrawable) LoadingLogo.getBackground();
    	progress = (ProgressBar) findViewById(R.id.progress_small_title);

        today = new TodayData();
        hourly = new HourlyData();
        weekly = new WeeklyData();

        DateFlag = Util.getDateFlag(getApplicationContext());
        TimeFlag = Util.getTimeFlag(getApplicationContext());
    	TempFlag = Util.getTempFlag(getApplicationContext());

        if (TempFlag)
	    TempSymbol = "???";
     	else
	    TempSymbol = "???";

        rw = getResources();
    }

    final Handler handler = new Handler() {
	    public void handleMessage(Message msg) {
        	//Log.d("myTag", "ReDraw~~~");
        	if (msg.what != -1)
		    TodayThemeWeather.this.Draw();
        	else if(isStop == false)
		    ShowUpdateAgain();
//		LoadingAnimation.stop();
		progress.setVisibility(View.INVISIBLE);
	    }
	};

    @Override
	protected void onPause() {
    	super.onPause();
    	isStop = true;
//    	LoadingAnimation.stop();
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
    
    @Override
	protected void onResume(){
    	isStop = false;
//		Draw();
		super.onResume();
	}

    @Override
	protected void onDestroy(){
    	super.onDestroy();
    	if(bm != null){
	    bm.recycle();
	    bm = null;
    	}
    	if(bmWeek != null){
	    bmWeek.recycle();
	    bmWeek = null;
    	}
    	if(rw != null)
    		rw = null;
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
    	ImageView ivThemeIcon = (ImageView) findViewById(R.id.ImageView02);
    	
    	bm = Bitmap.createBitmap(480, 475, Config.RGB_565);

    	Bitmap ThemeIcon;
    	boolean isDayTime = false;
    	if(rw == null)
	    rw = getResources();
        Week_BG = BitmapFactory.decodeResource(rw, R.drawable.weekend_weather_bg_01);
//        CityNameBG = BitmapFactory.decodeResource(rw, R.drawable.location_name_bg_01);
//        PinpointBG = BitmapFactory.decodeResource(rw, R.drawable.todays_weather_bg_01);

//        Logo_BG = BitmapFactory.decodeResource(rw, R.drawable.update_bg_01);
//        Logo = BitmapFactory.decodeResource(rw, R.drawable.update_wn_logo);
//        Today = BitmapFactory.decodeResource(rw, R.drawable.today_weather_text);

    	Canvas canvas = new Canvas(bm);

    	paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(20);
        
        getSavedData();
        
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(updateTime);
        
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
        paint1.setColor(nColor);
        paint2.setTextAlign(Paint.Align.LEFT);
        paint2.setTextSize(32);

        paint3.setAntiAlias(true);
        paint3.setColor(Color.BLACK);
        paint3.setTextAlign(Paint.Align.LEFT);

        

        switch(info.getThemeID()){
        case com.weathernews.Weather.Theme.THEME_GOLF:
	    ThemeIcon = BitmapFactory.decodeResource(rw, R.drawable.theme_weather_golf_icon);
	    break;

        case com.weathernews.Weather.Theme.THEME_SKI:
	    ThemeIcon = BitmapFactory.decodeResource(rw, R.drawable.theme_weather_resort_icon);
	    break;

        case com.weathernews.Weather.Theme.THEME_MOUNTAIN:
	    ThemeIcon = BitmapFactory.decodeResource(rw, R.drawable.theme_weather_mountain_icon);
	    break;

        case com.weathernews.Weather.Theme.THEME_BASEBALL:
	    ThemeIcon = BitmapFactory.decodeResource(rw, R.drawable.theme_weather_baseball_icon);
	    break;

        case com.weathernews.Weather.Theme.THEME_BEACH:
	    ThemeIcon = BitmapFactory.decodeResource(rw, R.drawable.theme_weather_beach_icon);
	    break;

        case com.weathernews.Weather.Theme.THEME_THEMEPARK:
	    ThemeIcon = BitmapFactory.decodeResource(rw, R.drawable.theme_weather_park_icon);
	    break;

        default :
	    ThemeIcon = BitmapFactory.decodeResource(rw, R.drawable.theme_weather_golf_icon);
	    break;
        }
        
        ivThemeIcon.setImageBitmap(ThemeIcon);
        ivThemeIcon.bringToFront();

        

    	// ?????? ????????? ?????????
        WeatherIcon = Util.getTodayIconID(today.getTodayIcon(), cl.get(Calendar.HOUR_OF_DAY));
        for (int i = 0 ; i < WeatherIcon.length ; ++i) {
		    icon[i] = BitmapFactory.decodeResource(rw, WeatherIcon[i][0]);
		    rc[i] = new Rect(WeatherIcon[i][1], WeatherIcon[i][2], WeatherIcon[i][3], WeatherIcon[i][4]);
		    canvas.drawBitmap(icon[i], null, rc[i], null);
		    if(icon[i] != null){
				icon[i].recycle();
				icon[i] = null;
		    }
        }

    	tvCityName1.setTextColor(Color.WHITE);
    	
    	if(info.getCityName() != null)
    		tvCityName1.setText(info.getCityName());
        paint.setTextAlign(Paint.Align.LEFT);
    	paint.setTextSize(40);
//    	if(info.getCityName() != null)
//    		canvas.drawText(info.getCityName(), 16, 52, paint);
        paint.setTextSize(50);
        paint.setColor(nColor);

        if (!isDayTime){
	    paint.setColor(Color.WHITE);
	    paint1.setColor(Color.WHITE);
	    paint2.setColor(Color.WHITE);
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
        tvTempCur.setText("????????????" + szlTempCur);
        tvTempFeel.setText("????????????" + szlTempFeel);


    	// ???????????? ?????? ?????????
        for (int i = 0 ; i < 4 ; i++) {
        	Hourly[i] = BitmapFactory.decodeResource(rw, Util.getHourIconID(rw, hourly.getHourlyIcons(i), today.getTodayIcon(), cl.get(Calendar.HOUR_OF_DAY)));
        }
        
        ivHourly1.setImageBitmap(Hourly[0]);
    	ivHourly2.setImageBitmap(Hourly[1]);
    	ivHourly3.setImageBitmap(Hourly[2]);
    	ivHourly4.setImageBitmap(Hourly[3]);

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
    	
    	tvHourlyDate1.setText((nTextIndex / 4) == 1 || nTextIndex == 0 ? "?????? " + Const.HOURLY_TXT[nTextIndex++%4] : "?????? " + Const.HOURLY_TXT[nTextIndex++%4]);
    	tvHourlyDate2.setText((nTextIndex / 4) == 1 || nTextIndex == 1 ? "?????? " + Const.HOURLY_TXT[nTextIndex++%4] : "?????? " + Const.HOURLY_TXT[nTextIndex++%4]);
    	tvHourlyDate3.setText((nTextIndex / 4) == 1 || nTextIndex == 2 ? "?????? " + Const.HOURLY_TXT[nTextIndex++%4] : "?????? " + Const.HOURLY_TXT[nTextIndex++%4]);
    	tvHourlyDate4.setText((nTextIndex / 4) == 1 || nTextIndex == 3 ? "?????? " + Const.HOURLY_TXT[nTextIndex++%4] : "?????? " + Const.HOURLY_TXT[nTextIndex++%4]);
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

    	cl.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        String DateStr;
        if (updateTime > 0){
        	if (DateFlag)
        		DateStr = String.format("%02d", (cl.get(Calendar.MONTH) + 1)) + "/" +
        		    String.format("%02d", cl.get(Calendar.DAY_OF_MONTH)) + "(" + Const.Week_day[cl.get(Calendar.DAY_OF_WEEK)-1] + ")";
        	else
        		DateStr = String.format("%02d", cl.get(Calendar.DAY_OF_MONTH)) + "/" +
        		    String.format("%02d", (cl.get(Calendar.MONTH) + 1)) + "(" + Const.Week_day[cl.get(Calendar.DAY_OF_WEEK)-1] + ")";
        } else 
        	DateStr = "01/01(???)";
        
        tvDate.setText(DateStr);

    	imgContent.setImageBitmap(bm);
    	
    	new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
		    	rc_Icon_btn = new Rect(0, 0, imgContent.getWidth(), imgContent.getHeight());
			}
		}, 300);

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
    	bmWeek = Bitmap.createBitmap(nWeeklyCnt * nWidth + (nWeeklyCnt-1) * nGap + 2*nWeekX, 153, Config.RGB_565);

    	Canvas canvas = new Canvas(bmWeek);
    	if(rw == null)
	    rw = getResources();
    	
    	int nColor = Color.rgb(5, 37, 102);

    	paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
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

        canvas.drawBitmap(Week_BG, null, rc_Week, null);
        nCount = 0;
        String szlWeekMax;
        String szlWeekMin;
        int posX = nWeekX;
        Rect rcw = new Rect(posX , nWeekY,
			    posX + nWidth, nWeekY + nHeight);

        for (int i = 0 ; i < nWeeklyCnt ; ++i){
	    szlWeekMax = weekly.getWeeklyTempMax(i);
	    szlWeekMin = weekly.getWeeklyTempMin(i);

	    if (!TempFlag) {
		szlWeekMax = Util.getFahrenheit(szlWeekMax);
		szlWeekMin = Util.getFahrenheit(szlWeekMin);
	    }
	    float t2 = 0, t3 = 0, t4 = 0, t5 = 0, startT = 0;
	    iconw[i] = BitmapFactory.decodeResource(rw, Util.getWeekIconID(rw, weekly.getWeeklyIcon(i)));
	    canvas.drawBitmap(iconw[i], null, rcw, null);
	    if(iconw[i] != null){
		iconw[i].recycle();
		iconw[i] = null;
	    }
	    rcw.left += nWidth + nGap;
	    rcw.right += nWidth + nGap;
	    paint.setTextSize(25);
	    paint.setTextAlign(Paint.Align.CENTER);
	    if (weekly.getWeeklyDayOfWeek(i) == 6)
		paint.setColor(Color.BLUE);
	    else if (weekly.getWeeklyDayOfWeek(i) == 0)
		paint.setColor(Color.RED);
	    else
		paint.setColor(Color.WHITE);
	    if (0 == i)
		canvas.drawText("??????", 54 + i * 122, 37, paint);
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
	    paint.setColor(Color.WHITE);
	    paint1.setTextSize(16);
	    paint1.setColor(Color.WHITE);
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
    	}
        imgWeekly.setImageBitmap(bmWeek);

        Week_BG.recycle();
        Week_BG = null;
        */
    }

    void setFooterImage() {
    	Calendar cl = Calendar.getInstance();
    	if (updateTime != 0)
	    cl.setTimeInMillis(updateTime);
        if(rw == null)
	    rw = getResources();
        Util.setFooter(footer, cl, TimeFlag, DateFlag);
    }

    private DialogInterface.OnDismissListener listener = new DialogInterface.OnDismissListener() {
	    public void onDismiss(DialogInterface dialog) {
    		//Log.d("myTag", "onDisMiss");
    		Draw();
	    }
	};

    @Override protected Dialog onCreateDialog(int id)
    {
        switch (id)
	    {
	    case 0:
		ProgressDialog dialog;

		dialog = new ProgressDialog(TodayThemeWeather.this);
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.setOnDismissListener(listener);
		dialog.setMessage("??????????????????...           ");
		dialog.closeOptionsMenu();

		return dialog;
	    }
        return null;
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item)
    {

	switch (item.getItemId()) {
        case 0:
        	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        	boolean network = Util.checkNetwork(manager);
        	
        	if(!network) {
        		
//        		LoadingAnimation.stop();
        	    progress.setVisibility(View.INVISIBLE);
        	    
    			AlertDialog.Builder alert = null;
//    	    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    	    		alert = new AlertDialog.Builder(TodayThemeWeather.this, AlertDialog.THEME_HOLO_DARK);
//    	    	else
    	    		alert = new AlertDialog.Builder(TodayThemeWeather.this);
            	alert.setTitle("???????????? ??????");
            	alert.setIcon(R.drawable.ic_dialog_menu_generic);
            	alert.setMessage("???????????? ????????? ????????? ????????????.");

            	alert.setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
        	if(/*LoadingAnimation.isRunning() ||*/ progress.getVisibility() == View.VISIBLE && isStop == false) {
    		    Toast.makeText(TodayThemeWeather.this,
    					   "???????????? ????????? ?????????????????????.",
    					   Toast.LENGTH_SHORT).show();
    		    return true;
        	}

	    	final ProgressDialog dialog;

        	dialog = new ProgressDialog(TodayThemeWeather.this);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setMessage("????????? ?????????..");
            dialog.closeOptionsMenu();
            dialog.show();
            
            final Handler sendhandler = new Handler() {
            	public void handleMessage(Message msg) {
                	if (msg.what == -1) {
            			AlertDialog.Builder alert = null;
//            	    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//            	    		alert = new AlertDialog.Builder(TodayThemeWeather.this, AlertDialog.THEME_HOLO_DARK);
//            	    	else
            	    		alert = new AlertDialog.Builder(TodayThemeWeather.this);
        		    	alert.setTitle("SD?????? ??????");
        		    	alert.setIcon(R.drawable.ic_dialog_menu_generic);
        		    	alert.setMessage("???????????? ????????? ????????? ????????????. SD????????? ????????? ????????????.");

        		    	alert.setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
//					sendhandler.postDelayed(new Runnable(){
//			            @Override
//			             public void run() {
			            	Bitmap bmp = null;
			            	boolean isOk = Util.setCaptureAndSend(TodayThemeWeather.this, (View)topLayer, bmp);
			            	dialog.dismiss();
			            	if(!isOk) {
			            		Message msg = sendhandler.obtainMessage(-1);
			            		sendhandler.sendMessage(msg);
			            	}
//			            }
//					}, 1);
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
        menu.add(0, 0,0, "????????????").setIcon(R.drawable.ic_menu_update);
        menu.add(0, 1,0, "????????????").setIcon(R.drawable.ic_menu_share);
        //menu.add(0, 2,0, "????????????").setIcon(R.drawable.ic_menu_share);
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

		if(Math.abs(nsx - x) < 40 )
		    return true;
		if (nButtonState == Const.WEATHER_FOCUS_BTN.WEATHER_ICON_AREA) {
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_NONE;
		    if (rc_Icon_btn.contains(nsx, nsy - 38)){
			SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
			int nCityCnt;
			int nNextID;
			String NextThemeCode;
			String NextCityID;
			String NextCityName;
			int nNextThemeID;
			String Temp;
			String[] TempArray;
			int direction;
			nCityCnt = prefs.getInt(Const.THEME_CNT, 0);
			if (nsx - x > 0){	// Prev
			    if ((int)info.getIndex() == 0)
				nNextID = nCityCnt - 1;
			    else
				nNextID = (int)info.getIndex() - 1;
			    direction = TabHostHide.DIRECTION_RIGHT;
			} else {	// Next
			    if ((int)info.getIndex() == nCityCnt - 1)
				nNextID = 0;
			    else
				nNextID = (int)info.getIndex() + 1;
			    direction = TabHostHide.DIRECTION_LEFT;
			}
			Temp = prefs.getString(Const.THEME_LIST + nNextID, "");
			//Log.d("myTag", "Index=" + nNextID + " data=" + Temp);
			TempArray = Temp.split("\t");
			NextThemeCode = TempArray[0];
			NextCityID = TempArray[1];
			NextCityName = TempArray[2];
			nNextThemeID = Integer.parseInt(TempArray[9]);

			if(nCityCnt > 1) {
				Intent intent1 = new Intent(TodayThemeWeather.this, TodayThemeWeather.class);
				intent1.putExtra("ThemeCode", NextThemeCode);
				intent1.putExtra("CityID", NextCityID);
				intent1.putExtra("CityName", NextCityName);
				intent1.putExtra("Index", nNextID);
				intent1.putExtra("ThemeID", nNextThemeID);
	
				((TabRoot)getParent()).setCurrentTab(intent1, direction);
			}
//			if (nsx - x > 0)
//			    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		    }
		}

		break;
	    case MotionEvent.ACTION_DOWN:
		if (rc_GPS.contains(nsx, nsy - 38)){
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_GPS_BTN;
		    //invalidate(rc_GPS);
		    setContentImage();
		} else if (rc_Week.contains(nsx, nsy - 38)) {
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_WEEK_AREA;
		    x = nsx;
		    y = nsy;
		    //Log.d("myTag", "x=" + nsx);
		    //	    } else if (rc_Weather.contains(nsx, nsy -38)){
		    //		nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_WEATHER_BTN;
		    //		//invalidate(rc_menu);
		    //		nPressedTabNum = 0;
		    //		isPressed = true;
		    //		//invalidate(rc_menu);
		    //		setTabImage();
		    //		intent = new Intent(TodayThemeWeather.this, Weather.class);
		    //		((TabRoot)getParent()).setCurrentTab(intent);
		    //	    } else if (rc_LiveCam.contains(nsx, nsy - 38)){
		    //		nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_LIVECAM_BTN;
		    //		//invalidate(rc_menu);
		    //		nPressedTabNum = 2;
		    //		isPressed = true;
		    //		setTabImage();
		    //		intent = new Intent(TodayThemeWeather.this, LiveCam.class);
		    //		intent.putExtra("CityID", info.getCityID());
		    //		intent.putExtra("CityName", info.getCityName());
		    //		intent.putExtra("Index", info.getIndex());
		    //		intent.putExtra("IsToday", false);
		    //		((TabRoot)getParent()).setCurrentTab(intent);
		} else if (rc_Icon_btn.contains(nsx, nsy - 38)) {
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_ICON_AREA;
		    x = nsx;
		    y = nsy;
		}
		break;

	    case MotionEvent.ACTION_UP:
		if (nButtonState == Const.WEATHER_FOCUS_BTN.WEATHER_WEATHER_BTN){
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_NONE;
//		    setTabImage();
		    //invalidate(rc_menu);
		    if (rc_Weather.contains(nsx, nsy - 38)){
			// ?????? ???????????? ??????
			//    				intent = new Intent(TodayThemeWeather.this, Weather.class);
			//    				startActivity(intent);
			//    				finish();
		    }
		} else if (nButtonState == Const.WEATHER_FOCUS_BTN.WEATHER_LIVECAM_BTN){
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_NONE;
		    //invalidate(rc_menu);
//		    setTabImage();
		    if (rc_LiveCam.contains(nsx, nsy - 38)){
			//?????????????????? ??????
			//    				intent = new Intent(TodayThemeWeather.this, LiveCam.class);
			//    				intent.putExtra("CityID", info.getCityID());
			//    	        	intent.putExtra("CityName", info.getCityName());
			//    	        	intent.putExtra("Index", info.getIndex());
			//    	        	intent.putExtra("IsToday", false);
			//    	    		startActivity(intent);
			//    	    		finish();
		    }
		} else if (nButtonState == Const.WEATHER_FOCUS_BTN.WEATHER_GPS_BTN){
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_NONE;
		    //invalidate(rc_GPS);
		    setContentImage();
		    //    			if (rc_GPS.contains(nsx, nsy)){
		    //    				// ???????????? ????????? ??????
		    //    				intent = new Intent(TodayWeather.this, CustomDialog.class);
		    //    				intent.putExtra("CityID", info.getCityID());
		    //    	        	intent.putExtra("CityName", info.getCityName());
		    //    	        	intent.putExtra("TimeZone", info.getTimeZone());
		    //    	        	intent.putExtra("Index", info.getIndex());
		    //    	    		startActivity(intent);
		    //    			}
		} else if (nButtonState == Const.WEATHER_FOCUS_BTN.WEATHER_ICON_AREA) {
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_NONE;
		    if (rc_Icon_btn.contains(nsx, nsy - 38)){
		    	imgContent.playSoundEffect(0);
			intent = new Intent(TodayThemeWeather.this, CustomDialog.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
			intent.putExtra("CityID", info.getThemeCode());
			intent.putExtra("CityName", info.getCityName());
			intent.putExtra("Index", (int)info.getIndex());
			intent.putExtra("IsToday", false);
			startActivity(intent);
			//((TabRoot)getParent()).setCurrentTab(intent);
		    }
		    //    			Update();
		    //    			saveData();
		    //    			//invalidate();
		    //    			Draw();
		    //    			invalidate(rc_Icon);
		    //    			nIconidx ++;
		    //    			if (nIconidx >= Const.weathericon.length)
		    //    				nIconidx = 0;
		} else if (nButtonState == Const.WEATHER_FOCUS_BTN.WEATHER_WEEK_AREA) {
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_NONE;
		    //Log.d("myTag", "ButtonUp Week");
		} else if (nButtonState == Const.WEATHER_FOCUS_BTN.WEATHER_ICON_AREA) {
		    nButtonState = Const.WEATHER_FOCUS_BTN.WEATHER_NONE;
		    //Log.d("myTag", "ButtonUp Icon");
		}
		break;
	    }
    	return true;
    }

    public void getSavedData() {
    	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
    	String[] TempArray;
    	String Temp;

    	Temp = prefs.getString(Const.THEME_TODAY_WEATHER + info.getThemeCode(), "");

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

	    //Log.d("myTag", "setTodayTempFeel= " +  TempArray[9]);
    	}

    	try{
	    updateTime = Long.parseLong(today.getTodayUpdateTime());
    	}catch(NumberFormatException e) {
	    updateTime = Calendar.getInstance().getTimeInMillis();
    	}

    	Temp = prefs.getString(Const.THEME_HOURLY_WEATHER + info.getThemeCode(), "");
    	if (Temp.equals("") || Temp.length() <= 0){
	    for (int i = 0, j = 0 ; i < 8 ; i++, j++){
		//Log.d("myTag","i=" + i);
		hourly.setHourlyIcon("01", i);
	    }
	    for (int i = 0, j = 8 ; i < 8 ; i++, j++)
		hourly.setHourlyTemp("--", i);
	    for (int i = 0, j = 16 ; i < 8 ; i++, j++)
		hourly.setHourlyPop("--", i);
    	} else {
	    TempArray = Temp.split("\t");
	    for (int i = 0, j = 0 ; i < 8 ; i++, j++)
		hourly.setHourlyIcon(TempArray[j], i);
	    for (int i = 0, j = 8 ; i < 8 ; i++, j++)
		hourly.setHourlyTemp(TempArray[j], i);
	    for (int i = 0, j = 16 ; i < 8 ; i++, j++){
		if (i + j >= TempArray.length)
		    break;
		if (TempArray[j] != null && TempArray[j].length() > 0)
		    hourly.setHourlyPop(TempArray[j], i);
		else
		    hourly.setHourlyPop("--", i);
	    }
    	}

    	Temp = prefs.getString(Const.THEME_WEEKLY_WEATHER + info.getThemeCode(), "");
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

    public boolean saveData(){
    	Calendar cl = Calendar.getInstance();
    	String Temp = "";
	//cl.add(Calendar.MILLISECOND, -(9 * 60 * 60 * 1000));

    	SharedPreferences.Editor prefs = getSharedPreferences(Const.PREFS_NAME, 0).edit();

    	prefs.putLong(Const.THEME_TODAY_UPDATETIME + info.getThemeCode(), cl.getTimeInMillis());
    	if (today.getTodayIcon() == null || today.getTodayIcon().length() <= 0)
	    return false;
    	Temp = today.getTodayIcon() + "\t" + today.getTodayTempMax() + "\t" + today.getTodayTempMin() + "\t" +
	    today.getTodayPop() + "\t" + today.getTodayVisi() + "\t" + today.getTodayTempCur() + "\t" +
	    today.getTodayHum() + "\t" + today.getTodayWindDir() + "\t" + today.getTodayWindSpeed() + "\t" +
	    today.getTodayTempFeel() + "\t" + today.getTodayPress() + "\t" + today.getTodayIndex1() + "\t" +
	    today.getTodayIndex2() + "\t" + today.getTodayExplain() + "\t" + cl.getTimeInMillis() + "\t";
    	//Log.d("myTag", "TodayData=[" + Temp + "]");
    	prefs.putString(Const.THEME_TODAY_WEATHER + info.getThemeCode(), Temp);

    	// Save 6Hourly Data
    	Temp = "";
    	// Icon
    	if (hourly.getHourlyIcons(0) == null || hourly.getHourlyIcons(0).length() <= 0)
	    return false;
    	for (int i = 0 ; i < 8 ; ++i)
	    Temp = Temp + hourly.getHourlyIcons(i) + "\t";
    	// Temp
    	for (int i = 0 ; i < 8 ; ++i)
	    Temp = Temp + hourly.getHourlyTemp(i) + "\t";
    	// ????????????
    	for (int i = 0 ; i < 8 ; ++i)
	    Temp = Temp + hourly.getHourlyPop(i) + "\t";
    	prefs.putString(Const.THEME_HOURLY_WEATHER + info.getThemeCode(), Temp);

    	// Save Weekly Data
    	Temp = "";
    	// date
    	if (weekly.getWeeklyDate(0) == null || weekly.getWeeklyDate(0).length() <= 0)
	    return false;
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
    	prefs.putString(Const.THEME_WEEKLY_WEATHER + info.getThemeCode(), Temp);

    	prefs.commit();

    	return true;
    }

    public void ShowUpdateAgain() {
		AlertDialog.Builder alert = null;
//    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    		alert = new AlertDialog.Builder(getParent(), AlertDialog.THEME_HOLO_DARK);
//    	else
    		alert = new AlertDialog.Builder(getParent());
    	alert.setTitle("???????????? ???????????? ??????");
    	alert.setIcon(R.drawable.ic_dialog_menu_generic);
    	//alert.setMessage("?????? ?????? ??????????????? ?????????????????????. ?????? ????????? ????????????.");
//    	Context mContext = getApplicationContext();
    	Context mContext = this;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.today_popup,(ViewGroup) findViewById(R.id.showtoday_root));

        alert.setView(layout);

    	alert.setPositiveButton("??????", new DialogInterface.OnClickListener() {
    		@Override
		    public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		}
	    });
    	alert.setNegativeButton("?????????", new DialogInterface.OnClickListener() {
    		@Override
		    public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		    UpdateData();
		}
	    });

    	alert.show();
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
//	    LoadingAnimation.start();
	    progress.setVisibility(View.VISIBLE);
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
//    		alert = new AlertDialog.Builder(TodayThemeWeather.this, AlertDialog.THEME_HOLO_DARK);
//    	else
    		alert = new AlertDialog.Builder(TodayThemeWeather.this);
    	alert.setTitle("???????????? ??????");
    	alert.setIcon(R.drawable.ic_dialog_menu_generic);
    	alert.setMessage("???????????? ????????? ????????? ????????????.");

    	alert.setPositiveButton("??????", new DialogInterface.OnClickListener() {
    		@Override
		    public void onClick(DialogInterface dialog, int which) {
    			dialog.dismiss();
    		}
    	});

    	alert.show();
    	return;
	}

	URL = Const.URL_TODAY + "region=" + info.getCityID();
	try {
		fis = Util.getByteArrayFromURL(URL);
	} catch (UnknownHostException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
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
    	//Log.d("myTag", "TempFeel=[" + mySample.gettempfeel()+"]");
    	//Log.d("myTag", "TempFeel=[" + today.getTodayTempFeel()+"]");
    	today.setTodayPress(mySample.getpress());
    	today.setTodayIndex1(mySample.getindex1());
    	today.setTodayIndex2(mySample.getindex2());
    	today.setTodayExplain(mySample.getexplain());
    	today.setTodayUpdateTime(Long.toString(Calendar.getInstance().getTimeInMillis()));

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
	    try{
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
	if(fis != null) {
		try {
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		fis = null;
	}
    }

    @Override public void onBackPressed()
    {
	((TabRoot)getParent()).onBackPressed();
    }
}
