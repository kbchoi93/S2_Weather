package com.weathernews.Weather;

import java.io.IOException;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ThemeList extends ListActivity { // ListActivity를 상속받습니다.

    private View footer;
    private String TempSymbol = "℃";
    private List2[] CityID;
    private int nCityCnt;
    private long updateTime = 0;
    private boolean isPressed;
    private boolean isRedraw = false;
    private boolean isFirst = true;
    private boolean TimeFlag = false;
    private boolean DateFlag = false;
    private boolean TempFlag = true;

//    private AnimationDrawable LoadingAnimation;
//    private ImageView LoadingLogo;
    private ProgressBar progress;
    static final private int GET_CODE = 0;

    private ThemeAdapter m_adapter;

    private Resources res;
    private Bitmap bm;
    private Bitmap ListImage;
//    private Bitmap icon;
    private ListView list;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        res = getResources();

        list = new ListView(getApplicationContext());

//        TimeFlag = Util.getTimeFlag(this);
//    	TempFlag = Util.getTempFlag(this);
//
//        if (TempFlag)
//        	TempSymbol = "℃";
//        else
//        	TempSymbol = "℉";

        getSavedValue();
        footer = findViewById(R.id.footer);

//        LoadingLogo = (ImageView) findViewById(R.id.main_loadinglogo);
//    	LoadingAnimation = (AnimationDrawable) LoadingLogo.getBackground();
    	progress = (ProgressBar) findViewById(R.id.progress_small_title);

        if (nCityCnt == 0){
			AlertDialog.Builder alert = null;
//	    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//	    		alert = new AlertDialog.Builder(ThemeList.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//	    	else
	    		alert = new AlertDialog.Builder(ThemeList.this);
		    alert.setTitle( "테마 리스트 추가" );
		    alert.setIcon(R.drawable.ic_dialog_menu_generic);
		    alert.setMessage("선택하신 테마 리스트가 없습니다. 테마를 추가하시겠습니까?");

		    alert.setPositiveButton( "확인", new DialogInterface.OnClickListener() {
			    @Override
				public void onClick( DialogInterface dialog, int which) {
			    	Intent intent0 = new Intent(ThemeList.this, ThemeGroup.class);
				startActivityForResult(intent0, GET_CODE);
		    	        dialog.dismiss();
		    	    }
		    	});

		    alert.show();
        }

        //Log.i("myTag", "Draw From onCreate");
        Draw();

        progress.setVisibility(View.INVISIBLE);


        Calendar cl = Calendar.getInstance();
        if(cl.getTimeInMillis() - updateTime > 1800000l) {
        	Thread background=new Thread(new Runnable() {
        		public void run() {
        			ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        			boolean network = Util.checkNetwork(manager);
        			
        			if(!network) {
        		    	return;
        			}
        		    try {
        		    	Thread.sleep(10);
//        		    	LoadingAnimation.start();
        		    	progress.setVisibility(View.VISIBLE);
        		    	getTodayInfo();
        		    	Message msg = handler.obtainMessage();
        		    	handler.sendMessage(msg);
        		    }
        		    catch (Throwable t) {
        		    	//Log.e("myTag", "error = " + t.toString());
        		    	Message msg = handler.obtainMessage();
        		    	handler.sendMessage(msg);
        		    }
        		}
        	});
        	background.start();
        }

        if (0 != updateTime)
        	cl.setTimeInMillis(updateTime);
        if (null == res)
        	res = getResources();
        Util.setFooter(footer, cl, TimeFlag, DateFlag);
    }

    final Handler handler = new Handler() {
	    public void handleMessage(Message msg) {
	    	//Log.i("myTag", "Draw from handler");
		ThemeList.this.Draw();
		Calendar cl = Calendar.getInstance();
		if(res == null)
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
    	super.onResume();

    	Draw();

    	if (isRedraw) {
    		ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
    		boolean network = Util.checkNetwork(manager);
    		
    		if(!network) {
    			
//    			LoadingAnimation.stop();
    		    progress.setVisibility(View.INVISIBLE);
    		    
    			AlertDialog.Builder alert = null;
//    	    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    	    		alert = new AlertDialog.Builder(ThemeList.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//    	    	else
    	    		alert = new AlertDialog.Builder(ThemeList.this);
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
    		if(/*!LoadingAnimation.isRunning()*/ progress.getVisibility() != View.VISIBLE && nCityCnt > 0)
    			new Handler().postDelayed(new Runnable() { public void run() { /*LoadingAnimation.start();*/ progress.setVisibility(View.VISIBLE); } }, 100);
    		Thread background = new Thread(new Runnable() {
    			public void run() {
    				try {
    					Thread.sleep(10);
    					getTodayInfo();
    					Message msg = handler.obtainMessage();
    					handler.sendMessage(msg);
    				}
    				catch (Throwable t) {
    					Message msg = handler.obtainMessage();
    					handler.sendMessage(msg);
    				}
    			}
    		});
    		if(nCityCnt > 0)
    			background.start();

    		isRedraw = false;
//    	} else {
//    		Draw();
    	}
    }

    @Override
	public void onWindowFocusChanged(boolean hasFocus) {
    	Calendar cl = Calendar.getInstance();
    	
    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
    	boolean network = Util.checkNetwork(manager);
    	
    	if(!network) {
        	return;
    	}

		if(hasFocus){
			if(isFirst) {
				if(cl.getTimeInMillis() - updateTime > 1800000l){
//					LoadingAnimation.start();
					progress.setVisibility(View.VISIBLE);
				}
				isFirst = false;
			} else if(isRedraw){
//				LoadingAnimation.start();
				progress.setVisibility(View.VISIBLE);
			}
		}
    }

    @Override
	protected void onPause() {
    	super.onPause();
//    	LoadingAnimation.stop();
//    	m_adapter = null;
    	Draw();
    }

    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	if (null != bm)
	    {
    		bm.recycle();
    		bm = null;
	    }
    	if (null != ListImage)
	    {
    		ListImage.recycle();
    		ListImage = null;
	    }
    	m_adapter.clear();
    	res = null;
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == GET_CODE)
	    {
	    if (resultCode == RESULT_OK)
		isRedraw = !(isFirst = false);
	    else
		isRedraw = isFirst = false;
        }
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item){
    	Intent intent;

    	switch (item.getItemId()) {
        case 0:
        	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        	boolean network = Util.checkNetwork(manager);
        	
        	if(!network) {
        		
//        		LoadingAnimation.stop();
        	    progress.setVisibility(View.INVISIBLE);
        	    
    			AlertDialog.Builder alert = null;
//    	    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    	    		alert = new AlertDialog.Builder(ThemeList.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//    	    	else
    	    		alert = new AlertDialog.Builder(ThemeList.this);
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
//		    LoadingAnimation.start();
		    progress.setVisibility(View.VISIBLE);
            Thread background=new Thread(new Runnable() {
			    public void run() {
					try {
					    Thread.sleep(10);
					    getTodayInfo();
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
        case 1:		// add
        	intent = new Intent(this, ThemeGroup.class);
        	intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        	startActivityForResult(intent, GET_CODE);
            return true;
        case 2:		// change order
        	intent = new Intent(this, DndActivityTheme.class);
        	intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        	intent.putExtra("IsCityList", true);
        	startActivityForResult(intent, GET_CODE);
            return true;
        case 3:		// delete
        	intent = new Intent(this, DeleteThemeList.class);
        	intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        	intent.putExtra("IsCityList", true);
        	startActivityForResult(intent, GET_CODE);
            return true;

        default:
	    return true;
        }
    }

    @Override
    public void onBackPressed() {
    // TODO Auto-generated method stub
    	super.onBackPressed();
    	ActivityManager am
        	= (ActivityManager)getSystemService(ACTIVITY_SERVICE);
    	am.restartPackage(getPackageName());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0,0, "업데이트").setIcon(R.drawable.ic_menu_update);
        menu.add(0, 1,0, "지역 추가").setIcon(R.drawable.ic_menu_new);
        menu.add(0, 2,0, "순서편집").setIcon(R.drawable.ic_menu_change);
        menu.add(0, 3,0, "삭제").setIcon(R.drawable.ic_menu_discard);
        return true;
    }

    private void Draw(){
    	ArrayList<List2> m_orders;
    	m_orders = new ArrayList<List2>();
    	
        TimeFlag = Util.getTimeFlag(this);
        DateFlag = Util.getDateFlag(this);
    	TempFlag = Util.getTempFlag(this);

        if (TempFlag)
        	TempSymbol = "℃";
        else
        	TempSymbol = "℉";


    	getSavedValue();

        for (int i = 0 ; i < nCityCnt ; ++i)
        	m_orders.add(CityID[i]);

        if (null == m_adapter) {
        	m_adapter = new ThemeAdapter(this, R.layout.row_theme, m_orders); // 어댑터를 생성합니다.
        	setListAdapter(m_adapter);
        }
        else m_adapter.setList(m_orders);
    }

    private void getSavedValue() {
	// TODO Auto-generated method stub
    	String Temp = null;
    	String[] szTemp;
    	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);

    	if (prefs == null){
    		nCityCnt = 0;
    		updateTime = 0;
    		return;
    	}

    	nCityCnt = prefs.getInt(Const.THEME_CNT, 0);
    	updateTime = prefs.getLong(Const.THEME_LIST_UPDATETIME, 0);

    	if (nCityCnt == 0)
    		return;

    	CityID = new List2[nCityCnt];
    	for (int i = 0 ; i < nCityCnt ; ++i){
    		Temp = prefs.getString(Const.THEME_LIST + i, "");
    		szTemp = Temp.split("\t");

    		for (int j = 0 ; j < 10 ; j++)
    			if (szTemp[j].equals("") || szTemp[j] == null)
		    szTemp[j] = "--";

    		CityID[i] = new List2(szTemp[0], szTemp[1], szTemp[2], szTemp[3], szTemp[4], szTemp[5], szTemp[6], szTemp[7], szTemp[8], szTemp[9]);
    	}
    }

    private void saveCityList(){
		String Temp = "";
		Calendar cl = Calendar.getInstance();
		cl.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
		SharedPreferences.Editor prefs = getSharedPreferences(Const.PREFS_NAME, 0).edit();
		for (int i = 0 ; i < nCityCnt ; ++i){
		    Temp = "";
		    Temp = CityID[i].toTSVString();
		    prefs.putString(Const.THEME_LIST + i, Temp);
		}
		prefs.putInt(Const.THEME_CNT, nCityCnt);
		prefs.putLong(Const.THEME_LIST_UPDATETIME, cl.getTimeInMillis());
		prefs.commit();
    }


    private void getTodayInfo() {
		String URL;
		String szCityList = "";
		InputStream fis = null;
		String[] szTemp;
		String[][] TodayInfo = new String[nCityCnt][10];
		
		ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
		boolean network = Util.checkNetwork(manager);
		
		if(!network) {
			
//			LoadingAnimation.stop();
		    progress.setVisibility(View.INVISIBLE);
		    
			AlertDialog.Builder alert = null;
//	    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//	    		alert = new AlertDialog.Builder(ThemeList.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//	    	else
	    		alert = new AlertDialog.Builder(ThemeList.this);
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
		for (int i = 0 ; i < nCityCnt ; ++i)
		    szCityList = szCityList + CityID[i].getAreaCode() + "|";

		URL = Const.URL_LIST + "cnt="+ nCityCnt + "&region=" + szCityList;

		try {
			fis = Util.getByteArrayFromURL(URL, 50000);
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

		XMLReader myReader = null;
		try {
		    myReader =  concreteParser.getXMLReader();
		} catch (SAXException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
		MySampleHandlerWeather mySample  = new MySampleHandlerWeather(this);
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

		for (int i = 0 ; i < nCityCnt ; ++i)
		    TodayInfo[i][0] = CityID[i].getThemeCode();

//		szTemp = mySample.getregion_id().toString().split("\t");
//		//Log.d("myTag", "RegionID=" + szTemp[0] + "  sb=" + mySample.getregion_id().toString());
//		if (szTemp.length < nCityCnt)
//		    return;

		for (int i = 0 ; i < nCityCnt ; ++i)
		    TodayInfo[i][1] = CityID[i].getAreaCode();

		szTemp = mySample.getpointname().toString().split("\t");
		//Log.d("myTag", "getpointname=" + szTemp[0]);
		for (int i = 0 ; i < nCityCnt ; ++i)
		    TodayInfo[i][2] = CityID[i].getName();

		for (int i = 0 ; i < nCityCnt ; ++i)
		    TodayInfo[i][3] = CityID[i].getAddress();

		szTemp = mySample.getcomment().toString().split("\t");
		//Log.d("myTag", "getcomment=" + szTemp[0]);
		for (int i = 0 ; i < nCityCnt ; ++i)
		    TodayInfo[i][4] = szTemp[i];

		szTemp = mySample.geticon().toString().split("\t");
		//Log.d("myTag", "geticon=" + szTemp[0]);
		for (int i = 0 ; i < nCityCnt ; ++i)
		    TodayInfo[i][5] = szTemp[i];

		szTemp = mySample.gettempcur().toString().split("\t");
		//Log.d("myTag", "gettempcur=" + szTemp[0]);
		for (int i = 0 ; i < nCityCnt ; ++i)
		    if (szTemp[i].equals(""))
			TodayInfo[i][6] = "--";
		    else
			TodayInfo[i][6] = szTemp[i];

		szTemp = mySample.gettempmax().toString().split("\t");
		//Log.d("myTag", "gettempmax=" + szTemp[0]);
		for (int i = 0 ; i < nCityCnt ; ++i)
		    TodayInfo[i][7] = szTemp[i];

		szTemp = mySample.gettempmin().toString().split("\t");
		//Log.d("myTag", "gettempmin=" + szTemp[0]);
		for (int i = 0 ; i < nCityCnt ; ++i)
		    TodayInfo[i][8] = szTemp[i];

		for (int i = 0 ; i < nCityCnt ; ++i)
		    TodayInfo[i][9] = CityID[i].getThemeID();

		for (int i = 0 ; i < nCityCnt ; ++i)
		    CityID[i] = new List2(TodayInfo[i][0], TodayInfo[i][1], TodayInfo[i][2], TodayInfo[i][3], TodayInfo[i][4],
					  TodayInfo[i][5], TodayInfo[i][6], TodayInfo[i][7], TodayInfo[i][8], TodayInfo[i][9]);
		saveCityList();
		if(fis != null) {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			fis = null;
		}

    }

    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent;
        intent = new Intent(this, TodayThemeWeather.class);
        intent.putExtra("ThemeCode", CityID[position].getThemeCode());
    	intent.putExtra("CityID", CityID[position].getAreaCode());
    	intent.putExtra("CityName", CityID[position].getName());
    	intent.putExtra("Index", (int)id);
    	intent.putExtra("ThemeID", Integer.parseInt(CityID[position].getThemeID()));

        ((TabRoot)getParent()).setCurrentTab(intent);
    }

    private class ThemeAdapter extends ArrayAdapter<List2> {

        private ArrayList<List2> items;

        public ThemeAdapter(Context context, int textViewResourceId, ArrayList<List2> items) {
        	super(context, textViewResourceId, items);
        	this.items = items;
        }

        public void setList(ArrayList<List2> items) {
        	clear();
        	int size = items.size();
        	for (int i = 0; i < size; ++i) add(items.get(i));
        }

        @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
        	View v = convertView;
        	if (v == null) {
        		LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        		v = vi.inflate(R.layout.row_theme, null);
        	}
		List2 p = items.get(position);
		if (p != null)
		    {
			// get City info
        		String areaCode = p.getAreaCode();
			String cityName = p.getName();
			String weatherComment = p.getComment();
			String icon = p.getIcon();
			String tempCur = collapseTemp(p.getTempCur());
			String tempMax = collapseTemp(p.getTempMax());
			String tempMin = collapseTemp(p.getTempMin());

        		int themeId;
			int themeResource;
			try{
			    themeId = Integer.parseInt(p.getThemeID());
			}catch(NumberFormatException e){
			    themeId = 0;
			}
			switch (themeId)
			    {
			    case Theme.THEME_GOLF:      themeResource = R.drawable.theme_weather_golf_icon; break;
			    case Theme.THEME_MOUNTAIN:  themeResource = R.drawable.theme_weather_mountain_icon; break;
			    case Theme.THEME_SKI:       themeResource = R.drawable.theme_weather_resort_icon; break;
			    case Theme.THEME_THEMEPARK: themeResource = R.drawable.theme_weather_park_icon; break;
			    case Theme.THEME_BASEBALL:  themeResource = R.drawable.theme_weather_baseball_icon; break;
			    case Theme.THEME_BEACH:     themeResource = R.drawable.theme_weather_beach_icon; break;
			    default:                    themeResource = R.drawable.theme_weather_golf_icon;
			    }


			((ImageView)v.findViewById(R.id.rowImage)).setImageResource(Util.getIconID(res, icon));
			((ImageView)v.findViewById(R.id.rowThemeImage)).setImageResource(themeResource);
			((TextView)v.findViewById(R.id.rowCityName)).setText(cityName);
			((TextView)v.findViewById(R.id.rowCurrentTemp)).setText(Util.setTempStyle(tempCur), TextView.BufferType.SPANNABLE);
			((TextView)v.findViewById(R.id.rowComment)).setText(weatherComment);

			((TextView)v.findViewById(R.id.rowFutureTemp)).setText(Util.setTempStyle(tempMax + "/" + tempMin), TextView.BufferType.SPANNABLE);
		    }
        	return v;
        }

        private String collapseTemp(String data)
        {
        	if (null == data || 0 == data.length() || data.equals("-999"))
        		return "--";
        	else
        		return (TempFlag ? data : Util.getFahrenheit(data)) + TempSymbol;
        }

//        private void getIconID(Resources r, String szIcon) {
//		    if (szIcon.equals("01"))
//		    	icon = BitmapFactory.decodeResource(r, R.drawable.weather_icon_01_ref);
//		    else if (szIcon.equals("02"))
//		    	icon = BitmapFactory.decodeResource(r, R.drawable.weather_icon_05_ref);
//		    else if (szIcon.equals("03"))
//		    	icon = BitmapFactory.decodeResource(r, R.drawable.weather_icon_06_ref);
//		    else if (szIcon.equals("04"))
//		    	icon = BitmapFactory.decodeResource(r, R.drawable.weather_icon_05_ref);
//		    else if (szIcon.equals("05"))
//		    	icon = BitmapFactory.decodeResource(r, R.drawable.weather_icon_06_ref);
//		    else if (szIcon.equals("06"))
//		    	icon = BitmapFactory.decodeResource(r, R.drawable.weather_icon_09_ref);
//		    else if (szIcon.equals("07"))
//		    	icon = BitmapFactory.decodeResource(r, R.drawable.weather_icon_10_ref);
//		    else if (szIcon.equals("08"))
//		    	icon = BitmapFactory.decodeResource(r, R.drawable.weather_icon_11_ref);
//		    else if (szIcon.equals("09"))
//		    	icon = BitmapFactory.decodeResource(r, R.drawable.weather_icon_11_ref);
//		    else if (szIcon.equals("11"))
//		    	icon = BitmapFactory.decodeResource(r, R.drawable.weather_icon_12_ref);
//		    else if (szIcon.equals("12"))
//		    	icon = BitmapFactory.decodeResource(r, R.drawable.weather_icon_15_ref);
//		    else if (szIcon.equals("13"))
//		    	icon = BitmapFactory.decodeResource(r, R.drawable.weather_icon_22_ref);
//		    else if (szIcon.equals("14"))
//		    	icon = BitmapFactory.decodeResource(r, R.drawable.weather_icon_19_ref);
//		    else if (szIcon.equals("15"))
//		    	icon = BitmapFactory.decodeResource(r, R.drawable.weather_icon_15_ref);
//		    else if (szIcon.equals("16"))
//		    	icon = BitmapFactory.decodeResource(r, R.drawable.weather_icon_26_ref);
//		    else
//		    	icon = BitmapFactory.decodeResource(r, R.drawable.weather_icon_01_ref);
//        }
    }

    class List2 {
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
		      String Tcur, String TMax, String TMin, String themeid){
	    this.szThemeCode = ThemeCode;
	    this.szAreaCode = code;
	    this.szName = Name;
	    this.szAddress = address;
	    this.szWeatherComment = szWC;
	    this.szIcon = Icon;
	    this.szTempCur = Tcur;
	    this.szTempMax = TMax;
	    this.szTempMin = TMin;
	    this.szThemeID = themeid;
    	}

    	public String getThemeCode() { return szThemeCode; }
    	public String getAreaCode()  { return szAreaCode; }
    	public String getName()      { return szName; }
    	public String getAddress()   { return szAddress; }
    	public String getComment()   { return szWeatherComment; }
    	public String getTempCur()   { return szTempCur; }
    	public String getIcon()      { return szIcon; }
    	public String getTempMax()   { return szTempMax; }
    	public String getTempMin()   { return szTempMin; }
    	public String getThemeID()   { return szThemeID; }
    	public String toTSVString() {
	    return szThemeCode + "\t" + szAreaCode + "\t" + szName + "\t" + szAddress + "\t" + szWeatherComment + "\t" +
		szIcon + "\t" + szTempCur + "\t" + szTempMax + "\t" + szTempMin + "\t" + szThemeID;
    	}
    }
}
