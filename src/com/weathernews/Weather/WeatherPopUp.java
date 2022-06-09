package com.weathernews.Weather;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class WeatherPopUp extends Activity
{
    private TextView title;
    private TextView tempcur;
    private TextView tempmax;
    private TextView tempmin;
    private TextView comment;
    private ImageView icon;
    private boolean TempFlag = true;
    private String TempSymbol;
    private LinearLayout ll;
//    private Bitmap bIcon;
    
    protected OrientationEventListener ol;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Intent intent = getIntent();

    	TempFlag = Util.getTempFlag(this);

        if (TempFlag)
	    TempSymbol = "℃";
     	else
	    TempSymbol = "℉";

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();    lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.0f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dialogmap);

        ll = (LinearLayout) findViewById(R.id.map_popup);
        title = (TextView) findViewById(R.id.title);
        tempcur = (TextView) findViewById(R.id.theme_tempcur);
        tempmax = (TextView) findViewById(R.id.theme_tempmax);
        tempmin = (TextView) findViewById(R.id.theme_tempmin);
        comment = (TextView) findViewById(R.id.theme_comment);
        icon = (ImageView) findViewById(R.id.themeicon);
        if (!TempFlag) {
	    ((TextView) findViewById(R.id.tempsymbol1)).setText(TempSymbol);
	    ((TextView) findViewById(R.id.tempsymbol2)).setText(TempSymbol);
	    ((TextView) findViewById(R.id.tempsymbol3)).setText(TempSymbol);
        }

        String sTitle = intent.getStringExtra("PointName");
        int nMapViewWidth = intent.getIntExtra("Width", 0);
        int nMapViewHeight = intent.getIntExtra("Height", 0);
        int nMarkerHeight = intent.getIntExtra("IconHeight", 0);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(310, 182);
        params.setMargins(nMapViewWidth / 2 - 50, nMapViewHeight / 2 - 182 - nMarkerHeight + 95, 0, 0);
//        params.setMargins(0, nMapViewHeight / 2 - 182 - nMarkerHeight, 0, 0);
        ll.setLayoutParams(params);
        
    	title.setText(sTitle);
        if(sTitle.length() > 10)
        	title.setTextSize(TypedValue.COMPLEX_UNIT_PX, 22);
        
        comment.setText(intent.getStringExtra("Comment"));
        if (!TempFlag) {
	    tempcur.setText(Util.getFahrenheit(intent.getStringExtra("TempCur")));
	    tempmax.setText(Util.getFahrenheit(intent.getStringExtra("TempMax")));
	    tempmin.setText(Util.getFahrenheit(intent.getStringExtra("TempMin")));
        } else {
	    tempcur.setText(intent.getStringExtra("TempCur"));
	    tempmax.setText(intent.getStringExtra("TempMax"));
	    tempmin.setText(intent.getStringExtra("TempMin"));
        }
        icon.setImageResource(Util.getWeekIconID(getResources(), intent.getStringExtra("Icon")));
        
        ol = new OrientationEventListener(this) {
    		@Override public void onOrientationChanged(int orientation)
    		{
    			if ((orientation > 0 && orientation <= 45) || (orientation >= 305) || (orientation >= 135 && orientation <= 225))
    		    {
    		    	this.disable();
    		    	finish();
    			}
    		}
    	};
    	ol.enable();
    }

    @Override public boolean onTouchEvent(MotionEvent event)
    {
	finish();
	return true;
    }
    @Override
	protected void onPause()
    {
    	super.onPause();
    	ol.disable();
//    	bIcon.recycle();
    	finish();
    }
}
