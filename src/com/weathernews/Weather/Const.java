package com.weathernews.Weather;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;

public class Const {
	public static final int FONT_COLOR_WHITE = Color.rgb(255, 255, 255);
	public static final int	FONT_COLOR_BLUE = Color.rgb(99, 151, 255);
	public static final int FONT_COLOR_CITY_NAME = Color.rgb(239, 255, 148);
	public static final int FONT_COLOR_RED = Color.rgb(255, 0, 0);
	public static final String[] Week_day = {"일", "월", "화", "수", "목", "금", "토" };
	public static final String[] HOURLY_TXT = {"새벽", "오전", "오후", "저녁"};
	public static final String PREFS_NAME = "com.weathernews.WeatherWiget";
	public static final String CITY_LIST = "CLIT_LIST";
	public static final String CITY_CNT = "CITY_CNT";
	public static final String UPDATE_TIME_LIST = "UPDATETIME";
	public static final String UPDATE_TIME_TODAY = "UPDATE_TODAY";
	public static final String Today_Today = "TODAY_T_WEATHER";
	public static final String Today_Hourly = "TODAY_H_WEATHER";
	public static final String Today_Weekly = "TODAY_W_WEATHER";
	public static final String LIVECAMID = "CAMID";
	
	public static final String THEME_LIST = "THEME_LIST";
	public static final String THEME_CNT = "THEME_CNT";
	public static final String THEME_LIST_UPDATETIME = "THEME_UPDATE";
	public static final String THEME_TODAY_UPDATETIME = "THEME_TODAY_UPDATE";
	public static final String THEME_TODAY_WEATHER = "THEME_T_WEATHER";
	public static final String THEME_HOURLY_WEATHER = "THEME_H_WEATHER";
	public static final String THEME_WEEKLY_WEATHER = "THEME_W_WEATHER"; 
	
	public static final String LIVECAM_UPDATE_TIME = "LIVE_UPDATE";
	public static final String LIVECAM_LIST = "LIVE_LIST";
	
	public static final String URL_0 = "http://soratomo.wni.co.kr/android/";
	public static final String URL_LIST = URL_0 + "list_weather.cgi?";
	public static final String URL_TODAY = URL_0 + "today_weather.cgi?";
	public static final String URL_WEEK = URL_0 + "weekly_weather.cgi?";
	public static final String URL_HOURLY = URL_0 + "6hourly_weather.cgi?";

	public static final int i_bg_01_1[]				= {R.drawable.weather_main_bg_01, 0, 0, 480, 474};
	public static final int i_bg_02_1[]				= {R.drawable.weather_main_bg_02, 0, 0, 480, 474};
	public static final int i_weather_sun_1[]		= {R.drawable.weather_sun_1, 115, 112, 365, 364};
	public static final int i_weather_sun_2_1[]		= {R.drawable.weather_sun_2, 78, 103, 306, 331};
	public static final int i_weather_moon_1_1[]	= {R.drawable.weather_moon_1, 166, 151, 318, 303};
	public static final int i_weather_moon_1_3[]	= {R.drawable.weather_moon_1, 166, 103, 318, 255};
	public static final int i_weather_rain_1[]		= {R.drawable.weather_rain_1, 19, 173, 480, 445};
	public static final int i_weather_snow_1_1[]	= {R.drawable.weather_snow_1, 33, 261, 479, 445};
	public static final int i_weather_fog[]			= {R.drawable.weather_fog, 0, 136, 480, 351};
	public static final int i_weather_cloud_2_2[]	= {R.drawable.weather_cloud_2_2, 0, 0, 480, 289};
	public static final int i_weather_cloud_3_1[]	= {R.drawable.weather_cloud_3_1, 0, 0, 480, 239};
	public static final int i_weather_cloud_4[]		= {R.drawable.weather_cloud_4, 0, 146, 480, 366};
	public static final int i_weather_cloud_4_1[]	= {R.drawable.weather_cloud_4, 0, 92, 480, 312};
	public static final int i_weather_cloud_9_1[]	= {R.drawable.weather_cloud_9, 0, 112, 480, 357};
	public static final int i_weather_cloud_14[]	= {R.drawable.weather_cloud_14, 0, 137, 480, 367};
	
