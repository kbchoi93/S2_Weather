package com.weathernews.Weather;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.weathernews.Weather.ThemeList.List2;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.OrientationEventListener;

public class MapTest extends MapActivity implements LocationListener
{
    private View footer;

    protected OrientationEventListener ol;
    private MapView mapview;
    private MapController mc;

    LocationManager locationManager;

    private List2[] CityID;
    private int nCityCnt;
    private long updateTime = 0;
    private Theme[] Selected;
    private long lastTouchTime = -1;
    private boolean TimeFlag = false;
    private boolean DateFlag = false;
//    private ImageView loadinglogo;
    private ProgressBar progress;

    public class InterestingLocations extends ItemizedOverlay
    {

	private List<OverlayItem> locations = new ArrayList<OverlayItem>();
	private Drawable marker;

	public InterestingLocations(Drawable marker, Theme theme)
	{
	    super(marker);
	    this.marker = marker;

	    GeoPoint gpoint = new GeoPoint((int)(theme.getLatitude() * 1E6), (int)(theme.getLongitude() * 1E6));
	    locations.add(new OverlayItem(gpoint, theme.getThemeCode(), Integer.toString(theme.getThemeID())));

	    populate();
	}

	@Override public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
	    super.draw(canvas, mapView, shadow);
	    boundCenterBottom(marker);
	}

	@Override protected boolean onTap(int k)
	{
	    //Log.d("myTag", "i= " + k);
	    int themeID = Integer.parseInt(locations.get(k).getSnippet());
	    Theme[] themearray;
	    Theme tempTheme = null;

	    switch(themeID){
	    case Theme.THEME_GOLF :
		themearray = ThemeClass.Theme_Golf;
		break;

	    case Theme.THEME_MOUNTAIN:
		themearray = ThemeClass.Theme_Mountain;
		break;

	    case Theme.THEME_SKI:
		themearray = ThemeClass.Theme_Ski;
		break;

	    case Theme.THEME_THEMEPARK:
		themearray = ThemeClass.Theme_ThemPark;
		break;

	    case Theme.THEME_BASEBALL:
		themearray = ThemeClass.Theme_Baseball;
		break;

	    case Theme.THEME_BEACH:
		themearray = ThemeClass.Theme_Beach;
		break;

	    default :
		themearray = ThemeClass.Theme_ThemPark;
	    }

	    for (int i = 0 ; i < themearray.length ; ++i)
		if (themearray[i].getThemeCode().equals(locations.get(k).getTitle())) {
		    tempTheme = themearray[i];
		    break;
		}

	    //Log.d("myTag", "ThemeCode=" + tempTheme.getThemeCode() + " length=" + tempTheme.getName().length() + " Name=[" + tempTheme.getName() + "]");

	    Double lat, lon;
	    mc.setZoom(10);
	    try
		{
		    lat = tempTheme.getLatitude() * 1E6;
		    lon = tempTheme.getLongitude() * 1E6;
	    }
	    catch (Exception e)
		{
		    // TODO: handle exception
		    lat = 37.657021 * 1E6;
		    lon = 126.854376 * 1E6;
		    //Log.e("myTag", "Can't find Location!!!!");
		}

	    GeoPoint point = new GeoPoint(lat.intValue(), lon.intValue());

	    //GeoPoint point = new GeoPoint(lat.intValue(), lon.intValue());
	    mc.setZoom(15);

	    Point point1 = new Point();

	    Projection projection = mapview.getProjection();

	    projection.toPixels(point, point1);

//	    Log.e("myTag", "Point x=" + point1.x + "y=" + point1.y + " mapview " + mapview.getWidth() + ", " + mapview.getHeight() + " " + marker.getIntrinsicHeight());

	    point1.y -= 90;
	    point1.x += 10;

	    point = projection.fromPixels(point1.x, point1.y);
	    mc.setCenter(point);

	    mapview.invalidate();

	    for (int i = 0 ; i < nCityCnt ; i++)
		if (tempTheme.getThemeCode().equals(CityID[i].getThemeCode())) {
		    Intent intent = new Intent(MapTest.this, WeatherPopUp.class);
		    intent.putExtra("PointName", CityID[i].getName());
		    intent.putExtra("TempMax", CityID[i].getTempMax());
		    intent.putExtra("TempMin", CityID[i].getTempMin());
		    intent.putExtra("TempCur", CityID[i].getTempCur());
		    intent.putExtra("Comment", CityID[i].getComment());
		    intent.putExtra("Icon", CityID[i].getIcon());
		    intent.putExtra("Width", mapview.getWidth());
		    intent.putExtra("Height", mapview.getHeight());
		    intent.putExtra("IconHeight", marker.getIntrinsicHeight());
		    startActivity(intent);
		    break;
		}

	    return true;
	}

