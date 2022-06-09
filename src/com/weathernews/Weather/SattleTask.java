package com.weathernews.Weather;

import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class SattleTask implements Runnable{
	private static LandSattle animation;
	private String BaseUrl;
	private String datestring;
	private String term;
	private String type;
	private final static Drawable[] drawable = new Drawable[10];

	SattleTask(LandSattle animation, String baseurl, String DateString, String TimeTerm, String FileType) {
		this.animation = animation;
		this.BaseUrl = baseurl;
		datestring = DateString;
		term = TimeTerm;
		type = FileType;
	}


	public void run(){
		String [] Temp = getFileName(datestring, term, type);
        
		if(BaseUrl != null || datestring != null || term != null) {
			for(int i = 0 ; i < 10 ; i++){
				try{
					final String url = BaseUrl + "japan-coverage/" + Temp[i];
//					Log.e("myTag", url);
					int j = 0;
					while(j < 3) {
					drawable[i] = Drawable.createFromStream((InputStream)(new URL(url).getContent()), "src");
					j++;
					if(drawable[i] != null) break;
					}
				} catch (Exception e) {}
			}
		}

		animation.SetButtonBackImage(drawable);

		clearmemory();
	}
	
	public void clearmemory() {
		for(int i = 0 ; i < 10 ; i ++){
			if(null != drawable[i]){
				drawable[i].setCallback(null);
				drawable[i] = null;
			}
		}
	}

	private String[] getFileName(String datestring2, String term2, String type2) {
		// TODO Auto-generated method stub
		Calendar cl = Calendar.getInstance();
		Calendar cl2 = Calendar.getInstance();
		String[] temp = datestring2.split("/");
		String[] ReturnValue = new String[10];
		int nTerm = 60;
		int nYear = 0, nMonth = 0, nDay = 0, nHour = 0, nMin = 0;

		try{
			if(term2 != null)
				nTerm = Integer.parseInt(term2);
			if(temp.length == 5) {
				if(temp[0] != null)
					nYear = Integer.parseInt(temp[0]);
				if(temp[1] != null)
					nMonth = Integer.parseInt(temp[1]);
				if(temp[2] != null)
					nDay = Integer.parseInt(temp[2]);
				if(temp[3] != null)
					nHour = Integer.parseInt(temp[3]);
				if(temp[4] != null)
					nMin = Integer.parseInt(temp[4]);
			}
		} catch(NumberFormatException ne) {
			cl.setTimeInMillis(cl.getTimeInMillis() - 10 * 60 * 60 * 1000);
			nYear = cl.get(Calendar.YEAR);
			nMonth = cl.get(Calendar.MONTH) + 1;
			nDay = cl.get(Calendar.DAY_OF_MONTH);
			nHour = cl.get(Calendar.HOUR_OF_DAY);
			nMin = 0;
			nTerm = 60;
		} catch(NullPointerException e) {
			cl.setTimeInMillis(cl.getTimeInMillis() - 10 * 60 * 60 * 1000);
			nYear = cl.get(Calendar.YEAR);
			nMonth = cl.get(Calendar.MONTH) + 1;
			nDay = cl.get(Calendar.DAY_OF_MONTH);
			nHour = cl.get(Calendar.HOUR_OF_DAY);
			nMin = 0;
			nTerm = 60;
		}

		cl.set(nYear, nMonth - 1, nDay, nHour, nMin, 0);

		for(int i = 10, j = 0 ; i > 0 ; i --, j++) {
			cl2.setTimeInMillis(cl.getTimeInMillis() - nTerm * 60 * 1000 * i);
			ReturnValue[j] = String.format("%04d%02d%02d%02d%02d", cl2.get(Calendar.YEAR), cl2.get(Calendar.MONTH) + 1, cl2.get(Calendar.DAY_OF_MONTH),
					cl2.get(Calendar.HOUR_OF_DAY), cl2.get(Calendar.MINUTE))  + "." + type2;
		}

		return ReturnValue;
	}
}