	public static final int icon_01[][] = {i_bg_01_1, i_weather_cloud_2_2, i_weather_sun_1};
	public static final int icon_02[][] = {i_bg_01_1, i_weather_cloud_2_2, i_weather_sun_2_1, i_weather_cloud_9_1};
	public static final int icon_03[][] = {i_bg_01_1, i_weather_cloud_2_2, i_weather_sun_2_1, i_weather_cloud_4, i_weather_rain_1};
	public static final int icon_05[][] = {i_bg_01_1, i_weather_cloud_2_2, i_weather_sun_2_1, i_weather_cloud_4};
	public static final int icon_08[][] = {i_bg_01_1, i_weather_cloud_4_1};
	public static final int icon_10[][] = {i_bg_02_1, i_weather_cloud_4_1, i_weather_rain_1};
	public static final int icon_16[][] = {i_bg_02_1, i_weather_cloud_4, i_weather_snow_1_1};
	public static final int icon_26[][] = {i_bg_01_1, i_weather_cloud_2_2, i_weather_sun_2_1, i_weather_cloud_4, i_weather_snow_1_1};
	
	public static final int icon_51[][] = {i_bg_02_1, i_weather_cloud_2_2, i_weather_moon_1_1};
	public static final int icon_52[][] = {i_bg_02_1, i_weather_cloud_2_2, i_weather_moon_1_3, i_weather_cloud_9_1};
	public static final int icon_53[][] = {i_bg_02_1, i_weather_cloud_2_2, i_weather_moon_1_3, i_weather_cloud_4, i_weather_rain_1};
	public static final int icon_55[][] = {i_bg_02_1, i_weather_cloud_2_2, i_weather_moon_1_3, i_weather_cloud_4};
	public static final int icon_58[][] = {i_bg_02_1, i_weather_cloud_4_1};
	public static final int icon_76[][] = {i_bg_02_1, i_weather_cloud_2_2, i_weather_moon_1_3, i_weather_cloud_4, i_weather_snow_1_1};
	
	
//	public static final int[][][] weathericon = {icon_01, icon_02, icon_03, icon_05, icon_08, icon_10, icon_16, icon_26, 
//		icon_51, icon_52, icon_53, icon_55, icon_58, icon_76
//	};
	
	public static enum WEATHER_FOCUS_BTN {
		WEATHER_NONE,
		WEATHER_WEATHER_BTN,
		WEATHER_THEME_BTN,
		WEATHER_LIVECAM_TAB,
		WEATHER_LIVECAM_BTN,
		WEATHER_GPS_BTN,
		WEATHER_ICON_AREA,
		WEATHER_WEEK_AREA
	};
	
