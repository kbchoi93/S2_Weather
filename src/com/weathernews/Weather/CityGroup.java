package com.weathernews.Weather;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

public class CityGroup extends ListActivity
{
//	private AutoCompleteTextView edittext;
	private MyAutoCompleteTextView edittext;
	private ListView lv;
	private ImageButton imgbtn;
	private TextWatcher watcher;
	private ArrayAdapter<String> adapter;
	private InputMethodManager imm;
	private ArrayAdapter<String> _arrAdapter ;
	private LinearLayout ll;
	private boolean isShowedKeypad = false;
	
	String[] mStrings = {"대한민국", "아시아", "아프리카", "북아메리카", "남아메리카", "유럽", "오세아니아", "중동"};
	String[] mStrings2 = {"대한민국", "아시아", "아프리카", "북아메리카", "남아메리카", "유럽", "오세아니아", "중동"};
	String[] mAreaID = {"Korea", "Asia", "Africa", "North America", "South America", "Europe", "Oceania", "MiddleEast"};
	final static private String WorldID = "World";

	private Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.city);

        _arrAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, mStrings);
        setListAdapter(_arrAdapter);
        ll = (LinearLayout) findViewById(R.id.citygroup);
        
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        
        edittext = (MyAutoCompleteTextView) findViewById(R.id.name_input);
        makeAutoComp();
        
        edittext.setActivity(this);
        
        edittext.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
		            	ReloadList();
					}
				});
				
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String Query = edittext.getText().toString().trim();
		        		if (Query.length() > 0) {
		    				imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
		    				isShowedKeypad = false;
		    				
		        			Intent intent = new Intent(CityGroup.this, SearchCity.class);
		        			intent.putExtra("Query", Query);
		        			intent.putExtra("Area", WorldID);
		        			startActivityForResult(intent, 1);
		        		}
					}
				}, 250);
        	}
        });
        
        edittext.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, final int actionId, KeyEvent event) {
				final String Query = edittext.getText().toString().trim();
				if (Query.length() <= 0) return false;

				handler.post(new Runnable() {
					@Override
					public void run() {
		            	ReloadList();
					}
				});
								
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if(actionId == EditorInfo.IME_ACTION_SEARCH) {
			        		if (Query.length() > 0) {
			    				edittext.dismissDropDown();
			    				imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
			    				isShowedKeypad = false;
			    				
			        			Intent intent = new Intent(CityGroup.this, SearchCity.class);
			        			intent.putExtra("Query", Query);
			        			intent.putExtra("Area", WorldID);
			        			startActivityForResult(intent, 1);
			        		}
		                 // search pressed and perform your functionality.                   
		                }
					}
				}, 50);

				return false;
			}
		});
        
        edittext.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    		    v.setFocusableInTouchMode(true);
    		    v.requestFocus();
    		    v.setOnClickListener(null);
    		    
//    		    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//    			imm.showSoftInput(edittext, InputMethodManager.SHOW_FORCED);
    			
				isShowedKeypad = true;
				
    		    new Handler().postDelayed(new Runnable() { public void run() {
    		    	//InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    		    	imm.showSoftInput(edittext, 0);
    		    } }, 100);
    		}
    	    });
        
        edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
					isShowedKeypad = true;
			}
		});
        
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
        lv = getListView();
        lv.setTextFilterEnabled(true);
        //getListView().setTextFilterEnabled(true);
        imgbtn = (ImageButton) findViewById(R.id.searchbtn);
        
