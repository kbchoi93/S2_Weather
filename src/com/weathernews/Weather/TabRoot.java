package com.weathernews.Weather;

import java.util.List;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityGroup;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import com.weathernews.Weather.TabHostHide;
import com.weathernews.Weather.TabHostHide.TabSpec;
import android.view.OrientationEventListener;

public class TabRoot extends EnhancedTabActivity
{
    protected OrientationEventListener ol;
    protected boolean resetOrientation;
    protected Context context;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.tabroot);

	Resources res = getResources(); // Resource object to get Drawables
	mTabHost = getTabHost();  // The activity TabHost
	TabHostHide.TabSpec spec;  // Resusable TabSpec for each tab
	Intent intent;  // Reusable Intent for each tab

	// Create an Intent to launch an Activity for the tab (to be reused)
	intent = new Intent().setClass(this, Weather.class);
	intent.putExtra("FIRST", true);
	
	getTabWidget().setDividerDrawable(R.drawable.tw_tab_divider_holo);
//	getTabWidget().setLeftStripDrawable(R.drawable.tw_tab_unselected_bar_holo);
//	getTabWidget().setRightStripDrawable(R.drawable.tw_tab_unselected_bar_holo);

	// Initialize a TabSpec for each tab and add it to the TabHostHide
	spec = mTabHost.newTabSpec("Weather").setIndicator("날씨", res.getDrawable(R.drawable.tab_icon_01)).setContent(intent);
	mTabHost.addTab(spec);
	res.getDrawable(R.drawable.tab_icon_01).setCallback(null);

	intent = new Intent().setClass(this, showTodayWeather.class);
	spec = mTabHost.newTabSpec("showTodayWeather").setIndicator("").setContent(intent);
	mTabHost.addTab(spec); // Hidden because the indicator is the empty string

	// Do the same for the other tabs
	intent = new Intent().setClass(this, ThemeList.class);
	spec = mTabHost.newTabSpec("ThemeList").setIndicator("테마날씨", res.getDrawable(R.drawable.tab_icon_02)).setContent(intent);
	mTabHost.addTab(spec);
	res.getDrawable(R.drawable.tab_icon_02).setCallback(null);

	intent = new Intent().setClass(this, TodayThemeWeather.class);
	spec = mTabHost.newTabSpec("TodayThemeWeather").setIndicator("").setContent(intent);
	mTabHost.addTab(spec); // Hidden because the indicator is the empty string

	intent = new Intent().setClass(this, LiveCam.class);
	intent.putExtra("Mapid", 0);
	spec = mTabHost.newTabSpec("LiveCam").setIndicator("Live Cam", res.getDrawable(R.drawable.tab_icon_03)).setContent(intent);
	mTabHost.addTab(spec);

	res.getDrawable(R.drawable.tab_icon_03).setCallback(null);
	intent = new Intent().setClass(this, ViewLiveCam.class);
	spec = mTabHost.newTabSpec("ViewLiveCam").setIndicator("").setContent(intent);
	mTabHost.addTab(spec); // Hidden because the indicator is the empty string
	

	Intent intent1 = getIntent();
	int tab_idx = -1;
	if(intent1 != null)
		tab_idx = intent1.getIntExtra("SET_TAB", -1);

	if(tab_idx == 1 || tab_idx == 2)
		mTabHost.setCurrentTab(tab_idx);
	else
		mTabHost.setCurrentTab(0);

	resetOrientation = true;

	ol = new OrientationEventListener(this) {
		@Override public void onOrientationChanged(int orientation)
		{
		    if (((orientation > 245 && orientation < 285) || (orientation > 65 && orientation < 115)) && resetOrientation)
			{
		    	try {
		    		int olOption = System.getInt(getContentResolver(), System.ACCELEROMETER_ROTATION);
					if(olOption == 0)
						return;
				} catch (SettingNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    if (1 == mTabHost.mCurrentTab)
				{
				    resetOrientation = false;
				    Intent intent = new Intent(TabRoot.this, MapTest.class);
				    startActivity(intent);
				}
			    if (3 == mTabHost.mCurrentTab)
				{
				    resetOrientation = false;
				    Intent original = mTabHost.getIntent();
				    Intent intent = new Intent(TabRoot.this, TabRootHorizontal.class);
				    if(original.getStringExtra("CityID") == null) {
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
			        	original.putExtra("CityID", CityID[0][0]);
			        	original.putExtra("CityName", CityID[0][1]);
			        	original.putExtra("TimeZone", CityID[0][2]);
			        	original.putExtra("Index", (int)0);
				    }
				    intent.replaceExtras(original);
				    intent.putExtra("StartTab", 0);
				    intent.putExtra("From", "CITY");
				    startActivity(intent);
				}
			    else if (4 == mTabHost.mCurrentTab)
				{
				    resetOrientation = false;
				    Intent original = mTabHost.getIntent();
				    Intent intent = new Intent(TabRoot.this, TabRootHorizontal.class);
				    if(original.getStringExtra("ThemeCode") == null) {
				    	String Temp = null;
			        	String[] szTemp;
			        	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
			        	int nCityCnt;

			        	if (prefs == null){
			        		nCityCnt = 0;
			        		return;
			        	}

			        	nCityCnt = prefs.getInt(Const.THEME_CNT, 0);

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

			        	intent.putExtra("ThemeCode", CityID[0][0]);
			        	intent.putExtra("CityID", CityID[0][1]);
			        	intent.putExtra("CityName", CityID[0][2]);
			        	intent.putExtra("Index", 0);
			        	intent.putExtra("ThemeID", Integer.parseInt(CityID[0][9]));
				    }
				    intent.replaceExtras(original);

				    //				    intent2.putExtra("CityID", original.getStringExtra(""));
				    intent.putExtra("StartTab", 0);
				    intent.putExtra("THEMEAREA", original.getStringExtra("CityID"));

				    intent.putExtra("From", "THEME");
				    intent.putExtra("TimeZone", "Asia/Seoul");

				    startActivity(intent);
				}
			}
		    else if (((orientation >= 0 && orientation <= 45) || (orientation >= 305) || (orientation >= 135 && orientation <= 225)) && orientation != -1)
			resetOrientation = true;
		}
	    };
    }

    @Override public void onPause()
    {
	super.onPause();	// ←こっちを先にやらないとダメ？
	ol.disable();
    }

    @Override public void onResume()
    {
    	super.onResume();
        ol.enable();
    }

    @Override public void onStop()
    {
	super.onStop();
    }

    @Override public void onDestroy()
    {
    	super.onDestroy();
    	mTabHost.getTabWidget().ClearMemory();
    	java.lang.System.gc();
    }

    @Override public void onSaveInstanceState(Bundle b)
    {
    	super.onSaveInstanceState(b);
	int oldTab = mTabHost.getTabWidget().getCurrentTab();
    	b.putInt("tabnum", oldTab);
    }
    @Override public void onRestoreInstanceState(Bundle b)
    {
    	super.onRestoreInstanceState(b);
    	if (null == b) return;
    	int oldTab = b.getInt("tabnum", 0);
    	mTabHost.getTabWidget().setCurrentTab(oldTab);
    }


    @Override public void onBackPressed()
    {
    	if (5 == mTabHost.getCurrentTab() && 0 == mTabHost.getCurrentVisibleTab())
	    mTabHost.setCurrentTab(3 + mTabHost.getCurrentVisibleTab()); // Stupid special case when displaying camera from detail weather
	else
	    mTabHost.setCurrentTab(mTabHost.getCurrentVisibleTab());
    }
}