	public static final String [][] CITY_VALUE = {
		{"백령도", "11A00101"},
		{"서울", "11B10101"},
		{"강화", "11B20101"},
		{"김포", "11B20102"},
		{"인천", "11B20201"},
		{"시흥", "11B20202"},
		{"안산", "11B20203"},
		{"부천", "11B20204"},
		{"의정부", "11B20301"},
		{"고양", "11B20302"},
		{"파주", "11B20303"},
		{"양주", "11B20304"},
		{"문산", "11B20305"},
		{"동두천", "11B20401"},
		{"연천", "11B20402"},
		{"포천", "11B20403"},
		{"가평", "11B20404"},
		{"구리", "11B20501"},
		{"남양주", "11B20502"},
		{"양평", "11B20503"},
		{"수원", "11B20601"},
		{"안양", "11B20602"},
		{"오산", "11B20603"},
		{"화성", "11B20604"},
		{"성남", "11B20605"},
		{"평택", "11B20606"},
		{"이천", "11B20701"},
		{"충주", "11C10101"},
		{"진천", "11C10102"},
		{"음성", "11C10103"},
		{"제천", "11C10201"},
		{"단양", "11C10202"},
		{"청주", "11C10301"},
		{"보은", "11C10302"},
		{"괴산", "11C10303"},
		{"증평", "11C10304"},
		{"추풍령", "11C10401"},
		{"영동", "11C10402"},
		{"옥천", "11C10403"},
		{"서산", "11C20101"},
		{"태안", "11C20102"},
		{"당진", "11C20103"},
		{"홍성", "11C20104"},
		{"보령", "11C20201"},
		{"서천", "11C20202"},
		{"천안", "11C20301"},
		{"아산", "11C20302"},
		{"대전", "11C20401"},
		{"공주", "11C20402"},
		{"부여", "11C20501"},
		{"청양", "11C20502"},
		{"금산", "11C20601"},
		{"논산", "11C20602"},
		{"철원", "11D10101"},
		{"화천", "11D10102"},
		{"인제", "11D10201"},
		{"양구", "11D10202"},
		{"춘천", "11D10301"},
		{"홍천", "11D10302"},
		{"원주", "11D10401"},
		{"횡성", "11D10402"},
		{"영월", "11D10501"},
		{"정선", "11D10502"},
		{"평창", "11D10503"},
		{"대관령", "11D20201"},
		{"태백", "11D20301"},
		{"속초", "11D20401"},
		{"양양", "11D20403"},
		{"강릉", "11D20501"},
		{"동해", "11D20601"},
		{"삼척", "11D20602"},
		{"울릉도", "11E00101"},
		{"독도", "11E00102"},
		{"전주", "11F10201"},
		{"익산", "11F10202"},
		{"정읍", "11F10203"},
		{"장수", "11F10301"},
		{"무주", "11F10302"},
		{"진안", "11F10303"},
		{"남원", "11F10401"},
		{"임실", "11F10402"},
		{"순창", "11F10403"},
		{"완도", "11F20301"},
		{"해남", "11F20302"},
		{"강진", "11F20303"},
		{"장흥", "11F20304"},
		{"여수", "11F20401"},
		{"광양", "11F20402"},
		{"고흥", "11F20403"},
		{"보성", "11F20404"},
		{"순천", "11F20405"},
		{"광주", "11F20501"},
		{"장성", "11F20502"},
		{"나주", "11F20503"},
		{"담양", "11F20504"},
		{"구례", "11F20601"},
		{"곡성", "11F20602"},
		{"흑산도", "11F20701"},
		{"성산", "11G00101"},
		{"제주", "11G00201"},
		{"성판악", "11G00302"},
		{"서귀포", "11G00401"},
		{"고산", "11G00501"},
		{"울진", "11H10101"},
		{"영덕", "11H10102"},
		{"포항", "11H10201"},
		{"경주", "11H10202"},
		{"문경", "11H10301"},
		{"상주", "11H10302"},
		{"예천", "11H10303"},
		{"영주", "11H10401"},
		{"봉화", "11H10402"},
		{"영양", "11H10403"},
		{"안동", "11H10501"},
		{"의성", "11H10502"},
		{"청송", "11H10503"},
		{"김천", "11H10601"},
		{"구미", "11H10602"},
		{"군위", "11H10603"},
		{"고령", "11H10604"},
		{"성주", "11H10605"},
		{"대구", "11H10701"},
		{"영천", "11H10702"},
		{"경산", "11H10703"},
		{"청도", "11H10704"},
		{"칠곡", "11H10705"},
		{"울산", "11H20101"},
		{"양산", "11H20102"},
		{"부산", "11H20201"},
		{"창원", "11H20301"},
		{"마산", "11H20302"},
		{"진해", "11H20303"},
		{"김해", "11H20304"},
		{"통영", "11H20401"},
		{"사천", "11H20402"},
		{"거제", "11H20403"},
		{"고성", "11H20404"},
		{"남해", "11H20405"},
		{"함양", "11H20501"},
		{"거창", "11H20502"},
		{"합천", "11H20503"},
		{"밀양", "11H20601"},
		{"의령", "11H20602"},
		{"함안", "11H20603"},
		{"창녕", "11H20604"},
		{"진주", "11H20701"},
		{"산청", "11H20703"},
		{"해주", "11I20001"},
		{"개성", "11I20002"},
		{"평양", "11J20001"},
		{"중강진", "11J20003"},
		{"청진", "11K10001"},
		{"함흥", "11K20001"},
		{"군산", "21F10501"},
		{"김제", "21F10502"},
		{"고창", "21F10601"},
		{"부안", "21F10602"},
		{"함평", "21F20101"},
		{"영광", "21F20102"},
		{"진도", "21F20201"},
		{"목포", "21F20801"},
		{"영암", "21F20802"},
		{"신안", "21F20803"},
		{"무안", "21F20804"}
	};
}