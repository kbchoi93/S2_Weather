package com.weathernews.Weather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.weathernews.Weather.SearchCity.CityList3;
import com.weathernews.Weather.SearchTheme.ThemeList3;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

public class CityGroupList extends ExpandableListActivity {

    private static final String LOG_TAG = "myTag";
    private List Parent;
    private List Child;

    private AutoCompleteTextView edittext;
    private TextWatcher watcher;
    private ImageButton imgbtn;
    private Button btn;
    private Button cancel;

    private Handler SearchThread;
    private Runnable searchTask;
    private ExecutorService ServiceThread;
    private Future SearchPending;
    private ExpandableListView expanded;

    static final CityListClass[] CITYLIST_ASIA[] = {
    AsiaNepal.AsiaNepal,
    AsiaTaiwan.AsiaTaiwan,
    AsiaLaos.AsiaLaos,
    AsiaMalaysia.malaysia,
    AsiaMongolia.AsiaMongolia,
    AsiaVietnam.AsiaVietnam,
    AsiaSingapore.AsiaSingapore,
    AsiaIndia.india,
    AsiaIndonesia.indonesia,
    AsiaJapan.japan,
    AsiaChina.china,
    AsiaCambodia.AsiaCambodia,
    AsiaThai.thai,
    AsiaPakistan.AsiaPakistan,
    AsiaPhilippine.philippine,
    AsiaEtc.asiaEtc,
    };
    static final String CITYLIST_NAME_ASIA[] = {
    "??????",
    "??????",
    "?????????",
	"???????????????",
	"??????",
	"?????????",
	"????????????",
	"??????",
	"???????????????",
	"??????",
	"??????",
	"????????????",
	"??????",
	"????????????",
	"?????????",
	"????????????",
    };
    static final CityListClass[] CITYLIST_AFRICA[] = {
	AfricaNambia.nambia,
	AfricaSouthafrica.southafrica,
	AfricaMadagascar.madagascar,
	AfricaSenegal.senegal,
	AfricaSwaziland.swaziland,
	AfricaAngola.angola,
	AfricaUganda.uganda,
	AfricaEgypt.egypt,
	AfricaZambia.zambia,
	AfricaZimbabwe.zimbabwe,
	AfricaKenya.kenya,
	AfricaCameroon.cameroon,
	AfricaEtc.africaEtc, };
    static final String CITYLIST_NAME_AFRICA[] = {
	"????????????",
	"????????????????????????",
	"??????????????????",
	"?????????",
	"???????????????",
	"?????????",
	"?????????",
	"?????????",
	"?????????",
	"????????????",
	"??????",
	"?????????",
	"????????????", };

