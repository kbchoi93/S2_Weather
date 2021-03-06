package com.weathernews.Weather;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.lang.Runnable;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;


public class ViewLiveCam extends Activity {
    private static final int DURATION = 1000;
    private BaseInfo info;
    private class TimeTracker implements Runnable {
	protected Handler timeUpdater;
	protected boolean playing;

	protected Drawable[] frames;
	protected long startTime = 0;
	protected long stride = 0;
	protected int index = 0;
	protected TextView textOutput;
	protected ImageView imageOutput;



	TimeTracker(TextView textOutput, ImageView imageOutput) {
	    timeUpdater = new Handler();
	    playing = false;
	    this.textOutput = textOutput;
	    this.imageOutput = imageOutput;
	    frames = new Drawable[0]; // This to avoid Null pointers on spurious run() calls
	}
	void reset() { index = 0; }
	void reset(Date d, long stride) { startTime = d.getTime(); this.stride = 1000l * stride; index = 0; }

	public void showTime() {
	    long currentTime = startTime + index * stride;
	    Calendar cl = Calendar.getInstance();

	    if(cl.getTimeInMillis() - currentTime > 1800000l)
	    	currentTime = cl.getTimeInMillis() - 1800000l;

	    cl.setTimeInMillis(currentTime);
	    cl.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
	    if(!TimeFlag) {
        	if(cl.get(Calendar.HOUR_OF_DAY) < 12) {
		    textOutput.setText(
				       String.format("%04d??? %02d??? %02d??? ?????? %02d:%02d", cl.get(Calendar.YEAR), cl.get(Calendar.MONTH) + 1, cl.get(Calendar.DAY_OF_MONTH),
						     cl.get(Calendar.HOUR), cl.get(Calendar.MINUTE))
				       );
        	} else {
		    textOutput.setText(
				       String.format("%04d??? %02d??? %02d??? ?????? %02d:%02d", cl.get(Calendar.YEAR), cl.get(Calendar.MONTH) + 1, cl.get(Calendar.DAY_OF_MONTH),
						     cl.get(Calendar.HOUR), cl.get(Calendar.MINUTE))
				       );
        	}
		//        	textOutput.setText(String.format((cl.get(Calendar.HOUR_OF_DAY) < 12 ?
		//        			"%1$tY??? %1$tm??? %1$td??? ?????? %1$tI:%1$tM" :
		//			      	"%1$tY??? %1$tm??? %1$td??? ?????? %1$tI:%1$tM"), currentTime));
	    } else {
        	textOutput.setText(
				   String.format("%04d??? %02d??? %02d???  %02d:%02d", cl.get(Calendar.YEAR), cl.get(Calendar.MONTH) + 1, cl.get(Calendar.DAY_OF_MONTH),
						 cl.get(Calendar.HOUR_OF_DAY), cl.get(Calendar.MINUTE))
				   );
        	//textOutput.setText(String.format(("%1$tY??? %1$tm??? %1$td??? %1$tH:%1$tM" ), currentTime));
	    }
	}
	public void showImage() {
	    if (0 != frames.length) imageOutput.setBackgroundDrawable(frames[index]);
	}
	public void run() {
	    ++index;
	    if (0 != frames.length) index %= frames.length; else index = 0;
	    showTime();
	    showImage();
	    if (playing) timeUpdater.postDelayed(this, DURATION);
	}

	public void setFrames(Drawable[] frames) {
	    this.frames = frames;
	}

	public void start() {
	    timeUpdater.removeCallbacks(this);
	    timeUpdater.postDelayed(this, playing ? DURATION : 1);
	    playing = true;
	}
	public void stop() {
	    playing = false;
	    timeUpdater.removeCallbacks(this);
	}
	public boolean isRunning() { return playing; }
    }
    private TimeTracker imagePlayer;

    private View footer;


//    private AnimationDrawable LoadingAnimation;
    private ImageView VideoView;
//    private ImageView LoadingLogo;
    private ProgressBar progress;
    private ProgressBar LoadingLogo;
    private String CamID;

    private int nPressedTabNum;
    private boolean isPressed;
    private boolean TimeFlag = false;
    private boolean DateFlag = false;
    private boolean TempFlag = true;
    private String TempSymbol;

    private ImageButton PLAY;
    private ImageButton PAUSE;
    private LinearLayout topLayer;

    private Resources res;
    private int nCurrentMap;

    private LiveCamDataLoader loader;
    private ProgressDialog dialog;

