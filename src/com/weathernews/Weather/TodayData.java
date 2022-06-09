package com.weathernews.Weather;

class TodayData{
    private String szTodayIcon;
    private String szTodayTempMax;
    private String szTodayTempMin;
    private String szTodayPop;
    private String szTodayVisi;
    private String szTodayTempCur;
    private String szTodayHum;
    private String szTodayWnddir;
    private String szTodayWndspd;
    private String szTodayTempFeel;
    private String szTodayPress;
    private String szTodayIndex1;
    private String szTodayIndex2;
    private String szTodayExplain;
    private String szTodayUpdateTime;
    
    public TodayData(){
    }
    
    public TodayData(String icon, String Tempmax, String Tempmin, String pop, String visi,
    					String Tempcur, String Hum, String Wnddir, String Wndspd, String Tempfeel,
    					String press, String index1, String index2, String explain, String updateTime){
    	szTodayIcon = icon;
    	szTodayTempMax = Tempmax;
    	szTodayTempMin = Tempmin;
    	szTodayPop = pop;
    	szTodayVisi = visi;
    	szTodayTempCur = Tempcur;
    	szTodayHum = Hum;
    	szTodayWnddir = Wnddir;
    	szTodayWndspd = Wndspd;
    	szTodayTempFeel = Tempfeel;
    	szTodayPress = press;
    	szTodayIndex1 = index1;
    	szTodayIndex2 = index2;
    	szTodayExplain = explain;
    	szTodayUpdateTime = updateTime;
    }
	
    public String getTodayIcon(){
    	return szTodayIcon;
    }
    
    public String getTodayTempMax(){
    	return szTodayTempMax;
    }
    
    public String getTodayTempMin(){
    	return szTodayTempMin;
    }
    
    public String getTodayPop(){
    	return szTodayPop;
    }
    
    public String getTodayVisi(){
    	return szTodayVisi;
    }
    
    public String getTodayTempCur(){
    	return szTodayTempCur;
    }
    
    public String getTodayHum(){
    	return szTodayHum;
    }
    
    public String getTodayWindDir(){
    	return szTodayWnddir;
    }
    
    public String getTodayWindSpeed(){
    	return szTodayWndspd;
    }
    
    public String getTodayTempFeel() {
    	return szTodayTempFeel;
    }
    
    public String getTodayPress(){
    	return szTodayPress;
    }
    
    public String getTodayIndex1(){
    	return szTodayIndex1;
    }
    
    public String getTodayIndex2(){
    	return szTodayIndex2;
    }
    
    public String getTodayExplain(){
    	return szTodayExplain;
    }
    
    public String getTodayUpdateTime(){
    	return szTodayUpdateTime;
    }
    
    public void setTodayIcon(String icon){
    	szTodayIcon = icon;
    }
    
    public void setTodayTempMax(String TempMax){
    	szTodayTempMax = TempMax;
    }
    
    public void setTodayTempMin(String TempMin){
    	szTodayTempMin = TempMin;
    }
    
    public void setTodayPop(String Pop){
    	szTodayPop = Pop;
    }
    
    public void setTodayVisi(String visi){
    	szTodayVisi = visi;
    }
    
    public void setTodayTempCur(String TempCur){
    	szTodayTempCur = TempCur;
    }
    
    public void setTodayHum(String Hum){
    	szTodayHum = Hum;
    }
    
    public void setTodayWindDir(String dir){
    	szTodayWnddir = dir;
    }
    
    public void setTodayWindSpeed(String speed){
    	szTodayWndspd = speed;
    }
    
    public void setTodayTempFeel(String TempFeel){
    	szTodayTempFeel = TempFeel;
    }
    
    public void setTodayPress(String press){
    	szTodayPress = press;
    }
    
    public void setTodayIndex1(String index){
    	szTodayIndex1 = index;
    }
    
    public void setTodayIndex2(String index){
    	szTodayIndex2 = index;
    }
    
    public void setTodayExplain(String explain){
    	szTodayExplain = explain;
    }
    
    public void setTodayUpdateTime(String UpdateTime){
    	szTodayUpdateTime = UpdateTime;
    }
}