	protected OverlayItem createItem(int i) { return locations.get(i); }
	public int size() { return locations.size(); }

    }

    public class menuOverlay extends Overlay
    {
	private Paint paint = new Paint();
	private Bitmap Shadow;
	private Bitmap Color_bg;
	private Bitmap IconGolf;
	private Bitmap IconSki;
	private Bitmap IconMountain;
	private Bitmap IconBaseball;
	private Bitmap IconThemePark;
	private Bitmap IconBeach;
	private Rect rcShadow;
	private Rect rcColor_bg;
	private Rect rcIconGolf;
	private Rect rcIconSki;
	private Rect rcIconMountain;
	private Rect rcIconBaseball;
	private Rect rcIconThemePark;
	private Rect rcIconBeach;

	@Override public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
	    final int width = mapView.getWidth();
	    final int height = mapView.getHeight();

	    super.draw(canvas, mapView, shadow);
	    paint.setColor(Color.rgb(255,255,255));
	    paint.setAntiAlias(true);
	    paint.setTextSize(20);
	    paint.setTextAlign(Paint.Align.CENTER);

	    Shadow = BitmapFactory.decodeResource(MapTest.this.getResources(), R.drawable.theme_weather_shadow_01);
	    Color_bg = BitmapFactory.decodeResource(MapTest.this.getResources(), R.drawable.theme_weather_color_bg);
	    IconGolf = BitmapFactory.decodeResource(MapTest.this.getResources(), R.drawable.theme_weather_golf_mark);
	    IconSki = BitmapFactory.decodeResource(MapTest.this.getResources(), R.drawable.theme_weather_ski_mark);
	    IconMountain = BitmapFactory.decodeResource(MapTest.this.getResources(), R.drawable.theme_weather_mountain_mark);
	    IconBaseball = BitmapFactory.decodeResource(MapTest.this.getResources(), R.drawable.theme_weather_baseball_mark);
	    IconThemePark = BitmapFactory.decodeResource(MapTest.this.getResources(), R.drawable.theme_weather_themepark_mark);
	    IconBeach = BitmapFactory.decodeResource(MapTest.this.getResources(), R.drawable.theme_weather_beach_mark);

	    rcShadow = new Rect(0, 0, width, 40);
	    rcColor_bg = new Rect(0, height - 30, width, height);

	    int centerShift = (width - 654) / 2;
	    rcIconGolf = new Rect(centerShift + 81, 8, centerShift + 106, 33);
	    rcIconSki = new Rect(centerShift + 183, 8, centerShift + 208, 33);
	    rcIconMountain = new Rect(centerShift + 261, 8, centerShift + 286, 33);
	    rcIconBaseball = new Rect(centerShift + 369, 8, centerShift + 394, 33);
	    rcIconThemePark = new Rect(centerShift + 499, 8, centerShift + 524, 33);
	    rcIconBeach = new Rect(centerShift + 629, 8, centerShift + 654, 33);

	    canvas.drawBitmap(Shadow, null, rcShadow, null);
	    canvas.drawBitmap(Color_bg, null, rcColor_bg, null);

	    canvas.drawBitmap(IconGolf, null, rcIconGolf, null);
	    canvas.drawBitmap(IconSki, null, rcIconSki, null);
	    canvas.drawBitmap(IconMountain, null, rcIconMountain, null);
	    canvas.drawBitmap(IconBaseball, null, rcIconBaseball, null);
	    canvas.drawBitmap(IconThemePark, null, rcIconThemePark, null);
	    canvas.drawBitmap(IconBeach, null, rcIconBeach, null);

	    canvas.drawText("골프장", centerShift + 49, 29, paint);
	    canvas.drawText("스키장", centerShift + 151, 29, paint);
	    canvas.drawText("산", centerShift + 244, 29, paint);
	    canvas.drawText("야구장", centerShift + 337, 29, paint);
	    canvas.drawText("테마파크", centerShift + 459, 29, paint);
	    canvas.drawText("해수욕장", centerShift + 589, 29, paint);

	    paint.setTextSize(18);
	    paint.setTextAlign(Paint.Align.RIGHT);
	    paint.setColor(Color.rgb(207, 207, 207));
	    canvas.drawText("* 컬러아이콘을 선택하면 해당장소의 날씨를 확인할 수 있습니다.", width - 17, height - 8, paint);
	    
	    Shadow.recycle();
	    Color_bg.recycle();
	    IconGolf.recycle();
	    IconSki.recycle();
	    IconMountain.recycle();
	    IconBaseball.recycle();
	    IconThemePark.recycle();
	    IconBeach.recycle();
	}

	@Override
	    public boolean onTouchEvent(MotionEvent event, MapView mv ){
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				long thisTime = System.currentTimeMillis();
				if (thisTime - lastTouchTime < 250) {
					mc.setZoom(10);
					mapview.invalidate();
					lastTouchTime = -1;
				} else {
					lastTouchTime = thisTime;
				}
			}
			return super.onTouchEvent(event, mv);
		}
	}

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.themeland);
        mapview = (MapView) findViewById(R.id.map);
        mc = mapview.getController();
        //mapview.setFocusableInTouchMode(true);
        mapview.setFocusable(true);
        mapview.setClickable(true);

        footer = findViewById(R.id.footer);