    static final CityListClass[] CITYLIST_OCEANIA[] = {
	Newzealand_oceania.Newzealand_oceania, Australia_oceania.Australia_oceania,  Etc_oceania.Etc_oceania,
    };
    static final String CITYLIST_NAME_OCEANIA[] = {
	"????????????", "?????????????????????", "????????????",
    };
    static final CityListClass[] CITYLIST_NORTH_AMERICA[] = {
	America_northamerica.America_northamerica, Canada_northamerica.Canada_northamerica, Etc_northamerica.Etc_northamerica
    };
    static final String CITYLIST_NAME_NORTH_AMERICA[] = {
	"??????", "?????????", "????????????",
    };
    static final CityListClass[] CITYLIST_KOREA[] = {
    	Seoul_korea.Gyeonggi_korea,
    	Gyeonggi_korea.Gyeonggi_korea,
    	ChungcheongS_korea.Chungcheong_korea,
    	ChungcheongN_korea.Chungcheong_korea,
    	Gangwon_korea.Gangwon_korea,
    	GyeongsangS_korea.Gyeongsang_korea,
    	GyeongsangN_korea.Gyeongsang_korea,
    	ChollaS_korea.Cholla_korea,
    	ChollaN_korea.Cholla_korea,
    	Jeju_korea.Jeju_korea
    };
    static final String CITYLIST_NAME_KOREA[] = {
    	"??????",
    	"?????????",
    	"????????????",
    	"????????????",
    	"?????????",
    	"????????????",
    	"????????????",
    	"????????????",
    	"????????????",
    	"?????????",
    };
    static final CityListClass[] CITYLIST_SOUTH_AMERICA[] = {
	Mexico_southamerica.Mexico_southamerica,
	Venezuela_southamerica.Venezuela_southamerica,
	Brazil_southamerica.Brazil_southamerica,
	Argentina_southamerica.Argentina_southamerica,
	Haiti_southamerica.Haiti_southamerica,
	Ecuador_southamerica.Ecuador_southamerica,
	Elsalvador_southamerica.Elsalvador_southamerica,
	Honduras_southamerica.Honduras_southamerica,
	Uruguay_southamerica.Uruguay_southamerica,
	Chile_southamerica.Chile_southamerica,
	Costarica_southamerica.Costarica_southamerica,
	Colombia_southamerica.Colombia_southamerica,
	Panama_southamerica.Panama_southamerica,
	Paraguay_southamerica.Paraguay_southamerica,
	Peru_southamerica.Peru_southamerica,
	Etc_southamerica.Etc_southamerica,

    };
    static final String CITYLIST_NAME_SOUTH_AMERICA[] = {
	"?????????",
	"???????????????",
	"?????????",
	"???????????????",
	"?????????",
	"????????????",
	"???????????????",
	"????????????",
	"????????????",
	"??????",
	"???????????????",
	"????????????",
	"?????????",
	"????????????",
	"??????",
	"????????????",

    };
    static final CityListClass[] CITYLIST_EUROPE[] = {
    	Greece_europe.Greece_europe,
    	Netherlands_europe.Netherlands_europe,
    	Norway_europe.Norway_europe,
    	Denmark_europe.Denmark_europe,
    	Germany_europe.Germany_europe,
    	Russia_europe.Russia_europe,
    	Romania_europe.Romania_europe,
    	Luxembourg_europe.Luxembourg_europe,
    	Lithuania_europe.Lithuania_europe,
    	Macedonia_europe.Macedonia_europe,
    	Belgium_europe.Belgium_europe,
    	Bosnia_europe.Bosnia_europe,
    	Bulgaria_europe.Bulgaria_europe,
    	Serbia_europe.Serbia_europe,
    	Sweden_europe.Sweden_europe,
    	Swiss_europe.Swiss_europe,
    	Spain_europe.Spain_europe,
    	Slovakia_europe.Slovakia_europe,
    	Iceland_europe.Iceland_europe,
    	Ireland_europe.Ireland_europe,
    	Albania_europe.Albania_europe,
    	Estonia_europe.Estonia_europe,
    	England_europe.England_europe,
    	Austria_europe.Austria_europe,
    	Italy_europe.Italy_europe,
    	Czech_europe.Czech_europe,
    	Croatia_europe.Croatia_europe,
    	Turkey_europe.Turkey_europe,
    	Portugal_europe.Portugal_europe,
    	Poland_europe.Poland_europe,
    	France_europe.France_europe,
    	Hungary_europe.Hungary_europe,
    	Etc_europe.Etc_europe,

        };
        static final String CITYLIST_NAME_EUROPE[] = {
    	"?????????",
    	"????????????",
    	"????????????",
    	"?????????",
    	"??????",
    	"?????????",
    	"????????????",
    	"???????????????",
    	"???????????????",
    	"???????????????",
    	"?????????",
    	"????????????",
    	"????????????",
    	"????????????",
    	"?????????",
    	"?????????",
    	"?????????",
    	"???????????????",
    	"???????????????",
    	"????????????",
    	"????????????",
    	"???????????????",
    	"??????",
    	"???????????????",
    	"????????????",
    	"??????",
    	"???????????????",
    	"??????",
    	"????????????",
    	"?????????",
    	"?????????",
    	"?????????",
    	"????????????",

        };
    static final CityListClass[] CITYLIST_MIDDLEEAST[] = {
	Saudiarabia_middleeast.Saudiarabia_middleeast,
	Syria_middleeast.Syria_middleeast,
	Arabemirates_middleeast.Arabemirates_middleeast,
	Oman_middleeast.Oman_middleeast,
	Israel_middleeast.israel_middleeast,
	Qatar_middleeast.Qatar_middleeast,
	Kuwait_middleeast.Kuwait_middleeast,
	Etc_middleeast.Etc_middleeast,

    };
    static final String CITYLIST_NAME_MIDDLEEAST[] = {
	"?????????????????????",
	"?????????",
	"??????????????????",
	"??????",
	"????????????",
	"?????????",
	"????????????",
	"????????????",

    };

    private ArrayList<CityListClass> selectedarray = new ArrayList<CityListClass>();

