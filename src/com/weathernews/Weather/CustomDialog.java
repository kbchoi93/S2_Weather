package com.weathernews.Weather;


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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;

public class CustomDialog extends Activity {

	private ImageView imagetop;
	private ImageView imageTableBG;
	private TextView Comment;
	private String TempSymbol = "℃";
	private BaseInfo info;
	private TodayData today = new TodayData();
	private long updateTime;
	private boolean isToday;
	private boolean TempFlag = true;
	private LinearLayout topLayer;
	private Bitmap bTopImage;
	private Bitmap bTableImage;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        Intent intent = getIntent();
        isToday = intent.getBooleanExtra("IsToday", false);
        
        if(isToday){
        	info = new BaseInfo(intent.getStringExtra("CityID"), intent.getStringExtra("CityName"), 
        						intent.getStringExtra("TimeZone"), intent.getIntExtra("Index", 0));
        }
        else{
        	info = new BaseInfo(intent.getStringExtra("CityID"), intent.getStringExtra("CityName"), 
								intent.getStringExtra("TimeZone"), intent.getIntExtra("Index", 0));
        }
        
        //Log.d("myTag", "isToday=" + isToday + " ID=" + info.getCityID() + " Name=" + info.getCityName());
        
    	TempFlag = Util.getTempFlag(this);
        
        getSaveData();
       // 이부분이 화면을 dimming시킴
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();    lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.0f;
         //lpWindow.windowAnimations = android.R.anim.accelerate_interpolator | android.R.anim.fade_in | android.R.anim.fade_out;
        getWindow().setAttributes(lpWindow);
         
        setContentView(R.layout.dialog);
        
        topLayer = (LinearLayout) findViewById(R.id.dialog_top); 
        if(TempFlag)
     		TempSymbol = "℃";
     	else
     		TempSymbol = "℉";

        imagetop = (ImageView) findViewById(R.id.ImageView01);
        bTopImage = getTopImg();
        imagetop.setImageBitmap(bTopImage);
        
        
        Comment = (TextView) findViewById(R.id.dialog_comment);
        Comment.setText(today.getTodayExplain());
         
