/*
 * This file is a modified version of the original TabHost.java file
 * in the android SDK version 7.
 * Original copyright notice follows :
 *
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.weathernews.Weather;

import java.util.List;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityGroup;
import android.os.Bundle;
import android.os.StrictMode;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ImageView;
import com.weathernews.Weather.TabHostHide.TabSpec;

public class TabRootHorizontal extends EnhancedTabActivity
{
    protected OrientationEventListener ol;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
	Display display = wm.getDefaultDisplay();
	int rotation = display.getRotation();
	
	if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
		finish();

	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.tabroothorizontal);

//	Resources res = getResources(); // Resource object to get Drawables
	mTabHost = getTabHost();  // The activity TabHost
	TabHostHide.TabSpec spec;  // Resusable TabSpec for each tab
	Intent intent;  // Reusable Intent for each tab

	// Create an Intent to launch an Activity for the tab (to be reused)
	intent = new Intent().setClass(this, Land48h.class);
//	intent = new Intent().setClass(this, Dummy.class);
	intent.replaceExtras(getIntent());

	boolean isKorea = "Asia/Seoul".equals(intent.getStringExtra("TimeZone"));

	// Initialize a TabSpec for each tab and add it to the TabHostHide
	spec = mTabHost.newTabSpec("Land48h").setIndicator("48?????? ??????").setContent(intent);
	if(isKorea)
		spec.link = 3;
	else
		spec.link = 2;
	mTabHost.addTab(spec);

	intent = new Intent().setClass(this, Land48hGraph.class);
	intent.replaceExtras(getIntent());
	spec = mTabHost.newTabSpec("Land48hGraph").setIndicator("").setContent(intent);
	spec.link = 0;
	mTabHost.addTab(spec);

	// Do the same for the other tabs
	intent = new Intent().setClass(this, Land10days.class);
	intent.replaceExtras(getIntent());
	spec = mTabHost.newTabSpec("Land10days").setIndicator("?????? ??????").setContent(intent);
	if(isKorea)
		spec.link = 4;
	else
		spec.link = 3;
	mTabHost.addTab(spec);

	intent = new Intent().setClass(this, Land10daysGraph.class);
	intent.replaceExtras(getIntent());
	spec = mTabHost.newTabSpec("Land10daysGraph").setIndicator("").setContent(intent);
	spec.link = 1;
	mTabHost.addTab(spec);

	if (isKorea){
		intent = new Intent().setClass(this, LandSattle.class);
		intent.replaceExtras(getIntent());
		spec = mTabHost.newTabSpec("LandSattle").setIndicator("????????????").setContent(intent);
		spec.link = 5;
		mTabHost.addTab(spec);

		intent = new Intent().setClass(this, LandRadar.class);
		intent.replaceExtras(getIntent());
		spec = mTabHost.newTabSpec("LandRadar").setIndicator("").setContent(intent);
		spec.link = 2;
		mTabHost.addTab(spec);
	}

	mTabHost.setCurrentTab(getIntent().getIntExtra("StartTab", 0));

	ol = new OrientationEventListener(this) {
		@Override public void onOrientationChanged(int orientation)
		{
		    if ((orientation >= 0 && orientation <= 35) || (orientation >= 315) || (orientation >= 145 && orientation <= 215))
			{
                            this.disable();
                            finish();
			}
		}
	};
	ol.enable();
    }

    @Override public void onPause()
    {
    	super.onPause();	// ?????????????????????????????????????????????
    	ol.disable();
    }

    @Override public void onResume()
    {
    	super.onResume();
    	ol.enable();
    }

    @Override public void onDestroy()
    {
//    	Log.d("myTag", "onDestroy TabRootHorizontal");
    	super.onDestroy();
    	mTabHost.getTabWidget().ClearMemory();
    	System.gc();
    }
}
