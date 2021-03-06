package com.weathernews.Weather;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LiveCam extends Activity {
    private View footer;
//    private Bitmap[] maskBmp = new Bitmap[7];
//    private final static int[] maskId = { R.drawable.mask_seoul_gyeonggi,
//					  R.drawable.mask_chungcheong, R.drawable.mask_gangwon,
//					  R.drawable.mask_jeolla, R.drawable.mask_gyeongsang,
//					  R.drawable.mask_jeju, R.drawable.mask_ulleungdo_dokdo };
//    private static Rect[] maskRc = { new Rect(151, 150 + 70, 0, 0),
//				     new Rect(125, 255 + 70, 0, 0), new Rect(260, 129 + 70, 0, 0),
//				     new Rect(0, 364 + 70, 0, 0), new Rect(239, 297 + 70, 0, 0),
//				     new Rect(74, 633 + 70, 0, 0), new Rect(350, 615 + 70, 0, 0) };

    private ImageView imgContent;

//    private Tap tap = new Tap();

    private int nCurrentMap;
    private final static int[] nResId = {
	R.drawable.base_korea,
	R.drawable.base_seoul_gyeonggi,
	R.drawable.base_chungcheong,
	R.drawable.base_gangwon,
	R.drawable.base_jeolla,
	R.drawable.base_gyeongsang,
	R.drawable.base_jeju,
	R.drawable.base_ulleungdo,
	R.drawable.base_seoul_city
    };
//    private final int id_seoul_gyeonggi = 1;
//    private final int id_seoul_city = 8;

    private final String[] UrlList = {
	"http://soratomo.wni.co.kr/android/livecam/korea.xml",
	"http://soratomo.wni.co.kr/android/livecam/Gyeonggi.xml",
	"http://soratomo.wni.co.kr/android/livecam/Chungcheong.xml",
	"http://soratomo.wni.co.kr/android/livecam/Gangwon.xml",
	"http://soratomo.wni.co.kr/android/livecam/Cholla.xml",
	"http://soratomo.wni.co.kr/android/livecam/Gyeongsang.xml",
	"http://soratomo.wni.co.kr/android/livecam/Jeju.xml",
	"http://soratomo.wni.co.kr/android/livecam/Ulleungdo.xml",
	"http://soratomo.wni.co.kr/android/livecam/Seoul.xml"
    };

    // for xml
    private StringBuffer sbBaseurl = new StringBuffer();
    private StringBuffer sbName = new StringBuffer();
    private StringBuffer sbX = new StringBuffer();
    private StringBuffer sbY = new StringBuffer();
    private StringBuffer sbThumb = new StringBuffer();
    private StringBuffer sbCamID = new StringBuffer();
    private StringBuffer sbMapID = new StringBuffer();
    //private Drawable[] dlThumb = null;
    private ImageButton[] imgbtn;
    private ImageView[] imgview;
    private TextView[] tv;
    //	private TextView[] tvbg;
    private int nThumbX = 100;
    private int nThumbY = 76;

    private Rect[] rcArea = new Rect[7];
    private String[] RegionName = { "????????????", "?????????", "?????????", "?????????", "?????????",
				    "?????????", "?????????", "?????????/??????", "??????" };
    private static final String[] XML_NAME = {"korea.xml", "Gyeonggi.xml", "Chungcheong.xml", "Gangwon.xml",
					      "Cholla.xml", "Gyeongsang.xml", "Jeju.xml", "Ulleungdo.xml", "Seoul.xml"
    };
//    private BaseInfo info;
//    private int nPressedTabNum;
//    private boolean isPressed;

    private Runnable animTask;
    private ExecutorService ServiceThread;
    private Future AnimPending;


//    private ImageView imageview;
//    private AnimationDrawable Loading;
    private ProgressBar progress;

//    private Bitmap[] ThumbNail = new Bitmap[5];
//    private boolean[] isShow = {false, false, false, false, false};
//    private boolean[] isDrawCurrentMap;
//    private String[] CamID = {"7812", "7811", "7801", "8518", "7803"};

    private boolean TimeFlag = false;
    private boolean DateFlag = false;

    private long updateTime;
    private Resources res;
    private Bitmap bm;
    private FrameLayout fram;
    private boolean isFrist = true;
//    private final static String[] AREA_NAME = {"?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "??????" };
    private AlertDialog.Builder alert = null;
    private boolean isStop = false;
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.livecam);

    	Intent tempIntent = getIntent();

    	nCurrentMap = tempIntent.getIntExtra("Mapid", 0);

    	TimeFlag = Util.getTimeFlag(getApplicationContext());
    	DateFlag = Util.getDateFlag(getApplicationContext());

        footer = findViewById(R.id.footer);

    	res = getResources();

        fram = (FrameLayout)findViewById(R.id.frame);

        imgContent = (ImageView) findViewById(R.id.livecam_content);

//        imageview = (ImageView) findViewById(R.id.main_loadinglogo);
//        Loading = (AnimationDrawable) imageview.getBackground();
        progress = (ProgressBar) findViewById(R.id.progress_small_title);
        //Log.i("myTag", "drawMap from onCreate");
        initThreading();

        new Handler().post(new Runnable() { public void run() { drawMap(); } });
        // imgContent.setImageResource(R.drawable.base_korea);
        // imgContent.setImageBitmap(getContentImage());

        Calendar cl = Calendar.getInstance();

        rcArea[0] = new Rect(211, 258, 306, 347);
        rcArea[1] = new Rect(168, 356, 311, 454);
        rcArea[2] = new Rect(307, 228, 433, 358);
        rcArea[3] = new Rect(126, 455, 264, 643);
        rcArea[4] = new Rect(312, 393, 454, 623);
        rcArea[5] = new Rect(72, 702, 157, 744);
        rcArea[6] = new Rect(346, 682, 435, 778);
//        nPressedTabNum = -1;
//        isPressed = false;