    @Override
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.viewlivecam);
        topLayer = (LinearLayout) findViewById(R.id.livecam_top);

        nPressedTabNum = -1;
    	isPressed = false;

	footer = findViewById(R.id.footer);
        Intent intent = getIntent();
        info = new BaseInfo(intent.getStringExtra("CityID"), intent.getStringExtra("CityName"),
			    intent.getStringExtra("TimeZone"), intent.getIntExtra("Index", 0));
        CamID = intent.getStringExtra("CamID");
        nCurrentMap = intent.getIntExtra("Mapid", 0);

        TimeFlag = Util.getTimeFlag(this);
        DateFlag = Util.getDateFlag(this);
    	TempFlag = Util.getTempFlag(this);

        res = getResources();

        if(TempFlag)
	    TempSymbol = "???";
     	else
	    TempSymbol = "???";

        PLAY = (ImageButton) findViewById(R.id.viewlivecam_play);
        PAUSE = (ImageButton) findViewById(R.id.viewlivecam_pause);

        PLAY.setVisibility(View.INVISIBLE);
        PAUSE.setVisibility(View.VISIBLE);

        Calendar cl = Calendar.getInstance();

//        findViewById(R.id.main_loadinglogo).setVisibility(View.INVISIBLE);
        progress = (ProgressBar) findViewById(R.id.progress_small_title);
        progress.setVisibility(View.INVISIBLE);
        Util.setFooter(footer, cl, TimeFlag, DateFlag);

	imagePlayer = new TimeTracker((TextView) findViewById(R.id.viewlivecam_date), (ImageView) findViewById(R.id.viewlivecam_video));

	LoadingLogo = (ProgressBar) findViewById(R.id.viewlivecam_loading);
//	LoadingAnimation = (AnimationDrawable) LoadingLogo.getBackground();
        VideoView = (ImageView) findViewById(R.id.viewlivecam_video);
        LoadingLogo.setVisibility(View.INVISIBLE);
        reload(null);
	
