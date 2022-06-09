package com.weathernews.Weather;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class ParseWeeklyWeather<XmlParser> extends DefaultHandler {
    private StringBuffer sbRegion_id = new StringBuffer();
    private StringBuffer sbIcon = new StringBuffer();
    private StringBuffer sbDate = new StringBuffer();
    private StringBuffer sbDayofWeek = new StringBuffer();
    private StringBuffer sbTempMin = new StringBuffer();
    private StringBuffer sbTempMax = new StringBuffer();


    private boolean region_id = false;
    private boolean date = false;
    private boolean dayofweek = false;
    private boolean icon = false;
    private boolean tempmin = false;
    private boolean tempmax = false;
    private XmlParser xp;

    public ParseWeeklyWeather(XmlParser xp)
	{
	    this.xp		=		xp;
	}
    public void startElement(String uri, String localName, String qName, Attributes atts)
    {
	if (localName.equals("id"))
	    region_id = true;
	else if (localName.equals("wd"))
	    date = true;
	else if (localName.equals("dow"))
	    dayofweek = true;
	else if (localName.equals("icon"))
	    icon = true;
	else if (localName.equals("TempMax"))
	    tempmax = true;
	else if (localName.equals("TempMin"))
	    tempmin = true;
    }

    public void characters(char[] chars, int start, int leng)
    {
	if (region_id)
	    {
		region_id = false;
		sbRegion_id.append(chars, start, leng);
		sbRegion_id.append('\t');
	    }
	else if (date)
	    {
		date = false;
		sbDate.append(chars, start, leng);
		sbDate.append('\t');
	    }
	else if (dayofweek)
	    {
		dayofweek = false;
		sbDayofWeek.append(chars, start, leng);
		sbDayofWeek.append('\t');
	    }
	else if (icon)
	    {
		icon = false;
		sbIcon.append(chars, start, leng);
		sbIcon.append('\t');
	    }
	else if (tempmin)
	    {
		tempmin = false;
		sbTempMin.append(chars, start, leng);
		sbTempMin.append('\t');
	    }
	else if (tempmax)
	    {
		tempmax = false;
		sbTempMax.append(chars, start, leng);
		sbTempMax.append('\t');
	    }
    }

    public StringBuffer getregion_id() { return sbRegion_id; }
    public StringBuffer getdate()      { return sbDate; }
    public StringBuffer getdayofweek() { return sbDayofWeek; }
    public StringBuffer geticon()      { return sbIcon; }
    public StringBuffer gettempmin()   { return sbTempMin; }
    public StringBuffer gettempmax()   { return sbTempMax; }
    
    public void ClearMemory() {
        sbRegion_id = null;
        sbIcon = null;
        sbDate = null;
        sbDayofWeek = null;
        sbTempMin = null;
        sbTempMax = null;
    	
    }
}
