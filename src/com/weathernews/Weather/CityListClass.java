package com.weathernews.Weather;

public class CityListClass {
	private String i;
	private String n;
	private String c;
	private String t;


	public final static int KOREA = 0;
	public final static int ASIA = 1;
	public final static int AFRICA = 2;
	public final static int NORTH_AMERICA = 3;
	public final static int SOUTH_AMERICA = 4;
	public final static int EUROPE = 5;
	public final static int OCEANIA = 6;
	public final static int MIDDLEEAST = 7;

	public CityListClass(String CityId, String CityName, String Country, String TimeZone) {
		// TODO Auto-generated constructor stub
		i = CityId;
		n = CityName;
		c = Country;
		t = TimeZone;
	}

	public String getCityID() { return i; }
	public String getCityName() { return n; }
	public String getCountry() { return c; }
	public String getTimeZone() { return t; }
}


