package com.weathernews.Weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Weather extends ListActivity { // ListActivity를 상속받습니다.

    private View footer;
    private String TempSymbol;
    private String[][] CityID;
    private int nCityCnt;
    private long updateTime = 0;
    private boolean isRedraw = false;

//    private RotateDrawable LoadingAnimation;
//    private ImageView LoadingLogo;
    private boolean isFirst = false;

    static final private int GET_CODE = 0;
    private boolean DateFlag = false;
    private boolean TimeFlag = false;
    private boolean TempFlag = true;

    private boolean First = true;

    private Resources res;
    private Bitmap bm;
    private ProgressBar progress;
//    private Bitmap bIcon;

    private boolean checkver;

    private List2Adapter m_adapter = null;
//    private static View main_view = null;
    private Context context;
    
    private Handler msgHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        	if(msg.what == 1) {
        		Message mesg;
        		String Ver = null;
        		try
        		{
        			Thread.sleep(10);
        			Ver = checkVersion();
        		}
        		catch (InterruptedException e) { e.printStackTrace(); }

        		if (Ver != null)
        			mesg = vercheckhandler.obtainMessage(0, Ver);
        		else
        			mesg = vercheckhandler.obtainMessage(-1);
        		vercheckhandler.sendMessage(mesg);
        	} else if(msg.what == 2) {
    			ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
    			boolean network = Util.checkNetwork(manager);
        	
    			if(!network) return;
    			try
    			{
    				Thread.sleep(10);
    				progress.setVisibility(View.VISIBLE);
    				getTodayInfo();
    				Message mesg = handler.obtainMessage();
    				handler.sendMessage(mesg);
    			}
    			catch (Throwable t)
    			{
    				Message mesg = handler.obtainMessage();
    				handler.sendMessage(mesg);
    			}
        	} else if(msg.what == 3) {
    			try
			    {
				Thread.sleep(10);
				getTodayInfo();
				Message mesg = handler.obtainMessage();
				handler.sendMessage(mesg);
			    }
			catch (Throwable t)
			    {
				Message mesg = handler.obtainMessage();
				handler.sendMessage(mesg);
			    }
        	}
        }
    };

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        if(main_view == null) {
//        	LayoutInflater layout = getLayoutInflater();
//        	main_view = layout.inflate(R.layout.main, null);
//        }
        setContentView(R.layout.main);

        getSavedValue();
        //CityID = getCityList();
        footer = findViewById(R.id.footer);
        res = getResources();

//        LoadingLogo = (ImageView) findViewById(R.id.main_loadinglogo);
//    	LoadingAnimation = (RotateDrawable) LoadingLogo.getBackground();
    	progress = (ProgressBar) findViewById(R.id.progress_small_title);

    	context = getApplicationContext();
    	DateFlag = Util.getDateFlag(context);
    	TimeFlag = Util.getTimeFlag(context);
    	TempFlag = Util.getTempFlag(context);

    	if (TempFlag)
	    TempSymbol = "℃";
    	else
	    TempSymbol = "℉";

        if (nCityCnt == 0){
        	AlertDialog.Builder alert = null;
//        	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//        		alert = new AlertDialog.Builder(Weather.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//        	else
        		alert = new AlertDialog.Builder(Weather.this);
        	alert.setTitle("도시 리스트 추가");
        	alert.setIcon(R.drawable.ic_dialog_menu_generic);
        	alert.setMessage("선택하신 도시 리스트가 없습니다. 도시를 추가하시겠습니까?");

        	alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
        		@Override
        		public void onClick(DialogInterface dialog, int which) {
        			Intent intent0 = new Intent(Weather.this, CityGroup.class);
        			startActivityForResult(intent0, GET_CODE);
        			dialog.dismiss();
        		}
        	});

        	alert.show();
        }

        Draw();

        SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
        checkver = prefs.getBoolean("START", false);
        updateTime = prefs.getLong(Const.UPDATE_TIME_LIST, 0l);

        Calendar cl = Calendar.getInstance();
        
        if (isFirst == false) {        	
        	if (!prefs.getBoolean("INFO", false) && getIntent().getBooleanExtra("FIRST", false)) {
//        		if(chekver == false)
        			showInfo();
        		isFirst = true;
        	}
        	


        	isFirst = false;

        	progress.setVisibility(View.INVISIBLE);

        	if (cl.getTimeInMillis() - updateTime > 1800000l){
	            new Handler().postDelayed(new Runnable() { public void run() { progress.setVisibility(View.VISIBLE); } }, 100);
	
	            Thread background = new Thread(new Runnable()
			    {
	            	public void run()
	            	{
	            		try
	            		{
	            			Thread.sleep(10);
	            			getTodayInfo();
	            			Message msg = handler.obtainMessage();
	            			handler.sendMessage(msg);
	            		}
	            		catch (Throwable t)
	            		{
	            			Message msg = handler.obtainMessage();
	            			handler.sendMessage(msg);
	            		}
	            	}
			    });
	
	            background.start();
        	}

        }

        SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
        prefs1.putBoolean("START", true);
        prefs1.commit();

        if (updateTime != 0)
    		cl.setTimeInMillis(updateTime);
        Util.setFooter(footer, cl, TimeFlag, DateFlag);
    }

    @Override
	public void onWindowFocusChanged(boolean hasFocus) {
    	Calendar cl = Calendar.getInstance();
    	
    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
    	boolean network = Util.checkNetwork(manager);
    	
    	if(!network) return;

	if (hasFocus)
	    {
		if (First)
		    {
			
			if (cl.getTimeInMillis() - updateTime > 1800000l)
			    {
//				LoadingAnimation.start();
//				LoadingLogo.setVisibility(View.VISIBLE);
				progress.setVisibility(View.VISIBLE);
			    }
			First = false;
		    }
		else if (isFirst)
		    {
//			LoadingAnimation.start();
//			LoadingLogo.setVisibility(View.VISIBLE);
			progress.setVisibility(View.VISIBLE);
		    }
	    }
    }

    @Override protected void onDestroy()
    {
    	super.onDestroy();
    	if (bm != null) {
    		bm.recycle();
    		bm = null;
	    }
//    	if(bIcon != null)
//    		bIcon.recycle();
    	m_adapter.items.clear();
    	m_adapter.ClearMemory();
    	res = null;
//    	main_view = null;
    	System.gc();
    }

    final Handler vercheckhandler = new Handler()
	{
	    int msgid;
	    String Ver;
	    public void handleMessage(Message msg)
	    {
        	msgid = msg.what;
        	if (msgid == 0) {
		    Ver = (String)msg.obj;

        	AlertDialog.Builder alert = null;
//        	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//        		alert = new AlertDialog.Builder(Weather.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//        	else
        		alert = new AlertDialog.Builder(Weather.this);
		    alert.setTitle("업그레이드 정보");
		    alert.setIcon(R.drawable.ic_dialog_menu_generic);
		    alert.setMessage("새로운 버전(" + Ver + ")으로 업그레이드 할 수 있습니다. 업그레이드 하시려면, Samsung Apps에 접속해서 다운로드 받으시기 바랍니다.");

		    alert.setPositiveButton("확인", new DialogInterface.OnClickListener()
			{
			    @Override public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
		    	});

		    alert.show();
        	}
	    }
	};

    private String checkVersion()
    {
    	PackageManager pm = getPackageManager();
        int vc = 0;
        String vn = "";
        try
	    {
		PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
		vc = pi.versionCode;
		vn = pi.versionName;
	    }
	catch (NameNotFoundException e)
	    {
		e.printStackTrace();
		return null;
	    }

	InputStream fis;

	BufferedReader in;
	String data = "";

	try
	    {
		fis = Util.getByteArrayFromURL(getString(R.string.vercheck));
		in = new BufferedReader(new InputStreamReader(fis));
		if (in == null) return null;
		data = in.readLine();
	    }
	catch (Exception e)
	    {
		e.printStackTrace();
		return null;
	    }
	if(fis != null) {
		try {
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		fis = null;
	}

	String[] Version;
	if (data != null && data.length() > 0)
	    {
		Version = data.split(",");
		try
		    {
			if (Integer.parseInt(Version[0]) > vc)
			    return Version[1];
			else return null;
		    }
		catch(NumberFormatException e)
		    {
			e.printStackTrace();
			return null;
		    }
	    } else return null;
    }

    public void showInfo()
    {
//        Context mContext = getApplicationContext();
    	Context mContext = this;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_dialog,(ViewGroup) findViewById(R.id.layout_root));
//        final Button check_button = (Button)layout.findViewById(R.id.custom_check_button);
        final CheckBox check = (CheckBox)layout.findViewById(R.id.custom_check);
        final LinearLayout ll = (LinearLayout)layout.findViewById(R.id.check_layout);
        
        ll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				check.setChecked(!check.isChecked());
			}
		});

