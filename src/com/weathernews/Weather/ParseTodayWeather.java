package com.weathernews.Weather;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class ParseTodayWeather<XmlParser> extends DefaultHandler {
    private String szRegion_id;
    private String szPointName;
    private String szIcon;
    private String szTempMax;
    private String szTempMin;
    private String szPop;
    private String szVisi;
    private String szTempCur;
    private String szHum;
    private String szWindDir;
    private String szWindSpeed;
    private String szTempFeel;
    private String szPress;
    private String szIndex1;
    private String szIndex2;
    private String szExplain;

    private boolean region_id = false;
    private boolean pointname = false;
    private boolean icon = false;
    private boolean tempmin = false;
    private boolean tempmax = false;
    private boolean pop = false;
    private boolean visi = false;
    private boolean tempcur = false;
    private boolean hum = false;
    private boolean winddir = false;
    private boolean windspeed = false;
    private boolean tempfeel = false;
    private boolean press = false;
    private boolean index1 = false;
    private boolean index2 = false;
    private boolean explain = false;

    private XmlParser xp;

    public ParseTodayWeather(XmlParser xp)
	{
	    this.xp = xp;
	}
    public void startElement(String uri, String localName, String qName, Attributes atts)
    {
	if (localName.equals("region_id"))
	    region_id = true;
	else if (localName.equals("PointName"))
	    pointname = true;
	else if (localName.equals("WX_icon"))
	    icon = true;
	else if (localName.equals("TempMax"))
	    tempmax = true;
	else if (localName.equals("TempMin"))
	    tempmin = true;
	else if (localName.equals("PopAM"))
	    pop = true;
	else if (localName.equals("Visi"))
	    visi = true;
	else if (localName.equals("TempCur"))
	    tempcur = true;
	else if (localName.equals("Hum"))
	    hum = true;
	else if (localName.equals("Wnddir"))
	    winddir = true;
	else if (localName.equals("Wndspd"))
	    windspeed = true;
	else if (localName.equals("TempFeel"))
	    tempfeel = true;
	else if (localName.equals("press"))
	    press = true;
	else if (localName.equals("index1"))
	    index1 = true;
	else if (localName.equals("index2"))
	    index2 = true;
	else if (localName.equals("explain"))
	    explain = true;
    }
    /*
      public void endElement(String uri, String localName, String qName)
      {
      if (localName.equals("person"))
      {
      xp.updateTextView(names.toString()+"\n"+comps.toString()+"\n"+departs.toString());
      }
      }
    */
    public void characters(char[] chars, int start, int leng)
    {
	if (region_id)
	    {
		region_id = false;
		szRegion_id = new String(chars, start, leng);
	    }
	else if (pointname)
	    {
		pointname = false;
		szPointName = new String(chars, start, leng);
	    }
	else if (icon)
	    {
		icon = false;
		szIcon = new String(chars, start, leng);
	    }
	else if (tempmax)
	    {
		tempmax = false;
		szTempMax = new String(chars, start, leng);
	    }
	else if (tempmin)
	    {
		tempmin = false;
		szTempMin = new String(chars, start, leng);
	    }
	else if (pop)
	    {
		pop = false;
		szPop = new String(chars, start, leng);
	    }
	else if (visi)
	    {
		visi = false;
		szVisi = new String(chars, start, leng);
	    }
	else if (hum)
	    {
		hum = false;
		szHum = new String(chars, start, leng);
	    }
	else if (winddir)
	    {
		winddir = false;
		szWindDir = new String(chars, start, leng);
	    }
	else if (windspeed)
	    {
		windspeed = false;
		szWindSpeed = new String(chars, start, leng);
	    }
	else if (tempcur)
	    {
		tempcur = false;
		szTempCur = new String(chars, start, leng);
	    }
	else if (tempfeel)
	    {
		tempfeel = false;
		szTempFeel = new String(chars, start, leng);
	    }
	else if (press)
	    {
		press = false;
		szPress = new String(chars, start, leng);
	    }
	else if (index1)
	    {
		index1 = false;
		szIndex1 = new String(chars, start, leng);
	    }
	else if (index2)
	    {
		index2 = false;
		szIndex2 = new String(chars, start, leng);
	    }
	else if (explain)
	    {
		explain = false;
		szExplain = new String(chars, start, leng);
	    }
    }

    public String getregion_id() { return (null == szRegion_id ? "" : szRegion_id); }
    public String getpointname() { return (null == szPointName ? "" : szPointName); }
    public String geticon()      { return (null == szIcon ?      "" : szIcon); }
    public String gettempmax()   { return (null == szTempMax ?   "" : szTempMax); }
    public String gettempmin()   { return (null == szTempMin ?   "" : szTempMin); }
    public String getpop()       { return (null == szPop ?       "" : szPop); }
    public String getvisi()      { return (null == szVisi ?      "" : szVisi); }
    public String gettempcur()   { return (null == szTempCur ?   "" : szTempCur); }
    public String gethum()       { return (null == szHum ?       "" : szHum); }
    public String getwnddir()    { return (null == szWindDir ?   "" : szWindDir); }
    public String getwndspd()    { return (null == szWindSpeed ? "" : szWindSpeed); }
    public String gettempfeel()  { return (null == szTempFeel ?  "" : szTempFeel); }
    public String getpress()     { return (null == szPress ?     "" : szPress); }
    public String getindex1()    { return (null == szIndex1 ?    "" : szIndex1); }
    public String getindex2()    { return (null == szIndex2 ?    "" : szIndex2); }
    public String getexplain()   { return (null == szExplain ?   "" : szExplain); }
}
