package com.weathernews.Weather;

public class Theme {
	private String szThemeCode;
	private String szAreaCode;
	private String szName;
	private String szAddress;
	private int	nThmeID;
	private int nRegionID;
	private double longi;
	private double lati;
	
	public static final int THEME_GOLF = 0;
	public static final int THEME_SKI = 1;
	public static final int THEME_MOUNTAIN = 2;
	public static final int THEME_BASEBALL = 3;
	public static final int THEME_THEMEPARK = 4;
	public static final int THEME_BEACH = 5;
	
	public static final int THEME_SEOUL = 0;
	public static final int THEME_GANGWON = 1;
	public static final int THEME_GYEONGGI = 2;
	public static final int THEME_GYEONGSANG = 3;
	public static final int THEME_JEOLLA = 4;
	public static final int THEME_CHUNGCHEONG = 5;
	public static final int THEME_JEJU = 6;
	
	public Theme(String ThemeCode, String code, String Name, String Address, int id, int Region, double lati, double longi){
		this.szThemeCode = ThemeCode;
		this.szAreaCode = code;
		this.szName = Name;
		this.szAddress = Address;
		this.nThmeID = id;
		this.nRegionID = Region;
		this.lati = lati;
		this.longi = longi;
	}

	public String getThemeCode(){
		return szThemeCode;
	}

	public String getAreaCode(){
		return szAreaCode;
	}
	
	public String getName(){
		return szName;
	}
	
	public String getAddress(){
		return szAddress;
	}
	
	public int getThemeID(){
		return nThmeID;
	}
	
	public int getRegionID(){
		return nRegionID;
	}
	
	public double getLatitude(){
		return lati;
	}
	
	public double getLongitude(){
		return longi;
	}
}