//        Loading.stop();
        progress.setVisibility(View.INVISIBLE);
    }

    final Handler AnimThread = new Handler() {
	    public void handleMessage(Message msg) {
    		progress.setVisibility(View.INVISIBLE);
//    		Loading.stop();
    		Calendar cl = Calendar.getInstance();
    		if (res == null)
		    res = getResources();
    		drawMap();
    		if (updateTime != 0)
    		    cl.setTimeInMillis(updateTime);
    		Util.setFooter(footer, cl, TimeFlag, DateFlag);
	    }
	};

    private Handler SearchThread;

    private void initThreading() {
    	SearchThread = new Handler();
    	ServiceThread = Executors.newSingleThreadExecutor();

    	animTask = new Runnable() {

    		@Override
		    public void run() {
		    // TODO Auto-generated method stub
		    if (AnimPending != null)
			AnimPending.cancel(true);

		    Thread task = new Thread(new Runnable() {
			    public void run() {
				//Log.d("myTag", "URL=" + UrlList[nCurrentMap]);
			    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
			    	boolean network = Util.checkNetwork(manager);
			    	
			    	if(!network) {
			    		
//			    		Loading.stop();
			    	    progress.setVisibility(View.INVISIBLE);
			    	    
						AlertDialog.Builder alert = null;
//				    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//				    		alert = new AlertDialog.Builder(LiveCam.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//				    	else
				    		alert = new AlertDialog.Builder(LiveCam.this);
			        	alert.setTitle("???????????? ??????");
			        	alert.setIcon(R.drawable.ic_dialog_menu_generic);
			        	alert.setMessage("???????????? ????????? ????????? ????????????.");

			        	alert.setPositiveButton("??????", new DialogInterface.OnClickListener() {
			        		@Override
			    		    public void onClick(DialogInterface dialog, int which) {
			        			dialog.dismiss();
			        		}
			        	});

			        	alert.show();
			        	return;
			    	}

				int mapid = nCurrentMap;

				sbBaseurl = new StringBuffer("");
				sbName = new StringBuffer("");
				sbX = new StringBuffer("");
				sbY = new StringBuffer("");
				sbThumb = new StringBuffer("");
				sbCamID = new StringBuffer("");
				sbMapID = new StringBuffer("");

				byte[] byteArray = null;

				try {
				    Thread.sleep(100);
				    byteArray = getByteArrayFromURL(UrlList[mapid]);

				    if (byteArray.length <= 0){
					//Log.d("myTag", "Retry");
					Thread.sleep(100);
					byteArray = getByteArrayFromURL(UrlList[mapid]);
				    }

				    if (byteArray.length <= 0){
					//Log.d("myTag", "Retry2");
					Thread.sleep(100);
					byteArray = getByteArrayFromURL(UrlList[mapid]);
				    }
				    
					if(byteArray == null || byteArray.length <= 0) {
						Message msg = handler.obtainMessage(-1);
					    handler.sendMessage(msg);
					}

				    //Log.d("myTag", "parse xml");
				    parseXmlData(byteArray);
				    //Log.d("myTag", "parse Xml Done");
				} catch (Throwable t) {
				    // just end the background thread
				    Message msg = handler.obtainMessage(-1);
				    handler.sendMessage(msg);
				}

				String[] ThumbPath = sbThumb.toString().split("\t");
				String[] camId = sbCamID.toString().split("\t");
				String[] mapId = sbMapID.toString().split("\t");
				String[] posX = sbX.toString().split("\t");
				String[] posY = sbY.toString().split("\t");
				String[] areaName = sbName.toString().split("\t");

				Drawable[] dlThumb = new Drawable[ThumbPath.length];
				for (int i = 0; i < ThumbPath.length; i++){
				    dlThumb[i] = null;
				    String thumbUrl = sbBaseurl.toString() + ThumbPath[i];
				    try {
					dlThumb[i] = Drawable.createFromStream((InputStream)(new URL(thumbUrl).getContent()), "src");
				    } catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				    } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//Log.e("myTag", e.toString());
				    }
				    //Log.d("myTag", "DEBUG3");
				    Bitmap bitmap = Bitmap.createBitmap(76, 58, Config.RGB_565);
				    Canvas canvas = new Canvas(bitmap);
				    if (dlThumb[i] != null){
					dlThumb[i].setBounds(0, 0, 76, 58);
					dlThumb[i].draw(canvas);
					String [] idTemp = ThumbPath[i].split("/");
					//Log.d("myTag", "id=" + idTemp[1]);
					saveThumnail(i, idTemp[1], bitmap);
					bitmap.recycle();
				    }
				}
				if (byteArray.length > 0) {
				    //Log.d("myTag", "Save File To " + XML_NAME[mapid]);
				    saveXml(byteArray, XML_NAME[mapid], mapid);
				}
				

				Message msg = handler.obtainMessage(1, mapid, 0, dlThumb);
				handler.sendMessage(msg);
				for (int i = 0; i < ThumbPath.length; i++){
					if(dlThumb[i] != null)
						dlThumb[i].setCallback(null);
				}
			    }
    		        });
		    AnimPending = ServiceThread.submit(task);
    		}
	    };
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	isStop = true;
    	return;
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if(nCurrentMap == 0 && isFrist) {
    		isFrist = false;
    		UpdateLiveCam();
    	}

    	isStop = false;
    }

    @Override protected void onDestroy()
    {
    	super.onDestroy();
    	if (null != bm) {
    		bm.recycle();
    		bm = null;
	    }
//    	for (int i = 0; i < maskBmp.length; ++i) {
//    		if(maskBmp[i] != null) {
//    			maskBmp[i].recycle();
//    			maskBmp[i] = null;
//    		}
//	    }
//    	for (int i = 0; i < ThumbNail.length; ++i) {
//    		if (null != ThumbNail[i]) {
//    			ThumbNail[i].recycle();
//    			ThumbNail[i] = null;
//    		}
//    	}
    	res = null;
    	ServiceThread.shutdown();
    }

    public void removeButtons()
    {
    	int idx = 0;
    	while (true)
	    {
		View tempview = fram.getChildAt(idx++);
		if (tempview == null)
		    break;
		else
		    {
			tempview.setVisibility(View.GONE);
			tempview = null;
		    }
    	}
    }

    private Rect getBgRectForPos(int x, int y)
    {
	final int width = fram.getWidth();
	final int height = fram.getHeight();
	// The hard-coded coordinates we receive are for a 480x628 frame : resize
	x = 480 * x / width;
	y = 628 * y / height;
	// 98x82+44+0 are the coordinates inside the picture
	return new Rect(x - 44, y - 82, x + 54, y);
    }

    private void drawMap() {
        // TODO Auto-generated method stub
    	if (res == null)
	    res = getResources();
    	
	final int width = fram.getWidth();
	final int height = fram.getHeight();
	
	imgContent.setBackgroundResource(nResId[nCurrentMap]);
	
	if (0 >= width || 0 >= height) return;

    	bm = Bitmap.createBitmap(width, height, Config.ARGB_4444);

    	Bitmap bg = BitmapFactory.decodeResource(res,
						 R.drawable.location_name_bg_01);
    	Bitmap thumb_bg = BitmapFactory.decodeResource(res,
						       R.drawable.livecam_thumbnail);

//    	Loading.stop();
        progress.setVisibility(View.INVISIBLE);

    	//Log.d("myTag", "Remove buttons");
    	removeButtons();

//    	isDrawCurrentMap = new boolean[]{false, false, false, false, false};
    	
    	int nBarHeight = (int) Util.dp2px(30, this);

    	Rect rcBG = new Rect(0, 0, width, nBarHeight);

//    	for (int i = 0; i < maskBmp.length; i++) {
//	    maskBmp[i] = BitmapFactory.decodeResource(res, maskId[i]);
//	    maskRc[i].set(maskRc[i].left, maskRc[i].top, maskRc[i].left
//			  + maskBmp[i].getWidth(), maskRc[i].top
//			  + maskBmp[i].getHeight());
//    	}

	final Rect[] rcThumbnailbg = new Rect[5];

    	rcThumbnailbg[0] = getBgRectForPos(161 + 44, 359);
    	rcThumbnailbg[1] = getBgRectForPos(364 + 44, 387);
    	rcThumbnailbg[2] = getBgRectForPos(215 + 44, 171);
    	rcThumbnailbg[3] = getBgRectForPos(136 + 44, 407);
    	rcThumbnailbg[4] = getBgRectForPos(164 + 44, 165);

    	if (nCurrentMap == 1)
	    {
		rcThumbnailbg[2] = getBgRectForPos(212 + 44, 330);
		rcThumbnailbg[4] = getBgRectForPos(127 + 44, 293);
	    }

    	if (nCurrentMap == 4)
	    rcThumbnailbg[3] = getBgRectForPos(184 + 44, 285);


    	//Rect rcThumbnail = new Rect(182, 116, 258, 174);
    	Rect[] rcThumbnail = new Rect[5];
    	Rect[] rcThumbBtn;

    	for(int i = 0 ; i < 5 ; i ++){
	    rcThumbnail[i] = new Rect(rcThumbnailbg[i].left + 7, rcThumbnailbg[i].top + 7, rcThumbnailbg[i].right - 15, rcThumbnailbg[i].bottom - 19);
    	}


    	Canvas canvas = new Canvas(bm);
    	Paint paint = new Paint();
    	paint.setAntiAlias(true);


    	if ( nCurrentMap != 0 ){
	    canvas.drawBitmap(bg, null, rcBG, null);
//	    paint.setTypeface(Const.typeFace);
	    paint.setColor(Const.FONT_COLOR_WHITE);
	    paint.setTextSize(nBarHeight/2);

	    canvas.drawText(RegionName[nCurrentMap], 20, nBarHeight - nBarHeight/3, paint);
    	}


    	imgContent.setImageBitmap(bm);

    	bg.recycle();
    	thumb_bg.recycle();
    	//Log.i("myTag", "LiveCam End drawMap");

    	Thread background = new Thread(new Runnable() {
    		public void run() {
		    //Log.d("myTag", "URL=" + UrlList[nCurrentMap]);
		    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
		    	boolean network = Util.checkNetwork(manager);
		    	
		    	if(!network) {
		    		
//		    		Loading.stop();
		    	    progress.setVisibility(View.INVISIBLE);
		    	    
					AlertDialog.Builder alert = null;
//			    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//			    		alert = new AlertDialog.Builder(LiveCam.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//			    	else
			    		alert = new AlertDialog.Builder(LiveCam.this);
		        	alert.setTitle("???????????? ??????");
		        	alert.setIcon(R.drawable.ic_dialog_menu_generic);
		        	alert.setMessage("???????????? ????????? ????????? ????????????.");

		        	alert.setPositiveButton("??????", new DialogInterface.OnClickListener() {
		        		@Override
		    		    public void onClick(DialogInterface dialog, int which) {
		        			dialog.dismiss();
		        		}
		        	});

		        	alert.show();
		        	return;
		    	}

		    int mapid = nCurrentMap;

		    sbBaseurl = new StringBuffer();
	            sbName = new StringBuffer();
	            sbX = new StringBuffer();
	            sbY = new StringBuffer();
	            sbThumb = new StringBuffer();
	            sbCamID = new StringBuffer();
	            sbMapID = new StringBuffer();

	            byte[] byteArray = null;

		    try {
			Thread.sleep(100);
			byteArray = getByteArrayFromURL(UrlList[mapid]);

			if (byteArray.length <= 0){
			    //Log.d("myTag", "Retry");
			    Thread.sleep(100);
			    byteArray = getByteArrayFromURL(UrlList[mapid]);
			}

			if (byteArray.length <= 0){
			    //Log.d("myTag", "Retry2");
			    Thread.sleep(100);
			    byteArray = getByteArrayFromURL(UrlList[mapid]);
			}
			if (byteArray.length > 0)
			    saveXml(byteArray, XML_NAME[mapid], mapid);
			
			if(byteArray == null || byteArray.length <= 0) {
				Message msg = handler.obtainMessage(-1);
			    handler.sendMessage(msg);
			}
			
			if(byteArray == null || byteArray.length <= 0) {
				Message msg = handler.obtainMessage(-1);
			    handler.sendMessage(msg);
			}

			//Log.d("myTag", "parse xml");
			parseXmlData(byteArray);
			//Log.d("myTag", "parse Xml Done");
		    } catch (Throwable t) {
			// just end the background thread
			Message msg = handler.obtainMessage(-1);
			handler.sendMessage(msg);
		    }

		    String[] ThumbPath = sbThumb.toString().split("\t");
		    String[] camId = sbCamID.toString().split("\t");
		    String[] mapId = sbMapID.toString().split("\t");
		    String[] posX = sbX.toString().split("\t");
		    String[] posY = sbY.toString().split("\t");
		    String[] areaName = sbName.toString().split("\t");

		    Drawable[] dlThumb = new Drawable[ThumbPath.length];
		    for (int i = 0; i < ThumbPath.length; i++){
			dlThumb[i] = null;
			String thumbUrl = sbBaseurl.toString() + ThumbPath[i];
			try {
			    dlThumb[i] = Drawable.createFromStream((InputStream)(new URL(thumbUrl).getContent()), "src");
			} catch (MalformedURLException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			} catch (IOException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			    //Log.e("myTag", e.toString());
			}
			//Log.d("myTag", "DEBUG3");
			Bitmap bitmap = Bitmap.createBitmap(76, 58, Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			if (dlThumb[i] != null){
			    dlThumb[i].setBounds(0, 0, 76, 58);
			    dlThumb[i].draw(canvas);
			    String [] idTemp = ThumbPath[i].split("/");
			    //Log.d("myTag", "id=" + idTemp[1]);
			    saveThumnail(i, idTemp[1], bitmap);
			}
			bitmap.recycle();
		    }
		    Message msg = handler.obtainMessage(1, mapid, 0, dlThumb);
		    handler.sendMessage(msg);
		    for (int i = 0; i < ThumbPath.length; i++){
		    	if(dlThumb[i] != null)
		    		dlThumb[i].setCallback(null);
		    }
    		}
	    });

    	Calendar cl = Calendar.getInstance();
    	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
        updateTime = prefs.getLong(Const.LIVECAM_UPDATE_TIME + nCurrentMap, 0l);

    	if (cl.getTimeInMillis() - updateTime <= 1800000l) {
	    File dir = getDir("temp", Activity.MODE_PRIVATE);
	    String Filename = dir.getAbsolutePath() + "/" + XML_NAME[nCurrentMap];
	    //Log.d("myTag", "Filename=" + Filename);
	    byte[] byteArray = null;
            String line;
            String document = "";
            FileInputStream fis = null;

            try {
            	BufferedReader in = new BufferedReader( new FileReader( Filename ) );
            	line = "";
            	try {
		    while((line = in.readLine())!=null)
			document += line;
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} finally {
		    try {
			in.close();
		    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
	    } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    byteArray = document.getBytes();

	    //Log.d("myTag", "URL=" + UrlList[nCurrentMap]);
            sbBaseurl = new StringBuffer("");
            sbName = new StringBuffer("");
            sbX = new StringBuffer("");
            sbY = new StringBuffer("");
            sbThumb = new StringBuffer("");
            sbCamID = new StringBuffer("");
            sbMapID = new StringBuffer("");

	    parseXmlData(byteArray);
	    String[] ThumbPath = sbThumb.toString().split("\t");
	    String[] camId = sbCamID.toString().split("\t");
	    String[] mapId = sbMapID.toString().split("\t");
	    String[] posX = sbX.toString().split("\t");
	    String[] posY = sbY.toString().split("\t");
	    String[] areaName = sbName.toString().split("\t");
	    int nCnt = ThumbPath.length;

	    Drawable[] dlThumb = new Drawable[nCnt];
	    imgbtn = new ImageButton[nCnt];
	    imgview = new ImageView[nCnt];
	    tv = new TextView[nCnt];
	    //	        tvbg = new TextView[nCnt];
	    for (int i=0; i<nCnt; i++){
		String thumbfile = dir + "/" + camId[i] + ".jpg";
		//Log.d("myTag", "thumbfile=" + thumbfile);
		dlThumb[i] = Drawable.createFromPath(dir + "/" + camId[i] + ".jpg");
		Bitmap bitmap = Bitmap.createBitmap(nThumbX, nThumbY, Config.RGB_565);
		Canvas canvas1 = new Canvas(bitmap);
		if (dlThumb[i]!=null){
		    dlThumb[i].setBounds(0, 0, nThumbX, nThumbY);
		    dlThumb[i].draw(canvas1);
		    makeButton(fram, Integer.parseInt(posX[i]), Integer.parseInt(posY[i]), bitmap,
			       camId[i], areaName[i], mapId[i], i);
		    if(dlThumb[i] != null)
		    	dlThumb[i].setCallback(null);
		}
//		bitmap.recycle();
	    }
    	}else {
	    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
	    	boolean network = Util.checkNetwork(manager);
	    	
	    	if(!network) {
	    		
//	    		Loading.stop();
	    	    progress.setVisibility(View.INVISIBLE);
	    	    
				AlertDialog.Builder alert = null;
//		    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//		    		alert = new AlertDialog.Builder(LiveCam.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//		    	else
		    		alert = new AlertDialog.Builder(LiveCam.this);
	        	alert.setTitle("???????????? ??????");
	        	alert.setIcon(R.drawable.ic_dialog_menu_generic);
	        	alert.setMessage("???????????? ????????? ????????? ????????????.");

	        	alert.setPositiveButton("??????", new DialogInterface.OnClickListener() {
	        		@Override
	    		    public void onClick(DialogInterface dialog, int which) {
	        			dialog.dismiss();
	        		}
	        	});

	        	alert.show();
	    	} else {
			    new Handler().postDelayed(new Runnable() { public void run() { /*Loading.start();*/ progress.setVisibility(View.VISIBLE); } }, 500);
			    //Log.d("myTag","Start Thread in DrawMap");
			    //background.start();
			    SearchThread.removeCallbacks(animTask);
			    SearchThread.postDelayed(animTask, 100);
	    	}
    	}

        if (updateTime != 0)
	    cl.setTimeInMillis(updateTime);
	if (res == null)
	    res = getResources();
	Util.setFooter(footer, cl, TimeFlag, DateFlag);
    }

    final Handler handler = new Handler() {
	    int msgid;
	    public void handleMessage(Message msg) {
        	//Log.i("myTag", "handler start~~~" + msg.what + (String)msg.obj);
//        	Loading.stop();
        	progress.setVisibility(View.INVISIBLE);
        	msgid = msg.what;
        	if (msgid == 1) {
        		if (msg.arg1 != nCurrentMap)
        			return;

        		String[] camId = sbCamID.toString().split("\t");
        		String[] mapId = sbMapID.toString().split("\t");
        		String[] posX = sbX.toString().split("\t");
        		String[] posY = sbY.toString().split("\t");
        		String[] areaName = sbName.toString().split("\t");
        		Drawable[] dlThumb = (Drawable[])msg.obj;
        		int nCnt = dlThumb.length;

        		imgbtn = new ImageButton[nCnt];
        		imgview = new ImageView[nCnt];
        		tv = new TextView[nCnt];
		    //				tvbg = new TextView[nCnt];
        		for (int i=0; i<nCnt; i++){
        			Bitmap bitmap = Bitmap.createBitmap(nThumbX, nThumbY, Config.RGB_565);
        			Canvas canvas = new Canvas(bitmap);
        			if (dlThumb[i] != null) {
        				dlThumb[i].setBounds(0, 0, nThumbX, nThumbY);
        				dlThumb[i].draw(canvas);
        				try{
        					makeButton(fram, Integer.parseInt(posX[i]), Integer.parseInt(posY[i]), bitmap,
        							camId[i], areaName[i], mapId[i], i);
        				}catch(NumberFormatException e) {
        					e.printStackTrace();
        				}catch(ArrayIndexOutOfBoundsException e){
        					e.printStackTrace();
        					//Log.e("myTag", "Error e=" + e.toString());
        				}
        				if(dlThumb[i] != null)
        					dlThumb[i].setCallback(null);
        			}
//        			bitmap.recycle();
        		}
        		Calendar cl = Calendar.getInstance();
        		if (res == null)
        			res = getResources();
        		Util.setFooter(footer, cl, TimeFlag, DateFlag);
        	} else {
        		if(isStop == false)
        			ShowUpdateAgain();
        	}
	    }
	};

    private void saveXml(byte[] byteArray, String file, int mapid) {
	// TODO Auto-generated method stub
    	File dir = getDir("temp", Activity.MODE_PRIVATE);

        String Filename = dir.getAbsolutePath() + "/" + file;
        //Log.d("myTag", "Filename= " + Filename);

        FileOutputStream Os = null;
        try {
	    //Os = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
	    Os = new FileOutputStream(Filename);

	    try {
		Os.write(byteArray);
	    } catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	    }
	    try {
		Os.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	    Calendar cl = Calendar.getInstance();

	    SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
	    prefs1.putLong(Const.LIVECAM_UPDATE_TIME + mapid, cl.getTimeInMillis());
	    prefs1.commit();
        } catch (FileNotFoundException e) {
	    e.printStackTrace();
        }
    }

    protected void parseXmlData(byte[] byteArray){
    	String readXml = new String(byteArray);

        try {
	    final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	    final XmlPullParser parser = factory.newPullParser();
	    parser.setInput(new StringReader(readXml));

	    String readbuff;

	    boolean tag_baseurl = false;
	    boolean tag_point = false;
	    boolean tag_name = false;
	    boolean tag_x = false;
	    boolean tag_y = false;
	    boolean tag_thumb = false;
	    boolean tag_camid = false;
	    boolean tab_mapid = false;

	    int eventType = parser.getEventType();
	    while (eventType != XmlPullParser.END_DOCUMENT){
		switch(eventType) {
		case XmlPullParser.START_DOCUMENT:
		    break;
		case XmlPullParser.START_TAG:{
		    readbuff = parser.getName();
		    if (readbuff.equals("baseurl"))
			tag_baseurl = true;
		    else if (readbuff.equals("name"))
			tag_name = true;
		    else if (readbuff.equals("x"))
			tag_x = true;
		    else if (readbuff.equals("y"))
			tag_y = true;
		    else if (readbuff.equals("thumb"))
			tag_thumb = true;
		    else if (readbuff.equals("camid"))
			tag_camid = true;
		    else if (readbuff.equals("mapid"))
			tab_mapid = true;

		    break;
		}
		case XmlPullParser.TEXT: {
		    if (parser.isWhitespace())
			break;

		    readbuff = parser.getText();

		    if (tag_baseurl) {
			tag_baseurl = false;
			sbBaseurl.append(readbuff);
		    } else if (tag_name) {
			sbName.append(readbuff);
			sbName.append('\t');
		    } else if (tag_x) {
			sbX.append(readbuff);
			sbX.append('\t');
		    } else if (tag_y) {
			sbY.append(readbuff);
			sbY.append('\t');
		    } else if (tag_thumb) {
			sbThumb.append(readbuff);
			sbThumb.append('\t');
		    } else if (tag_camid) {
			sbCamID.append(readbuff);
			sbCamID.append('\t');
		    } else if (tab_mapid) {
			sbMapID.append(readbuff);
			sbMapID.append('\t');
		    }

		    break;
		}
		case XmlPullParser.END_TAG:{
		    readbuff = parser.getName();
		    if (readbuff.equals("baseurl"))
			tag_baseurl = false;
		    else if (readbuff.equals("name"))
			tag_name = false;
		    else if (readbuff.equals("x"))
			tag_x = false;
		    else if (readbuff.equals("y"))
			tag_y = false;
		    else if (readbuff.equals("thumb"))
			tag_thumb = false;
		    else if (readbuff.equals("camid"))
			tag_camid = false;
		    else if (readbuff.equals("mapid"))
			tab_mapid = false;

		    break;
		}
		case XmlPullParser.END_DOCUMENT:
		    break;
		}

		eventType = parser.next();
	    }
        } catch (Exception e) {
	    e.printStackTrace();
        }
    }

    public static byte[] getByteArrayFromURL(String strUrl)  {
    	byte[] line = new byte[1024];
    	byte[] result = null;
    	HttpURLConnection con = null;
    	InputStream in = null;
    	ByteArrayOutputStream out = null;
    	int size = 0;

    	try {
	    URL url = new URL(strUrl);
	    con = (HttpURLConnection) url.openConnection();
	    //con.setRequestMethod("GET");
	    con.setReadTimeout(90000);
	    con.setConnectTimeout(10000);
	    con.connect();
	    in = con.getInputStream();

	    out = new ByteArrayOutputStream();

	    while (true) {
		size = in.read(line);
		if (size <= 0)
		    break;

		out.write(line, 0, size);
	    }
	    result = out.toByteArray();
    	} catch (MalformedURLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    //Log.e("myTag", e.toString());
    	} catch (Exception e) {
	    e.printStackTrace();
	    //Log.e("myTag", e.toString());
    	} finally {
	    try {
		if (con != null)
		    con.disconnect();
		con = null;
		if (in != null)
		    in.close();
		in = null;
		if (out != null)
		    out.close();
		out = null;

	    } catch (Exception e) {
		e.printStackTrace();
	    }
    	}
    	return result;
    }


//    private int checkMask(int x, int y) {
//    	if (nCurrentMap != 0) {
//
//	    // SeoulCity
//	    if (nCurrentMap == 1) {
//		Rect rcSeoul = new Rect(150, 395, 265, 475);
//		if (rcSeoul.contains(x, y))
//		    return id_seoul_city;
//	    }
//
//	    Bitmap img = ((BitmapDrawable) imgContent.getBackground()).getBitmap();
//
//	    Rect rc = new Rect(0, 140, imgContent.getRight(), imgContent.getBottom());
//	    if (!rc.contains(x, y))
//		return nCurrentMap;
//
//	    int col = img.getPixel(x, y - 140);
//	    int r = Color.red(col);
//	    int g = Color.green(col);
//	    int b = Color.blue(col);
//
//	    // Seoul City
//	    if (nCurrentMap == id_seoul_city) {
//		if (r == 146 && g == 204 && b == 122)
//		    return nCurrentMap;
//		else
//		    return id_seoul_gyeonggi;
//	    }
//
//	    if (r == 148 && (g == 203 || g == 207) && b == 123)
//		return nCurrentMap;
//	    else
//		return 0;
//    	}
//    	for (int i = 0; i < maskBmp.length; i++) {
//
//	    if (!maskRc[i].contains(x, y))
//		continue;
//
//	    if (i == 6)
//		return i + 1;
//
//	    int col = maskBmp[i].getPixel(x - maskRc[i].left, y - maskRc[i].top);
//	    if (Color.red(col) == 255 && Color.green(col) == 255
//		&& Color.blue(col) == 255)
//		return i + 1;
//    	}
//
//    	return 0;
//    }

    @Override
	public void onBackPressed() {
	// TODO Auto-generated method stub
        if (nCurrentMap == 0){
	    super.onBackPressed();
	    ActivityManager am
		= (ActivityManager)getSystemService(ACTIVITY_SERVICE);
	    am.restartPackage(getPackageName());
        } else {
	    SearchThread.removeCallbacks(animTask);
	    nCurrentMap = 0;
            imgContent.invalidate();
            //Log.i("myTag", "DrawMap from onBackPress");
            drawMap();
        }
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item){
//    	Loading.start();
    	progress.setVisibility(View.VISIBLE);

    	if (item.getItemId() == 0 ){
	    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
	    	boolean network = Util.checkNetwork(manager);
	    	
	    	if(!network) {
	    		
//	    		Loading.stop();
	    	    progress.setVisibility(View.INVISIBLE);
	    	    
				AlertDialog.Builder alert = null;
//		    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//		    		alert = new AlertDialog.Builder(LiveCam.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//		    	else
		    		alert = new AlertDialog.Builder(LiveCam.this);
	        	alert.setTitle("???????????? ??????");
	        	alert.setIcon(R.drawable.ic_dialog_menu_generic);
	        	alert.setMessage("???????????? ????????? ????????? ????????????.");

	        	alert.setPositiveButton("??????", new DialogInterface.OnClickListener() {
	        		@Override
	    		    public void onClick(DialogInterface dialog, int which) {
	        			dialog.dismiss();
	        		}
	        	});

	        	alert.show();
	        	return true;
	    	}

	    UpdateLiveCam();
	    //    		removeButtons();
	    //    		Thread background = new Thread(new Runnable() {
	    //
	    //    			public void run() {
	    //    				//Log.d("myTag", "URL=" + UrlList[nCurrentMap]);
	    //    				int mapid = nCurrentMap;
	    //
	    //    				sbBaseurl = new StringBuffer();
	    //    				sbName = new StringBuffer();
	    //    				sbX = new StringBuffer();
	    //    				sbY = new StringBuffer();
	    //    				sbThumb = new StringBuffer();
	    //    				sbCamID = new StringBuffer();
	    //    				sbMapID = new StringBuffer();
	    //
	    //    				byte[] byteArray = null;
	    //
	    //    				try {
	    //    					Thread.sleep(100);
	    //    					byteArray = getByteArrayFromURL(UrlList[mapid]);
	    //
	    //    					if (byteArray.length <= 0){
	    //    						Log.d("myTag", "Retry");
	    //    						Thread.sleep(100);
	    //    						byteArray = getByteArrayFromURL(UrlList[mapid]);
	    //    					}
	    //
	    //    					if (byteArray.length <= 0){
	    //    						Log.e("myTag", "Retry2");
	    //    						Thread.sleep(100);
	    //    						byteArray = getByteArrayFromURL(UrlList[mapid]);
	    //    					}
	    //    					if (byteArray.length > 0)
	    //    						saveXml(byteArray, XML_NAME[mapid], mapid);
	    //
	    //    					parseXmlData(byteArray);
	    //    				} catch (Throwable t) {
	    //    					// just end the background thread
	    //    					Log.e("myTag", "error = " + t.toString());
	    //    					Message msg = handler.obtainMessage(-1);
	    //    					handler.sendMessage(msg);
	    //    				}
	    //
	    //    				String[] ThumbPath = sbThumb.toString().split("\t");
	    //    				String[] camId = sbCamID.toString().split("\t");
	    //    				String[] mapId = sbMapID.toString().split("\t");
	    //    				String[] posX = sbX.toString().split("\t");
	    //    				String[] posY = sbY.toString().split("\t");
	    //    				String[] areaName = sbName.toString().split("\t");
	    //
	    //    				Drawable[] dlThumb = new Drawable[ThumbPath.length];
	    //    				Log.d("myTag", "DEBUG2");
	    //    				for (int i = 0; i < ThumbPath.length; i++){
	    //    					dlThumb[i] = null;
	    //    					String thumbUrl = sbBaseurl.toString() + ThumbPath[i];
	    //    					Log.d("myTag", "BaseUrl=" + sbBaseurl.toString());
	    //    					Log.d("myTag", "thumbUrl=" + thumbUrl);
	    //    					try {
	    //    						dlThumb[i] = Drawable.createFromStream((InputStream)(new URL(thumbUrl).getContent()), "src");
	    //    					} catch (MalformedURLException e) {
	    //    						// TODO Auto-generated catch block
	    //    						e.printStackTrace();
	    //    					} catch (IOException e) {
	    //    						// TODO Auto-generated catch block
	    //    						e.printStackTrace();
	    //    						Log.e("myTag", e.toString());
	    //    					}
	    //    					//Log.d("myTag", "DEBUG3");
	    //    					Bitmap bitmap = Bitmap.createBitmap(76, 58, Config.RGB_565);
	    //    					Canvas canvas = new Canvas(bitmap);
	    //    					if (dlThumb[i] != null){
	    //    						dlThumb[i].setBounds(0, 0, 76, 58);
	    //    						dlThumb[i].draw(canvas);
	    //    						String [] idTemp = ThumbPath[i].split("/");
	    //    						//Log.d("myTag", "id=" + idTemp[1]);
	    //    						saveThumnail(i, idTemp[1], bitmap);
	    //    					}
	    //    				}
	    //    				Message msg = handler.obtainMessage(1, mapid, 0, dlThumb);
	    //    				handler.sendMessage(msg);
	    //    			}
	    //    		});
	    //    		//Log.d("myTag", "Start In SoftKey");
	    //    		background.start();
	    ////    		SearchThread.removeCallbacks(animTask);
	    ////    		SearchThread.postDelayed(animTask, 100);
    	}
        return true;
    }
    
    final Handler chknethandler = new Handler(){
	    public void handleMessage(Message msg) {
//    		Loading.stop();
    	    progress.setVisibility(View.INVISIBLE);
    	    
    	    if(isStop == false) {
				AlertDialog.Builder alert = null;
//		    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//		    		alert = new AlertDialog.Builder(LiveCam.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//		    	else
		    		alert = new AlertDialog.Builder(LiveCam.this);
	        	alert.setTitle("???????????? ??????");
	        	alert.setIcon(R.drawable.ic_dialog_menu_generic);
	        	alert.setMessage("???????????? ????????? ????????? ????????????.");
	
	        	alert.setPositiveButton("??????", new DialogInterface.OnClickListener() {
	        		@Override
	    		    public void onClick(DialogInterface dialog, int which) {
	        			dialog.dismiss();
	        		}
	        	});
	
	        	alert.show();
    	    }
        	return;
	    }
	};

    public void UpdateLiveCam() {
//    	Loading.start();
    	progress.setVisibility(View.VISIBLE);

	removeButtons();
	Thread background = new Thread(new Runnable() {

		public void run() {
		    //Log.d("myTag", "URL=" + UrlList[nCurrentMap]);
	    	ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
	    	boolean network = Util.checkNetwork(manager);
	    	
	    	if(!network) {
	    		Message msg = chknethandler.obtainMessage();
	    		chknethandler.sendMessage(msg);
//	    		Loading.stop();
//	    	    imageview.setVisibility(View.INVISIBLE);
//	    	    
//	    		AlertDialog.Builder alert = new AlertDialog.Builder(LiveCam.this);
//	        	alert.setTitle("???????????? ??????");
//	        	alert.setIcon(R.drawable.ic_dialog_menu_generic);
//	        	alert.setMessage("???????????? ????????? ????????? ????????????.");
//
//	        	alert.setPositiveButton("??????", new DialogInterface.OnClickListener() {
//	        		@Override
//	    		    public void onClick(DialogInterface dialog, int which) {
//	        			dialog.dismiss();
//	        		}
//	        	});
//
//	        	alert.show();
	        	return;
	    	}

		    int mapid = nCurrentMap;

		    sbBaseurl = new StringBuffer();
		    sbName = new StringBuffer();
		    sbX = new StringBuffer();
		    sbY = new StringBuffer();
		    sbThumb = new StringBuffer();
		    sbCamID = new StringBuffer();
		    sbMapID = new StringBuffer();

		    byte[] byteArray = null;

		    try {
		    	Thread.sleep(100);
		    	byteArray = getByteArrayFromURL(UrlList[mapid]);

		    	if (byteArray.length <= 0){
			    //						Log.d("myTag", "Retry");
		    		Thread.sleep(100);
		    		byteArray = getByteArrayFromURL(UrlList[mapid]);
		    	}

		    	if (byteArray.length <= 0){
			    //						Log.e("myTag", "Retry2");
		    		Thread.sleep(100);
		    		byteArray = getByteArrayFromURL(UrlList[mapid]);
		    	}
		    	if (byteArray.length > 0)
		    		saveXml(byteArray, XML_NAME[mapid], mapid);

		    	if(byteArray == null || byteArray.length <= 0) {
		    		Message msg = handler.obtainMessage(-1);
		    		handler.sendMessage(msg);
		    	}
		    	parseXmlData(byteArray);
		    } catch (Throwable t) {
			// just end the background thread
			//					Log.e("myTag", "error = " + t.toString());
		    	Message msg = handler.obtainMessage(-1);
		    	handler.sendMessage(msg);
		    	return;
		    }

		    String[] ThumbPath = sbThumb.toString().split("\t");
		    String[] camId = sbCamID.toString().split("\t");
		    String[] mapId = sbMapID.toString().split("\t");
		    String[] posX = sbX.toString().split("\t");
		    String[] posY = sbY.toString().split("\t");
		    String[] areaName = sbName.toString().split("\t");

		    Drawable[] dlThumb = new Drawable[ThumbPath.length];
		    //Log.d("myTag", "DEBUG2");
		    for (int i = 0; i < ThumbPath.length; i++){
		    	dlThumb[i] = null;
		    	String thumbUrl = sbBaseurl.toString() + ThumbPath[i];
			//					Log.d("myTag", "BaseUrl=" + sbBaseurl.toString());
			//					Log.d("myTag", "thumbUrl=" + thumbUrl);
		    	try {
		    		dlThumb[i] = Drawable.createFromStream((InputStream)(new URL(thumbUrl).getContent()), "src");
		    	} catch (MalformedURLException e) {
			    // TODO Auto-generated catch block
		    		e.printStackTrace();
		    	} catch (IOException e) {
			    // TODO Auto-generated catch block
		    		e.printStackTrace();
			    //						Log.e("myTag", e.toString());
		    	} catch (IllegalStateException e) {
		    		e.printStackTrace();
		    	}
			//					Log.d("myTag", "DEBUG3");
		    	Bitmap bitmap = Bitmap.createBitmap(76, 58, Config.RGB_565);
		    	Canvas canvas = new Canvas(bitmap);
		    	if (dlThumb[i] != null){
		    		dlThumb[i].setBounds(0, 0, 76, 58);
		    		dlThumb[i].draw(canvas);
		    		String [] idTemp = ThumbPath[i].split("/");
			    //						Log.d("myTag", "id=" + idTemp[1]);
		    		saveThumnail(i, idTemp[1], bitmap);
		    	}
		    	bitmap.recycle();
		    }
		    Message msg = handler.obtainMessage(1, mapid, 0, dlThumb);
		    handler.sendMessage(msg);
		    for (int i = 0; i < ThumbPath.length; i++){
		    	if(dlThumb[i] != null)
		    		dlThumb[i].setCallback(null);
		    }
		}
	    });
	//Log.d("myTag", "Start In SoftKey");
	background.start();
	//		SearchThread.removeCallbacks(animTask);
	//		SearchThread.postDelayed(animTask, 100);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        menu.add(0, 0,0, "????????????").setIcon(R.drawable.ic_menu_update);
        return true;
    }

    public void saveThumnail(int idx, String filename, Bitmap image){
    	File dir = getDir("temp", Activity.MODE_PRIVATE);

    	String Filename = dir.getAbsolutePath() + "/" + filename + ".jpg";

    	FileOutputStream Os = null;
    	try {
	    //Os = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
	    Os = new FileOutputStream(Filename);

	    image.compress(Bitmap.CompressFormat.JPEG, 100, Os);
	    try {
		Os.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
    	} catch (FileNotFoundException e) {
	    e.printStackTrace();
    	}

    }


    public void makeButton(FrameLayout layout, int LeftMargin, int TopMargin, Bitmap imageBitmap, String LiveCamID, String AreaName,
			   final String mapId, int idx){
//    	LinearLayout ll = new LinearLayout(getApplicationContext());
//    	ll.setOrientation(LinearLayout.VERTICAL);
//    	ll.setGravity(Gravity.CENTER_HORIZONTAL);
    	
    	FrameLayout ll = new FrameLayout(getApplicationContext());
    	
    	FrameLayout.LayoutParams LL_PARAMS = new FrameLayout.LayoutParams(
    											ViewGroup.LayoutParams.WRAP_CONTENT, 
    											ViewGroup.LayoutParams.WRAP_CONTENT
    										);
    	
    	
    	FrameLayout.LayoutParams BUTTON_PARAMS = new FrameLayout.LayoutParams(
//    											ViewGroup.LayoutParams.WRAP_CONTENT,
//    											ViewGroup.LayoutParams.WRAP_CONTENT,
    											120,
    											115,
    											Gravity.CENTER_HORIZONTAL | Gravity.TOP
    										);
    	FrameLayout.LayoutParams IMAGE_PARAMS = new FrameLayout.LayoutParams(
									     ViewGroup.LayoutParams.WRAP_CONTENT,
									     ViewGroup.LayoutParams.WRAP_CONTENT,
									     Gravity.CENTER_HORIZONTAL | Gravity.TOP
									     );
    	FrameLayout.LayoutParams TEXT_PARAMS = new FrameLayout.LayoutParams(
									    ViewGroup.LayoutParams.WRAP_CONTENT,
									    ViewGroup.LayoutParams.WRAP_CONTENT,
									    Gravity.TOP|Gravity.CENTER_HORIZONTAL
									    );

    	LeftMargin += 44;
    	LeftMargin *= fram.getWidth();
    	LeftMargin /= 480;
    	LeftMargin -= 44;
    	LeftMargin -= 15;
    	TopMargin += 82;
    	TopMargin *= fram.getHeight();
    	TopMargin /= 628;
    	TopMargin -= 82;
    	TopMargin -= 25;
    	
    	LL_PARAMS.setMargins(LeftMargin, TopMargin, 0, 0);

    	String[] szTemp = AreaName.split(" ");

    	String areaname = szTemp[szTemp.length - 1].trim();
    	if (szTemp.length == 2)
	    areaname = szTemp[0] + szTemp[1];

    	imgbtn[idx] = new ImageButton(getApplicationContext());
    	imgview[idx] = new ImageView(getApplicationContext());
    	tv[idx] = new TextView(getApplicationContext());
	//    	tvbg[idx] = new TextView(this);
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(90, 20);

//    	if (areaname.length() == 1)
//    		TEXT_PARAMS.setMargins(LeftMargin + 50, TopMargin + 104, 0, 0);
//    	else if (areaname.length() == 2)
//    		TEXT_PARAMS.setMargins(LeftMargin + 27, TopMargin + 104, 0, 0);
//    	else if (areaname.length() == 3)
//    		TEXT_PARAMS.setMargins(LeftMargin + 12, TopMargin + 104, 0, 0);
//    	else if (areaname.length() == 4)
//    		TEXT_PARAMS.setMargins(LeftMargin, TopMargin + 104, 0, 0);
//    	else if (areaname.length() == 5)
//    		TEXT_PARAMS.setMargins(LeftMargin - 12, TopMargin + 104, 0, 0);
//    	else
//    		TEXT_PARAMS.setMargins(LeftMargin - 27, TopMargin + 104, 0, 0);
    	
//    	tv[idx].setLayoutParams(params);
    	tv[idx].setText(areaname);
    	tv[idx].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
    	tv[idx].setTextColor(Const.FONT_COLOR_WHITE);
    	tv[idx].setShadowLayer(1, 1, 1, Color.BLACK);

    	if (LiveCamID == null || LiveCamID.length() <= 0)
	    imgbtn[idx].setClickable(false);

    	imgbtn[idx].setTag(LiveCamID);

    	imgbtn[idx].setOnClickListener(new OnClickListener() {

		@Override
		    public void onClick(View v) {
		    // TODO Auto-generated method stub
		    if (!mapId.equals("-1")){
			try{
			    nCurrentMap = Integer.parseInt(mapId);
			}catch (NumberFormatException e) {
			    nCurrentMap = 0;
			}
			imgContent.invalidate();
			//Log.i("myTag", "DrawMap Button");
			drawMap();
		    } else {
			Intent intent;
			intent = new Intent(LiveCam.this, ViewLiveCam.class);
			//Log.d("myTag", "CamID = " + v.getTag().toString());
			intent.putExtra("CamID", v.getTag().toString());
			intent.putExtra("Mapid", nCurrentMap);
			((TabRoot)getParent()).setCurrentTab(intent);
		    }
		}
	    });

    	IMAGE_PARAMS.setMargins(0, 10, 0, 0);
    	imgview[idx].setImageBitmap(imageBitmap);

//    	BUTTON_PARAMS.setMargins(LeftMargin, TopMargin, 0, 0);

    	imgbtn[idx].setBackgroundResource(R.drawable.livecambtn_selector);
    	
    	TEXT_PARAMS.setMargins(0, 107, 0, 0);
    	
    	ll.addView(imgbtn[idx], BUTTON_PARAMS);
    	ll.addView(imgview[idx], IMAGE_PARAMS);
    	ll.addView(tv[idx], TEXT_PARAMS);
    	layout.addView(ll, LL_PARAMS);

//    	layout.addView(imgbtn[idx], BUTTON_PARAMS);
//    	layout.addView(imgview[idx], IMAGE_PARAMS);
//    	layout.addView(tv[idx], TEXT_PARAMS);
    }

    public void ShowUpdateAgain() {
    	if (alert != null){
	    //Log.i("myTag", "alert is not null");
	    return;
	    //    		((DialogInterface) alert).dismiss();
	    //    		alert = null;
    	}

//    	Loading.stop();
    	progress.setVisibility(View.INVISIBLE);

    	Context mContext = getApplicationContext();
//    	Context mContext = this;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.live_popup,(ViewGroup) findViewById(R.id.livecam_root));

    	if(null == alert)
    		return;
//    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    		alert = new AlertDialog.Builder(getParent(), AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//    	else
    		alert = new AlertDialog.Builder(getParent());
    	alert.setTitle("???????????? ??????");
    	alert.setIcon(R.drawable.ic_dialog_menu_generic);
    	//alert.setMessage("????????? ????????? ?????? ??????????????? ?????????????????????. ?????? ????????? ????????????.");
    	alert.setView(layout);

    	alert.setPositiveButton("??????", new DialogInterface.OnClickListener() {
    		@Override
		    public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		    //Log.i("myTag", "alert set to null!!");
		    alert = null;
    		}
	    });

    	alert.setNegativeButton("?????????", new DialogInterface.OnClickListener() {
    		@Override
		    public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		    alert = null;
		    //Log.i("myTag", "alert set to null!!");
		    UpdateLiveCam();
    		}
	    });

    	alert.show();

    }
}