    private CityListClass[][] cityList;
    private String[] cityListName;

    private String areaID;
    private boolean isShowInputMethod = true;
    
    private InputMethodManager imm;
//    private boolean isShowedKeypad = false;
    
    AlertDialog.Builder alert = null;

    /** Called when the activity is first created. */
    @Override
	public void onCreate(Bundle icicle)
    {
	super.onCreate(icicle);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.searchgroup);
	
	expanded = getExpandableListView();
	
	edittext = (AutoCompleteTextView) findViewById(R.id.name_input);
	
	watcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			String InputText = s.toString().trim();
			
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
	
	imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

	try {
	    areaID = getIntent().getExtras().getString("AreaID");
	    if (areaID.equals("Korea")){
		cityList = CITYLIST_KOREA;
		cityListName = CITYLIST_NAME_KOREA;
	    }
	    else if (areaID.equals("Asia")){
		cityList = CITYLIST_ASIA;
		cityListName = CITYLIST_NAME_ASIA;
	    }
	    else if (areaID.equals("Africa")){
		cityList = CITYLIST_AFRICA;
		cityListName = CITYLIST_NAME_AFRICA;
	    }
	    else if (areaID.equals("North America")){
		cityList = CITYLIST_NORTH_AMERICA;
		cityListName = CITYLIST_NAME_NORTH_AMERICA;
	    }
	    else if (areaID.equals("South America")){
		cityList = CITYLIST_SOUTH_AMERICA;
		cityListName = CITYLIST_NAME_SOUTH_AMERICA;
	    }
	    else if (areaID.equals("Europe")){
		cityList = CITYLIST_EUROPE;
		cityListName = CITYLIST_NAME_EUROPE;
	    }
	    else if (areaID.equals("Oceania")){
		cityList = CITYLIST_OCEANIA;
		cityListName = CITYLIST_NAME_OCEANIA;
	    }
	    else if (areaID.equals("MiddleEast")){
		cityList = CITYLIST_MIDDLEEAST;
		cityListName = CITYLIST_NAME_MIDDLEEAST;
	    }
	} catch (Exception e) {
	    e.printStackTrace(); finish(); return;
	}

	makeAutoComp();
	imgbtn = (ImageButton) findViewById(R.id.searchbtn);

	

