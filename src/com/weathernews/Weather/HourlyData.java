package com.weathernews.Weather;

class HourlyData {
	private String[] szHourlyIcon = new String[8];
	private String[] szHourlyTemp = new String[8];
	private String[] szHourlyPop = new String[8];
	
	public HourlyData(){}
	
	public HourlyData(String[] icon, String[] temp, String[] Pop){
		szHourlyIcon = icon;
		szHourlyTemp = temp;
		szHourlyPop = Pop;
	}
	
	public String[] getHourlyIcons(){
		return szHourlyIcon;
	}
	
	public String getHourlyIcons(int idx){
		return (idx < szHourlyIcon.length ? szHourlyIcon[idx] : "") ;
	}
	
	public String[] getHourlyTemp(){
		return szHourlyTemp;
	}
	
	public String getHourlyTemp(int idx){
		return (idx < szHourlyTemp.length ? szHourlyTemp[idx] : "");
	}
	
	public String[] getHourlyPop(){
		return szHourlyPop;
	}
	
	public String getHourlyPop(int i){
		return (i < szHourlyPop.length ? szHourlyPop[i] : "");
	}
	
	public void setHourlyIcon(String[] icon){
		szHourlyIcon = icon;
	}
	
	public void setHourlyIcon(String icon, int idx){
		if (idx < szHourlyIcon.length) szHourlyIcon[idx] = icon;
	}
	
	public void setHourlyTemp(String[] temp){
		szHourlyTemp = temp;
	}
	
	public void setHourlyTemp(String temp, int idx){
		if (idx < szHourlyTemp.length) szHourlyTemp[idx] = temp;
	}
	
	public void setHourlyPop(String[] pop){
		szHourlyPop = pop;
	}
	
	public void setHourlyPop(String pop, int i){
		if (i < szHourlyPop.length) szHourlyPop[i] = pop;
	}
	
	public void ClearMemory() {
		for(int i = 0 ; i < szHourlyIcon.length ; i++) {
			szHourlyIcon[i] = null;
			szHourlyTemp[i] = null;
			szHourlyPop[i] = null;
		}
		
		szHourlyIcon = null;
		szHourlyTemp = null;
		szHourlyPop = null;
	}
}