//	new Handler().postDelayed(new Runnable() { public void run() { /*LoadingAnimation.start();*/ LoadingLogo.setVisibility(View.VISIBLE); } }, 100);
    }

    @Override
	protected void onDestroy(){
    	super.onDestroy();
    	if(res != null)
    		res = null;

    	if(imagePlayer != null) {
    		imagePlayer.stop();
    		for(int i = 0 ; i < imagePlayer.frames.length ; i ++) {
    			if(imagePlayer.frames[i] != null)
    				imagePlayer.frames[i].setCallback(null);
    		}
    		imagePlayer = null;
    	}

    	if(loader != null) {
    		loader.isRun = false;
    		loader.cancel(true);
    		loader.finalize();
    		loader = null;
    	}
    }

    public void reload(View v) {
    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
    	boolean isMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
    	boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
    	
    	if(!isMobile && !isWifi) {
			AlertDialog.Builder alert = null;
//	    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//	    		alert = new AlertDialog.Builder(getParent(), AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//	    	else
	    		alert = new AlertDialog.Builder(getParent());
    		if(alert == null)
    			return;
	    	alert.setTitle("???????????? ??????");
	    	alert.setIcon(R.drawable.ic_dialog_menu_generic);
	    	alert.setMessage("???????????? ????????? ????????? ????????????.");

	    	alert.setPositiveButton("??????", new DialogInterface.OnClickListener() {
	    		@Override
			    public void onClick(DialogInterface dialog, int which) {
	        		LoadingLogo.setVisibility(View.INVISIBLE);
	    			dialog.dismiss();
	    		}
	    	});

	    	alert.show();

	    	return;
    	}

//    	if(LoadingAnimation.isRunning() && LoadingAnimation.isVisible()) {
    	if(LoadingLogo.getVisibility() == View.VISIBLE) {
//    		Toast.makeText(this,
// 				   "???????????? ?????? ????????? ????????? ??? ????????????.",
// 				   Toast.LENGTH_SHORT).show();
    		return;
    	}
    	LoadingLogo.setVisibility(View.VISIBLE);
	VideoView.setVisibility(View.INVISIBLE);
	imagePlayer.stop();
	String loadingText = getString(R.string.viewlivecam_loading_text);
	((TextView) findViewById(R.id.viewlivecam_addr)).setText(loadingText);
        ((TextView) findViewById(R.id.viewlivecam_temp)).setText(loadingText);
	((TextView) findViewById(R.id.viewlivecam_prec)).setText(loadingText);
        ((TextView) findViewById(R.id.viewlivecam_wspd)).setText(loadingText);
        ((TextView) findViewById(R.id.viewlivecam_wdir)).setText(loadingText);
	((TextView) findViewById(R.id.viewlivecam_date)).setText(loadingText);



	LoadingLogo.setVisibility(View.VISIBLE);
	loader = new LiveCamDataLoader(this);
	loader.execute(CamID);
//	new LiveCamDataLoader(this).execute(CamID);
    }

    protected static final String[] WINDDIRS = { "???", "?????????", "??????", "?????????", "???", "?????????", "??????", "?????????", "???", "?????????", "??????", "?????????", "???", "?????????", "??????", "?????????", "???" };
    public void setData(LiveCamData data) {
	if (null != data) {
	    //Log.i("WeatherWidget", "Got live camera data " + data.toString());
	    ((TextView) findViewById(R.id.viewlivecam_addr)).setText(data.address);
	    if(!TempFlag)
	    	((TextView) findViewById(R.id.viewlivecam_temp)).setText(Util.getFahrenheit(Integer.toString((int)data.temp)) + TempSymbol);
	    else
	    	((TextView) findViewById(R.id.viewlivecam_temp)).setText((int)data.temp + TempSymbol);
	    ((TextView) findViewById(R.id.viewlivecam_prec)).setText((int)data.prec + "mm");
	    ((TextView) findViewById(R.id.viewlivecam_wspd)).setText(data.windspd + "m/s");
	    ((TextView) findViewById(R.id.viewlivecam_wdir)).setText(WINDDIRS[(int)data.winddir % 16]);
	    if(data != null && imagePlayer != null){
	    	imagePlayer.reset(data.basedate, data.stride);
	    	imagePlayer.showTime();
	    }
	} else; //Log.e("WeatherWidget", "Unable to load live camera data");
    }
    public void setAnimationImage(int index, LiveCamData d) {
	Boolean all = true;
	//Log.e("WeatherWidget", "Got image " + Integer.toString(index));
	for (int i = d.images.length - 1; i >= 0; --i)
	    if (null == d.images[i]) all = false;
	if (all && (null != imagePlayer)) {
	    imagePlayer.setFrames(d.images);
	    imagePlayer.reset(d.basedate, d.stride);
	    imagePlayer.start();
	    PAUSE.setVisibility(View.VISIBLE);
	    PLAY.setVisibility(View.INVISIBLE);
	    LoadingLogo.setVisibility(View.GONE);
	}
	else if (0 == index)
	    VideoView.setBackgroundDrawable(d.images[0]);
	VideoView.setVisibility(View.VISIBLE);
    }

    public void pausePlay(View v) {
    	if(imagePlayer == null)
	    return;
	if (imagePlayer.isRunning()) {
	    imagePlayer.stop();
	    PLAY.setVisibility(View.VISIBLE);
	    PAUSE.setVisibility(View.INVISIBLE);
	} else {
	    imagePlayer.start();
	    PLAY.setVisibility(View.INVISIBLE);
	    PAUSE.setVisibility(View.VISIBLE);
	}
    }

    @Override
	public void onWindowFocusChanged(boolean hasFocus) {
//	if(hasFocus) LoadingAnimation.start();
    }

    boolean wasRunning = true;
    @Override
	protected void onResume() {
	//Log.d("myTag", "Resume movie");
	super.onResume();
	System.gc();
//	LoadingAnimation.start();
	if (wasRunning && imagePlayer != null)
	    imagePlayer.start();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
	protected void onPause() {
	super.onPause();
//	LoadingAnimation.stop();
	wasRunning = imagePlayer.isRunning();
	imagePlayer.stop();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "????????????").setIcon(R.drawable.ic_menu_update);
        menu.add(0, 1, 0, "????????????").setIcon(R.drawable.ic_menu_share);
        return true;
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item){

	switch (item.getItemId()) {
        case 0:
	    reload(null);
            return true;
        case 1:
        	if(LoadingLogo.getVisibility() == View.VISIBLE) {
        		Toast.makeText(this,
     				   "???????????? ?????? ????????? ????????? ??? ????????????.",
     				   Toast.LENGTH_SHORT).show();
        		return true;
        	}
//        	final ProgressDialog dialog;

        	dialog = new ProgressDialog(ViewLiveCam.this);
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
//            	    		alert = new AlertDialog.Builder(ViewLiveCam.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//            	    	else
            	    		alert = new AlertDialog.Builder(ViewLiveCam.this);
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
					Bitmap bmp = null;
					boolean isOk = Util.setCaptureAndSend(ViewLiveCam.this, (View)topLayer, bmp);
					dialog.dismiss();
					if(!isOk) {
						Message msg = sendhandler.obtainMessage(-1);
						sendhandler.sendMessage(msg);
					}
				}
			});
        	send.start();
            return true;

        default :
	    return true;
        }
    }

    @Override public void onBackPressed()
    {
	((TabRoot)getParent()).onBackPressed();
    }
}