	expanded.setOnScrollListener(new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		    // TODO Auto-generated method stub
			imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//			isShowedKeypad = false;
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
					 int visibleItemCount, int totalItemCount) {
		}
	    });


	edittext.setOnEditorActionListener(new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			// TODO Auto-generated method stub
			if(actionId == EditorInfo.IME_ACTION_SEARCH) {
				String Query = edittext.getText().toString().trim();
			    if (Query.length() > 0) {
				//Log.d("myTag", "SearchBtn Pressed");
			    	//InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			    	imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//			    	isShowedKeypad = false;
					Intent intent = new Intent(CityGroupList.this, SearchCity.class);
					intent.putExtra("Query", Query);
					intent.putExtra("Area", areaID);
					startActivityForResult(intent, 1);
			    }
             // search pressed and perform your functionality.                   
            }

			return false;
		}
	});
	
	Parent = createGroupList();
	Child = createChildList();

        imgbtn.setImageResource(R.drawable.ic_btn_search);
        imgbtn.setOnClickListener(new OnClickListener() {

		@Override
		    public void onClick(View v) {

		    String Query = edittext.getText().toString().trim();
		    if (Query.length() > 0) {
			//Log.d("myTag", "SearchBtn Pressed");
		    	//InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		    	imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//		    	isShowedKeypad = false;
				Intent intent = new Intent(CityGroupList.this, SearchCity.class);
				intent.putExtra("Query", Query);
				intent.putExtra("Area", areaID);
				startActivityForResult(intent, 1);
		    }
		}
	    });

        btn = (Button) findViewById(R.id.search_confirm);
        btn.setOnClickListener(new OnClickListener() {

        	@Override
		    public void onClick(View v) {
		    // TODO Auto-generated method stub
		    //Log.d("myTag", "Confirm Btn pressed");
		    if (getSelectedList()) {
				if (checkSelectedList()) {
				    if(saveSelectedList() > 100) {
				    	
						AlertDialog.Builder alert = null;
//				    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//				    		alert = new AlertDialog.Builder(CityGroupList.this, AlertDialog.THEME_HOLO_DARK);
//				    	else
				    		alert = new AlertDialog.Builder(CityGroupList.this);

					    alert.setTitle( "????????????" );
					    alert.setIcon(R.drawable.ic_dialog_menu_generic);
					    alert.setMessage("???????????? 100?????? ??????????????????.\n100???????????? ???????????????.");
					    alert.setPositiveButton( "??????", new DialogInterface.OnClickListener() {
						    @Override
							public void onClick( DialogInterface dialog, int which) {
						    	setResult(RESULT_OK, (new Intent()).setAction("REDRAW"));
						    	finish();
						    }
						});
		
					    alert.show();
				    } else {
				    	setResult(RESULT_OK, (new Intent()).setAction("REDRAW"));
				    	finish();
				    }
				} else {
					AlertDialog.Builder alert = null;
//			    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//			    		alert = new AlertDialog.Builder(CityGroupList.this, AlertDialog.THEME_HOLO_DARK);
//			    	else
			    		alert = new AlertDialog.Builder(CityGroupList.this);

				    alert.setTitle( "????????????" );
				    alert.setIcon(R.drawable.ic_dialog_menu_generic);
				    alert.setMessage("????????? ?????? ??? ????????? ?????? ????????? ????????????.");
	
				    alert.setPositiveButton( "??????", new DialogInterface.OnClickListener() {
					    @Override
						public void onClick( DialogInterface dialog, int which) {
					    	saveNewSelectedLIst();
					    	dialog.dismiss();
					    }
					});
	
				    alert.show();
				}
		    } else {
		    	if(alert != null) return;
//		    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//		    		alert = new AlertDialog.Builder(CityGroupList.this, AlertDialog.THEME_HOLO_DARK);
//		    	else
		    		alert = new AlertDialog.Builder(CityGroupList.this);
			    alert.setTitle( "??????" );
			    alert.setIcon(R.drawable.ic_dialog_menu_generic);
			    alert.setMessage("????????? ????????? ????????????.");

			    alert.setPositiveButton( "??????", new DialogInterface.OnClickListener() {
				    @Override
					public void onClick( DialogInterface dialog, int which) {
				    	dialog.dismiss();
				    	alert = null;
				    }
				});

			    OnCancelListener onCancelListener = new OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						dialog.dismiss();
		    			alert = null;
					}
				};

			    alert.setOnCancelListener(onCancelListener);
			    alert.show();
		    }
		}
	    });

        cancel = (Button) findViewById(R.id.search_cancel);
        cancel.setOnClickListener(new OnClickListener() {
		@Override
		    public void onClick(View v) { setResult(RESULT_CANCELED); finish(); }
	    });

        edittext.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
		    v.setFocusableInTouchMode(true);
		    v.requestFocus();
		    v.setOnClickListener(null);
		    
