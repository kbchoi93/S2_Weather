package com.weathernews.Weather;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectTimeoutException;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.NinePatchDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.view.ViewGroup;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

public class Util
{
    public static boolean getDateFlag(Context context)
    {
	SharedPreferences prefs = context.getSharedPreferences(Const.PREFS_NAME, 0);
	return null == prefs ? false : prefs.getBoolean("DateFlag", true);
    }

    public static boolean getTimeFlag(Context context)
    {
	SharedPreferences prefs = context.getSharedPreferences(Const.PREFS_NAME, 0);
	return null == prefs ? false : prefs.getBoolean("TimeFlag", true);
    }

    public static boolean getTempFlag(Context context)
    {
	SharedPreferences prefs = context.getSharedPreferences(Const.PREFS_NAME, 0);
	return null == prefs ? true : prefs.getBoolean("TempFlag", true);
    }

    public static String getFahrenheit(String centigrade)
    {
	String Fahrenheit;
	float nC;
	try {
	    nC = Float.parseFloat(centigrade);
	    Fahrenheit = Integer.toString((int)(nC * 1.8f + 32f));
	} catch(Exception e) {
	    Fahrenheit = "--";
	}

	return Fahrenheit;
    }

    public static SpannableStringBuilder setTempStyle(String str)
    {
	SpannableStringBuilder s = new SpannableStringBuilder(str);
	int smallStart = -1;
	int len = str.length();
	for (int i = 0; i < len; ++i)
	    {
		int c = str.codePointAt(i);
		if (Character.isDigit(c) || '-' == c || '+' == c)
		    {
			if (-1 != smallStart)
			    {
				s.setSpan(new RelativeSizeSpan(0.5f), smallStart, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				smallStart = -1;
			    }
		    }
		else
		    {
			if (-1 == smallStart) smallStart = i;
		    }
	    }
	if (smallStart != -1) s.setSpan(new RelativeSizeSpan(0.5f), smallStart, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	return s;
    }

    public static String formatDateAsFooter(Calendar cl, boolean in24hour, boolean isMMDD)
    {
    	String result = "";
	    if (isMMDD)
	    	result += String.format("%02d.%02d.%02d ", cl.get(Calendar.YEAR) % 100, cl.get(Calendar.MONTH) + 1, cl.get(Calendar.DAY_OF_MONTH), Const.Week_day[cl.get(Calendar.DAY_OF_WEEK)-1]);
	    else
	    	result += String.format("%02d.%02d.%02d ", cl.get(Calendar.YEAR) % 100, cl.get(Calendar.DAY_OF_MONTH), cl.get(Calendar.MONTH) + 1, Const.Week_day[cl.get(Calendar.DAY_OF_WEEK)-1]);

	    if (!in24hour)
	    	result += String.format(cl.get(Calendar.HOUR_OF_DAY) >= 12 ? "오후 %02d:%02d" : "오전 %02d:%02d", cl.get(Calendar.HOUR), cl.get(Calendar.MINUTE));
	    else
			result += String.format("%02d:%02d 업데이트", cl.get(Calendar.HOUR_OF_DAY), cl.get(Calendar.MINUTE));
	    
	    return result;
    }

    public static void setFooter(View v, Calendar cl, boolean in24hour, boolean isMMDD)
    {
	String date = formatDateAsFooter(cl, in24hour, isMMDD);
	((TextView)v.findViewById(R.id.bottombar_date)).setText(date);
    }

//    public static Bitmap getFooter(Resources rs, Calendar cl, int mode, boolean TimeFlag)
//    {
//	Bitmap bm;
//	Bitmap logobg = BitmapFactory.decodeResource(rs, R.drawable.update_bg_01);
//    	Bitmap logo = BitmapFactory.decodeResource(rs, R.drawable.update_wn_logo);
//    	String UpdateTime;
//
//    	if (0 == mode) // 세로모드
//	    bm = Bitmap.createBitmap(480, 38, Config.RGB_565);
//    	else			// 가로모드
//	    bm = Bitmap.createBitmap(800, 38, Config.RGB_565);
//
//    	Canvas canvas = new Canvas(bm);
//    	Rect rcLogo = new Rect(0, 0, 132, 38);
//    	Rect rcLogoBG = new Rect(0, 0, 800, 38);
//
//    	Paint paint = new Paint();
//
//    	paint.setAntiAlias(true);
//    	canvas.drawBitmap(logobg, null, rcLogoBG, null);
//    	canvas.drawBitmap(logo, null, rcLogo, null);
//
////    	paint.setTypeface(Const.typeFace);
//	paint.setColor(Const.FONT_COLOR_WHITE);
//	paint.setTextSize(18);
//	paint.setTextAlign(Paint.Align.LEFT);
//
//	if (!TimeFlag)
//	    {
//		if (cl.get(Calendar.HOUR_OF_DAY) >= 12)
//		    UpdateTime = String.format("%02d.%02d.%02d 오후 %02d:%02d", cl.get(Calendar.YEAR)- 2000, cl.get(Calendar.MONTH) + 1, cl.get(Calendar.DAY_OF_MONTH), cl.get(Calendar.HOUR), cl.get(Calendar.MINUTE));
//		else
//		UpdateTime = String.format("%02d.%02d.%02d 오전 %02d:%02d", cl.get(Calendar.YEAR)- 2000, cl.get(Calendar.MONTH) + 1, cl.get(Calendar.DAY_OF_MONTH), cl.get(Calendar.HOUR), cl.get(Calendar.MINUTE));
//	    }
//	else
//	    UpdateTime = String.format("%02d.%02d.%02d %02d:%02d", cl.get(Calendar.YEAR)- 2000, cl.get(Calendar.MONTH) + 1, cl.get(Calendar.DAY_OF_MONTH), cl.get(Calendar.HOUR_OF_DAY), cl.get(Calendar.MINUTE));
//
//	if (mode == 0)
//	    {
//		if (!TimeFlag)
//		    canvas.drawText(UpdateTime + " 업데이트", 252 - 46, rcLogo.top + 26, paint);
//		else
//		    canvas.drawText(UpdateTime + " 업데이트", 252, rcLogo.top + 26, paint);
//	    }
//	else
//	    {
//		if (!TimeFlag)
//		    canvas.drawText(UpdateTime + " 업데이트", 560 - 46, rcLogo.top + 26, paint);
//		else
//		    canvas.drawText(UpdateTime + " 업데이트", 560, rcLogo.top + 26, paint);
//	    }
//	logo.recycle();
//	logobg.recycle();
//
//	return bm;
//    }

    public static InputStream getByteArrayFromURL(String strUrl) throws UnknownHostException {
	InputStream in = null;
	URL url = null;
	URLConnection urlcon = null;
	HttpURLConnection hurlc = null;

	try {
		url = new URL(strUrl);
		urlcon = url.openConnection();
		hurlc = (HttpURLConnection)urlcon;
		hurlc.setReadTimeout(2500);
		hurlc.setConnectTimeout(1000);
		hurlc.setRequestProperty("Connection", "close");
		int nHTTPResultCode = hurlc.getResponseCode();
		if(nHTTPResultCode < HttpURLConnection.HTTP_BAD_REQUEST) {
			in = hurlc.getInputStream();
		}
	}
	catch (SocketTimeoutException e) { e.printStackTrace(); }
	catch (ConnectTimeoutException e) { e.printStackTrace(); }
	catch (MalformedURLException e) { e.printStackTrace(); }
	catch (IOException e) { e.printStackTrace(); }

	return in;
    }
    
    public static InputStream getByteArrayFromURL(String strUrl, int nReadTimeOut) throws UnknownHostException{
    	InputStream in = null;
    	URL url = null;
    	URLConnection urlcon = null;
    	HttpURLConnection hurlc = null;

    	try{
    		url = new URL(strUrl);
    		urlcon = url.openConnection();
    		hurlc = (HttpURLConnection)urlcon;
    		hurlc.setReadTimeout(nReadTimeOut);
    		hurlc.setConnectTimeout(1500);
    		hurlc.setRequestProperty("Connection", "close");
    		int nHTTPResultCode = hurlc.getResponseCode();
    		if(nHTTPResultCode < HttpURLConnection.HTTP_BAD_REQUEST) {
    			in = hurlc.getInputStream();
    		}
    	}
    	catch (SocketTimeoutException e) { e.printStackTrace(); }
    	catch (ConnectTimeoutException e) { e.printStackTrace(); }
    	catch (MalformedURLException e) { e.printStackTrace(); }
    	catch (IOException e) { e.printStackTrace(); }

    	return in;
    }

    public static int[][] getTodayIconID(String szIcon, int hour)
    {
	int [][] Icon;

	boolean isDayTime = getIsDayTime(null, hour);

	if (szIcon.equals("01"))
	    {
		if (isDayTime)
		    Icon = Const.icon_01;
		else
		    Icon = Const.icon_51;
	    }
	else if (szIcon.equals("02"))
	    {
		if (isDayTime)
		    Icon = Const.icon_02;
		else
		    Icon = Const.icon_52;
	    }
	else if (szIcon.equals("03"))
	    {
		if (isDayTime)
		    Icon = Const.icon_03;
		else
		    Icon = Const.icon_53;
	    }
	else if (szIcon.equals("04"))
	    {
		if (isDayTime)
		    Icon = Const.icon_05;
		else
		    Icon = Const.icon_55;
	    }
	else if (szIcon.equals("05"))
	    {
		if (isDayTime)
		    Icon = Const.icon_03;
		else
		Icon = Const.icon_53;
	    }
	else if (szIcon.equals("06"))
	    {
		if (isDayTime)
		    Icon = Const.icon_08;
		else
		    Icon = Const.icon_58;
	    }
	else if (szIcon.equals("07"))
	    Icon = Const.icon_10;
	else if (szIcon.equals("08"))
	    Icon = Const.icon_16;
	else if (szIcon.equals("09"))
	    Icon = Const.icon_16;
	else if (szIcon.equals("11"))
	    {
		if (isDayTime)
		    Icon = Const.icon_05;
		else
		    Icon = Const.icon_55;
	    }
	else if (szIcon.equals("12"))
	    Icon = Const.icon_10;
	else if (szIcon.equals("13"))
	    Icon = Const.icon_16;
	else if (szIcon.equals("14"))
	    {
		if (isDayTime)
		    Icon = Const.icon_03;
		else
		    Icon = Const.icon_53;
	    }
	else if (szIcon.equals("15"))
	    Icon = Const.icon_10;
	else if (szIcon.equals("16"))
	    {
		if (isDayTime)
		    Icon = Const.icon_26;
		else
		    Icon = Const.icon_76;
	    }
	else
	    {
		if (isDayTime)
		Icon = Const.icon_01;
		else
		    Icon = Const.icon_51;
	    }
	return Icon;
    }

    public static boolean getIsDayTime(String mainIcon, int hour)
    {
	boolean isDayTime = false;
	String s = mainIcon;
	if (hour >= 7 && hour < 18)
	    isDayTime = true;
	else
	    isDayTime = false;

	if (mainIcon != null)
	    if (s.equals("07") || s.equals("08") || s.equals("09") || s.equals("12") || s.equals("13") || s.equals("15"))
		isDayTime = false;

	return isDayTime;
    }

//    public static Bitmap getWeekIconID(Resources r, String szIcon)
//    {
//	Bitmap bm;
//
//	if(szIcon == null) {
//		bm = BitmapFactory.decodeResource(r, R.drawable.weather_icon_01);
//		return bm;
//	}
//	if (szIcon.equals("01"))
//	    bm = BitmapFactory.decodeResource(r, R.drawable.weather_icon_01);
//	else if (szIcon.equals("02"))
//	    bm = BitmapFactory.decodeResource(r, R.drawable.weather_icon_05);
//	else if (szIcon.equals("03"))
//	    bm = BitmapFactory.decodeResource(r, R.drawable.weather_icon_06);
//	else if (szIcon.equals("04"))
//	    bm = BitmapFactory.decodeResource(r, R.drawable.weather_icon_05);
//	else if (szIcon.equals("05"))
//	    bm = BitmapFactory.decodeResource(r, R.drawable.weather_icon_06);
//	else if (szIcon.equals("06"))
//	    bm = BitmapFactory.decodeResource(r, R.drawable.weather_icon_09);
//	else if (szIcon.equals("07"))
//	    bm = BitmapFactory.decodeResource(r, R.drawable.weather_icon_10);
//	else if (szIcon.equals("08"))
//	    bm = BitmapFactory.decodeResource(r, R.drawable.weather_icon_11);
//	else if (szIcon.equals("09"))
//	    bm = BitmapFactory.decodeResource(r, R.drawable.weather_icon_11);
//	else if (szIcon.equals("11"))
//	    bm = BitmapFactory.decodeResource(r, R.drawable.weather_icon_12);
//	else if (szIcon.equals("12"))
//	    bm = BitmapFactory.decodeResource(r, R.drawable.weather_icon_15);
//	else if (szIcon.equals("13"))
//	    bm = BitmapFactory.decodeResource(r, R.drawable.weather_icon_22);
//	else if (szIcon.equals("14"))
//	    bm = BitmapFactory.decodeResource(r, R.drawable.weather_icon_19);
//	else if (szIcon.equals("15"))
//	    bm = BitmapFactory.decodeResource(r, R.drawable.weather_icon_15);
//	else if (szIcon.equals("16"))
//	    bm = BitmapFactory.decodeResource(r, R.drawable.weather_icon_26);
//	else
//	    bm = BitmapFactory.decodeResource(r, R.drawable.weather_icon_01);
//	return bm;
//    }

    public static int getWeekIconID(Resources r, String szIcon)
    {
	int bm;

	if(szIcon == null) {
		bm = R.drawable.weather_icon_01;
		return bm;
	}
	if (szIcon.equals("01"))
	    bm = R.drawable.weather_icon_01;
	else if (szIcon.equals("02"))
	    bm = R.drawable.weather_icon_05;
	else if (szIcon.equals("03"))
	    bm = R.drawable.weather_icon_06;
	else if (szIcon.equals("04"))
	    bm = R.drawable.weather_icon_05;
	else if (szIcon.equals("05"))
	    bm = R.drawable.weather_icon_06;
	else if (szIcon.equals("06"))
	    bm = R.drawable.weather_icon_09;
	else if (szIcon.equals("07"))
	    bm = R.drawable.weather_icon_10;
	else if (szIcon.equals("08"))
	    bm = R.drawable.weather_icon_11;
	else if (szIcon.equals("09"))
	    bm = R.drawable.weather_icon_11;
	else if (szIcon.equals("11"))
	    bm = R.drawable.weather_icon_12;
	else if (szIcon.equals("12"))
	    bm = R.drawable.weather_icon_15;
	else if (szIcon.equals("13"))
	    bm = R.drawable.weather_icon_22;
	else if (szIcon.equals("14"))
	    bm = R.drawable.weather_icon_19;
	else if (szIcon.equals("15"))
	    bm = R.drawable.weather_icon_15;
	else if (szIcon.equals("16"))
	    bm = R.drawable.weather_icon_26;
	else
	    bm = R.drawable.weather_icon_01;
	return bm;
    }
    
//    public static Bitmap getHourIconID(Resources r, String szIcon, String mainIcon, int hour)
//    {
//	boolean isDayTime = getIsDayTime(mainIcon, hour);
//	try{
//	if (szIcon.equals("01"))
//	    return BitmapFactory.decodeResource(r, isDayTime ? R.drawable.weather_icon_01s : R.drawable.weather_icon_01s_w);
//	else if (szIcon.equals("02"))
//	    return BitmapFactory.decodeResource(r, isDayTime ? R.drawable.weather_icon_05s : R.drawable.weather_icon_05s_w);
//	else if (szIcon.equals("03"))
//	    return BitmapFactory.decodeResource(r, isDayTime ? R.drawable.weather_icon_06s : R.drawable.weather_icon_06s_w);
//	else if (szIcon.equals("04"))
//	    return BitmapFactory.decodeResource(r, isDayTime ? R.drawable.weather_icon_05s : R.drawable.weather_icon_05s_w);
//	else if (szIcon.equals("05"))
//	    return BitmapFactory.decodeResource(r, isDayTime ? R.drawable.weather_icon_06s : R.drawable.weather_icon_06s_w);
//	else if (szIcon.equals("06"))
//	    return BitmapFactory.decodeResource(r, isDayTime ? R.drawable.weather_icon_09s : R.drawable.weather_icon_09s_w);
//	else if (szIcon.equals("07"))
//	    return BitmapFactory.decodeResource(r, isDayTime ? R.drawable.weather_icon_10s : R.drawable.weather_icon_10s_w);
//	else if (szIcon.equals("08"))
//	    return BitmapFactory.decodeResource(r, isDayTime ? R.drawable.weather_icon_11s : R.drawable.weather_icon_11s_w);
//	else if (szIcon.equals("09"))
//	    return BitmapFactory.decodeResource(r, isDayTime ? R.drawable.weather_icon_11s : R.drawable.weather_icon_11s_w);
//	else if (szIcon.equals("11"))
//	    return BitmapFactory.decodeResource(r, isDayTime ? R.drawable.weather_icon_12s : R.drawable.weather_icon_12s_w);
//	else if (szIcon.equals("12"))
//	    return BitmapFactory.decodeResource(r, isDayTime ? R.drawable.weather_icon_15s : R.drawable.weather_icon_15s_w);
//	else if (szIcon.equals("13"))
//	    return BitmapFactory.decodeResource(r, isDayTime ? R.drawable.weather_icon_22s : R.drawable.weather_icon_22s_w);
//	else if (szIcon.equals("14"))
//	    return BitmapFactory.decodeResource(r, isDayTime ? R.drawable.weather_icon_19s : R.drawable.weather_icon_19s_w);
//	else if (szIcon.equals("15"))
//	    return BitmapFactory.decodeResource(r, isDayTime ? R.drawable.weather_icon_15s : R.drawable.weather_icon_15s_w);
//	else if (szIcon.equals("16"))
//	    return BitmapFactory.decodeResource(r, isDayTime ? R.drawable.weather_icon_26s : R.drawable.weather_icon_26s_w);
//	else
//	    return BitmapFactory.decodeResource(r, R.drawable.weather_icon_01s);
//	}catch(NullPointerException e) {
//		System.gc();
//		return null;
//	}
//    }

    public static int getHourIconID(Resources r, String szIcon, String mainIcon, int hour)
    {
	boolean isDayTime = getIsDayTime(mainIcon, hour);
	try{
	if (szIcon.equals("01"))
	    return isDayTime ? R.drawable.weather_icon_01s : R.drawable.weather_icon_01s_w;
	else if (szIcon.equals("02"))
	    return isDayTime ? R.drawable.weather_icon_05s : R.drawable.weather_icon_05s_w;
	else if (szIcon.equals("03"))
	    return isDayTime ? R.drawable.weather_icon_06s : R.drawable.weather_icon_06s_w;
	else if (szIcon.equals("04"))
	    return isDayTime ? R.drawable.weather_icon_05s : R.drawable.weather_icon_05s_w;
	else if (szIcon.equals("05"))
	    return isDayTime ? R.drawable.weather_icon_06s : R.drawable.weather_icon_06s_w;
	else if (szIcon.equals("06"))
	    return isDayTime ? R.drawable.weather_icon_09s : R.drawable.weather_icon_09s_w;
	else if (szIcon.equals("07"))
	    return isDayTime ? R.drawable.weather_icon_10s : R.drawable.weather_icon_10s_w;
	else if (szIcon.equals("08"))
	    return isDayTime ? R.drawable.weather_icon_11s : R.drawable.weather_icon_11s_w;
	else if (szIcon.equals("09"))
	    return isDayTime ? R.drawable.weather_icon_11s : R.drawable.weather_icon_11s_w;
	else if (szIcon.equals("11"))
	    return isDayTime ? R.drawable.weather_icon_12s : R.drawable.weather_icon_12s_w;
	else if (szIcon.equals("12"))
	    return isDayTime ? R.drawable.weather_icon_15s : R.drawable.weather_icon_15s_w;
	else if (szIcon.equals("13"))
	    return isDayTime ? R.drawable.weather_icon_22s : R.drawable.weather_icon_22s_w;
	else if (szIcon.equals("14"))
	    return isDayTime ? R.drawable.weather_icon_19s : R.drawable.weather_icon_19s_w;
	else if (szIcon.equals("15"))
	    return isDayTime ? R.drawable.weather_icon_15s : R.drawable.weather_icon_15s_w;
	else if (szIcon.equals("16"))
	    return isDayTime ? R.drawable.weather_icon_26s : R.drawable.weather_icon_26s_w;
	else
	    return R.drawable.weather_icon_01s;
	}catch(NullPointerException e) {
		System.gc();
		return R.drawable.weather_icon_01s;
	}
    }
    
//    public static Bitmap getIconID(Resources r, String szIcon)
//    {
//    	if(r == null)
//    		return null;
//    	try{
//	if (szIcon.equals("01"))
//	    return BitmapFactory.decodeResource(r, R.drawable.weather_icon_01_ref);
//	else if (szIcon.equals("02"))
//	    return BitmapFactory.decodeResource(r, R.drawable.weather_icon_05_ref);
//	else if (szIcon.equals("03"))
//	    return BitmapFactory.decodeResource(r, R.drawable.weather_icon_06_ref);
//	else if (szIcon.equals("04"))
//	    return BitmapFactory.decodeResource(r, R.drawable.weather_icon_05_ref);
//	else if (szIcon.equals("05"))
//	    return BitmapFactory.decodeResource(r, R.drawable.weather_icon_06_ref);
//	else if (szIcon.equals("06"))
//	    return BitmapFactory.decodeResource(r, R.drawable.weather_icon_09_ref);
//	else if (szIcon.equals("07"))
//	    return BitmapFactory.decodeResource(r, R.drawable.weather_icon_10_ref);
//	else if (szIcon.equals("08"))
//	    return BitmapFactory.decodeResource(r, R.drawable.weather_icon_11_ref);
//	else if (szIcon.equals("09"))
//	    return BitmapFactory.decodeResource(r, R.drawable.weather_icon_11_ref);
//	else if (szIcon.equals("11"))
//	    return BitmapFactory.decodeResource(r, R.drawable.weather_icon_12_ref);
//	else if (szIcon.equals("12"))
//	    return BitmapFactory.decodeResource(r, R.drawable.weather_icon_15_ref);
//	else if (szIcon.equals("13"))
//	    return BitmapFactory.decodeResource(r, R.drawable.weather_icon_22_ref);
//	else if (szIcon.equals("14"))
//	    return BitmapFactory.decodeResource(r, R.drawable.weather_icon_19_ref);
//	else if (szIcon.equals("15"))
//	    return BitmapFactory.decodeResource(r, R.drawable.weather_icon_15_ref);
//	else if (szIcon.equals("16"))
//	    return BitmapFactory.decodeResource(r, R.drawable.weather_icon_26_ref);
//	else
//	    return BitmapFactory.decodeResource(r, R.drawable.weather_icon_01_ref);
//    	}catch(NullPointerException e) {
//    		System.gc();
//    		return null;
//    	}
//    }

    public static int getIconID(Resources r, String szIcon)
    {
    	if(r == null)
    		return R.drawable.weather_icon_01_ref;
    	try{
	if (szIcon.equals("01"))
	    return  R.drawable.weather_icon_01_ref;
	else if (szIcon.equals("02"))
	    return R.drawable.weather_icon_05_ref;
	else if (szIcon.equals("03"))
	    return R.drawable.weather_icon_06_ref;
	else if (szIcon.equals("04"))
	    return R.drawable.weather_icon_05_ref;
	else if (szIcon.equals("05"))
	    return R.drawable.weather_icon_06_ref;
	else if (szIcon.equals("06"))
	    return R.drawable.weather_icon_09_ref;
	else if (szIcon.equals("07"))
	    return R.drawable.weather_icon_10_ref;
	else if (szIcon.equals("08"))
	    return R.drawable.weather_icon_11_ref;
	else if (szIcon.equals("09"))
	    return R.drawable.weather_icon_11_ref;
	else if (szIcon.equals("11"))
	    return R.drawable.weather_icon_12_ref;
	else if (szIcon.equals("12"))
	    return R.drawable.weather_icon_15_ref;
	else if (szIcon.equals("13"))
	    return R.drawable.weather_icon_22_ref;
	else if (szIcon.equals("14"))
	    return R.drawable.weather_icon_19_ref;
	else if (szIcon.equals("15"))
	    return R.drawable.weather_icon_15_ref;
	else if (szIcon.equals("16"))
	    return R.drawable.weather_icon_26_ref;
	else
	    return R.drawable.weather_icon_01_ref;
    	}catch(NullPointerException e) {
    		System.gc();
    		return R.drawable.weather_icon_01_ref;
    	}
    }

    public static String getWindDirText(String Winddir)
    {
	String WindDirText;

	if (Winddir.equals("0"))
	    WindDirText = "북";
	else if (Winddir.equals("1"))
	    WindDirText = "북북동";
	else if (Winddir.equals("2"))
	    WindDirText = "북동";
	else if (Winddir.equals("3"))
	    WindDirText = "동북동";
	else if (Winddir.equals("4"))
	    WindDirText = "동";
	else if (Winddir.equals("5"))
	    WindDirText = "동남동";
	else if (Winddir.equals("6"))
	    WindDirText = "남동";
	else if (Winddir.equals("7"))
	    WindDirText = "남남동";
	else if (Winddir.equals("8"))
	    WindDirText = "남";
	else if (Winddir.equals("9"))
	    WindDirText = "남남서";
	else if (Winddir.equals("10"))
	    WindDirText = "남서";
	else if (Winddir.equals("11"))
	    WindDirText = "서남서";
	else if (Winddir.equals("12"))
	    WindDirText = "서";
	else if (Winddir.equals("13"))
	    WindDirText = "서북서";
	else if (Winddir.equals("14"))
	    WindDirText = "서북";
	else if (Winddir.equals("15"))
	    WindDirText = "북북서";
	else
	    WindDirText = "--";

	return WindDirText;
    }

    public static boolean setCaptureAndSend(Context context, View view, Bitmap bmp) {
		// TODO Auto-generated method stub
    	if(view == null)
    		return false;
		view.setDrawingCacheEnabled(true);
		try {
			if(view != null && view.isDrawingCacheEnabled()) {
				view.buildDrawingCache();
				bmp = view.getDrawingCache();
			} else {
				return false;
			}
		}catch (NullPointerException e) {
			return false;
		}catch (OutOfMemoryError e) {
			return false;
		}
		
		File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() , "WeatherInfo") ;
		if(!dir.exists())
			dir.mkdirs();

		Calendar cl = Calendar.getInstance();

		String tempFileName = String.format("%04d-%02d-%02d %02d.%02d.%02d", 
				cl.get(Calendar.YEAR), cl.get(Calendar.MONTH) + 1, cl.get(Calendar.DAY_OF_MONTH),
				cl.get(Calendar.HOUR_OF_DAY), cl.get(Calendar.MINUTE), cl.get(Calendar.SECOND));

		String fileName = dir.getAbsolutePath() + "/" + tempFileName + ".jpg";

		FileOutputStream Os = null;
		try {
			Os = new FileOutputStream(fileName);
			if(!bmp.compress(Bitmap.CompressFormat.JPEG, 50, Os)) {
				Os.close();
				return false;
			}
			
			Os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		view.setDrawingCacheEnabled(false);
		bmp.recycle();
		view.destroyDrawingCache();

		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("image/jpeg");
		
	    File F = new File(fileName);
	    ContentValues values = new ContentValues();
	    values.put(MediaStore.Images.Media.DATA, F.toString());
	    values.put(MediaStore.Images.Media.DISPLAY_NAME, tempFileName);
	    values.put(MediaStore.Images.Media.TITLE, tempFileName);
	    values.put(MediaStore.Images.Media.DATE_TAKEN, cl.getTimeInMillis());
	    values.put(MediaStore.Images.Media.SIZE, F.length());
	    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
	    try{
	    	Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	    	intent.putExtra(Intent.EXTRA_STREAM, uri);
	    } catch (UnsupportedOperationException e) {
	    	e.printStackTrace();
	    	values.clear();
	    	values = null;
	    	return false;
	    }

	    values.clear();
		values = null;

	    try {
	    	context.startActivity(Intent.createChooser(intent, "날씨정보 보내기"));
	    } catch (ActivityNotFoundException e) {
	    	e.printStackTrace();
	    	return false;
	    }

		return true;
	}

    public static void shareFunction(Context context, String fileName)
    {
	Intent intent = new Intent(android.content.Intent.ACTION_SEND);

	if (fileName != null) {
	    intent.setType("image/jpeg");
	    File F = new File(fileName);
	    ContentValues values = new ContentValues();
	    values.put(MediaStore.Images.Media.DATA, F.toString());
	    values.put(MediaStore.Images.Media.SIZE, F.length());
	    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
	    Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
	    intent.putExtra(Intent.EXTRA_STREAM, uri);
	}
	context.startActivity(Intent.createChooser(intent, "날씨정보보내기"));
	return;
    }
    
    public static boolean isAlpha(int ch) {
		if((ch > 64 && ch < 91) || (ch > 96 && ch < 123))
			return true;
		else
			return false;
	}
    
    public static boolean checkNetwork(ConnectivityManager manager) {
    	boolean isMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
    	boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
    	if( !isMobile && !isWifi) {
//    		LoadingAnimation.stop();
//    		LoadingLogo.setVisibility(View.INVISIBLE);
    		return false;
    	} else {
    		return true;
    	}
    }
    
    public static float dp2px(int dip, Context context){
    	float scale = context.getResources().getDisplayMetrics().density;
    	return dip * scale + 0.5f;
    }
}
