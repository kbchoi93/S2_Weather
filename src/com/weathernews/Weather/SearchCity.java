package com.weathernews.Weather;

import java.util.ArrayList;


import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView.OnEditorActionListener;

public class SearchCity extends ListActivity implements OnCheckedChangeListener {

    private AutoCompleteTextView edittext;
    private TextWatcher watcher;
    private ImageButton imgbtn;
    private Button btn;
    private Button cancel;

    private String[] CityList;
    private String[] CityName;
    private String[] CountryName;
    private String[] TimeZone;
    private String[] szSearchResult;
    private CityList3[] SearchResult;
    private CityList3[] citylist;
    private ListView listview;
    private InputMethodManager imm;

    private final static int KOREA_COUNT = Korea.korea.length;
    private final static int ASIA_COUNT = Asia.asia.length;
    private final static int AFRICA_COUNT = Africa.africa.length;
    private final static int NORTH_AMERICA = NorthAmerica.northamerica.length;
    private final static int SOUTH_AMERICA = SouthAmerica.southamerica.length;
    private final static int EUROPE_COUNT = Europe.europe.length;
    private final static int OCEANIA_COUNT = Oceania.oceania.length;
    private final static int MIDDLEEAST_COUNT = MiddleEast.middleeast.length;
    private final static int TOTAL_COUNT = KOREA_COUNT + ASIA_COUNT + AFRICA_COUNT + NORTH_AMERICA + SOUTH_AMERICA +
	EUROPE_COUNT + OCEANIA_COUNT + MIDDLEEAST_COUNT;

    private static final int [] ListCnt = {KOREA_COUNT, ASIA_COUNT, AFRICA_COUNT, NORTH_AMERICA, SOUTH_AMERICA, EUROPE_COUNT, OCEANIA_COUNT, MIDDLEEAST_COUNT };

    private boolean mShowing;
    private boolean mReady;
    private String mPrevLetter = "";
    private CityList3Adapter m_adapter3;
    private ArrayList<CityList3> m_orders3;

    private CityList3[] plist3;
    private CityList3[] selectedlist;
    private String areaID;
//    private boolean isShowedKeypad = false;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        StringBuffer sbTemp = null;

        Intent intent = getIntent();
        String Query = intent.getStringExtra("Query");
        areaID = intent.getStringExtra("Area");
        
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        
        //Log.d("myTag", "Queyr = " + Query + " areaID=" + areaID);

        setContentView(R.layout.search);
        makeAutoComp(Query);
        
        watcher = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String InputText = s.toString();
				
				if(InputText.length() == 1)
					makeAutoComp(InputText);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		};
		
		edittext.addTextChangedListener(watcher);
		
		edittext.setText(Query);
		
		edittext.dismissDropDown();
		
		edittext.setSelection(edittext.getText().length());

        LayoutInflater inflate = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imgbtn = (ImageButton) findViewById(R.id.searchbtn);

        imgbtn.setImageResource(R.drawable.ic_btn_search);
        imgbtn.setOnClickListener(new OnClickListener() {

			@Override
		    public void onClick(View v) {
				imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
			    String query = edittext.getText().toString().trim();
			    if(query.length()>0)
			    	SearchResult(query);
			}
        });

        setCityList(Query);
