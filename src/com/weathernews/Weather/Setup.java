package com.weathernews.Weather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class Setup extends Activity {
	private FrameLayout btn1;
	private FrameLayout btn2;
	private FrameLayout btn3;
	private FrameLayout btn4;
	private FrameLayout btn5;
	private FrameLayout btn6;
	
	private ImageView ivRadio1;
	private ImageView ivRadio2;
	private ImageView ivRadio3;
	private ImageView ivRadio4;
	private ImageView ivRadio5;
	private ImageView ivRadio6;
	

	private boolean DateFlag;
	private boolean TimeFlag;
	private boolean TempFlag;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setup);
        
        btn1 = (FrameLayout) findViewById(R.id.setupbtn1);
        btn2 = (FrameLayout) findViewById(R.id.setupbtn2);
        btn3 = (FrameLayout) findViewById(R.id.setupbtn3);
        btn4 = (FrameLayout) findViewById(R.id.setupbtn4);
        btn5 = (FrameLayout) findViewById(R.id.setupbtn5);
        btn6 = (FrameLayout) findViewById(R.id.setupbtn6);
        
        ivRadio1 = (ImageView) findViewById(R.id.radiobtn1);
        ivRadio2 = (ImageView) findViewById(R.id.radiobtn2);
        ivRadio3 = (ImageView) findViewById(R.id.radiobtn3);
        ivRadio4 = (ImageView) findViewById(R.id.radiobtn4);
        ivRadio5 = (ImageView) findViewById(R.id.radiobtn5);
        ivRadio6 = (ImageView) findViewById(R.id.radiobtn6);
        
        ivRadio1.setImageResource(R.drawable.btn_radio_on);
        ivRadio2.setImageResource(R.drawable.btn_radio_off_popup);
        ivRadio3.setImageResource(R.drawable.btn_radio_on);
        ivRadio4.setImageResource(R.drawable.btn_radio_off_popup);
        ivRadio5.setImageResource(R.drawable.btn_radio_on);
        ivRadio6.setImageResource(R.drawable.btn_radio_off_popup);
        
        SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
        if(prefs == null) {
            ivRadio1.setImageResource(R.drawable.btn_radio_on);
            ivRadio2.setImageResource(R.drawable.btn_radio_off_popup);
            ivRadio3.setImageResource(R.drawable.btn_radio_on);
            ivRadio4.setImageResource(R.drawable.btn_radio_off_popup);
            ivRadio5.setImageResource(R.drawable.btn_radio_on);
            ivRadio6.setImageResource(R.drawable.btn_radio_off_popup);
        }

        DateFlag = prefs.getBoolean("DateFlag", true);
        TimeFlag = prefs.getBoolean("TimeFlag", true);
        TempFlag = prefs.getBoolean("TempFlag", true);
        
        if(DateFlag){
            ivRadio1.setImageResource(R.drawable.btn_radio_on);
            ivRadio2.setImageResource(R.drawable.btn_radio_off_popup);
        } else {
            ivRadio1.setImageResource(R.drawable.btn_radio_off_popup);
            ivRadio2.setImageResource(R.drawable.btn_radio_on);
        }
        
        if(TimeFlag) {
            ivRadio3.setImageResource(R.drawable.btn_radio_on);
            ivRadio4.setImageResource(R.drawable.btn_radio_off_popup);
        } else {
            ivRadio3.setImageResource(R.drawable.btn_radio_off_popup);
            ivRadio4.setImageResource(R.drawable.btn_radio_on);
        }
        
        if(TempFlag) {
            ivRadio5.setImageResource(R.drawable.btn_radio_on);
            ivRadio6.setImageResource(R.drawable.btn_radio_off_popup);
        } else {
            ivRadio5.setImageResource(R.drawable.btn_radio_off_popup);
            ivRadio6.setImageResource(R.drawable.btn_radio_on);
        }
        
        btn1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	            ivRadio1.setImageResource(R.drawable.btn_radio_on);
	            ivRadio2.setImageResource(R.drawable.btn_radio_off_popup);
				SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
		        prefs1.putBoolean("DateFlag", true);
		        prefs1.commit();
			}
		});
        
        btn2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	            ivRadio1.setImageResource(R.drawable.btn_radio_off_popup);
	            ivRadio2.setImageResource(R.drawable.btn_radio_on);
				SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
		        prefs1.putBoolean("DateFlag", false);
		        prefs1.commit();
			}
		});
        
        btn3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	            ivRadio3.setImageResource(R.drawable.btn_radio_on);
	            ivRadio4.setImageResource(R.drawable.btn_radio_off_popup);
				SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
		        prefs1.putBoolean("TimeFlag", true);
		        prefs1.commit();
			}
		});
        
        btn4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	            ivRadio3.setImageResource(R.drawable.btn_radio_off_popup);
	            ivRadio4.setImageResource(R.drawable.btn_radio_on);
				SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
		        prefs1.putBoolean("TimeFlag", false);
		        prefs1.commit();
			}
		});

        btn5.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	            ivRadio5.setImageResource(R.drawable.btn_radio_on);
	            ivRadio6.setImageResource(R.drawable.btn_radio_off_popup);
				SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
		        prefs1.putBoolean("TempFlag", true);
		        prefs1.commit();
			}
		});
        
        btn6.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	            ivRadio5.setImageResource(R.drawable.btn_radio_off_popup);
	            ivRadio6.setImageResource(R.drawable.btn_radio_on);
				SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
		        prefs1.putBoolean("TempFlag", false);
		        prefs1.commit();
			}
		});
        
        setResult(RESULT_OK, (new Intent()).setAction("SETUP"));
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}