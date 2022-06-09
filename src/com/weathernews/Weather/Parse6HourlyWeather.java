package com.weathernews.Weather;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class Parse6HourlyWeather<XmlParser> extends DefaultHandler {
    private StringBuffer sbRegion_id	= new StringBuffer();
    private StringBuffer sbIcon = new StringBuffer();
    private StringBuffer sbTemp = new StringBuffer();
    private StringBuffer sbPop = new StringBuffer();

    private boolean region_id = false;
    private boolean icon = false;
    private boolean temp = false;
    private boolean pop = false;

    private XmlParser xp;

    public Parse6HourlyWeather(XmlParser xp)
	{
	    this.xp		=		xp;
	}
    public void startElement(String uri, String localName, String qName, Attributes atts)
    {
	if (localName.equals("region_id"))
	    region_id = true;
	else if (localName.equals("icon"))
	    icon = true;
	else if (localName.equals("temp"))
	    temp = true;
	else if (localName.equals("pop"))
	    pop = true;
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
		sbRegion_id.append(chars, start, leng);
		sbRegion_id.append('\t');
	    }
	else if (icon)
	    {
		icon = false;
		sbIcon.append(chars, start, leng);
		sbIcon.append('\t');
	    }
	else if (temp)
	    {
		temp = false;
		sbTemp.append(chars, start, leng);
		sbTemp.append('\t');
	    }
	else if (pop)
	    {
		pop = false;
		sbPop.append(chars, start, leng);
		sbPop.append('\t');
	    }
    }

    public StringBuffer getregion_id() { return sbRegion_id; }
    public StringBuffer geticon()      { return sbIcon; }
    public StringBuffer gettemp()      { return sbTemp; }
    public StringBuffer getpop()       { return sbPop; }
}

