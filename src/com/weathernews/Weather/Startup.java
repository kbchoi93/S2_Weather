package com.weathernews.Weather;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

public class Startup extends Activity {
//	private static View main_view = null;
	
//	private int REQ_CODE = 1000;
//	private Boolean isFinish = false;
//	@Override protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//		if (requestCode == REQ_CODE) {
//			isFinish = true;
//			finish();
//		}
//	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        if(main_view == null) {
//        	LayoutInflater layout = getLayoutInflater();
//        	main_view = layout.inflate(R.layout.noformat, null);
//        }
        setContentView(R.layout.noformat);
        boolean InstallBaseList = false;

        // Do something to Startup

        SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);

        if(prefs == null)
        	InstallBaseList = true;
        else
        	InstallBaseList = prefs.getBoolean("RUN", true);

        SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
        prefs1.putBoolean("START", false);
        prefs1.putBoolean("USE_GPS", false);

        if(InstallBaseList) {
        	String Temp = "11B10101" + "\t" + "서울" + "\t" + "Asia/Seoul" + "\t" +
        				"--" + "\t" +  R.drawable.weather_icon_01_ref + "\t" +  "-999" + "\t" +
        				"-999" + "\t" + "-999";

        	prefs1.putString(Const.CITY_LIST + 0, Temp);

        	prefs1.putInt(Const.CITY_CNT, 1);

        	Temp = "MO023" + "\t" +  "11D20401" + "\t" + "설악산" + "\t" + "강원도 속초시" + "\t" +
        			"--" + "\t" + R.drawable.weather_icon_01_ref + "\t" + "--" + "\t" + "--" + "\t" + "--" + "\t" + Theme.THEME_MOUNTAIN;
        	prefs1.putString(Const.THEME_LIST + 0, Temp);
        	Temp = "BA000" + "\t" +  "11B10101" + "\t" + "잠실 야구장" + "\t" + "서울특별시 송파구 잠실1동 10" + "\t" +
					"--" + "\t" + R.drawable.weather_icon_01_ref + "\t" + "--" + "\t" + "--" + "\t" + "--" + "\t" + Theme.THEME_BASEBALL;
        	prefs1.putString(Const.THEME_LIST + 1, Temp);
        	Temp = "PA000" + "\t" +  "11B20601" + "\t" + "에버랜드" + "\t" + "경기도 용인시 처인구 포곡읍 전대리 310" + "\t" +
        			"--" + "\t" + R.drawable.weather_icon_01_ref + "\t" + "--" + "\t" + "--" + "\t" + "--" + "\t" + Theme.THEME_THEMEPARK;
        	prefs1.putString(Const.THEME_LIST + 2, Temp);

        	prefs1.putInt(Const.THEME_CNT, 3);

        	prefs1.putLong(Const.UPDATE_TIME_LIST, 0);
        	prefs1.putLong(Const.THEME_LIST_UPDATETIME, 0);

        	prefs1.putBoolean("RUN", false);
        }
        prefs1.commit();
    	checkNetwork();
	}
	
	private void checkNetwork() {
		// TODO Auto-generated method stub
		ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
    	boolean isMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
    	boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
    	
    	Intent intent1 = getIntent();
    	final int tab_idx = intent1.getIntExtra("SET_TAB", -1);
    	
    	if(!isWifi && isMobile){
			AlertDialog.Builder alert = null;
//	    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//	    		alert = new AlertDialog.Builder(Startup.this, AlertDialog.THEME_HOLO_DARK);
//	    	else
	    		alert = new AlertDialog.Builder(Startup.this);
	    	alert.setTitle("네트워크 이용안내");
	    	alert.setIcon(R.drawable.ic_dialog_menu_generic);
	    	alert.setMessage("데이터 네트워크 연결시 가입하신 요금제에 따라 데이터통화료가 발생할수 있습니다.\n연결하시겠습니까?");

	    	alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
	    		@Override
			    public void onClick(DialogInterface dialog, int which) {
	    			if(dialog != null)
	    				dialog.dismiss();
	    			Intent intent = new Intent(Startup.this, TabRoot.class);
	    	        intent.putExtra("FIRST", true);
	    	        intent.putExtra("SET_TAB", tab_idx);
	    			startActivity(intent);
	    			finish();

	    		}
	    	});
	    	alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
	    		@Override
			    public void onClick(DialogInterface dialog, int which) {
	    			if(dialog != null)
	    				dialog.dismiss();
	    			finish();
	    		}
	    	});
	    	
	    	OnCancelListener onCancelListener = new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
	    			if(dialog != null)
	    				dialog.dismiss();
	    			finish();
				}
			};

	    	alert.setOnCancelListener(onCancelListener);

	    	alert.show();
	    	
	    	return;
    	} else {
    		Intent intent = new Intent(Startup.this, TabRoot.class);
	        intent.putExtra("FIRST", true);
	        intent.putExtra("SET_TAB", tab_idx);
			startActivity(intent);
			finish();
    	}
	}

	
    @Override
	protected void onDestroy(){
    	super.onDestroy();
//    	main_view = null;
//		if (isFinish){
//			android.os.Process.killProcess(android.os.Process.myPid());
//		}

    }
}
