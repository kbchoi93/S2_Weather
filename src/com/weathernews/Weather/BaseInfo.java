package com.weathernews.Weather;

public class BaseInfo {
	private String szCityID;
	private String szCityName;
	private String szTimeZone;
	private int nIndex;
	
	public BaseInfo(){
	}
	
	public BaseInfo(String CityID, String CityName, String timezone, int idx){
		this.szCityID = CityID;
		this.szCityName = CityName;
		this.szTimeZone = timezone;
		this.nIndex = idx;
	}
	
	public String getCityID(){
		return szCityID;
	}
	
	public String getCityName(){
		return szCityName;
	}
	
	public String getTimeZone(){
		return szTimeZone;
	}
	
	public int getIndex(){
		return nIndex;
	}
	
	public void ClearMemory(){
		szCityID = null;
		szCityName = null;
		szTimeZone = null;
	}
}