//        imgbtn.setImageResource(R.drawable.ic_btn_search);
        imgbtn.setOnClickListener(new OnClickListener() {

        	@Override
        	public void onClick(View v) {
        		String Query = edittext.getText().toString().trim();
        		if( Query.length() <= 0 ) return;
            	ReloadList();
        		if (Query.length() > 0) {
        			Intent intent = new Intent(CityGroup.this, SearchCity.class);
        			intent.putExtra("Query", Query);
        			intent.putExtra("Area", WorldID);
        			startActivityForResult(intent, 1);
        		}
        	}
	    });
        
        lv.setOnScrollListener(new OnScrollListener() {

    		@Override
    		    public void onScrollStateChanged(AbsListView view, int scrollState) {
    		    // TODO Auto-generated method stub
   		    	imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
				isShowedKeypad = false;
    		}

    		@Override
    		    public void onScroll(AbsListView view, int firstVisibleItem,
    					 int visibleItemCount, int totalItemCount) {
    		    // TODO Auto-generated method stub
    		}
    	});
    }
    

	public void onBackPressInEditView() {
		isShowedKeypad = false;
	}

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	ReloadList();
        Intent intent;
        intent = new Intent(this, CityGroupList.class);
    	intent.putExtra("AreaID", mAreaID[position]);

    	startActivityForResult(intent, 1);
    }
    
    public void makeAutoComp()
    {
		CityListClass_E[] biglist[] = { Korea.korea, Asia.asia, Africa.africa, NorthAmerica.northamerica, SouthAmerica.southamerica, Europe.europe, Oceania.oceania, MiddleEast.middleeast };
		int size = 0;
		for (int i = 0; i < biglist.length; ++i) size += biglist[i].length;
		String[] AutoComp = new String[size];
		int nListCnt = 0;
	
		for (int i = 0; i < biglist.length; ++i) {
		    CityListClass_E[] current = biglist[i];
		    for (int j = 0; j < current.length; ++nListCnt, ++j)
			AutoComp[nListCnt] = current[j].getCityName();
		}
	
		adapter = new ArrayAdapter<String>(getApplicationContext(),
									android.R.layout.simple_dropdown_item_1line, AutoComp);
				
		edittext.setAdapter(adapter);
    }
    
    public void makeAutoComp(String text)
    {
    	String[] AutoComp = null;
    	char tempChar = text.toUpperCase().charAt(0);
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
				AutoComp = new String[current.length];
			else
				return;
			
			for(int i = 0 ; i < current.length; i ++)
				AutoComp[i] = current[i].getEngCityName();
	
		} else {
			String StartString = HangulUtils.getHangulInitialSound(text.substring(0, 1));
			CityListClass[] current = null;
			if(StartString.equals("ㄱ") || StartString.equals("ㄲ")) current = GA.ga;
			else if(StartString.equals("ㄴ")) current = NA.na;
			else if(StartString.equals("ㄷ") || StartString.equals("ㄸ")) current = DA.da;
			else if(StartString.equals("ㄹ")) current = RA.ra;
			else if(StartString.equals("ㅁ")) current = MA.ma;
			else if(StartString.equals("ㅂ") || StartString.equals("ㅃ")) current = BA.ba;
			else if(StartString.equals("ㅅ") || StartString.equals("ㅆ")) current = SA.sa;
			else if(StartString.equals("ㅇ")) current = AA.aa;
			else if(StartString.equals("ㅈ") || StartString.equals("ㅉ")) current = JA.ja;
			else if(StartString.equals("ㅊ")) current = CHA.cha;
			else if(StartString.equals("ㅋ")) current = KA.ka;
			else if(StartString.equals("ㅌ")) current = TA.ta;
			else if(StartString.equals("ㅍ")) current = PA.pa;
			else if(StartString.equals("ㅎ")) current = HA.ha;
			else current = GA.ga;
			
			
			
			
			if(current != null)
				AutoComp = new String[current.length];
			else
				return;
			
			for(int i = 0 ; i < current.length; i ++)
				AutoComp[i] = current[i].getCityName();
		}
		
		adapter = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_dropdown_item_1line, AutoComp);
		
		edittext.setAdapter(adapter);

    }
    
    @Override
    protected void onActivityResult(int requestCode, int result, Intent data) {
	    if (RESULT_OK == result){
	    	setResult(RESULT_OK, (new Intent()).setAction(""));
	    	finish();
	    } else if(RESULT_CANCELED == result) {
//	    	setResult(RESULT_CANCELED, (new Intent()).setAction(""));
//	    	finish();
	    }
    }

    public void ReloadList() {
    	if(imm.isActive()) {
    		imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
			isShowedKeypad = false;
    	}
    	ll.invalidate();
    	lv.invalidate();
    	_arrAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, mStrings2);
        _arrAdapter.notifyDataSetChanged();
        setListAdapter(_arrAdapter);
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
//    	edittext.setText("");
    	if(isShowedKeypad) {
		    new Handler().postDelayed(new Runnable() { public void run() {
		    	//InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		    	imm.showSoftInput(edittext, 0);
		    } }, 100);
    	}
    }


    @Override
	protected void onPause() {
    	super.onPause();
    	if(imm.isActive()) {
    		imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
    	}
		return;
    }
    
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	isShowedKeypad = false;
    }
}