//		    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.showSoftInput(edittext, InputMethodManager.SHOW_FORCED);
			isShowInputMethod = true;
			
		    new Handler().postDelayed(new Runnable() { public void run() {
		    	isShowInputMethod = true;
		    	//InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		    	imm.showSoftInput(edittext, 0);
//		    	isShowedKeypad = true;
		    } }, 100);
		}
	    });
        Draw();
    }

    @Override
	protected void onActivityResult(int requestCode, int result, Intent data) {
		if (RESULT_OK == result) {
			setResult(RESULT_OK, (new Intent()).setAction(""));
		    finish();
		}
    }

    @Override
    protected void onResume(){
    	super.onResume();
//    	edittext.setText("");
//    	if(isShowedKeypad)
//		    new Handler().postDelayed(new Runnable() { public void run() {
//		    	//InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//		    	imm.showSoftInput(edittext, 0);
//				isShowedKeypad = true;
//		    } }, 100);
    }

    public void makeAutoComp()
    {
	CityListClass_E[] biglist;
	if (areaID.equals("Korea"))
	    biglist = Korea.korea;
	else if (areaID.equals("Asia"))
	    biglist = Asia.asia;
	else if (areaID.equals("Africa"))
	    biglist = Africa.africa;
	else if (areaID.equals("North America"))
	    biglist = NorthAmerica.northamerica;
	else if (areaID.equals("South America"))
	    biglist = SouthAmerica.southamerica;
	else if (areaID.equals("Europe"))
	    biglist = Europe.europe;
	else if (areaID.equals("Oceania"))
	    biglist = Oceania.oceania;
	else if (areaID.equals("MiddleEast"))
	    biglist = MiddleEast.middleeast;
	else return;


	String[] AutoComp = new String[biglist.length];
	for (int j = 0; j < biglist.length; ++j) {
		AutoComp[j] = biglist[j].getCityName();
	}

	ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
								android.R.layout.simple_dropdown_item_1line, AutoComp);

	edittext.setAdapter(adapter);
        edittext.setOnItemClickListener(new OnItemClickListener() {
		@Override
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		    String Query = edittext.getText().toString().trim();
		    if (Query.length() > 0) {
			//Log.d("myTag", "SearchBtn Pressed");
		    	//InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    	imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//	    	isShowedKeypad = false;
			Intent intent = new Intent(CityGroupList.this, SearchCity.class);
			intent.putExtra("Query", Query);
			intent.putExtra("Area", areaID);
			startActivityForResult(intent, 1);
		    }
		}
	    });

    }
    
    public void makeAutoComp(String query)
    {
	CityListClass_E[] biglist;
	if (areaID.equals("Korea"))
	    biglist = Korea.korea;
	else if (areaID.equals("Asia"))
	    biglist = Asia.asia;
	else if (areaID.equals("Africa"))
	    biglist = Africa.africa;
	else if (areaID.equals("North America"))
	    biglist = NorthAmerica.northamerica;
	else if (areaID.equals("South America"))
	    biglist = SouthAmerica.southamerica;
	else if (areaID.equals("Europe"))
	    biglist = Europe.europe;
	else if (areaID.equals("Oceania"))
	    biglist = Oceania.oceania;
	else if (areaID.equals("MiddleEast"))
	    biglist = MiddleEast.middleeast;
	else return;

	char tempChar;
	try {
		tempChar = query.toUpperCase().charAt(0);
	} catch(IndexOutOfBoundsException e) {
		tempChar = '0';
	}
	boolean isalpha = Util.isAlpha((int) tempChar);
	String[] AutoComp = new String[biglist.length];
	for (int j = 0; j < biglist.length; ++j) {
		if(isalpha)
			AutoComp[j] = biglist[j].getEngCityName();
		else
			AutoComp[j] = biglist[j].getCityName();
	}

	ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
								android.R.layout.simple_dropdown_item_1line, AutoComp);

	edittext.setAdapter(adapter);
        edittext.setOnItemClickListener(new OnItemClickListener() {
		@Override
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		    String Query = edittext.getText().toString().trim();
		    if (Query.length() > 0) {
			//Log.d("myTag", "SearchBtn Pressed");
		    	//InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    	imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//	    	isShowedKeypad = false;
			Intent intent = new Intent(CityGroupList.this, SearchCity.class);
			intent.putExtra("Query", Query);
			intent.putExtra("Area", areaID);
			startActivityForResult(intent, 1);
		    }
		}
	    });

    }

    public List getCityNameList()
    {
    	return Child;
    }

    public void setSearchResult(List result, int idx)
    {
	//Log.d("myTag", "idx=" + idx + " result=" + result.toString());
	setSearchList(result, idx);
    }

    private void setSearchList(final List result, final int idx)
    {
    	SearchThread.post(new Runnable() {

		@Override
		    public void run() {
		    // TODO Auto-generated method stub
		    if (idx == -1)
			Child = result;
		    else
			Child.set(idx, result);
		    Draw();
		    ExpandableListView list = getExpandableListView();
		    for (int i = 0 ; i < Parent.size(); i++)
			list.expandGroup(i);
		}
	    });
    }

    public void Draw() {
	Drawable icon = this.getResources().getDrawable(R.drawable.expander_group);

	getExpandableListView().setGroupIndicator(icon);

	CityExpandableListAdapter expListAdapter =
	    new CityExpandableListAdapter(
					  getApplicationContext(),
					  Parent,	// groupData describes the first-level entries
					  R.layout.citygroup_row,	// Layout for the first-level entries
					  new String[] { "CITYNAME" },	// Key in the groupData maps to display
					  new int[] { R.id.childname },		// Data under "colorName" key goes into this TextView
					  Child,	// childData describes second-level entries
					  R.layout.citychild_row,	// Layout for second-level entries
					  new String[] { "Name", "Checked" },	// Keys in childData maps to display
					  new int[] { R.id.childname, R.id.check1}	// Data under the keys above go into these TextViews
					  );
	setListAdapter( expListAdapter );
	icon.setCallback(null);
    }

    protected int saveSelectedList() {
	// TODO Auto-generated method stub
	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
	int nThemeCnt = 0;
	String Temp = "";
	String[] szTemp;

	nThemeCnt = prefs.getInt(Const.CITY_CNT, 0);

	SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
	for(int i = 0, j = nThemeCnt ; i < selectedarray.size() && j < 100 ; i++ , j++){
	    Temp = selectedarray.get(i).getCityID() + "\t" + selectedarray.get(i).getCityName() + "\t" + selectedarray.get(i).getTimeZone() + "\t" +
		"--" + "\t" + Integer.toString(R.drawable.weather_icon_01_ref) + "\t" +
		"-999" + "\t" + "-999" + "\t" + "-999";
	    int nIdx = i + nThemeCnt;
	    prefs1.putString(Const.CITY_LIST + Integer.toString(nIdx), Temp);
	    Temp = "";
	}
	int listCnt = nThemeCnt + selectedarray.size();
	prefs1.putInt(Const.CITY_CNT, listCnt > 100 ? 100 : listCnt);
	prefs1.commit();
	return nThemeCnt + selectedarray.size();
    }

    protected int saveNewSelectedLIst() {
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
	    	try {
	    		if (szTemp[j].equals("") || szTemp[j] == null || szTemp[j].equals("\n"))
	    			ThemeID[i][j] = "--";
	    		else
	    			ThemeID[i][j] = szTemp[j];
	    	} catch(ArrayIndexOutOfBoundsException e) {
	    		ThemeID[i][j] = "--";
	    	}
    	}
	}

	//Log.d("myTag", "nThemeCnt=" + nThemeCnt);

	SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
	int k = 0;
	int nAddedCnt = 0;
	boolean isAdded = false;

	for(int j = 0 ; j < selectedarray.size() && k + nThemeCnt < 100 ; j++){
	    isAdded = false;
	    for(int i = 0 ; i < nThemeCnt ; i++){
	    	if ( ThemeID[i][0].equals(selectedarray.get(j).getCityID()) ) {
	    		isAdded = true;
	    		nAddedCnt++;
	    		break;
	    	}
	    }

	    if (!isAdded){
	    	Temp = selectedarray.get(j).getCityID() + "\t" + selectedarray.get(j).getCityName() + "\t" + selectedarray.get(j).getTimeZone() + "\t" +
	    		"--" + "\t" + Integer.toString(R.drawable.weather_icon_01_ref) + "\t" +
	    		"-999" + "\t" + "-999" + "\t" + "-999";

	    	int nIdx = k + nThemeCnt;
	    	prefs1.putString(Const.CITY_LIST + Integer.toString(nIdx), Temp);
	    	k ++;
	    }
	}

	int listCnt = nThemeCnt + selectedarray.size() - nAddedCnt;
	prefs1.putInt(Const.CITY_CNT, listCnt > 100 ? 100 : listCnt);
	prefs1.commit();
	
	if(listCnt > 100) {
		AlertDialog.Builder alert1 = null;
//    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    		alert1 = new AlertDialog.Builder(CityGroupList.this, AlertDialog.THEME_HOLO_DARK);
//    	else
    		alert1 = new AlertDialog.Builder(CityGroupList.this);
	    alert1.setTitle( "????????????" );
	    alert1.setIcon(R.drawable.ic_dialog_menu_generic);
	    alert1.setMessage("???????????? 100?????? ??????????????????.\n100???????????? ???????????????.");
	    alert1.setPositiveButton( "??????", new DialogInterface.OnClickListener() {
		    @Override
			public void onClick( DialogInterface dialog, int which) {
		    	setResult(RESULT_OK, (new Intent()).setAction("REDRAW"));
		    	finish();
		    }
		});
	
	    alert1.show();
	} else {
		setResult(RESULT_OK, (new Intent()).setAction("REDRAW"));
    	finish();
	}
    
	return listCnt;
    }

    protected boolean checkSelectedList() {
	// TODO Auto-generated method stub
	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
	String[][] ThemeID;
	int nThemeCnt = 0;
	String Temp = null;
	String[] szTemp;

	if (prefs == null)
	    return true;

	nThemeCnt = prefs.getInt(Const.CITY_CNT, 0);
	ThemeID = new String[nThemeCnt][8];

	for(int i = 0 ; i < nThemeCnt ; i++){
	    Temp = prefs.getString(Const.CITY_LIST + i, "");
	    szTemp = Temp.split("\t");
	    for(int j = 0 ; j < 8 ; j++){
		//Log.d("myTag", "szTemp[" + j + "]=" + szTemp[j]);
	    	try {
				if (szTemp[j].equals("") || szTemp[j] == null || szTemp[j].equals("\n"))
				    ThemeID[i][j] = "--";
				else
				    ThemeID[i][j] = szTemp[j];
	    	} catch (ArrayIndexOutOfBoundsException e) {
	    		e.printStackTrace();
	    	}
	    }
	}

	for(int i = 0 ; i < nThemeCnt ; i++){
	    for(int j = 0 ; j < selectedarray.size() ; j++){
		if ( ThemeID[i][0].equals(selectedarray.get(j).getCityID()) ) {
		    //Log.d("myTag", selectedarray.get(j).getCityID() + " is being in the ThemeList!!");
		    return false;
		}
	    }
	}

	return true;
    }

    protected boolean getSelectedList() {
	// TODO Auto-generated method stub

	int len = Child.size();
	for(int i = 0 ; i < len; i++)
	    for(int j = 0; j < ((ArrayList)Child.get(i)).size() ; j++){
		HashMap tempmap = (HashMap)(((ArrayList)Child.get(i)).get(j));
		boolean isChecked = (Boolean)tempmap.get("Checked");
		CityListClass tempclass;
		if (isChecked) {
		    tempclass = new CityListClass((String)tempmap.get("ID"), (String)tempmap.get("Name"),
						  (String)tempmap.get("CN"), (String)tempmap.get("TZ"));
		    selectedarray.add(tempclass);
		}
	    }
	
	if(selectedarray.size() >= 1)
		return true;
	else
		return false;
    }

    public void onContentChanged() {
	super.onContentChanged();
	//Log.d(LOG_TAG, "onContentChanged");
    }

    public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {

	//Log.d(LOG_TAG, "onChildClick: groupPosition=" + groupPosition + " childPosition=" + childPosition + " id=" + id);
	boolean isChecked = false;
	CheckBox cv = null;

	LinearLayout lv = (LinearLayout) v;

	try{
		cv = (CheckBox)((LinearLayout)lv.getChildAt(1)).getChildAt(0);
	} catch(NullPointerException e) {
		return super.onChildClick(parent, v, groupPosition, childPosition, id);
	}
	cv.setChecked(!cv.isChecked());

	isChecked = cv.isChecked();

//	ImageView iv = (ImageView)((LinearLayout)lv.getChildAt(1)).getChildAt(1);
//	if (isChecked)
//	    iv.setImageResource(R.drawable.btn_check_on);
//	else
//	    iv.setImageResource(R.drawable.btn_check_off);

	ArrayList secList = new ArrayList();
	secList = (ArrayList)Child.get(groupPosition);

	HashMap tempMap = new HashMap();
	tempMap = (HashMap)secList.get(childPosition);
	tempMap.put("Checked", isChecked);

	secList.set(childPosition, tempMap);

	Child.set(groupPosition, secList);

	return super.onChildClick(parent, v, groupPosition, childPosition, id);
    }

    public void  onGroupExpand  (int groupPosition) {
        //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//        isShowedKeypad = false;
    }


    private List createGroupList() {
	ArrayList result = new ArrayList();
	for (int i = 0; i < cityListName.length; ++i) {
	    HashMap m = new HashMap();
	    m.put("CITYNAME", cityListName[i]);
	    result.add(m);
	}
	return (List) result;
    }

    private List createChildList() {
	ArrayList result = new ArrayList();

	for (int i = 0; i < cityList.length; ++i) {
	    // Second-level lists
	    CityListClass[] cityarray = null;

	    cityarray = cityList[i];

	    ArrayList secList = new ArrayList();
	    for (int n = 0; n < cityarray.length; n++) {
		HashMap child = new HashMap();
		child.put("Name", cityarray[n].getCityName());
		child.put("ID", cityarray[n].getCityID());
		child.put("TZ", cityarray[n].getTimeZone());
		child.put("CN", cityarray[n].getCountry());
		child.put("Checked", false);
		secList.add(child);
	    }
	    result.add(secList);
	}
	return result;
    }

    @Override
    protected void onPause() {
    	if(imm.isActive()) {
//    		isShowedKeypad = true;
    		imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
    	}
        super.onPause();
    }
}