//	citylist = null;
//	if ("World".equals(areaID)) {
//		
//		char tempChar = Query.toUpperCase().charAt(0);
//		if(Util.isAlpha((int)tempChar)) {
//			CityListClass_E[] current = null;
//			switch(tempChar) {
//			case 'A' :
//				current = A.a;
//				break;
//			case 'B' :
//				current = B.b;
//				break;
//			case 'C' :
//				current = C.c;
//				break;
//			case 'D' :
//				current = D.d;
//				break;
//			case 'E' :
//				current = E.e;
//				break;
//			case 'F' :
//				current = F.f;
//				break;
//			case 'G' :
//				current = G.g;
//				break;
//			case 'H' :
//				current = H.h;
//				break;
//			case 'I' :
//				current = I.i;
//				break;
//			case 'J' :
//				current = J.j;
//				break;
//			case 'K' :
//				current = K.k;
//				break;
//			case 'L' :
//				current = L.l;
//				break;
//			case 'M' :
//				current = M.m;
//				break;
//			case 'N' :
//				current = N.n;
//				break;
//			case 'O' :
//				current = O.o;
//				break;
//			case 'P' :
//				current = P.p;
//				break;
//			case 'Q' :
//				current = Q.q;
//				break;
//			case 'R' :
//				current = RR.rr;
//				break;
//			case 'S' :
//				current = S.s;
//				break;
//			case 'T' :
//				current = T.t;
//				break;
//			case 'U' :
//				current = U.u;
//				break;
//			case 'V' :
//				current = V.v;
//				break;
//			case 'W' :
//				current = W.w;
//				break;
//			case 'X' :
//				current = X.x;
//				break;
//			case 'Y' :
//				current = Y.y;
//				break;
//			case 'Z' :
//				current = Z.z;
//				break;
//			default :
//				current = A.a;
//				break;
//			}
//			
//			if(current != null)
//				citylist = new CityList3[current.length];
//			
//			for(int i = 0 ; i < current.length; i ++) {
//				citylist[i] = new CityList3(current[i].getEngCityName(), current[i].getEngCityName(), current[i].getCityName(), 
//						current[i].getCityID(),	current[i].getCountry(), current[i].getTimeZone(), false);
//			}
//		} else {
//			String StartString = HangulUtils.getHangulInitialSound(Query.substring(0, 1));
//			CityListClass[] current = null;
//			if(StartString.equals("???") || StartString.equals("???")) current = GA.ga;
//			else if(StartString.equals("???")) current = NA.na;
//			else if(StartString.equals("???") || StartString.equals("???")) current = DA.da;
//			else if(StartString.equals("???")) current = RA.ra;
//			else if(StartString.equals("???")) current = MA.ma;
//			else if(StartString.equals("???") || StartString.equals("???")) current = BA.ba;
//			else if(StartString.equals("???") || StartString.equals("???")) current = SA.sa;
//			else if(StartString.equals("???")) current = AA.aa;
//			else if(StartString.equals("???") || StartString.equals("???")) current = JA.ja;
//			else if(StartString.equals("???")) current = CHA.cha;
//			else if(StartString.equals("???")) current = KA.ka;
//			else if(StartString.equals("???")) current = TA.ta;
//			else if(StartString.equals("???")) current = PA.pa;
//			else if(StartString.equals("???")) current = HA.ha;
//			else current = GA.ga;
//			
//			if(current != null)
//				citylist = new CityList3[current.length];
//			
//			for(int i = 0 ; i < current.length; i ++) {
//				citylist[i] = new CityList3(current[i].getCityName(),"", current[i].getCityName(), current[i].getCityID(),
//						current[i].getCountry(), current[i].getTimeZone(), false);
//			}
//		}
//		
//	} else { //if ("World".equals(areaID))
//	    CityListClass_E[] list = null;
//	    if ("Korea".equals(areaID))
//	    	list = Korea.korea;
//	    else if ("Asia".equals(areaID))
//	    	list = Asia.asia;
//	    else if ("Africa".equals(areaID))
//	    	list = Africa.africa;
//	    else if ("North America".equals(areaID))
//	    	list = NorthAmerica.northamerica;
//	    else if ("South America".equals(areaID))
//	    	list = SouthAmerica.southamerica;
//	    else if ("Europe".equals(areaID))
//	    	list = Europe.europe;
//	    else if ("Oceania".equals(areaID))
//	    	list = Oceania.oceania;
//	    else if ("MiddleEast".equals(areaID))
//	    	list = MiddleEast.middleeast;
//
//	    if (null != list) {
//			citylist = new CityList3[list.length];
//			for (int i = 0 ; i < list.length; ++i) {
//			    citylist[i] = new CityList3(list[i].getEngCityName(), list[i].getEngCityName(), list[i].getCityName(), list[i].getCityID(),
//							list[i].getCountry(), list[i].getTimeZone(), false);
//			}
//	    }
//	}
//
//	if (null != citylist) {
//	    Draw();
//	    SearchResult(Query);
//	}
    }
    
    private void setCityList(String Query){
    	citylist = null;
    	if ("World".equals(areaID)) {
    		
    		char tempChar = Query.toUpperCase().charAt(0);
    		if(Util.isAlpha((int)tempChar)) {
    			CityListClass_E[] current = null;
    			switch(tempChar) {
    			case 'A' :
    				current = A.a;
    				break;
    			case 'B' :
    				current = B.b;
    				break;
    			case 'C' :
    				current = C.c;
    				break;
    			case 'D' :
    				current = D.d;
    				break;
    			case 'E' :
    				current = E.e;
    				break;
    			case 'F' :
    				current = F.f;
    				break;
    			case 'G' :
    				current = G.g;
    				break;
    			case 'H' :
    				current = H.h;
    				break;
    			case 'I' :
    				current = I.i;
    				break;
    			case 'J' :
    				current = J.j;
    				break;
    			case 'K' :
    				current = K.k;
    				break;
    			case 'L' :
    				current = L.l;
    				break;
    			case 'M' :
    				current = M.m;
    				break;
    			case 'N' :
    				current = N.n;
    				break;
    			case 'O' :
    				current = O.o;
    				break;
    			case 'P' :
    				current = P.p;
    				break;
    			case 'Q' :
    				current = Q.q;
    				break;
    			case 'R' :
    				current = RR.rr;
    				break;
    			case 'S' :
    				current = S.s;
    				break;
    			case 'T' :
    				current = T.t;
    				break;
    			case 'U' :
    				current = U.u;
    				break;
    			case 'V' :
    				current = V.v;
    				break;
    			case 'W' :
    				current = W.w;
    				break;
    			case 'X' :
    				current = X.x;
    				break;
    			case 'Y' :
    				current = Y.y;
    				break;
    			case 'Z' :
    				current = Z.z;
    				break;
    			default :
    				current = A.a;
    				break;
    			}
    			
    			if(current != null)
    				citylist = new CityList3[current.length];
    			
    			for(int i = 0 ; i < current.length; i ++) {
    				citylist[i] = new CityList3(current[i].getEngCityName(), current[i].getEngCityName(), current[i].getCityName(), 
    						current[i].getCityID(),	current[i].getCountry(), current[i].getTimeZone(), false);
    			}
    		} else {
    			String StartString = HangulUtils.getHangulInitialSound(Query.substring(0, 1));
    			CityListClass[] current = null;
    			if(StartString.equals("???") || StartString.equals("???")) current = GA.ga;
    			else if(StartString.equals("???")) current = NA.na;
    			else if(StartString.equals("???") || StartString.equals("???")) current = DA.da;
    			else if(StartString.equals("???")) current = RA.ra;
    			else if(StartString.equals("???")) current = MA.ma;
    			else if(StartString.equals("???") || StartString.equals("???")) current = BA.ba;
    			else if(StartString.equals("???") || StartString.equals("???")) current = SA.sa;
    			else if(StartString.equals("???")) current = AA.aa;
    			else if(StartString.equals("???") || StartString.equals("???")) current = JA.ja;
    			else if(StartString.equals("???")) current = CHA.cha;
    			else if(StartString.equals("???")) current = KA.ka;
    			else if(StartString.equals("???")) current = TA.ta;
    			else if(StartString.equals("???")) current = PA.pa;
    			else if(StartString.equals("???")) current = HA.ha;
    			else current = GA.ga;
    			
    			if(current != null)
    				citylist = new CityList3[current.length];
    			
    			for(int i = 0 ; i < current.length; i ++) {
    				citylist[i] = new CityList3(current[i].getCityName(),"", current[i].getCityName(), current[i].getCityID(),
    						current[i].getCountry(), current[i].getTimeZone(), false);
    			}
    		}
    		
    	} else { //if ("World".equals(areaID))
    	    CityListClass_E[] list = null;
    	    if ("Korea".equals(areaID))
    	    	list = Korea.korea;
    	    else if ("Asia".equals(areaID))
    	    	list = Asia.asia;
    	    else if ("Africa".equals(areaID))
    	    	list = Africa.africa;
    	    else if ("North America".equals(areaID))
    	    	list = NorthAmerica.northamerica;
    	    else if ("South America".equals(areaID))
    	    	list = SouthAmerica.southamerica;
    	    else if ("Europe".equals(areaID))
    	    	list = Europe.europe;
    	    else if ("Oceania".equals(areaID))
    	    	list = Oceania.oceania;
    	    else if ("MiddleEast".equals(areaID))
    	    	list = MiddleEast.middleeast;

    	    if (null != list) {
    			citylist = new CityList3[list.length];
    			for (int i = 0 ; i < list.length; ++i) {
    			    citylist[i] = new CityList3(list[i].getEngCityName(), list[i].getEngCityName(), list[i].getCityName(), list[i].getCityID(),
    							list[i].getCountry(), list[i].getTimeZone(), false);
    			}
    	    }
    	}    	

    	if (null != citylist) {
		    Draw();
		    SearchResult(Query);
		}
    }

    public void makeAutoComp(String Query){
	//String[] AutoComp = new String[KOREA_COUNT + ASIA_COUNT + AFRICA_COUNT + NORTH_AMERICA + SOUTH_AMERICA + EUROPE_COUNT + OCEANIA_COUNT + MIDDLEEAST_COUNT];
	String[] AutoComp = null;
	char tempChar = Query.toUpperCase().charAt(0);
	boolean isalpha = Util.isAlpha((int)tempChar);
//	int nListCnt = 0;
//	Log.d("myTag", "Query = " + Query + " isAlpha=" + isalpha);
//	//AutoComp = new String[GOLFCNT + SKICNT];
//	for (int i = 0; i < ListCnt.length; ++i) {
//	    if (i == 0)
//	    	nListCnt = 0;
//	    else
//	    	nListCnt += ListCnt[i-1];
//	    CityListClass_E[] themearray = null;
//	    if (i == 0)
//	    	themearray = Korea.korea;
//	    else if (i == 1)
//	    	themearray = Asia.asia;
//	    else if (i == 2)
//	    	themearray = Africa.africa;
//	    else if (i == 3)
//	    	themearray = NorthAmerica.northamerica;
//	    else if (i == 4)
//	    	themearray = SouthAmerica.southamerica;
//	    else if (i == 5)
//	    	themearray = Europe.europe;
//	    else if (i == 6)
//	    	themearray = Oceania.oceania;
//	    else if (i == 7)
//	    	themearray = MiddleEast.middleeast;
//
//	    for (int n = 0; n < themearray.length; n++) {
//	    	if(isalpha)
//	    		AutoComp[nListCnt + n] = themearray[n].getEngCityName();
//	    	else
//	    		AutoComp[nListCnt + n] = themearray[n].getCityName();
//	    }
//	}

		if("World".equals(areaID)) {
			if(isalpha) {
				CityListClass_E[] current = null;
				switch(tempChar) {
				case 'A' :
					current = A.a;
					break;
				case 'B' :
					current = B.b;
					break;
				case 'C' :
					current = C.c;
					break;
				case 'D' :
					current = D.d;
					break;
				case 'E' :
					current = E.e;
					break;
				case 'F' :
					current = F.f;
					break;
				case 'G' :
					current = G.g;
					break;
				case 'H' :
					current = H.h;
					break;
				case 'I' :
					current = I.i;
					break;
				case 'J' :
					current = J.j;
					break;
				case 'K' :
					current = K.k;
					break;
				case 'L' :
					current = L.l;
					break;
				case 'M' :
					current = M.m;
					break;
				case 'N' :
					current = N.n;
					break;
				case 'O' :
					current = O.o;
					break;
				case 'P' :
					current = P.p;
					break;
				case 'Q' :
					current = Q.q;
					break;
				case 'R' :
					current = RR.rr;
					break;
				case 'S' :
					current = S.s;
					break;
				case 'T' :
					current = T.t;
					break;
				case 'U' :
					current = U.u;
					break;
				case 'V' :
					current = V.v;
					break;
				case 'W' :
					current = W.w;
					break;
				case 'X' :
					current = X.x;
					break;
				case 'Y' :
					current = Y.y;
					break;
				case 'Z' :
					current = Z.z;
					break;
				default :
					current = A.a;
					break;
				}
				
				if(current != null)
					AutoComp = new String[current.length];
				
				for(int i = 0 ; i < current.length; i ++) {
					AutoComp[i] = current[i].getEngCityName();
				}
			} else {
				String StartString = HangulUtils.getHangulInitialSound(Query.substring(0, 1));
				CityListClass[] current = null;
				if(StartString.equals("???") || StartString.equals("???")) current = GA.ga;
				else if(StartString.equals("???")) current = NA.na;
				else if(StartString.equals("???") || StartString.equals("???")) current = DA.da;
				else if(StartString.equals("???")) current = RA.ra;
				else if(StartString.equals("???")) current = MA.ma;
				else if(StartString.equals("???") || StartString.equals("???")) current = BA.ba;
				else if(StartString.equals("???") || StartString.equals("???")) current = SA.sa;
				else if(StartString.equals("???")) current = AA.aa;
				else if(StartString.equals("???") || StartString.equals("???")) current = JA.ja;
				else if(StartString.equals("???")) current = CHA.cha;
				else if(StartString.equals("???")) current = KA.ka;
				else if(StartString.equals("???")) current = TA.ta;
				else if(StartString.equals("???")) current = PA.pa;
				else if(StartString.equals("???")) current = HA.ha;
				else current = GA.ga;
				
				if(current != null)
					AutoComp = new String[current.length];
				
				for(int i = 0 ; i < current.length; i ++) {
					AutoComp[i] = current[i].getCityName();
				}
			}
		} else {
			CityListClass_E[] list = null;
		    if ("Korea".equals(areaID))
		    	list = Korea.korea;
		    else if ("Asia".equals(areaID))
		    	list = Asia.asia;
		    else if ("Africa".equals(areaID))
		    	list = Africa.africa;
		    else if ("North America".equals(areaID))
		    	list = NorthAmerica.northamerica;
		    else if ("South America".equals(areaID))
		    	list = SouthAmerica.southamerica;
		    else if ("Europe".equals(areaID))
		    	list = Europe.europe;
		    else if ("Oceania".equals(areaID))
		    	list = Oceania.oceania;
		    else if ("MiddleEast".equals(areaID))
		    	list = MiddleEast.middleeast;

		    if (null != list) {
		    	AutoComp = new String[list.length];
		    }
		    
			for (int i = 0 ; i < list.length; ++i) {
				if(isalpha)
					AutoComp[i] = list[i].getEngCityName();
				else 
					AutoComp[i] = list[i].getCityName();
			}
		}

	ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
								android.R.layout.simple_dropdown_item_1line, AutoComp);
	if (Query != null && Query.length() > 0)
		edittext = (AutoCompleteTextView) findViewById(R.id.name_input);
        edittext.setAdapter(adapter);
        
        
        edittext.setOnEditorActionListener(new OnEditorActionListener() {
    		
    		@Override
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    			// TODO Auto-generated method stub
    			if(actionId == EditorInfo.IME_ACTION_SEARCH) {
    				String Query = edittext.getText().toString().trim();
    				if (Query.length() > 0) {
    					imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//    					SearchResult(edittext.getText().toString().trim());
//    					Intent intent = new Intent(SearchCity.this, SearchCity.class);
//    					intent.putExtra("Query", Query);
//    					intent.putExtra("Area", areaID);
//    					startActivityForResult(intent, 1);
    					edittext.dismissDropDown();
    					edittext.setSelection(edittext.getText().length());
    					SearchResult(Query);
    				}
                }

    			return false;
    		}
    	});
        
        edittext.setOnItemClickListener(new OnItemClickListener() {
    		@Override
    		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    		    String Query = edittext.getText().toString().trim();
    		    if (Query.length() > 0) {
    			//Log.d("myTag", "SearchBtn Pressed");
    		    	imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
    		    	SearchResult(Query);
    		    }
    		}
    	    });
    }

    protected int saveSelectedLIst() {
	// TODO Auto-generated method stub
	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
	String[][] CityID;
	int nCityCnt = 0;
	String Temp = "";
	String[] szTemp;

	nCityCnt = prefs.getInt(Const.CITY_CNT, 0);
	if(nCityCnt >= 100)
		return 101;

	//Log.d("myTag", "nCityCnt=" + nCityCnt);

	CityID = new String[selectedlist.length][8];
	for(int i = 0 ; i < selectedlist.length ; i ++) {
	    CityID[i][0] = selectedlist[i].getCityID();
	    CityID[i][1] = selectedlist[i].getCityName();
	    CityID[i][2] = selectedlist[i].getTimeZone();
	    CityID[i][3] = "--";
	    CityID[i][4] = "01";
	    CityID[i][5] = "-999";
	    CityID[i][6] = "-999";
	    CityID[i][7] = "-999";
	}

	SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
	for(int i = 0 ; i < selectedlist.length ; i++){
	    for(int j = 0 ; j < 8 ; j++){
		Temp = Temp + CityID[i][j] + "\t";
	    }
	    
	    int nIdx = i + nCityCnt;
	    //Log.i("myTag", "Inserted value[" + nIdx + "]= " + Temp);
	    prefs1.putString(Const.CITY_LIST + Integer.toString(nIdx), Temp);
	    //Log.d("myTag", Const.CITY_LIST + Integer.toString(nIdx) + Temp);
	    Temp = "";
	}
	prefs1.putInt(Const.CITY_CNT, nCityCnt + selectedlist.length);
	prefs1.commit();
	return 1;
    }
    
    protected int saveSelectedLIst(CityList3 checkedlist) {
    	// TODO Auto-generated method stub
    	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
    	String[][] CityID;
    	int nCityCnt = 0;
    	String Temp = "";
    	String[] szTemp;

    	nCityCnt = prefs.getInt(Const.CITY_CNT, 0);
    	if(nCityCnt >= 100)
    		return 101;

    	//Log.d("myTag", "nCityCnt=" + nCityCnt);

    	CityID = new String[1][8];
	    CityID[0][0] = checkedlist.getCityID();
	    CityID[0][1] = checkedlist.getCityName();
	    CityID[0][2] = checkedlist.getTimeZone();
	    CityID[0][3] = "--";
	    CityID[0][4] = "01";
	    CityID[0][5] = "-999";
	    CityID[0][6] = "-999";
	    CityID[0][7] = "-999";

    	SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
	    for(int j = 0 ; j < 8 ; j++){
	    	Temp = Temp + CityID[0][j] + "\t";
	    }
	    
	    prefs1.putString(Const.CITY_LIST + Integer.toString(nCityCnt), Temp);
	    Temp = "";
    	prefs1.putInt(Const.CITY_CNT, nCityCnt + 1);
    	prefs1.commit();
    	return 1;
    }

    protected void saveNewSelectedLIst() {
	// TODO Auto-generated method stub
	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
	int nThemeCnt = 0;
	String Temp = "";
	String[] szTemp;

	String[][] ThemeID;

	nThemeCnt = prefs.getInt(Const.CITY_CNT, 0);

	ThemeID = new String[nThemeCnt][8];

	for(int i = 0 ; i < nThemeCnt ; i++){
	    Temp = prefs.getString(Const.CITY_LIST + i, "");
	    szTemp = Temp.split("\t");
	    for(int j = 0 ; j < 8 ; j++){
		//Log.d("myTag", "szTemp[" + j + "]=" + szTemp[j]);
		if (szTemp[j].equals("") || szTemp[j] == null || szTemp[j].equals("\n"))
		    ThemeID[i][j] = "--";
		else
		    ThemeID[i][j] = szTemp[j];
	    }
	}

	//Log.d("myTag", "nThemeCnt=" + nThemeCnt);

	SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
	int k = 0;
	boolean isAdded = false;

	for(int j = 0 ; j < selectedlist.length ; j++){
	    isAdded = false;
	    for(int i = 0 ; i < nThemeCnt ; i++){
		if ( ThemeID[i][0].equals(selectedlist[j].getCityID()) ) {
		    isAdded = true;
		    break;
		}
	    }

	    if (!isAdded){
		Temp = selectedlist[j].getCityID() + "\t" + selectedlist[j].getCityName() + "\t" + selectedlist[j].getTimeZone() + "\t" +
			"--" + "\t" + Integer.toString(R.drawable.weather_icon_01_ref) + "\t" +
		    "-999" + "\t" + "-999" + "\t" + "-999";

		int nIdx = k + nThemeCnt;
		prefs1.putString(Const.CITY_LIST + Integer.toString(nIdx), Temp);
		//Log.d("myTag", "Add New List=" + Temp);
		k ++;
	    }
	}

	prefs1.putInt(Const.CITY_CNT, nThemeCnt + k);
	prefs1.commit();
    }

    protected boolean checkSelectedList() {
	// TODO Auto-generated method stub
	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
	String[][] CityID;
	int nCityCnt = 0;
	String Temp = null;
	String[] szTemp;

	if (prefs == null)
	    return true;

	nCityCnt = prefs.getInt(Const.CITY_CNT, 0);
	CityID = new String[nCityCnt][8];

	for(int i = 0 ; i < nCityCnt ; i++){
	    Temp = prefs.getString(Const.CITY_LIST + i, "");
	    szTemp = Temp.split("\t");
	    for(int j = 0 ; j < 8 ; j++){
		//Log.d("myTag", "szTemp[" + j + "]=" + szTemp[j]);
		if (szTemp[j].equals("") || szTemp[j] == null || szTemp[j].equals("\n"))
		    CityID[i][j] = "--";
		else
		    CityID[i][j] = szTemp[j];
	    }
	}

	for(int i = 0 ; i < nCityCnt ; i++){
	    for(int j = 0 ; j < selectedlist.length ; j++){
		//Log.d("myTag", "Saved Cityid=[" + CityID[i][0] + "] selectedCityid=[" + selectedlist[j].getCityID() + "]");
		if ( CityID[i][0].equals(selectedlist[j].getCityID()) ) {
		    //Log.d("myTag", selectedlist[j].getCityID() + " is being in the CityList!!");
		    return false;
		}
	    }
	}

	return true;
    }
    
    protected boolean checkSelectedList(CityList3 checked) {
    	// TODO Auto-generated method stub
    	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
    	String[][] CityID;
    	int nCityCnt = 0;
    	String Temp = null;
    	String[] szTemp;

    	if (prefs == null)
    	    return true;

    	nCityCnt = prefs.getInt(Const.CITY_CNT, 0);
    	CityID = new String[nCityCnt][8];

    	for(int i = 0 ; i < nCityCnt ; i++){
    	    Temp = prefs.getString(Const.CITY_LIST + i, "");
    	    szTemp = Temp.split("\t");
    	    for(int j = 0 ; j < 8 ; j++){
    		//Log.d("myTag", "szTemp[" + j + "]=" + szTemp[j]);
    	    	try {
    	    		if (szTemp[j] == null || szTemp[j].equals("") || szTemp[j].equals("\n"))
    	    			CityID[i][j] = "--";
    	    		else
    	    			CityID[i][j] = szTemp[j];
    	    	}catch (ArrayIndexOutOfBoundsException e) {
    	    		CityID[i][j] = "--";
    	    	}
    	    }
    	}

    	for(int i = 0 ; i < nCityCnt ; i++){
//    	    for(int j = 0 ; j < selectedlist.length ; j++){
    		//Log.d("myTag", "Saved Cityid=[" + CityID[i][0] + "] selectedCityid=[" + selectedlist[j].getCityID() + "]");
    		if ( CityID[i][0].equals(checked.getCityID()) ) {
    		    //Log.d("myTag", selectedlist[j].getCityID() + " is being in the CityList!!");
    		    return false;
    		}
//    	    }
    	}

    	return true;
        }

    protected int getSelectedList() {
	// TODO Auto-generated method stub
	CityList3[] TempList = new CityList3[plist3.length];
	int nSelectedCnt = 0;

	for(int i = 0 ; i < plist3.length ; i++) {
	    if (plist3[i].getisChecked() == true){
		TempList[nSelectedCnt] = plist3[i];
		nSelectedCnt++;
	    }
	}

	selectedlist = new CityList3[nSelectedCnt];

	//Log.d("myTag", "nSelectedCount=" + nSelectedCnt);

	System.arraycopy(TempList, 0, selectedlist, 0, nSelectedCnt);

//	for(int i = 0 ; i < selectedlist.length ; i++){
//	    Log.d("myTag", "CityName=[" + selectedlist[i].getCityName() + "] CityID=[" + selectedlist[i].getCityID() + "] checked=[" + selectedlist[i].getisChecked() + "]");
//	}

	return nSelectedCnt;
    }

    //	private void queueSearch(long delayMillis) {
    //	      // Cancel previous update if it hasn't started yet
    //	      SearchThread.removeCallbacks(searchTask);
    //	      // Start an update if nothing happens after a few milliseconds
    //	      SearchThread.postDelayed(searchTask, delayMillis);
    //	   }
    //
    //	private void initThreading() {
    //		SearchThread = new Handler();
    //		ServiceThread = Executors.newSingleThreadExecutor();
    //
    //		searchTask = new Runnable() {
    //
    //			@Override
    //			public void run() {
    //				// TODO Auto-generated method stub
    //				String query = edittext.getText().toString().trim();
    //				if (SearchPending != null)
    //					SearchPending.cancel(true);
    //
    //				if (query.length() == 0 ) {
    //					setSearchResult(citylist);
    //				} else {
    //					SearchTask task = new SearchTask(SearchCity.this, query);
    //					SearchPending = ServiceThread.submit(task);
    //				}
    //			}
    //		};
    //	}

    public CityList3[] getCityNameList() {
	return this.citylist;
    }

    private void SearchResult(String string) {
    	StringBuffer TempResult = new StringBuffer();
    	CityList3[] TempList = new CityList3[citylist.length];
    	int nMatchedCount = 0;
    	char tempChar = string.toUpperCase().charAt(0);
    	boolean isalpha = Util.isAlpha((int)tempChar);
    	if ("World".equals(areaID)) {
    		
    		if(isalpha) {
    			CityListClass_E[] current = null;
    			switch(tempChar) {
    			case 'A' :
    				current = A.a;
    				break;
    			case 'B' :
    				current = B.b;
    				break;
    			case 'C' :
    				current = C.c;
    				break;
    			case 'D' :
    				current = D.d;
    				break;
    			case 'E' :
    				current = E.e;
    				break;
    			case 'F' :
    				current = F.f;
    				break;
    			case 'G' :
    				current = G.g;
    				break;
    			case 'H' :
    				current = H.h;
    				break;
    			case 'I' :
    				current = I.i;
    				break;
    			case 'J' :
    				current = J.j;
    				break;
    			case 'K' :
    				current = K.k;
    				break;
    			case 'L' :
    				current = L.l;
    				break;
    			case 'M' :
    				current = M.m;
    				break;
    			case 'N' :
    				current = N.n;
    				break;
    			case 'O' :
    				current = O.o;
    				break;
    			case 'P' :
    				current = P.p;
    				break;
    			case 'Q' :
    				current = Q.q;
    				break;
    			case 'R' :
    				current = RR.rr;
    				break;
    			case 'S' :
    				current = S.s;
    				break;
    			case 'T' :
    				current = T.t;
    				break;
    			case 'U' :
    				current = U.u;
    				break;
    			case 'V' :
    				current = V.v;
    				break;
    			case 'W' :
    				current = W.w;
    				break;
    			case 'X' :
    				current = X.x;
    				break;
    			case 'Y' :
    				current = Y.y;
    				break;
    			case 'Z' :
    				current = Z.z;
    				break;
				default :
					current = A.a;
					break;
    			}
    			
    			if(current != null)
    				citylist = new CityList3[current.length];
    			
    			for(int i = 0 ; i < current.length; i ++) {
    				citylist[i] = new CityList3(current[i].getEngCityName(), current[i].getEngCityName(), current[i].getCityName(),
    						current[i].getCityID(),	current[i].getCountry(), current[i].getTimeZone(), false);
    			}
    		} else {
		    	String StartString = HangulUtils.getHangulInitialSound(string.substring(0, 1));
				CityListClass[] current = null;
				if(StartString.equals("???") || StartString.equals("???")) current = GA.ga;
				else if(StartString.equals("???")) current = NA.na;
				else if(StartString.equals("???") || StartString.equals("???")) current = DA.da;
				else if(StartString.equals("???")) current = RA.ra;
				else if(StartString.equals("???")) current = MA.ma;
				else if(StartString.equals("???") || StartString.equals("???")) current = BA.ba;
				else if(StartString.equals("???") || StartString.equals("???")) current = SA.sa;
				else if(StartString.equals("???")) current = AA.aa;
				else if(StartString.equals("???") || StartString.equals("???")) current = JA.ja;
				else if(StartString.equals("???")) current = CHA.cha;
				else if(StartString.equals("???")) current = KA.ka;
				else if(StartString.equals("???")) current = TA.ta;
				else if(StartString.equals("???")) current = PA.pa;
				else if(StartString.equals("???")) current = HA.ha;
				else current = GA.ga;
				
				if(current != null)
					citylist = new CityList3[current.length];
				
				for(int i = 0 ; i < current.length; i ++) {
					citylist[i] = new CityList3(current[i].getCityName(), "", current[i].getCityName(), current[i].getCityID(),
							current[i].getCountry(), current[i].getTimeZone(), false);
				}
    		}
    	} else {
    		CityListClass_E[] current = null;
    		if (areaID.equals("Korea"))
    			current = Korea.korea;
    		else if (areaID.equals("Asia"))
    			current = Asia.asia;
    		else if (areaID.equals("Africa"))
    			current = Africa.africa;
    		else if (areaID.equals("North America"))
    			current = NorthAmerica.northamerica;
    		else if (areaID.equals("South America"))
    			current = SouthAmerica.southamerica;
    		else if (areaID.equals("Europe"))
    			current = Europe.europe;
    		else if (areaID.equals("Oceania"))
    			current = Oceania.oceania;
    		else if (areaID.equals("MiddleEast"))
    			current = MiddleEast.middleeast;
    		else 
    			current = Korea.korea;
    		
    		if(current != null)
				citylist = new CityList3[current.length];
			
			for(int i = 0 ; i < current.length; i ++) {
				if(isalpha)
					citylist[i] = new CityList3(current[i].getEngCityName(), current[i].getEngCityName(), current[i].getCityName(),
						current[i].getCityID(),	current[i].getCountry(), current[i].getTimeZone(), false);
				else
					citylist[i] = new CityList3(current[i].getCityName(), current[i].getEngCityName(), current[i].getCityName(),
							current[i].getCityID(),	current[i].getCountry(), current[i].getTimeZone(), false);
			}
    	}

    	//Log.d("DEBUG", "[" + string + "]");
    	if ( string == null || string.length() == 0 ){
    		m_orders3 = new ArrayList<CityList3>();
    		m_adapter3 = new CityList3Adapter(getApplicationContext(), R.layout.search_row, m_orders3);
    		setListAdapter(m_adapter3);

    		SearchResult = citylist;

    		plist3 = new CityList3[SearchResult.length];
    		for(int i = 0 ; i < SearchResult.length ; i++){
    			plist3[i] = new CityList3(SearchResult[i]);
    			m_adapter3.add(plist3[i]);
    		}
    		return;
    	}

    	//Log.d("myTag", "cityList.length=" + citylist.length);
    	for( int i = 0 ; i < citylist.length ; i ++) {
    		//Log.d("myTag", "citylist = " + citylist[i].getCityName() + citylist[i].getCityID() + " string = " + string);
    		
//    		if(string.substring(0, 1).equals("?") || string.substring(0, 1).equals(".") || 
//    				string.substring(0, 1).equals("*") || string.substring(0, 1).equals("^") ||
//    				string.substring(0, 1).equals("(") || string.substring(0, 1).equals(")") ||
//    				string.substring(0, 1).equals("|") || string.substring(0, 1).equals("[") || 
//    				string.substring(0, 1).equals("]") || string.substring(0, 1).equals("{") || 
//    				string.substring(0, 1).equals("}"))
    		if(string.contains("?") || string.contains(".") || string.contains("+") ||
    				string.contains("*") || string.contains("^") ||
//    				string.contains("(") || string.contains(")") ||
    				string.contains("|") || string.contains("[") || 
    				string.contains("]") || string.contains("{") || 
    				string.contains("}") || string.contains("\\"))
    			break;
		    if (citylist[i].getDspName().toUpperCase().matches(string.toUpperCase() + ".*") || citylist[i].getDspName().equals(string)) {
		    	TempList[nMatchedCount] = citylist[i];
		    	nMatchedCount++;
	    }
	}

	if (nMatchedCount != 0) {
	    SearchResult = new CityList3[nMatchedCount];
	    System.arraycopy(TempList, 0, SearchResult, 0, nMatchedCount);
	} else {
	    SearchResult = null;
	}

	//		mDialogText.setVisibility(View.GONE);
	RedrawList();

    }

    private void Draw() {
	// TODO Auto-generated method stub
	m_orders3 = new ArrayList<CityList3>();

	m_adapter3 = new CityList3Adapter(getApplicationContext(), R.layout.search_row, m_orders3);

	setListAdapter(m_adapter3);

	for(int i = 0 ; i < citylist.length ; i++) {
		//Log.d("myTag", "citylist=" + citylist[i].getCityName() + citylist[i].getCityID());
	    m_adapter3.add(citylist[i]);
	}

	plist3 = citylist;
    }

    @Override
	protected void onResume() {
        super.onResume();
	//        mReady = true;
//    	if(isShowedKeypad) {
//			isShowedKeypad = true;
//		    new Handler().postDelayed(new Runnable() { public void run() {
//		    	//InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//		    	imm.showSoftInput(edittext, 0);
//		    } }, 100);
//    	}
    }


    @Override
	protected void onPause() {
        super.onPause();
	//        removeWindow();
        mReady = false;
        if(imm.isActive()) {
//        	isShowedKeypad = true;
        	imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//        } else {
//        	isShowedKeypad = false;
        }
    }

    @Override
	protected void onDestroy() {
        super.onDestroy();
    }

    public void setSearchResult(CityList3[] result) {
	//    	mDialogText.setVisibility(View.GONE);
    	setSearchList(result);
    }

    private void setSearchList(final CityList3[] result) {
	//    	SearchThread.post(new Runnable() {
	//
	//			@Override
	//			public void run() {
	//				// TODO Auto-generated method stub
	//				SearchResult = result;
	//				RedrawList();
	//			}
	//		});
    }

    private void RedrawList(){
    	if (SearchResult != null && SearchResult.length > 0) {
		    m_orders3 = new ArrayList<CityList3>();
		    m_adapter3 = new CityList3Adapter(getApplicationContext(), R.layout.search_row, m_orders3);
		    setListAdapter(m_adapter3);
	
		    plist3 = SearchResult;
		    for(int i = 0 ; i < SearchResult.length ; i++){
		    	m_adapter3.add(plist3[i]);
			//Log.d("myTag", "ID = " + plist3[i].getCityID() + " Name=" + plist3[i].getCityName() + " Country=" + plist3[i].getCountry());
		    }
    	} else {
		    mReady = false;
		    String[] noresult = new String[1];
		    noresult[0] = "??????????????? ????????????.";
		    setListAdapter(new ArrayAdapter<String>(getApplicationContext(),
							    android.R.layout.simple_list_item_1, noresult));
    	}
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
    	//Log.i("myTag", "buttonview=[" + buttonView + "] isChecked=" + isChecked);
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//    	Log.d("myTag", "onScroll");
//    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//	    if (imm.isActive()) {
//	    	imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//	    }
	//        int lastItem = firstVisibleItem + visibleItemCount - 1;

	//        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	//        imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);

	//        if (mReady) {
	//            String firstLetter = szSearchResult[firstVisibleItem].substring(0, 1);
	//            int unicode = HangulUtils.convertCharToUnicode(firstLetter.charAt(0));
	//            if (unicode >= HangulUtils.HANGUL_BASE_UNIT && unicode <= HangulUtils.HANGUL_END_UNICODE){
	//            	int tmp = (unicode - HangulUtils.HANGUL_BEGIN_UNICODE);
	//				int index = tmp / HangulUtils.HANGUL_BASE_UNIT;
	//				firstLetter = Character.toString(HangulUtils.INITIAL_SOUND[index]);
	//            }
	//
	//            if (!mShowing && !firstLetter.equals(mPrevLetter)) {
	//                mShowing = true;
	////                mDialogText.setVisibility(View.VISIBLE);
	//            }
	//            mDialogText.setText(firstLetter);
	//            mHandler.removeCallbacks(mRemoveWindow);
	//            mHandler.postDelayed(mRemoveWindow, 1000);
	//            mPrevLetter = firstLetter;
	//        }
    }


    public void onScrollStateChanged(AbsListView view, int scrollState) {
//    	Log.d("myTag", "onScrollStateChanged");
//    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//	    if (imm.isActive()) {
//	    	imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//	    }
    }


    private void removeWindow() {
        if (mShowing) {
            mShowing = false;
	    //            mDialogText.setVisibility(View.INVISIBLE);
        }
    }
    
    @Override
	protected void onActivityResult(int requestCode, int result, Intent data) {
		if (RESULT_OK == result) {
			setResult(RESULT_OK, (new Intent()).setAction(""));
		    finish();
		} else if (RESULT_CANCELED == result) {
			setResult(RESULT_CANCELED, (new Intent()).setAction("REDRAW"));
			finish();
		}
    }

    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
    	imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);

		super.onListItemClick(l, v, position, id);
		LinearLayout listview = null;
		try{
		    listview = (LinearLayout) v;
		} catch (ClassCastException e) {
		    return;
		}
		//Log.d("myTag", "position=" + position + " id=" + id);
	
		CheckedTextView ctv = (CheckedTextView)listview.getChildAt(0);
	
		ctv.setChecked(!ctv.isChecked());
		//Log.d("myTag", "SelectedText is " + ctv.getText());
	
		plist3[position].setisChecked(!plist3[position].getisChecked());
		//Log.d("myTag", "Select CityID=" + plist3[position].getCityID());
	
		if (checkSelectedList(plist3[position])) {
			if(saveSelectedLIst(plist3[position])> 100) {
				AlertDialog.Builder alert = null;
//		    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//		    		alert = new AlertDialog.Builder(SearchCity.this, AlertDialog.THEME_HOLO_DARK);
//		    	else
		    		alert = new AlertDialog.Builder(SearchCity.this);
				alert.setTitle( "????????????" );
				alert.setIcon(R.drawable.ic_dialog_menu_generic);
				alert.setMessage("???????????? 100?????? ??????????????????.\n100???????????? ???????????????.");
		
				alert.setPositiveButton( "??????", new DialogInterface.OnClickListener() {
					@Override
					public void onClick( DialogInterface dialog, int which) {
						setResult(RESULT_CANCELED, (new Intent()).setAction("REDRAW"));
						finish();
			    	}
				});
		
			    OnCancelListener onCancelListener = new OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
		    			setResult(RESULT_CANCELED, (new Intent()).setAction("REDRAW"));
						finish();
						dialog.dismiss();
					}
				};
				
				alert.setOnCancelListener(onCancelListener);
				alert.show();
			} else {
				setResult(RESULT_OK, (new Intent()).setAction("REDRAW"));
				finish();
			}
		} else {
			SearchResult = citylist;
	
			AlertDialog.Builder alert = null;
//	    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//	    		alert = new AlertDialog.Builder(SearchCity.this, AlertDialog.THEME_HOLO_DARK);
//	    	else
	    		alert = new AlertDialog.Builder(SearchCity.this);
			alert.setTitle( "????????????" );
			alert.setIcon(R.drawable.ic_dialog_menu_generic);
			alert.setMessage("????????? ????????? ?????? ????????? ????????????.");
	
			alert.setPositiveButton( "??????", new DialogInterface.OnClickListener() {
				@Override
				public void onClick( DialogInterface dialog, int which) {
					setResult(RESULT_CANCELED, (new Intent()).setAction("REDRAW"));
					finish();
		    	}
			});
			
		    OnCancelListener onCancelListener = new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
	    			setResult(RESULT_CANCELED, (new Intent()).setAction("REDRAW"));
					finish();
					dialog.dismiss();
				}
			};
			
			alert.setOnCancelListener(onCancelListener);
	
			alert.show();
		}

    }

    static class ViewHolder {
	CheckedTextView checkbox;
    }

    public class CityList3Adapter extends ArrayAdapter<CityList3> {

    	private ArrayList<CityList3> items;

        public CityList3Adapter(Context context, int textViewResourceId, ArrayList<CityList3> items) {
	    super(context, textViewResourceId, items);
	    this.items = items;
        }
        @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    View v = convertView;
	    //ViewHolder vholder;
	    if (v == null) {
		//vholder = new ViewHolder();
		LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = vi.inflate(R.layout.search_row, null);
		//v.setTag(vholder);
	    }else{
		//vholder = (ViewHolder)v.getTag();
	    }
	    CityList3 p = items.get(position);
	    if (p != null) {
		String szCity = p.getDspName();
		CheckedTextView tv = (CheckedTextView) v.findViewById(R.id.text);
		tv.setChecked(p.getisChecked());
		tv.setText(szCity + "  (" + p.getCountry() + ")");
	    }
	    return v;
        }

    }

    class CityList3 {
    	private CheckedTextView City;
    	private String CityEngName;
    	private String CityName;
    	private String CityID;
    	private String Country;
    	private String timezone;

    	public CityList3(String DspName, String EngName, String Name, String cityid, String country, String timezone, boolean isChecked){
	    City = new CheckedTextView(getBaseContext());
	    //if (City == null)
		//Log.e("myTag", "City null");
	    this.City.setText(DspName);
	    this.City.setChecked(isChecked);
	    this.CityEngName = EngName;
	    this.CityName = Name;
	    this.CityID = cityid;
	    this.Country = country;
	    this.timezone = timezone;
    	}
    	public CityList3(CityList3 list) {
    		this(list.getDspName(), list.getEngCityName(), list.getCityName(), 
    				list.getCityID(), list.getCountry(), list.getTimeZone(), list.getisChecked());
    	}

    	public CityList3 getCity() { return this; }
    	public boolean getisChecked() { return City.isChecked(); }
    	public String getCityName() { return CityName; }
    	public void setisChecked(boolean isChecked) { City.setChecked(isChecked); }
    	public String getDspName() { return (String) City.getText(); }
    	public String getEngCityName() { return CityEngName; }
    	public String getCityID() { return CityID; }
    	public String getCountry() { return Country; }
    	public String getTimeZone() { return timezone; }
    }
}

