package com.weathernews.Weather;

public class WeeklyData {
	private String[] szWeeklyDate = new String[10];
	private int[] nWeeklyDayOfWeek = new int[10];
	private String[] szWeeklyIcon = new String[10];
	private String[] szWeeklyTempMax = new String[10];
	private String[] szWeeklyTempMin = new String[10];
	
	public WeeklyData(){
	}
	
	public WeeklyData(String[] date, int[] dow, String[] icon, String[] TempMax, String[] TempMin){
		szWeeklyDate = date;
		nWeeklyDayOfWeek = dow;
		szWeeklyIcon = icon;
		szWeeklyTempMax = TempMax;
		szWeeklyTempMin = TempMin;
	}
	
	public String[] getWeeklyDate(){
		return szWeeklyDate;
	}

	public String getWeeklyDate(int i){
		return (i < szWeeklyDate.length ? szWeeklyDate[i] : "");
	}

	public int[] getWeeklyDayOfWeek(){
		return nWeeklyDayOfWeek;
	}

	public int getWeeklyDayOfWeek(int i){
		return (i < nWeeklyDayOfWeek.length ? nWeeklyDayOfWeek[i] : 0);
	}

	public String[] getWeeklyIcon(){
		return szWeeklyIcon;
	}

	public String getWeeklyIcon(int i){
		return (i < szWeeklyIcon.length ? szWeeklyIcon[i] : "");
	}

	public String[] getWeeklyTempMax(){
		return szWeeklyTempMax;
	}

	public String getWeeklyTempMax(int i){
		return (i < szWeeklyTempMax.length ? szWeeklyTempMax[i] : "");
	}

	public String[] getWeeklyTempMin(){
		return szWeeklyTempMin;
	}

	public String getWeeklyTempMin(int i){
		return (i < szWeeklyTempMin.length ? szWeeklyTempMin[i] : "");
	}

	public void setWeeklyDate(String[] date){
		szWeeklyDate = date;
	}

	public void setWeeklyDate(String date, int idx){
		if (idx < szWeeklyDate.length) szWeeklyDate[idx] = date;
	}

	public void setWeeklyDayOfWeek(int[] dow){
		nWeeklyDayOfWeek = dow;
	}

	public void setWeeklyDayOfWeek(int dow, int idx){
		if (idx < nWeeklyDayOfWeek.length) nWeeklyDayOfWeek[idx] = dow;
	}

	public void setWeeklyIcon(String[] icon){
		szWeeklyIcon = icon;
	}

	public void setWeeklyIcon(String icon, int i){
		if (i < szWeeklyIcon.length) szWeeklyIcon[i] = icon;
	}

	public void setWeeklyTempMax(String[] Temp){
		szWeeklyTempMax = Temp;
	}

	public void setWeeklyTempMax(String Temp, int idx){
		if (idx < szWeeklyTempMax.length) szWeeklyTempMax[idx] = Temp;
	}

	public void setWeeklyTempMin(String[] Temp){
		szWeeklyTempMin = Temp;
	}

	public void setWeeklyTempMin(String Temp, int idx){
		if (idx < szWeeklyTempMin.length) szWeeklyTempMin[idx] = Temp;
	}
}