//        final Drawable alpha0 = check_button.getBackground();
//        alpha0.setAlpha(0);
//
//        check_button.bringToFront();
//
//        check_button.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				boolean hideInfo = true;
//				hideInfo = !check.isChecked();
//				check.setChecked(hideInfo);
//			    SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
//			    prefs1.putBoolean("INFO", hideInfo);
//			    prefs1.commit();
//
//			}
//		});

        SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);

        boolean hideInfo = false;
        if (prefs == null)
	    hideInfo = false;
        else
	    hideInfo = prefs.getBoolean("INFO", false);

        check.setChecked(hideInfo);

    	AlertDialog.Builder alert = null;
//    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    		alert = new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//    	else
    		alert = new AlertDialog.Builder(mContext);

    	alert.setTitle("이용안내");
    	alert.setView(layout);
    	//alert.setCustomTitle(layout_top);

    	alert.setIcon(R.drawable.ic_dialog_menu_generic);

    	alert.setPositiveButton("확인", new DialogInterface.OnClickListener()
	    {
    		@Override public void onClick(DialogInterface dialog, int which)
		{
		    boolean hideInfo = true;
		    hideInfo = check.isChecked();
		    SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
		    prefs1.putBoolean("INFO", hideInfo);
		    prefs1.commit();
		    dialog.dismiss();
//		    alpha0.setCallback(null);
		}
	    });

    	AlertDialog ad = alert.create();
    	ad.show();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == GET_CODE)
	    {
		isFirst = true;
		if (resultCode == RESULT_OK)
		    {
			isRedraw = true;

			if (data.getAction().equals("SETUP"))
			    {
				isRedraw = false;
				SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);

				DateFlag = prefs.getBoolean("DateFlag", true);
				TimeFlag = prefs.getBoolean("TimeFlag", true);
				TempFlag = prefs.getBoolean("TempFlag", true);

				if (TempFlag)
				    TempSymbol = "℃";
				else
				    TempSymbol = "℉";
			    }
		    }
		else if (resultCode == RESULT_CANCELED)
		    {
			isFirst = false;
			isRedraw = false;
		    }
	    }
	else
	    {
		isRedraw = false;
		isFirst = false;
	    }
    }
    
    private AlertDialog.Builder alertmain = null;

    @Override protected void onResume()
    {
    	super.onResume();
    	Calendar cl = Calendar.getInstance();
    	if (updateTime != 0)
    	    cl.setTimeInMillis(updateTime);
    	Util.setFooter(footer, cl, TimeFlag, DateFlag);

    	Draw();
    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
    	boolean network = Util.checkNetwork(manager);
    	
    	if(!network) {    		
//    		LoadingAnimation.stop();
//    	    LoadingLogo.setVisibility(View.INVISIBLE);
    	    progress.setVisibility(View.INVISIBLE);
    	    
    	    if(alertmain != null)
    	    	return;
//        	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//        		alertmain = new AlertDialog.Builder(Weather.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//        	else
        		alertmain = new AlertDialog.Builder(Weather.this);

    	    alertmain.setTitle("네트워크 오류");
    	    alertmain.setIcon(R.drawable.ic_dialog_menu_generic);
    	    alertmain.setMessage("네트워크 상태를 확인해 주십시오.");

    	    alertmain.setPositiveButton("확인", new DialogInterface.OnClickListener() {
	    		@Override
			    public void onClick(DialogInterface dialog, int which) {
	    			dialog.dismiss();
	    			alertmain = null;
	    		}
	    	});

    	    alertmain.show();
	    	return;
    	}

    	if (isFirst)
//    	if(true)
	    {
//		if (!LoadingAnimation.isRunning() && nCityCnt > 0)
//		    new Handler().postDelayed(new Runnable() { public void run() { LoadingAnimation.start(); LoadingLogo.setVisibility(View.VISIBLE); progress.setVisibility(View.VISIBLE); } }, 100);

		if (progress.getVisibility() != View.VISIBLE  && nCityCnt > 0)
		    new Handler().postDelayed(new Runnable() { public void run() { /*LoadingAnimation.start(); LoadingLogo.setVisibility(View.VISIBLE);*/ progress.setVisibility(View.VISIBLE); } }, 100);

	    Thread background = new Thread(new Runnable()
		{
		    public void run()
		    {
			try
			    {
				Thread.sleep(10);
				getTodayInfo();
				Message msg = handler.obtainMessage();
				handler.sendMessage(msg);
			    }
			catch (Throwable t)
			    {
				Message msg = handler.obtainMessage();
				handler.sendMessage(msg);
			    }
		    }
		});
	    if (nCityCnt > 0) {
	    	background.start();
	    }
//	    if(nCityCnt > 0) msgHandler.sendMessage(msgHandler.obtainMessage(3));
	    isRedraw = false;
	    isFirst = false;
	    }
    	if(checkver == false) {
	    	checkver = true;
		    Thread background = new Thread(new Runnable()
			{
			    public void run()
			    {
	        		Message mesg;
	        		String Ver = null;
	        		try
	        		{
	        			Thread.sleep(10);
	        			Ver = checkVersion();
	        		}
	        		catch (InterruptedException e) { e.printStackTrace(); }

	        		if (Ver != null)
	        			mesg = vercheckhandler.obtainMessage(0, Ver);
	        		else
	        			mesg = vercheckhandler.obtainMessage(-1);
	        		vercheckhandler.sendMessage(mesg);
			    }
			});
		    
		    background.start();
	    }

    }

    @Override protected void onPause()
    {
    	super.onPause();
//    	LoadingAnimation.stop();
    }

    final Handler handler = new Handler()
	{
	    public void handleMessage(Message msg)
	    {
	    	Weather.this.Draw();
	    	Calendar cl = Calendar.getInstance();
	    	if (res == null)
	    		res = getResources();
	    	if (updateTime != 0)
	    	    cl.setTimeInMillis(updateTime);
	    	Util.setFooter(footer, cl, TimeFlag, DateFlag);
//	    	LoadingAnimation.stop();
//	    	LoadingLogo.setVisibility(View.INVISIBLE);
	    	progress.setVisibility(View.INVISIBLE);
	    }
	};

    @Override public void onBackPressed()
    {
    	super.onBackPressed();
    	ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
    	am.restartPackage(getPackageName());
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
    	Intent intent;

    	switch (item.getItemId()) {
        case 0:
        	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        	boolean network = Util.checkNetwork(manager);
        	
        	if(!network) {        		
//        		LoadingAnimation.stop();
//        	    LoadingLogo.setVisibility(View.INVISIBLE);
        	    progress.setVisibility(View.INVISIBLE);
        	    
        	    if(alertmain != null)
        	    	return true;

//        	    if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//            		alertmain = new AlertDialog.Builder(Weather.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//            	else
            		alertmain = new AlertDialog.Builder(Weather.this);

        		alertmain.setTitle("네트워크 오류");
        		alertmain.setIcon(R.drawable.ic_dialog_menu_generic);
        		alertmain.setMessage("네트워크 상태를 확인해 주십시오.");

        		alertmain.setPositiveButton("확인", new DialogInterface.OnClickListener() {
    	    		@Override
    			    public void onClick(DialogInterface dialog, int which) {
    	    			dialog.dismiss();
    	    			alertmain = null;
    	    		}
    	    	});

        		alertmain.show();
    	    	return true;
        	}
            new Handler().postDelayed(new Runnable() { public void run() { /*LoadingAnimation.start(); LoadingLogo.setVisibility(View.VISIBLE);*/ progress.setVisibility(View.VISIBLE); } }, 100);

            Thread background = new Thread(new Runnable()
		    {
            	public void run()
            	{
            		try
            		{
            			Thread.sleep(10);
            			getTodayInfo();
            			Message msg = handler.obtainMessage();
            			handler.sendMessage(msg);
            		}
            		catch (Throwable t)
            		{
            			Message msg = handler.obtainMessage();
            			handler.sendMessage(msg);
            		}
            	}
		    });

            background.start();
//            msgHandler.sendMessage(msgHandler.obtainMessage(3));
            

            return true;
            
	    case 1:		// add
	    	intent = new Intent(context, CityGroup.class);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
	    	startActivityForResult(intent, GET_CODE);
	    	return true;
	    	
	    case 2:		// change order
	    	intent = new Intent(context, DndActivity.class);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
	    	intent.putExtra("IsCityList", true);
	    	startActivityForResult(intent, GET_CODE);
	    	return true;
	    	
	    case 3:		// delete
	    	intent = new Intent(context, DeleteWeatherList.class);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
	    	intent.putExtra("IsCityList", true);
	    	startActivityForResult(intent, GET_CODE);
	    	return true;
	    	
	    case 4:		// setup
	    	intent = new Intent(context, Setup.class);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
	    	startActivityForResult(intent, GET_CODE);
	    	return true;
	    	
	    case 5:		// infomation
	    	showInfo();
	    	return true;
    	}
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0,0, "업데이트").setIcon(R.drawable.ic_menu_update);
        menu.add(0, 1,0, "도시 추가").setIcon(R.drawable.ic_menu_new);
        menu.add(0, 2,0, "순서편집").setIcon(R.drawable.ic_menu_change);
        menu.add(0, 3,0, "삭제").setIcon(R.drawable.ic_menu_discard);
        menu.add(0, 4,0, "설정").setIcon(R.drawable.ic_menu_settings);
        menu.add(0, 5,0, "이용안내").setIcon(R.drawable.ic_menu_details);
        return true;
    }

    private void Draw()
    {
    	ArrayList<List2> m_orders;
    	m_orders = new ArrayList<List2>();

    	getSavedValue();

    	List2[] plist;
    	plist = new List2[nCityCnt];

    	for (int i = 0 ; i < nCityCnt ; i++)
	    plist[i] = new List2(CityID[i][0], CityID[i][1], CityID[i][2], CityID[i][3], CityID[i][4],
				 CityID[i][5], CityID[i][6], CityID[i][7]);

        for (int i = 0 ; i < nCityCnt ; i++) m_orders.add(plist[i]);

	if (null == m_adapter)
	    {
		m_adapter = new List2Adapter(Weather.this, R.layout.row, m_orders); // 어댑터를 생성합니다.
		setListAdapter(m_adapter);
	    }
	else m_adapter.setList(m_orders);
	
	m_orders.clear();
	m_orders = null;
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

	nCityCnt = prefs.getInt(Const.CITY_CNT, 0);
	updateTime = prefs.getLong(Const.UPDATE_TIME_LIST, 0);

	if (nCityCnt == 0) return;

	CityID = new String[nCityCnt][8];
	for (int i = 0 ; i < nCityCnt ; i++)
	    {
		Temp = prefs.getString(Const.CITY_LIST + i, "");
		szTemp = Temp.split("\t");
		for (int j = 0 ; j < szTemp.length ; j++)
		    if (szTemp[j].equals("") || szTemp[j] == null || szTemp[j].equals("\n"))
			CityID[i][j] = "--";
		    else
			CityID[i][j] = szTemp[j];
	    }
    }

    private void saveCityList()
    {
	String Temp = "";
	Calendar cl = Calendar.getInstance();
	SharedPreferences.Editor prefs = getSharedPreferences(Const.PREFS_NAME, 0).edit();

	for (int i = 0 ; i < nCityCnt ; i++)
	    {
		prefs.remove(Const.CITY_LIST + i);
		for (int j = 0 ; j < 8 ; j++)
		    Temp = Temp + CityID[i][j] + "\t";

		prefs.putString(Const.CITY_LIST + i, Temp);
		Temp = "";
	    }
	prefs.putInt(Const.CITY_CNT, nCityCnt);
	prefs.putLong(Const.UPDATE_TIME_LIST, cl.getTimeInMillis());
	prefs.commit();
    }
    