//class MySampleHandler<XmlParser> extends DefaultHandler
//{
//    private StringBuffer cityid	= new StringBuffer();
//    private StringBuffer cityname = new StringBuffer();
//    private StringBuffer countryname = new StringBuffer();
//    private StringBuffer timezone = new StringBuffer();
//
//    private boolean hasid = false;
//    private boolean hasname = false;
//    private boolean hascon = false;
//    private boolean hastz = false;
//    private XmlParser xp;
//
//    public MySampleHandler(XmlParser xp)
//	{
//	    this.xp		=		xp;
//	}
//    public void startElement(String uri, String localName, String qName, Attributes atts)
//    {
//	if (localName.equals("id"))
//	    hasid = true;
//	else if (localName.equals("cn"))
//	    hasname = true;
//	else if (localName.equals("co"))
//	    hascon = true;
//	else if (localName.equals("tz"))
//	    hastz = true;
//    }
//    /*
//      public void endElement(String uri, String localName, String qName)
//      {
//      if (localName.equals("person"))
//      {
//      xp.updateTextView(names.toString()+"\n"+comps.toString()+"\n"+departs.toString());
//      }
//      }
//    */
//    public void characters(char[] chars, int start, int leng)
//    {
//	if (hasid)
//	    {
//		hasid = false;
//		cityid.append(chars, start, leng);
//		cityid.append(',');
//	    }
//	else if (hasname)
//	    {
//		hasname = false;
//		cityname.append(chars, start, leng);
//		cityname.append(',');
//	    }
//	else if (hascon)
//	    {
//		hascon = false;
//		countryname.append(chars, start, leng);
//		countryname.append(',');
//	    }
//	else if (hastz)
//	    {
//		hastz = false;
//		timezone.append(chars, start, leng);
//		timezone.append(',');
//	    }
//    }
//
//    public StringBuffer getCityID()      { return cityid; }
//    public StringBuffer getCityName()    { return cityname; }
//    public StringBuffer getCountryName() { return countryname; }
//    public StringBuffer getTimeZone()    { return timezone; }
//}