//        loadinglogo = (ImageView)findViewById(R.id.main_loadinglogo);
//        loadinglogo.setVisibility(View.GONE);
        progress = (ProgressBar) findViewById(R.id.progress_small_title);
        progress.setVisibility(View.GONE);

        TimeFlag = Util.getTimeFlag(getApplicationContext());
        DateFlag = Util.getDateFlag(getApplicationContext());

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        getSavedValue();

        Selected = new Theme[nCityCnt];

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = locationManager.getBestProvider(criteria, true);

        //Log.i("myTag", "provider=" + provider);

        Location location = null;
        if (provider != null) {
	    location = locationManager.getLastKnownLocation(provider);
	    locationManager.requestLocationUpdates(provider, 1000, 0, this);
        }

        if (location != null)
	    ;//Log.i("myTag", "location=" + location.toString());
        else
	    ;//Log.e("myTag", "location= NULL");

        Double lat, lon;

        try
	    {
		lat = location.getLatitude() * 1E6;
		lon = location.getLongitude() * 1E6;
		//Log.d("myTag", "lat = " + lat + " long= " + lon );
	    } catch (Exception e) {
	    // TODO: handle exception
	    lat = 37.657021 * 1E6;
	    lon = 126.854376 * 1E6;
	    //Log.e("myTag", "Can't find Location!!!!");
	}

        GeoPoint point = new GeoPoint(lat.intValue(), lon.intValue());
        mc.setCenter(point);
        mc.setZoom(10);

        Drawable[] marker = new Drawable[6];
        marker[0] = getResources().getDrawable(R.drawable.theme_weather_golf_location);
        marker[0].setBounds(0, 0, marker[0].getIntrinsicWidth(), marker[0].getIntrinsicHeight());

        marker[1] = getResources().getDrawable(R.drawable.theme_weather_ski_location);
        marker[1].setBounds(0, 0, marker[1].getIntrinsicWidth(), marker[1].getIntrinsicHeight());

        marker[2] = getResources().getDrawable(R.drawable.theme_weather_mountain_location);
        marker[2].setBounds(0, 0, marker[2].getIntrinsicWidth(), marker[2].getIntrinsicHeight());

        marker[3] = getResources().getDrawable(R.drawable.theme_weather_baseball_location);
        marker[3].setBounds(0, 0, marker[3].getIntrinsicWidth(), marker[3].getIntrinsicHeight());

        marker[4] = getResources().getDrawable(R.drawable.theme_weather_themepark_location);
        marker[4].setBounds(0, 0, marker[4].getIntrinsicWidth(), marker[4].getIntrinsicHeight());

        marker[5] = getResources().getDrawable(R.drawable.theme_weather_beach_location);
        marker[5].setBounds(0, 0, marker[5].getIntrinsicWidth(), marker[5].getIntrinsicHeight());

        for (int i = 0 ; i < nCityCnt ; i ++)
	    {
		Theme[] themearray = null;
		switch(Integer.parseInt(CityID[i].getThemeID()))
		    {
		    case Theme.THEME_GOLF:
			themearray = ThemeClass.Theme_Golf;
			break;

		    case Theme.THEME_SKI:
			themearray = ThemeClass.Theme_Ski;
			break;

		    case Theme.THEME_MOUNTAIN:
			themearray = ThemeClass.Theme_Mountain;
			break;

		    case Theme.THEME_BASEBALL:
			themearray = ThemeClass.Theme_Baseball;
			break;

		    case Theme.THEME_THEMEPARK:
			themearray = ThemeClass.Theme_ThemPark;
			break;

		    case Theme.THEME_BEACH:
			themearray = ThemeClass.Theme_Beach;
			break;
		    }

		for (int j = 0 ; j < themearray.length ; j ++)
		    if (CityID[i].getThemeCode().equals(themearray[j].getThemeCode())){
			Selected[i] = themearray[j];
			mapview.getOverlays().add(new InterestingLocations(marker[Selected[i].getThemeID()], Selected[i]));
		    }
	    }

        mapview.getOverlays().add(new menuOverlay());

        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(updateTime);
        Util.setFooter(footer, cl, TimeFlag, DateFlag);
	ol = new OrientationEventListener(this) {
		@Override public void onOrientationChanged(int orientation)
		{
			if ((orientation > 0 && orientation <= 35) || (orientation >= 315) || (orientation >= 145 && orientation <= 215))
			{
			    this.disable();
			    finish();
			}
		}
	    };
	ol.enable();
    }

    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
	if (ev.getAction() == MotionEvent.ACTION_DOWN) {
	    //Log.d("myTag", "touch event onInterceptTouchEvent Activity");
	    long thisTime = System.currentTimeMillis();
	    if (thisTime - lastTouchTime < 250)
		{
		    //Log.i("myTag", "double click onInterceptTouchEvent Activity");
		    // Double tap
		    mc.setZoom(10);
		    mapview.invalidate();
		    lastTouchTime = -1;
		}
	    else lastTouchTime = thisTime; // Too slow :)
	}

	return super.onTouchEvent(ev);
    }

	@Override
    public void onBackPressed() {
    // TODO Auto-generated method stub
    	ActivityManager am 
        	= (ActivityManager)getSystemService(ACTIVITY_SERVICE);
    	am.restartPackage(getPackageName());
    	super.onBackPressed();
    }

    @Override protected boolean isRouteDisplayed()
    {
	return false;
    }

    @Override protected void onResume()
    {
	super.onResume();
    }

    @Override protected void onPause()
    {
	super.onPause();
	locationManager.removeUpdates(this);
    }

    private void getSavedValue()
    {
	String Temp = null;
	String[] szTemp;
	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);

	if (prefs == null)
	    {
		nCityCnt = 0;
		updateTime = 0;
	    }

	nCityCnt = prefs.getInt(Const.THEME_CNT, 0);
	updateTime = prefs.getLong(Const.THEME_LIST_UPDATETIME, 0);

	if (nCityCnt == 0)
	    return;

	CityID = new List2[nCityCnt];
	for (int i = 0 ; i < nCityCnt ; i++){
	    Temp = prefs.getString(Const.THEME_LIST + i, "");
	    //Log.d("myTag", Temp);
	    szTemp = Temp.split("\t");

	    for (int j = 0 ; j < 10 ; j++)
		if (szTemp[j].equals("") || szTemp[j] == null)
		    szTemp[j] = "--";

	    CityID[i] = new List2(szTemp[0], szTemp[1], szTemp[2], szTemp[3], szTemp[4], szTemp[5], szTemp[6], szTemp[7], szTemp[8], szTemp[9]);
	}
    }

    class List2
    {
    	private String szThemeCode;
    	private String szAreaCode;
    	private String szName;
    	private String szAddress;
    	private String szWeatherComment;
    	private String szIcon;
    	private String szTempCur;
    	private String szTempMax;
    	private String szTempMin;
    	private String szThemeID;

    	public List2(String ThemeCode, String code, String Name, String address, String szWC, String Icon,
		     String Tcur, String TMax, String TMin, String themeid)
	{
	    szThemeCode = ThemeCode;
	    szAreaCode = code;
	    szName = Name;
	    szAddress = address;
	    szWeatherComment = szWC;
	    szIcon = Icon;
	    szTempCur = Tcur;
	    szTempMax = TMax;
	    szTempMin = TMin;
	    szThemeID = themeid;
    	}

    	public String getThemeCode(){ return szThemeCode; }
    	public String getAreaCode() { return szAreaCode; }
    	public String getName()     { return szName; }
    	public String getAddress()  { return szAddress; }
    	public String getComment()  { return szWeatherComment; }
    	public String getTempCur()  { return szTempCur; }
    	public String getIcon()     { return szIcon; }
    	public String getTempMax()  { return szTempMax; }
    	public String getTempMin()  { return szTempMin; }
    	public String getThemeID()  { return szThemeID; }

    	public String toTSVString(){
	    return szThemeCode + "\t" + szAreaCode + "\t" + szName + "\t" + szAddress + "\t" + szWeatherComment + "\t" +
		szIcon + "\t" + szTempCur + "\t" + szTempMax + "\t" + szTempMin + "\t" + szThemeID;
    	}
    }

    @Override public void onLocationChanged(Location location) {}
    @Override public void onProviderDisabled(String provider) {}
    @Override public void onProviderEnabled(String provider) {}
    @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
}
