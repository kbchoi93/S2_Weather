package com.weathernews.Weather;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView.OnEditorActionListener;

public class SearchTheme extends ListActivity implements OnCheckedChangeListener {
	
	private AutoCompleteTextView edittext;
	private TextWatcher watcher;
	private ImageButton imgbtn;
	private Button btn;
	private Button cancel;
	
	private String[] ThemeList;
	private String[] ThemeName;
	private String[] address;
	private String[] TimeZone;
	private String[] szSearchResult;
	private ThemeList3[] SearchResult;
	private ThemeList3[] themelist;
	private ListView listview;
	
//	private Handler SearchThread;
//	private Runnable searchTask;
//	private ExecutorService ServiceThread;
//	private Future SearchPending;
	
//	private final class RemoveWindow implements Runnable {
//        public void run() {
//            removeWindow();
//        }
//    }

//    private RemoveWindow mRemoveWindow = new RemoveWindow();
    private Handler mHandler = new Handler();
    private WindowManager mWindowManager;
//    private TextView mDialogText;
    private boolean mShowing;
    private boolean mReady;
    private String mPrevLetter = "";
	private ThemeList3Adapter m_adapter3;
	private ArrayList<ThemeList3> m_orders3;
	private InputMethodManager imm;
	
	private ThemeList3[] plist3;
	private ThemeList3[] selectedlist;
	private static final int GOLFCNT = ThemeClass.Theme_Golf.length;
	private static final int SKICNT = ThemeClass.Theme_Ski.length;
	private static final int MOUNTAINCNT = ThemeClass.Theme_Mountain.length;
	private static final int BASEBALLCNT = ThemeClass.Theme_Baseball.length;
	private static final int THEMEPARKCNT = ThemeClass.Theme_ThemPark.length;
	private static final int BEACHCNT = ThemeClass.Theme_Beach.length;
	
	
	private static final int [] ListCnt = {GOLFCNT, SKICNT, MOUNTAINCNT, BASEBALLCNT, THEMEPARKCNT, BEACHCNT };
    /** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        StringBuffer sbTemp = null;
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        
//        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        
        setContentView(R.layout.search);

        //getListView().setOnScrollListener(this);
        
        Intent intent = getIntent();
        String Query = intent.getStringExtra("Query");
        //Log.d("myTag", Query);
        
        LayoutInflater inflate = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
//        initThreading();
        
//        mDialogText = (TextView) inflate.inflate(R.layout.list_position, null);
//        mDialogText.setVisibility(View.INVISIBLE);

        makeAutoComp(Query);
        
//        listview = getListView();
//        
//        listview.setOnScrollListener(new OnScrollListener() {
//			
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				// TODO Auto-generated method stub
//			}
//			
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//				// TODO Auto-generated method stub
//				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//				if(imm.isActive())
//					imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//			}
//		});
//        edittext = (EditText) findViewById(R.id.name_input);
//        edittext.setHint("장소명을 입력하십시오");
//        edittext.setText(Query);
        imgbtn = (ImageButton) findViewById(R.id.searchbtn);
        
        
        imgbtn.setImageResource(R.drawable.ic_btn_search);
        imgbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Log.d("myTag", "SearchBtn Pressed");
//				SearchThread.removeCallbacks(searchTask);
				imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
	    		SearchResult(edittext.getText().toString().trim());
			}
		});
        
//        btn = (Button) findViewById(R.id.search_confirm);
//        btn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				//Log.d("myTag", "Confirm Btn pressed");
//				
//				if(getSelectedList()) {
//					if(checkSelectedList()) {
//						saveSelectedLIst();//end intent;
//						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//						if(imm.isActive())
//							imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//
//						setResult(RESULT_OK, (new Intent()).setAction("REDRAW"));
//						finish();
//					} else {
//						AlertDialog.Builder alert = new AlertDialog.Builder(SearchTheme.this);
//				    	alert.setTitle( "테마추가" );
//				    	alert.setIcon(R.drawable.ic_dialog_menu_generic);
//				    	alert.setMessage("선택한 테마 중 일부가 이미 목록에 있습니다.");
//				    	
//				    	alert.setPositiveButton( "확인", new DialogInterface.OnClickListener() {
//				    		@Override
//				    	    public void onClick( DialogInterface dialog, int which) {
//				    			saveNewSelectedLIst();
//								InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//								if(imm.isActive())
//									imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//
//				    			setResult(RESULT_OK, (new Intent()).setAction("REDRAW"));
//								finish();
//				    	        //dialog.dismiss();
//				    	    }
//				    	});
//				    	
//				    	alert.show();
//					}
//				}
//			}
//		});
//        
//        cancel = (Button) findViewById(R.id.search_cancel);
//        cancel.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//				if(imm.isActive())
//					imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//
//				finish();
//			}
//		});
        
//        watcher = new TextWatcher(){
//        	@Override
//        	public void afterTextChanged(Editable s) {
//       		}
//        	@Override
//        	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        	}
//        	
//    		public void onTextChanged(CharSequence s, int start, int before, int count){
//    			queueSearch(1000);
//    		}
//    	};
//    	
//    	edittext.addTextChangedListener(watcher);

//        mHandler.post(new Runnable() {
//
//            public void run() {
//                mReady = true;
//                WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
//                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
//                        WindowManager.LayoutParams.TYPE_APPLICATION,
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                        PixelFormat.TRANSLUCENT);
//                mWindowManager.addView(mDialogText, lp);
//            }});
        
		themelist = new ThemeList3[GOLFCNT + SKICNT + MOUNTAINCNT + BASEBALLCNT + THEMEPARKCNT + BEACHCNT];
		int nIndex = 0;
		for(int i = 0 ; i < GOLFCNT ; i++){
			themelist[i] = new ThemeList3(ThemeClass.Theme_Golf[i], false);
		}
		
		nIndex += GOLFCNT;
		for(int i = nIndex, j = 0 ; i < nIndex + SKICNT ; i++, j++)
			themelist[i] = new ThemeList3(ThemeClass.Theme_Ski[j], false);
		
		nIndex += SKICNT;
		for(int i = nIndex, j = 0 ; i < nIndex + MOUNTAINCNT ; i++, j++)
			themelist[i] = new ThemeList3(ThemeClass.Theme_Mountain[j], false);
		
		nIndex += MOUNTAINCNT;
		for(int i = nIndex, j = 0 ; i < nIndex + BASEBALLCNT ; i++, j++)
			themelist[i] = new ThemeList3(ThemeClass.Theme_Baseball[j], false);
		
		nIndex += BASEBALLCNT;
		for(int i = nIndex, j = 0 ; i < nIndex + THEMEPARKCNT ; i++, j++)
			themelist[i] = new ThemeList3(ThemeClass.Theme_ThemPark[j], false);
		
		nIndex += THEMEPARKCNT;
		for(int i = nIndex, j = 0 ; i < nIndex + BEACHCNT ; i++, j++)
			themelist[i] = new ThemeList3(ThemeClass.Theme_Beach[j], false);

    	Draw();
    	
		SearchResult(Query);
    }
	
	public void makeAutoComp(String Query){
		String[] AutoComp = new String[GOLFCNT + SKICNT + MOUNTAINCNT + BASEBALLCNT + THEMEPARKCNT + BEACHCNT];
		int nListCnt = 0;
		
		for (int i = 0; i < ListCnt.length; ++i) {
			// Second-level lists
			if(i == 0)
				nListCnt = 0;
			else
				nListCnt += ListCnt[i-1];
			Theme[] themearray = null;
			if(i == Theme.THEME_GOLF)
				themearray = ThemeClass.Theme_Golf;
			else if(i == Theme.THEME_SKI)
				themearray = ThemeClass.Theme_Ski;
			else if(i == Theme.THEME_MOUNTAIN)
				themearray = ThemeClass.Theme_Mountain;
			else if(i == Theme.THEME_BASEBALL)
				themearray = ThemeClass.Theme_Baseball;
			else if(i == Theme.THEME_THEMEPARK)
				themearray = ThemeClass.Theme_ThemPark;
			else if(i == Theme.THEME_BEACH)
				themearray = ThemeClass.Theme_Beach;
			
			for (int n = 0; n < themearray.length; n++) {
				AutoComp[nListCnt + n] = themearray[n].getName();
			}
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line, AutoComp);
		
		edittext = (AutoCompleteTextView) findViewById(R.id.name_input);
        edittext.setHint("장소명을 입력하십시오");
        edittext.setAdapter(adapter);
        
		edittext.dismissDropDown();
		edittext.setSelection(edittext.getText().length());

        
        edittext.setOnEditorActionListener(new OnEditorActionListener() {
    		
    		@Override
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    			// TODO Auto-generated method stub
    			if(actionId == EditorInfo.IME_ACTION_SEARCH) {
    				String Query = edittext.getText().toString().trim();
    				if (Query.length() > 0) {
    					imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//    					SearchResult(edittext.getText().toString().trim());
//    					Intent intent = new Intent(SearchTheme.this, SearchTheme.class);
//    					intent.putExtra("Query", Query);
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
    		    	imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
    		    	edittext.setSelection(edittext.getText().length());
    		    	SearchResult(Query);
    		    }
    		}
        });
        if(Query != null && Query.length() > 0)
        	edittext.setText(Query);
	}
	
	protected int saveSelectedLIst() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
		int nThemeCnt = 0;
		String Temp = "";
		String[] szTemp;
		
		nThemeCnt = prefs.getInt(Const.THEME_CNT, 0);
		
		//Log.d("myTag", "nThemeCnt=" + nThemeCnt);
		
		if(nThemeCnt >= 100)
			return 101;
		
		SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
		for(int i = 0 ; i < selectedlist.length ; i++){
			Temp = selectedlist[i].theme.getThemeCode() + "\t" + selectedlist[i].theme.getAreaCode() + "\t" + selectedlist[i].theme.getName() + "\t" +
					selectedlist[i].theme.getAddress() + "\t" + "--" + "\t" + Integer.toString(R.drawable.weather_icon_01_ref) + "\t" +
					"--" + "\t" + "--" + "\t" + "--" + "\t" + Integer.toString(selectedlist[i].theme.getThemeID());
			int nIdx = i + nThemeCnt;
			prefs1.putString(Const.THEME_LIST + Integer.toString(nIdx), Temp);
			//Log.d("myTag", Const.THEME_LIST + Integer.toString(nIdx) + Temp);
			Temp = "";
		}
		prefs1.putInt(Const.THEME_CNT, nThemeCnt + selectedlist.length);
		prefs1.commit();
		
		return nThemeCnt;
	}
	
	protected int saveSelectedLIst(ThemeList3 checked) {
		// TODO Auto-generated method stub
		SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
		int nThemeCnt = 0;
		String Temp = "";
		String[] szTemp;
		
		nThemeCnt = prefs.getInt(Const.THEME_CNT, 0);
		
    	if(nThemeCnt >= 100)
    		return 101;
		
		
		//Log.d("myTag", "nThemeCnt=" + nThemeCnt);
		
		SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
			Temp = checked.theme.getThemeCode() + "\t" + checked.theme.getAreaCode() + "\t" + checked.theme.getName() + "\t" +
					checked.theme.getAddress() + "\t" + "--" + "\t" + Integer.toString(R.drawable.weather_icon_01_ref) + "\t" +
					"--" + "\t" + "--" + "\t" + "--" + "\t" + Integer.toString(checked.theme.getThemeID());
			prefs1.putString(Const.THEME_LIST + Integer.toString(nThemeCnt), Temp);
			Temp = "";
		prefs1.putInt(Const.THEME_CNT, nThemeCnt + 1);
		prefs1.commit();
		
		return nThemeCnt;
	}
	
	protected void saveNewSelectedLIst() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
		int nThemeCnt = 0;
		String Temp = "";
		String[] szTemp;
		
		String[][] ThemeID;
		
		nThemeCnt = prefs.getInt(Const.THEME_CNT, 0);
		
		ThemeID = new String[nThemeCnt][8];
		
		for(int i = 0 ; i < nThemeCnt ; i++){
			Temp = prefs.getString(Const.THEME_LIST + i, "");
			szTemp = Temp.split("\t");
			for(int j = 0 ; j < 8 ; j++){
				//Log.d("myTag", "szTemp[" + j + "]=" + szTemp[j]);
				if(szTemp[j].equals("") || szTemp[j] == null || szTemp[j].equals("\n"))
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
				if( ThemeID[i][0].equals(selectedlist[j].theme.getThemeCode()) ) {
					isAdded = true;
					break;
				}
			}
			
			if(!isAdded){
				Temp = selectedlist[j].theme.getThemeCode() + "\t" + selectedlist[j].theme.getAreaCode() + "\t" + selectedlist[j].theme.getName() + "\t" +
						selectedlist[j].theme.getAddress() + "\t" + "--" + "\t" + Integer.toString(R.drawable.weather_icon_01_ref) + "\t" +
						"--" + "\t" + "--" + "\t" + "--" + "\t" + Integer.toString(selectedlist[j].theme.getThemeID());
				
				int nIdx = k + nThemeCnt;
				prefs1.putString(Const.THEME_LIST + Integer.toString(nIdx), Temp);
				//Log.d("myTag", "Add New List=" + Temp);
				k ++;
			}
		}
		
		prefs1.putInt(Const.THEME_CNT, nThemeCnt + k);
		prefs1.commit();
	}

	protected boolean checkSelectedList() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
		String[][] ThemeID;
		int nThemeCnt = 0;
		String Temp = null;
		String[] szTemp;
		
		if(prefs == null)
			return true;
		
		nThemeCnt = prefs.getInt(Const.THEME_CNT, 0);
		ThemeID = new String[nThemeCnt][8];
		
		for(int i = 0 ; i < nThemeCnt ; i++){
			Temp = prefs.getString(Const.THEME_LIST + i, "");
			szTemp = Temp.split("\t");
			for(int j = 0 ; j < 8 ; j++){
				//Log.d("myTag", "szTemp[" + j + "]=" + szTemp[j]);
				try {
					if(szTemp[j].equals("") || szTemp[j] == null || szTemp[j].equals("\n"))
						ThemeID[i][j] = "--";
					else 
						ThemeID[i][j] = szTemp[j];
				} catch (ArrayIndexOutOfBoundsException e) {
					ThemeID[i][j] = "--";
				}
			}
		}
		
		for(int i = 0 ; i < nThemeCnt ; i++){
			for(int j = 0 ; j < selectedlist.length ; j++){
				//Log.d("myTag", "Saved Themeid=[" + ThemeID[i][0] + "] selectedThemeid=[" + selectedlist[j].theme.getThemeCode() + "]");
				if( ThemeID[i][0].equals(selectedlist[j].theme.getThemeCode()) ) {
					//Log.d("myTag", selectedlist[j].theme.getThemeCode() + " is being in the ThemeList!!");
					return false;
				}
			}
		}
		
		return true;
	}
	
	protected boolean checkSelectedList(ThemeList3 checked) {
		// TODO Auto-generated method stub
		SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
		String[][] ThemeID;
		int nThemeCnt = 0;
		String Temp = null;
		String[] szTemp;
		
		if(prefs == null)
			return true;
		
		nThemeCnt = prefs.getInt(Const.THEME_CNT, 0);
		ThemeID = new String[nThemeCnt][8];
		
		for(int i = 0 ; i < nThemeCnt ; i++){
			Temp = prefs.getString(Const.THEME_LIST + i, "");
			szTemp = Temp.split("\t");
			for(int j = 0 ; j < 8 ; j++){
				//Log.d("myTag", "szTemp[" + j + "]=" + szTemp[j]);
				if(szTemp[j].equals("") || szTemp[j] == null || szTemp[j].equals("\n"))
					ThemeID[i][j] = "--";
				else 
					ThemeID[i][j] = szTemp[j];
			}
		}
		
		for(int i = 0 ; i < nThemeCnt ; i++){
			if( ThemeID[i][0].equals(checked.theme.getThemeCode()) ) {
				return false;
			}
		}
		
		return true;
	}

	protected boolean getSelectedList() {
		// TODO Auto-generated method stub
		ThemeList3[] TempList = new ThemeList3[plist3.length];
		int nSelectedCnt = 0;
		
		for(int i = 0 ; i < plist3.length ; i++) {
			if(plist3[i].getisChecked() == true){
				TempList[nSelectedCnt] = plist3[i];
				nSelectedCnt++;
			}
		}
		
		selectedlist = new ThemeList3[nSelectedCnt];
		
		//Log.d("myTag", "nSelectedCount=" + nSelectedCnt);
		
		System.arraycopy(TempList, 0, selectedlist, 0, nSelectedCnt);
		
//		for(int i = 0 ; i < selectedlist.length ; i++){
//			//Log.d("myTag", "ThemeName=[" + selectedlist[i].theme.getName() + "] ThemeID=[" + selectedlist[i].theme.getThemeCode() + "] checked=[" + selectedlist[i].getisChecked() + "]");
//		}
		
		return true;
	}

//	private void queueSearch(long delayMillis) {
//	      // Cancel previous update if it hasn't started yet
//	      SearchThread.removeCallbacks(searchTask);
//	      // Start an update if nothing happens after a few milliseconds
//	      SearchThread.postDelayed(searchTask, delayMillis);
//	   }
	
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
//				if(SearchPending != null)
//					SearchPending.cancel(true);
//				
//				if(query.length() == 0 ) {
//					setSearchResult(themelist);
//				} else {
//					SearchTaskTheme task = new SearchTaskTheme(SearchTheme.this, query);
//					SearchPending = ServiceThread.submit(task);
//				}
//			}
//		};
//	}
	
	public ThemeList3[] getThemeNameList() {
		return this.themelist;
	}
	
    private void SearchResult(String string) {
    	ThemeList3[] TempList = new ThemeList3[themelist.length];
    	int nMatchedCount = 0;
    	
    	//Log.d("DEBUG", "[" + string + "]");
    	if( string == null || string.length() == 0 ){
    		m_orders3 = new ArrayList<ThemeList3>();
    		m_adapter3 = new ThemeList3Adapter(getApplicationContext(), R.layout.search_row, m_orders3);
    		setListAdapter(m_adapter3);
    		
    		SearchResult = themelist;
    		
    		plist3 = new ThemeList3[SearchResult.length];
    		for(int i = 0 ; i < SearchResult.length ; i++){
    			plist3[i] = new ThemeList3(SearchResult[i].theme, false);
    			m_adapter3.add(plist3[i]);
    		}
    		return;
    	}
    	
    	for( int i = 0 ; i < themelist.length ; i ++) {
//    		if(string.substring(0, 1).equals("?") || string.substring(0, 1).equals(".") || 
//    				string.substring(0, 1).equals("*") || string.substring(0, 1).equals("^") ||
//    				string.substring(0, 1).equals("(") || string.substring(0, 1).equals(")") ||
//    				string.substring(0, 1).equals("|") || string.substring(0, 1).equals("[") || 
//    				string.substring(0, 1).equals("]") || string.substring(0, 1).equals("{") || 
//    				string.substring(0, 1).equals("}"))
    		if(string.contains("?") || string.contains(".") || string.contains("+") || 
    				string.contains("*") || string.contains("^") ||
    				string.contains("(") || string.contains(")") ||
    				string.contains("|") || string.contains("[") || 
    				string.contains("]") || string.contains("{") || 
    				string.contains("}") || string.contains("\\"))
    			break;
			if(themelist[i].theme.getName().toUpperCase().matches(string.toUpperCase() + ".*")) {
				TempList[nMatchedCount] = themelist[i];
				nMatchedCount++;
			}
		}
		
		if(nMatchedCount != 0) {
			SearchResult = new ThemeList3[nMatchedCount];
			System.arraycopy(TempList, 0, SearchResult, 0, nMatchedCount);
		} else {
			SearchResult = null;
		}
//		mDialogText.setVisibility(View.GONE);
		
		RedrawList();
	}
    
	private void Draw() {
		// TODO Auto-generated method stub
		m_orders3 = new ArrayList<ThemeList3>();
		
		m_adapter3 = new ThemeList3Adapter(getApplicationContext(), R.layout.search_row, m_orders3);
		
		setListAdapter(m_adapter3);
		
		for(int i = 0 ; i < themelist.length ; i++)
			m_adapter3.add(themelist[i]);
		
		plist3 = themelist;
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        mReady = true;
    }

    
    @Override
    protected void onPause() {
    	imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    public void setSearchResult(ThemeList3[] result) {
//    	mDialogText.setVisibility(View.GONE);
    	setSearchList(result);
    }
    
    private void setSearchList(final ThemeList3[] result) {
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
    
    private void RedrawList(){
    	if(SearchResult != null && SearchResult.length > 0) {
	    	m_orders3 = new ArrayList<ThemeList3>();
	    	m_adapter3 = new ThemeList3Adapter(getApplicationContext(), R.layout.search_row, m_orders3);
			setListAdapter(m_adapter3);
			
			plist3 = SearchResult;
			for(int i = 0 ; i < SearchResult.length ; i++){
				m_adapter3.add(plist3[i]);
			}
    	} else {
    		mReady = false;
    		String[] noresult = new String[1];
    		noresult[0] = "검색결과가 없습니다.";
    		setListAdapter(new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, noresult));
    	}
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
    	//Log.i("myTag", "buttonview=[" + buttonView + "] isChecked=" + isChecked);
    }
   
    
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int lastItem = firstVisibleItem + visibleItemCount - 1;
        
        if (mReady) {
            String firstLetter = szSearchResult[firstVisibleItem].substring(0, 1);
            int unicode = HangulUtils.convertCharToUnicode(firstLetter.charAt(0)); 
            if(unicode >= HangulUtils.HANGUL_BASE_UNIT && unicode <= HangulUtils.HANGUL_END_UNICODE){
            	int tmp = (unicode - HangulUtils.HANGUL_BEGIN_UNICODE);
				int index = tmp / HangulUtils.HANGUL_BASE_UNIT;
				firstLetter = Character.toString(HangulUtils.INITIAL_SOUND[index]);
            }
            
//            if (!mShowing && !firstLetter.equals(mPrevLetter)) {
//                mShowing = true;
//                mDialogText.setVisibility(View.VISIBLE);
//            }
//            mDialogText.setText(firstLetter);
//            mHandler.removeCallbacks(mRemoveWindow);
//            mHandler.postDelayed(mRemoveWindow, 1000);
//            mPrevLetter = firstLetter;
        }
    }
    

    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
    
    
//    private void removeWindow() {
//        if (mShowing) {
//            mShowing = false;
//            mDialogText.setVisibility(View.INVISIBLE);
//        }
//    }

	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
        imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
        
		LinearLayout listview = null;
		try{
			listview = (LinearLayout) v;
		} catch (ClassCastException e) {
			return;
		}
		
		CheckedTextView ctv = (CheckedTextView)listview.getChildAt(0);
		
		ctv.setChecked(!ctv.isChecked());
		
		plist3[position].setisChecked(!plist3[position].getisChecked());
		
		if(checkSelectedList(plist3[position])) {
			if(saveSelectedLIst(plist3[position])> 100) {
				AlertDialog.Builder alert = null;
//		    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//		    		alert = new AlertDialog.Builder(SearchTheme.this, AlertDialog.THEME_HOLO_DARK);
//		    	else
		    		alert = new AlertDialog.Builder(SearchTheme.this);
				alert.setTitle( "테마추가" );
				alert.setIcon(R.drawable.ic_dialog_menu_generic);
				alert.setMessage("리스트가 100개를 초과했습니다.\n100개까지만 저장됩니다.");
		
				alert.setPositiveButton( "확인", new DialogInterface.OnClickListener() {
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
			AlertDialog.Builder alert = null;
//	    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//	    		alert = new AlertDialog.Builder(SearchTheme.this, AlertDialog.THEME_HOLO_DARK);
//	    	else
	    		alert = new AlertDialog.Builder(SearchTheme.this);
	    	alert.setTitle( "테마추가" );
	    	alert.setIcon(R.drawable.ic_dialog_menu_generic);
	    	alert.setMessage("선택한 테마가 이미 목록에 있습니다.");
	    	
	    	alert.setPositiveButton( "확인", new DialogInterface.OnClickListener() {
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
    
    public class ThemeList3Adapter extends ArrayAdapter<ThemeList3> {

    	private ArrayList<ThemeList3> items;

        public ThemeList3Adapter(Context context, int textViewResourceId, ArrayList<ThemeList3> items) {
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
                ThemeList3 p = items.get(position);
                if (p != null) {
                	String szTheme = p.theme.getName();
                	CheckedTextView tv = (CheckedTextView) v.findViewById(R.id.text);
                    tv.setChecked(p.getisChecked());
                    tv.setText(szTheme);
                }
                return v;
        }

    }
    
    class ThemeList3 {
    	private Theme theme;
    	private CheckBox checkbox;
    	
    	public ThemeList3(Theme themeinfo, boolean isChecked){
    		checkbox = new CheckBox(getBaseContext());
    		this.theme = themeinfo;
    		this.checkbox.setChecked(isChecked);
    	}
    	
    	public Theme getTheme(){
    		return theme;
    	}
    	
    	public boolean getisChecked(){
    		return checkbox.isChecked();
    	}
    	
    	public void setisChecked(boolean isChecked){
    		this.checkbox.setChecked(isChecked);
    	}
    }
}