//    public boolean checkNetwork() {
//		ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
//    	boolean isMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
//    	boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
//    	if( !isMobile && !isWifi) {
////    		LoadingAnimation.stop();
////    		LoadingLogo.setVisibility(View.INVISIBLE);
//    		return false;
//    	} else {
//    		return true;
//    	}
//    }

    private void getTodayInfo()
    {
	String URL;
	String szCityList = "";
	InputStream fis = null;
	String[] szTemp;
	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
	boolean network = Util.checkNetwork(manager);
	
	if(!network) {
		
//		LoadingAnimation.stop();
//	    LoadingLogo.setVisibility(View.INVISIBLE);
	    progress.setVisibility(View.INVISIBLE);
	    
		AlertDialog.Builder alert = null;
//    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    		alert = new AlertDialog.Builder(Weather.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//    	else
    		alert = new AlertDialog.Builder(Weather.this);

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

	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);

	if (prefs == null){
	    nCityCnt = 0;
	}

	CityID = new String[nCityCnt][8];
	String Temp;
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

	for (int i = 0 ; i < nCityCnt ; i++) szCityList = szCityList + CityID[i][0] + "|";
	URL = Const.URL_LIST + "cnt="+ nCityCnt + "&region=" + szCityList;
	try {
		fis = Util.getByteArrayFromURL(URL, 50000);
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	if (fis == null) return;

	SAXParserFactory parserModel = SAXParserFactory.newInstance();
        SAXParser concreteParser = null;
        try
	    {
		concreteParser   = parserModel.newSAXParser();
	    }
	catch (ParserConfigurationException e1) { e1.printStackTrace(); }
	catch (SAXException e1) { e1.printStackTrace(); }

	XMLReader myReader = null;
	try
	    {
		myReader =  concreteParser.getXMLReader();
	    }
	catch (SAXException e1) { e1.printStackTrace(); }

	MySampleHandlerWeather mySample  = new MySampleHandlerWeather(getApplication());
	myReader.setContentHandler(mySample);
	try
	    {
		myReader.parse(new InputSource(fis));
	    }
	catch (IOException e1) { e1.printStackTrace(); }
	catch (SAXException e1) { e1.printStackTrace(); }

	if(fis != null) {
		try {
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		fis = null;
	}

	szTemp = mySample.gettimezone().toString().split("\t");
	if (szTemp.length < nCityCnt) return;

	szTemp = mySample.getcomment().toString().split("\t");
	if (szTemp.length < nCityCnt) return;

	for (int i = 0 ; i < nCityCnt ; ++i) CityID[i][3] = szTemp[i];

	szTemp = mySample.geticon().toString().split("\t");
	if (szTemp.length < nCityCnt) return;

	for (int i = 0 ; i < nCityCnt ; ++i) CityID[i][4] = szTemp[i];

	szTemp = mySample.gettempcur().toString().split("\t");
	if (szTemp.length < nCityCnt) return;

	for (int i = 0 ; i < nCityCnt ; ++i)
	    if (szTemp[i].equals(""))
		CityID[i][5] = "--";
	    else
		CityID[i][5] = szTemp[i];

	szTemp = mySample.gettempmax().toString().split("\t");
	if (szTemp.length < nCityCnt) return;

	for (int i = 0 ; i < nCityCnt ; ++i) CityID[i][6] = szTemp[i];

	szTemp = mySample.gettempmin().toString().split("\t");
	if (szTemp.length < nCityCnt) return;

	for (int i = 0 ; i < nCityCnt ; i++) CityID[i][7] = szTemp[i];

	saveCityList();
    }

    @Override protected void onListItemClick(ListView l, View v, int position, long id)
    {
        Intent intent;
        intent = new Intent(context, showTodayWeather.class);
        if(CityID != null && CityID.length >= position && CityID[position].length >= 3) {
        	intent.putExtra("CityID", CityID[position][0]);
        	intent.putExtra("CityName", CityID[position][1]);
        	intent.putExtra("TimeZone", CityID[position][2]);
        	intent.putExtra("Index", (int)id);
        }
    	isFirst = true;

        ((TabRoot)getParent()).setCurrentTab(intent);
    }

    private class List2Adapter extends ArrayAdapter<List2>
    {
//        private Context mContext = null;
    	private ArrayList<List2> items;
//        private ViewWrapper wrapper = null;

        public List2Adapter(Context context, int textViewResourceId, ArrayList<List2> items)
	    {
        	super(context, textViewResourceId, items);
        	this.items = items;
//        	mContext = context;
	    }

        public void setList(ArrayList<List2> items)
        {
        	clear();
        	int size = items.size();
        	for (int i = 0; i < size; ++i) add(items.get(i));
        }

        @Override public View getView(int position, View convertView, ViewGroup parent)
	    {
        	View v = convertView;
        	if (v == null)
		    {
        		LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        		LayoutInflater vi = ((Activity)mContext).getLayoutInflater();

        		v = vi.inflate(R.layout.row, null);
        		
//        		wrapper = new ViewWrapper(v);
//        		v.setTag(wrapper);
//		    } else {
//		    	wrapper = (ViewWrapper)v.getTag();
		    }
        	List2 p = items.get(position);
        	if (p != null)
		    {
			// get City info
        		String cityName = p.getCityName();
        		String timeZone = p.getTimezone();
        		String weatherComment = p.getComment();
        		String icon = p.getIcon();
        		String tempCur = collapseTemp(p.getTempCur());
        		String tempMax = collapseTemp(p.getTempMax());
        		String tempMin = collapseTemp(p.getTempMin());

        		((ImageView)v.findViewById(R.id.rowImage)).setImageResource(Util.getIconID(res, icon));
        		((TextView)v.findViewById(R.id.rowCityName)).setText(cityName);
        		((TextView)v.findViewById(R.id.rowCurrentTemp)).setText(Util.setTempStyle(tempCur), TextView.BufferType.SPANNABLE);
        		((TextView)v.findViewById(R.id.rowComment)).setText(weatherComment);

        		((TextView)v.findViewById(R.id.rowFutureTemp)).setText(Util.setTempStyle(tempMax + "/" + tempMin), TextView.BufferType.SPANNABLE);
        		((TextView)v.findViewById(R.id.rowTime)).setText(formatCurrentTime(timeZone));
        		
//        		wrapper.getIcon().setImageBitmap(bIcon);
//        		wrapper.getCityName().setText(cityName);
//        		wrapper.getTempCur().setText(Util.setTempStyle(tempCur), TextView.BufferType.SPANNABLE);
//        		wrapper.getComment().setText(weatherComment);
//        		wrapper.getTemp().setText(Util.setTempStyle(tempMax + "/" + tempMin), TextView.BufferType.SPANNABLE);
//        		wrapper.getTime().setText(formatCurrentTime(timeZone));
        		p = null;
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

	    public String formatCurrentTime(String timezone)
		{
		    Calendar cl;
		    cl = Calendar.getInstance();

		    cl.setTimeZone(TimeZone.getTimeZone(timezone));
		    String result = "";
		    if (DateFlag)
		    	result += String.format("%02d/%02d(%s) ", cl.get(Calendar.MONTH) + 1, cl.get(Calendar.DAY_OF_MONTH), Const.Week_day[cl.get(Calendar.DAY_OF_WEEK)-1]);
		    else
		    	result += String.format("%02d/%02d(%s) ", cl.get(Calendar.DAY_OF_MONTH), cl.get(Calendar.MONTH) + 1, Const.Week_day[cl.get(Calendar.DAY_OF_WEEK)-1]);

		    if (!TimeFlag)
		    	result += String.format(cl.get(Calendar.HOUR_OF_DAY) >= 12 ? "오후 %02d:%02d" : "오전 %02d:%02d", cl.get(Calendar.HOUR), cl.get(Calendar.MINUTE));
		    else
				result += String.format("%02d:%02d", cl.get(Calendar.HOUR_OF_DAY), cl.get(Calendar.MINUTE));
		    
		    return result;
	    }
	    
	    public void ClearMemory(){
	    	super.clear();
//	    	wrapper.ClearMemory();
	    	clear();
	    }
    }
    class ViewWrapper {
    	View base;
    	ImageView icon = null;
    	TextView CityName = null;
    	TextView TempCur = null;
    	TextView Comment = null;
    	TextView Temp = null;
    	TextView Time = null;
		
		ViewWrapper(View base){
			this.base = base;
		}
		
		ImageView getIcon() {
			if(icon == null) {
				icon = (ImageView)base.findViewById(R.id.rowImage);
			}
			
			return icon;
		}
		
		TextView getCityName() {
			if(CityName == null) {
				CityName = (TextView)base.findViewById(R.id.rowCityName);
			}
			return CityName;
		}
		
		TextView getTempCur() {
			if(TempCur == null) {
				TempCur = (TextView)base.findViewById(R.id.rowCurrentTemp);
			}
			return TempCur;
		}
		
		TextView getComment() {
			if(Comment == null) {
				Comment = (TextView)base.findViewById(R.id.rowComment);
			}
			return Comment;
		}
		
		TextView getTemp() {
			if(Temp == null) {
				Temp = (TextView)base.findViewById(R.id.rowFutureTemp);
			}
			return Temp;
		}
		
		TextView getTime() {
			if(Time == null) {
				Time = (TextView)base.findViewById(R.id.rowTime);
			}
			return Time;
		}
		
		void ClearMemory() {
	    	base = null;
	    	icon = null;
	    	CityName = null;
	    	TempCur = null;
	    	Comment = null;
	    	Temp = null;
	    	Time = null;
		}
    }
    class List2
    {
    	private String szCode;
    	private String szCity;
    	private String szTimeZone;
    	private String szWeatherComment;
    	private String szIcon;
    	private String szTempCur;
    	private String szTempMax;
    	private String szTempMin;

    	public List2(String code, String CityName, String TimeZone, String szWC, String Icon,
		     String Tcur, String TMax, String TMin)
	{
	    szCode = code;
	    szCity = CityName;
	    szTimeZone = TimeZone;
	    szWeatherComment = szWC;
	    szIcon = Icon;
	    szTempCur = Tcur;
	    szTempMax = TMax;
	    szTempMin = TMin;
    	}

    	public String getCityCode() { return szCode; }
    	public String getCityName() { return szCity; }
    	public String getTimezone() { return szTimeZone; }
    	public String getComment()  { return szWeatherComment; }
    	public String getTempCur()  { return szTempCur; }
    	public String getIcon()     { return szIcon; }
    	public String getTempMax()  { return szTempMax; }
    	public String getTempMin()  { return szTempMin; }
    }
}

class MySampleHandlerWeather<XmlParser> extends DefaultHandler
{
    private StringBuffer sbRegion_id = new StringBuffer();
    private StringBuffer sbPointName = new StringBuffer();
    private StringBuffer sbIcon = new StringBuffer();
    private StringBuffer sbTempMin = new StringBuffer();
    private StringBuffer sbTempMax = new StringBuffer();
    private StringBuffer sbTempCur = new StringBuffer();
    private StringBuffer sbPop = new StringBuffer();
    private StringBuffer sbComment = new StringBuffer();
    private StringBuffer sbtimezone = new StringBuffer();

    private boolean region_id = false;
    private boolean pointname = false;
    private boolean icon = false;
    private boolean tempmin = false;
    private boolean tempmax = false;
    private boolean tempcur = false;
    private boolean pop = false;
    private boolean comment = false;
    private boolean timezone = false;
    private XmlParser xp;

    public MySampleHandlerWeather(XmlParser xp)
	{
	    this.xp = xp;
	}
    public void startElement(String uri, String localName, String qName, Attributes atts)
    {
	if (localName.equals("region_id"))
	    region_id = true;
	else if (localName.equals("PointName"))
	    pointname = true;
	else if (localName.equals("WX_icon"))
	    icon = true;
	else if (localName.equals("TempMax"))
	    tempmax = true;
	else if (localName.equals("TempMin"))
	    tempmin = true;
	else if (localName.equals("PopAM"))
	    pop = true;
	else if (localName.equals("TempCur"))
	    tempcur = true;
	else if (localName.equals("Comment"))
	    comment = true;
	else if (localName.equals("tz"))
	    timezone = true;
    }

    public void characters(char[] chars, int start, int leng)
    {
	if (region_id)
	    {
		region_id = false;
		sbRegion_id.append(chars, start, leng);
		sbRegion_id.append('\t');
	    }
	else if (pointname)
	    {
		pointname = false;
		sbPointName.append(chars, start, leng);
		sbPointName.append('\t');
	    }
	else if (icon)
	    {
		icon = false;
		sbIcon.append(chars, start, leng);
		sbIcon.append('\t');
	    }
	else if (tempmin)
	    {
		tempmin = false;
		sbTempMin.append(chars, start, leng);
		sbTempMin.append('\t');
	    }
	else if (tempmax)
	    {
		tempmax = false;
		sbTempMax.append(chars, start, leng);
		sbTempMax.append('\t');
	    }
	else if (tempcur)
	    {
		tempcur = false;
		sbTempCur.append(chars, start, leng);
		sbTempCur.append('\t');
	    }
	else if (pop)
	    {
		pop = false;
		sbPop.append(chars, start, leng);
		sbPop.append('\t');
	    }
	else if (comment)
	    {
		comment = false;
		String Temp;

		Temp = new String(chars, start, leng);

		if (Temp.length() <= 1)
		    Temp = " ";

		sbComment.append(Temp);
		sbComment.append('\t');
	    }
	else if (timezone)
	    {
		timezone = false;
		sbtimezone.append(chars, start, leng);
		sbtimezone.append('\t');
	    }
    }

    public StringBuffer getregion_id() { return sbRegion_id; }
    public StringBuffer getpointname() { return sbPointName; }
    public StringBuffer geticon()      { return sbIcon; }
    public StringBuffer gettempmin()   { return sbTempMin; }
    public StringBuffer gettempmax()   { return sbTempMax; }
    public StringBuffer gettempcur()   { return sbTempCur; }
    public StringBuffer getpop()       { return sbPop; }
    public StringBuffer getcomment()   { return sbComment; }
    public StringBuffer gettimezone()  { return sbtimezone; }
}