         // 애니메이션을 수행 왼쪽에서 박스가 나와서 화면의 가운데에 위치하게 됨
        /*
         TranslateAnimation anim=new TranslateAnimation(   
           TranslateAnimation.RELATIVE_TO_PARENT, -1.0f, 
           TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
           TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
           TranslateAnimation.RELATIVE_TO_SELF, 0.0f);
         anim.setDuration(500);
         anim.setFillAfter(true);
         findViewById(R.id.TableLayout01).setAnimation(anim);
         anim.start();
         */
	}

	private Bitmap getTopImg() {
		// TODO Auto-generated method stub
		Bitmap bm = Bitmap.createBitmap(450, 159, Config.ARGB_4444);
		Resources res = getResources();
		
		//Bitmap bg = BitmapFactory.decodeResource(res, R.drawable.weather_popup02_top);
		//Bitmap icon1 = BitmapFactory.decodeResource(res, R.drawable.weather_icon_01);
		
		Bitmap bg = BitmapFactory.decodeResource(res, Util.getIconID(res, today.getTodayIcon()));
		imageTableBG = (ImageView) findViewById(R.id.table);
		bTableImage = getTableBG();
        imageTableBG.setImageBitmap( bTableImage );
		
		Canvas canvas = new Canvas(bm);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		Paint paint1 = new Paint();
		paint1.setAntiAlias(true);
		paint1.setColor(Const.FONT_COLOR_WHITE);
		Paint paint2 = new Paint();
		paint2.setAntiAlias(true);
		paint2.setColor(Const.FONT_COLOR_WHITE);
		
		Rect rc_icon = new Rect(316, 20, 416, 120);
		
		canvas.drawBitmap(bg, null, rc_icon, null);
		bg.recycle();
		
		paint.setColor(Const.FONT_COLOR_WHITE);
		
		if(info.getCityName().length() > 10)
			paint.setTextSize(28);
		else 
			paint.setTextSize(32);
		canvas.drawText(info.getCityName(), 20, 55, paint);
		
		paint.setTextSize(60);
		paint1.setTextSize(40);
		paint2.setTextSize(32);
		float t1 = 20;
		String szlTempMax = today.getTodayTempMax();
		String szlTempMin = today.getTodayTempMin();
		String szlTempCur = today.getTodayTempCur();
		String szlTempFeel = today.getTodayTempFeel();
		
		if(!TempFlag) {
			szlTempMax = Util.getFahrenheit(szlTempMax);
			szlTempMin = Util.getFahrenheit(szlTempMin);
			szlTempCur = Util.getFahrenheit(szlTempCur);
			szlTempFeel = Util.getFahrenheit(szlTempFeel);
		}
		
		canvas.drawText(szlTempMax, t1, 122, paint);
		t1 += paint.measureText(szlTempMax);
		canvas.drawText(TempSymbol, t1, 122, paint1);
		t1 += paint1.measureText(TempSymbol);
		canvas.drawText("/", t1, 122, paint2);
		t1 += paint2.measureText("/");
		canvas.drawText(szlTempMin, t1, 122, paint);
		t1 += paint.measureText(szlTempMin);
		canvas.drawText(TempSymbol, t1, 122, paint1);
		
		paint.setTextSize(22);
        paint1.setTextSize(16);
        canvas.drawText("현재기온" + szlTempCur, 20, 146, paint);
        t1 = paint.measureText("현재기온" + szlTempCur) + 20;
        canvas.drawText(TempSymbol + "/", t1, 146, paint1);
        t1 += paint1.measureText(TempSymbol + "/");
        canvas.drawText("체감기온" + szlTempFeel, t1, 146, paint);
        t1 += paint.measureText("체감기온" + szlTempFeel);
        canvas.drawText(TempSymbol, t1, 146, paint1);
		//canvas.drawText("현재기온4℃/체감기온8℃", 20, 146, paint);
		
		return bm;
	}

	private void getSaveData() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
    	String[] TempArray;
    	String Temp;
 
    	if(isToday) {
    		updateTime = prefs.getLong(Const.UPDATE_TIME_TODAY + info.getCityID(), 0);
    		Temp = prefs.getString(Const.Today_Today + info.getCityID(), "");
    		//Log.d("myTag", "UPDATENAME=" + Const.UPDATE_TIME_TODAY + info.getCityID());
    		//Log.d("myTag", "ID=" + Const.Today_Today + info.getCityID());
    	}else{
    		updateTime = prefs.getLong(Const.THEME_TODAY_UPDATETIME + info.getCityID(), 0);
    		Temp = prefs.getString(Const.THEME_TODAY_WEATHER + info.getCityID(), "");
    		//Log.d("myTag", "UPDATENAME=" + Const.THEME_TODAY_UPDATETIME + info.getCityID());
    		//Log.d("myTag", "ID=" + Const.THEME_TODAY_WEATHER + info.getCityID());
    	}
    		
    	//Log.d("myTag", "Temp=" + Temp);
    	
    	if(Temp.equals("") || Temp.length() <= 0){
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
        	today.setTodayIndex1("--,--");
        	today.setTodayIndex2("--,--");
        	today.setTodayExplain("--");
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
        	
        	//Log.d("myTag", "setTodayTempFeel= " +  TempArray[9]);
    	}
	}

	private Bitmap getTableBG() {
		// TODO Auto-generated method stub
		System.gc();
		Bitmap bm = Bitmap.createBitmap(450, 210, Bitmap.Config.ARGB_4444);
		Rect rcBg1 = new Rect(0, 0, 450, 210);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		Resources res = getResources();
		
		Canvas canvas = new Canvas(bm);
		Bitmap bg1 = BitmapFactory.decodeResource(res, R.drawable.weather_popup02_middle);
		
		String[] Index1;
		String[] Index2;
		
		Index1 = today.getTodayIndex1().split(",");
		Index2 = today.getTodayIndex2().split(",");
		
		paint.setColor(Color.rgb(42, 42, 49));		
		canvas.drawBitmap(bg1, null, rcBg1, null);
		bg1.recycle();
		canvas.drawRect(3, 0, 446, 444, paint);
		paint.setColor(Color.rgb(239, 225, 148));
		//paint.setTypeface(Const.typeFace);
		paint.setTextSize(20);
		
		canvas.drawText("강수확률", 20, 47, paint);
		canvas.drawText("풍향", 242, 47, paint);
		if(Index1[0] != null && Index1[0].length() > 0)
			canvas.drawText(Index1[0], 20, 94, paint);
		else
			canvas.drawText("자외선지수", 20, 94, paint);
		canvas.drawText("풍속", 242, 94, paint);
		if(Index2[0] != null && Index2[0].length() > 0)
			canvas.drawText(Index2[0], 20, 141, paint);
		else
			canvas.drawText("황사지수", 20, 141, paint);
		canvas.drawText("가시거리", 242, 141, paint);
		canvas.drawText("기압", 20, 188, paint);
		canvas.drawText("습도", 242, 188, paint);
		
		paint.setColor(Const.FONT_COLOR_WHITE);
		paint.setTextSize(22);
		canvas.drawText(today.getTodayPop() + "%", 130, 47, paint);
		canvas.drawText(Util.getWindDirText(today.getTodayWindDir()), 352, 47, paint);
		
		try{
			if(Index1[1] != null && Index1[1].length() > 0)
				canvas.drawText(Index1[1] + "%", 130, 94, paint);
			else
				canvas.drawText( "--%", 130, 94, paint);
		} catch(ArrayIndexOutOfBoundsException e){
			canvas.drawText( "--%", 130, 94, paint);
		}
		
		canvas.drawText(today.getTodayWindSpeed() + "m/s", 352, 94, paint);
		try{ 
			if(Index2[1] != null && Index2[1].length() > 0)
				canvas.drawText(Index2[1] + "%", 130, 141, paint);
			else
				canvas.drawText("--%", 130, 141, paint);
		} catch(ArrayIndexOutOfBoundsException e){
			canvas.drawText("--%", 130, 141, paint);
		}
		
		canvas.drawText(today.getTodayVisi() + "km", 352, 141, paint);
		canvas.drawText(today.getTodayPress() + "hPa", 130, 188, paint);
		canvas.drawText(today.getTodayHum() + "%", 352, 188, paint);
		
		return bm;
	}
	
	final Handler Capture = new Handler(){
    	public void handleMessage(Message msg) {
	    	dialog = new ProgressDialog(CustomDialog.this);
	        dialog.setIndeterminate(true);
	        dialog.setCancelable(false);
	        dialog.setMessage("이미지 저장중..");
	        dialog.closeOptionsMenu();
	        dialog.show();
    	}
	};
	
	ProgressDialog dialog;
	
    final Handler sendhandler = new Handler() {
    	public void handleMessage(Message msg) {
        	if (msg.what == -1) {
				AlertDialog.Builder alert = null;
//		    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//		    		alert = new AlertDialog.Builder(CustomDialog.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//		    	else
		    		alert = new AlertDialog.Builder(CustomDialog.this);
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
        	} else if(msg.what == 0) {
        		if(dialog != null) {
        			try {
        				if(dialog.isShowing() == true)
        					dialog.dismiss();
        			} catch (IllegalArgumentException e) {
        				e.printStackTrace();
        			}
        		}
        	}
	    }
    };

	@Override
    public boolean onOptionsItemSelected(MenuItem item){

		switch (item.getItemId()) {
		case 0:
//        	final ProgressDialog dialog;
//
//        	dialog = new ProgressDialog(CustomDialog.this);
//            dialog.setIndeterminate(true);
//            dialog.setCancelable(true);
//            dialog.setMessage("이미지 저장중..");
//            dialog.closeOptionsMenu();
//            dialog.show();
            
//            final Handler sendhandler = new Handler() {
//            	public void handleMessage(Message msg) {
//                	if (msg.what == -1) {
//                		AlertDialog.Builder alert = new AlertDialog.Builder(CustomDialog.this);
//        		    	alert.setTitle("SD카드 오류");
//        		    	alert.setIcon(R.drawable.ic_dialog_menu_generic);
//        		    	alert.setMessage("이미지를 저장할 공간이 없습니다. SD카드를 확인해 주십시오.");
//
//        		    	alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//        		    		@Override
//        				    public void onClick(DialogInterface dialog, int which) {
//        		    			dialog.dismiss();
//        		    		}
//        		    	});
//        		    	
//        		    	alert.show();
//                	}
//        	    }
//            };
                        
            
//            Thread send1 = new Thread(new Runnable() {
//            	
//            	@Override
//            	public void run() {
//            		// TODO Auto-generated method stub
//            		Bitmap bmp = null;
//            		boolean isOk = Util.setCaptureAndSend(CustomDialog.this, (View)topLayer, bmp);
//            		dialog.dismiss();
//            		if(!isOk) {
//            			Message msg = sendhandler.obtainMessage(-1);
//            			sendhandler.sendMessage(msg);
//            		}
//            	}
//            });
//            send1.start();
            Capture.postDelayed(new Runnable() {
            	
            	@Override
            	public void run() {
            		// TODO Auto-generated method stub
            		Message msg = Capture.obtainMessage(0);
            		Capture.sendMessage(msg);
            	}
            }, 10);
            
            Capture.postDelayed(new Runnable() {
            	
            	@Override
            	public void run() {
            		// TODO Auto-generated method stub
            		Message msg;
            		Bitmap bmp = null;
            		boolean isOk = Util.setCaptureAndSend(CustomDialog.this, (View)topLayer, bmp);
            		if(!isOk) {
            			msg = sendhandler.obtainMessage(-1);
            			sendhandler.sendMessage(msg);
            		} else {
            			msg = sendhandler.obtainMessage(0);
            			sendhandler.sendMessage(msg);
            		}
//            		if(dialog != null) {
//            			dialog.dismiss();
//            		}
            	}
            }, 500);
            break;
		}
		return true;
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //menu.add(0, 0, 0, "업데이트").setIcon(R.drawable.ic_menu_update);
        menu.add(0, 0, 0, "공유하기").setIcon(R.drawable.ic_menu_share);
        return true;
    }
	
	@Override
    public boolean onTouchEvent(MotionEvent event ){
		finish();
		return true;
	}
	
    @Override
	protected void onDestroy() {
    	super.onDestroy();
    	bTopImage.recycle();
    	bTableImage.recycle();
    }
}
