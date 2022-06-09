package com.weathernews.Weather;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.weathernews.Weather.SearchTheme.ThemeList3;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;


public class ThemeGroup extends ExpandableListActivity {

    private static final String LOG_TAG = "myTag";
    private List Parent;
    private List Child;

    private AutoCompleteTextView edittext;
	private TextWatcher watcher;
	private ImageButton imgbtn;
	private Button btn;
	private Button cancel;
	private ExpandableListView expanded;
//	private boolean isShowedKeypad = false;

    static final String ThemeName[] = {
	  "골프장",
	  "스키장",
	  "산",
	  "야구장",
	  "테마파크",
	  "해수욕장"
	};

    private ArrayList<Theme> selectedarray = new ArrayList<Theme>();
    //private String[] AutoComp;

    private static final int GOLFCNT = ThemeClass.Theme_Golf.length;
	private static final int SKICNT = ThemeClass.Theme_Ski.length;
	private static final int MOUNTAINCNT = ThemeClass.Theme_Mountain.length;
	private static final int BASEBALLCNT = ThemeClass.Theme_Baseball.length;
	private static final int THEMEPARKCNT = ThemeClass.Theme_ThemPark.length;
	private static final int BEACHCNT = ThemeClass.Theme_Beach.length;

	private static final int [] ListCnt = {GOLFCNT, SKICNT, MOUNTAINCNT, BASEBALLCNT, THEMEPARKCNT, BEACHCNT };
	private boolean isShowInputMethod = true;
	
	private InputMethodManager imm;
	
	AlertDialog.Builder alert = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.searchgroup);

		makeAutoComp();
		
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		expanded = getExpandableListView();

		expanded.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
//				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//				isShowedKeypad = false;
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
			}
		});
		Parent = createGroupList();
		Child = createChildList();

		Drawable icon =
			this.getResources().getDrawable(R.drawable.expander_group);

		expanded.setGroupIndicator(icon);

		ThemeExpandableListAdapter expListAdapter =
			new ThemeExpandableListAdapter(
				getApplicationContext(),
				Parent,	// groupData describes the first-level entries
				R.layout.themegroup_row,	// Layout for the first-level entries
				new String[] { "ThemeName" },	// Key in the groupData maps to display
				new int[] { R.id.childname },		// Data under "colorName" key goes into this TextView
				Child,	// childData describes second-level entries
				R.layout.themechild_row,	// Layout for second-level entries
				new String[] { "Name", "Address", "Checked" },	// Keys in childData maps to display
				new int[] { R.id.childname, R.id.childaddress, R.id.check1 }	// Data under the keys above go into these TextViewse TextViews
			);
		setListAdapter( expListAdapter );

		icon.setCallback(null);

		imgbtn = (ImageButton) findViewById(R.id.searchbtn);


		imgbtn.setImageResource(R.drawable.ic_btn_search);
        imgbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String Query = edittext.getText().toString().trim();
				if(Query.length() > 0) {
					//Log.d("myTag", "SearchBtn Pressed");
//			    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			    	imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//			    	isShowedKeypad = false;
					Intent intent = new Intent(ThemeGroup.this, SearchTheme.class);
					intent.putExtra("Query", Query);
					startActivityForResult(intent, 1);
				}
			}
		});

        btn = (Button) findViewById(R.id.search_confirm);
        btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(getSelectedList()) {
					if(checkSelectedList()) {
					    if(saveSelectedList() > 100) {
					    	
							AlertDialog.Builder alert = null;
//					    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//					    		alert = new AlertDialog.Builder(ThemeGroup.this, AlertDialog.THEME_HOLO_DARK);
//					    	else
					    		alert = new AlertDialog.Builder(ThemeGroup.this);

						    alert.setTitle( "테마추가" );
						    alert.setIcon(R.drawable.ic_dialog_menu_generic);
						    alert.setMessage("리스트가 100개를 초과했습니다.\n100개까지만 저장됩니다.");
						    alert.setPositiveButton( "확인", new DialogInterface.OnClickListener() {
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
//				    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//				    		alert = new AlertDialog.Builder(ThemeGroup.this, AlertDialog.THEME_HOLO_DARK);
//				    	else
				    		alert = new AlertDialog.Builder(ThemeGroup.this);
				    	alert.setTitle( "테마추가" );
				    	alert.setIcon(R.drawable.ic_dialog_menu_generic);
				    	alert.setMessage("선택한 테마 중 일부가 이미 목록에 있습니다.");

				    	alert.setPositiveButton( "확인", new DialogInterface.OnClickListener() {
				    		@Override
				    	    public void onClick( DialogInterface dialog, int which) {
				    			saveNewSelectedLIst();
				    			setResult(RESULT_OK, (new Intent()).setAction("REDRAW"));
								finish();
				    	        //dialog.dismiss();
				    	    }
				    	});

				    	alert.show();
					}
				} else {
					if(alert != null) return;
//			    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//			    		alert = new AlertDialog.Builder(ThemeGroup.this, AlertDialog.THEME_HOLO_DARK);
//			    	else
			    		alert = new AlertDialog.Builder(ThemeGroup.this);

				    alert.setTitle( "알림" );
				    alert.setIcon(R.drawable.ic_dialog_menu_generic);
				    alert.setMessage("선택한 목록이 없습니다.");

				    alert.setPositiveButton( "확인", new DialogInterface.OnClickListener() {
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
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(RESULT_CANCELED, (new Intent()).setAction(""));
				finish();
			}
		});

        edittext.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    		    v.setFocusableInTouchMode(true);
    		    v.requestFocus();
    		    v.setOnClickListener(null);
//    			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//    			imm.showSoftInput(edittext, InputMethodManager.SHOW_FORCED);

    			isShowInputMethod = true;
    		    new Handler().postDelayed(new Runnable() { public void run() {
//    			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    			imm.showSoftInput(edittext, 0);
//    			isShowedKeypad = true;
    			isShowInputMethod = true;
    		    } }, 100);
    		}
    	    });
        edittext.setOnItemClickListener(new OnItemClickListener() {
    		@Override
    		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    		    String Query = edittext.getText().toString().trim();
    		    if (Query.length() > 0) {
    			//Log.d("myTag", "SearchBtn Pressed");
//    		    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			    	imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//			    	isShowedKeypad = false;
    			Intent intent = new Intent(ThemeGroup.this, SearchTheme.class);
    			intent.putExtra("Query", Query);
    			startActivityForResult(intent, 1);
    		    }
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
//        		    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
   				    	imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//   				    	isShowedKeypad = false;
	        			Intent intent = new Intent(ThemeGroup.this, SearchTheme.class);
	        			intent.putExtra("Query", Query);
	        			startActivityForResult(intent, 1);
        		    }
                }

    			return false;
    		}
    	});
    }

    protected int saveSelectedList() {
	// TODO Auto-generated method stub
	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
	int nThemeCnt = 0;
	String Temp = "";
	String[] szTemp;

	nThemeCnt = prefs.getInt(Const.THEME_CNT, 0);

	SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
	for(int i = 0, j = nThemeCnt ; i < selectedarray.size() && j < 100 ; i++ , j++){
		Temp = selectedarray.get(i).getThemeCode() + "\t" + selectedarray.get(i).getAreaCode() + "\t" + selectedarray.get(i).getName() + "\t" +
		selectedarray.get(i).getAddress() + "\t" + "--" + "\t" + Integer.toString(R.drawable.weather_icon_01_ref) + "\t" +
				"--" + "\t" + "--" + "\t" + "--" + "\t" + Integer.toString(selectedarray.get(i).getThemeID());
	    int nIdx = i + nThemeCnt;
	    prefs1.putString(Const.THEME_LIST + Integer.toString(nIdx), Temp);
	    Temp = "";
	}
	int listCnt = nThemeCnt + selectedarray.size();
	prefs1.putInt(Const.THEME_CNT, listCnt > 100 ? 100 : listCnt);
	prefs1.commit();
	return nThemeCnt + selectedarray.size();
    }

    public void makeAutoComp(){
		String[] AutoComp = new String[GOLFCNT + SKICNT + MOUNTAINCNT + BASEBALLCNT + THEMEPARKCNT + BEACHCNT];
		int nListCnt = 0;
		//AutoComp = new String[GOLFCNT + SKICNT];
		for (int i = 0; i < ThemeName.length; ++i) {
		//for (int i = 0; i < 1 ; ++i) {
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
	}

	protected void saveSelectedLIst() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);
		int nThemeCnt = 0;
		String Temp = "";
		String[] szTemp;

		nThemeCnt = prefs.getInt(Const.THEME_CNT, 0);

		//Log.d("myTag", "nThemeCnt=" + nThemeCnt);

		SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();
		for(int i = 0 ; i < selectedarray.size() ; i++){
			Temp = selectedarray.get(i).getThemeCode() + "\t" + selectedarray.get(i).getAreaCode() + "\t" + selectedarray.get(i).getName() + "\t" +
			selectedarray.get(i).getAddress() + "\t" + "--" + "\t" + Integer.toString(R.drawable.weather_icon_01_ref) + "\t" +
					"--" + "\t" + "--" + "\t" + "--" + "\t" + Integer.toString(selectedarray.get(i).getThemeID());
			int nIdx = i + nThemeCnt;
			prefs1.putString(Const.THEME_LIST + Integer.toString(nIdx), Temp);
			//Log.d("myTag", Const.THEME_LIST + Integer.toString(nIdx) + Temp);
			Temp = "";
		}
		prefs1.putInt(Const.THEME_CNT, nThemeCnt + selectedarray.size());
		prefs1.commit();
	}

	protected int saveNewSelectedLIst() {
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
		int nAddedCnt = 0;
		boolean isAdded = false;

		for(int j = 0 ; j < selectedarray.size() && k + nThemeCnt < 100 ; j++){
		    isAdded = false;
		    for(int i = 0 ; i < nThemeCnt ; i++){
		    	if ( ThemeID[i][0].equals(selectedarray.get(j).getThemeCode()) ) {
		    		isAdded = true;
		    		nAddedCnt++;
		    		break;
		    	}
		    }


			if(!isAdded){
				Temp = selectedarray.get(j).getThemeCode() + "\t" + selectedarray.get(j).getAreaCode() + "\t" + selectedarray.get(j).getName() + "\t" +
						selectedarray.get(j).getAddress() + "\t" + "--" + "\t" + Integer.toString(R.drawable.weather_icon_01_ref) + "\t" +
						"--" + "\t" + "--" + "\t" + "--" + "\t" + Integer.toString(selectedarray.get(j).getThemeID());

				int nIdx = k + nThemeCnt;
				prefs1.putString(Const.THEME_LIST + Integer.toString(nIdx), Temp);
				//Log.d("myTag", "Add New List=" + Temp);
				k ++;
			}
		}

		int listCnt = nThemeCnt + selectedarray.size() - nAddedCnt;
		prefs1.putInt(Const.CITY_CNT, listCnt > 100 ? 100 : listCnt);
		prefs1.commit();
		
		if(listCnt > 100) {
			AlertDialog.Builder alert1 = null;
//	    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//	    		alert1 = new AlertDialog.Builder(ThemeGroup.this, AlertDialog.THEME_HOLO_DARK);
//	    	else
	    		alert1 = new AlertDialog.Builder(ThemeGroup.this);
		    alert1.setTitle( "테마추가" );
		    alert1.setIcon(R.drawable.ic_dialog_menu_generic);
		    alert1.setMessage("리스트가 100개를 초과했습니다.\n100개까지만 저장됩니다.");
		    alert1.setPositiveButton( "확인", new DialogInterface.OnClickListener() {
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
			for(int j = 0 ; j < selectedarray.size() ; j++){
				if( ThemeID[i][0].equals(selectedarray.get(j).getThemeCode()) ) {
					//Log.d("myTag", selectedarray.get(j).getThemeCode() + " is being in the ThemeList!!");
					return false;
				}
			}
		}

		return true;
	}

	protected boolean getSelectedList() {
		// TODO Auto-generated method stub

		for(int i = 0 ; i < ThemeName.length; i++){
			for(int j = 0; j < ListCnt[i] ; j++){
				HashMap tempmap = (HashMap)(((ArrayList)Child.get(i)).get(j));
				boolean isChecked = (Boolean)tempmap.get("Checked");
				if(isChecked) {
					switch(i) {
					case Theme.THEME_GOLF:
						selectedarray.add(ThemeClass.Theme_Golf[j]);
						break;
					case Theme.THEME_SKI:
						selectedarray.add(ThemeClass.Theme_Ski[j]);
						break;
					case Theme.THEME_MOUNTAIN:
						selectedarray.add(ThemeClass.Theme_Mountain[j]);
						break;
					case Theme.THEME_BASEBALL:
						selectedarray.add(ThemeClass.Theme_Baseball[j]);
						break;
					case Theme.THEME_THEMEPARK:
						selectedarray.add(ThemeClass.Theme_ThemPark[j]);
						break;
					case Theme.THEME_BEACH:
						selectedarray.add(ThemeClass.Theme_Beach[j]);
						break;

					default:
						break;
					}
				}
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
		CheckBox cv;

		LinearLayout lv = (LinearLayout) v;

		cv = (CheckBox)((LinearLayout)lv.getChildAt(1)).getChildAt(0);
		cv.setChecked(!cv.isChecked());

		isChecked = cv.isChecked();

//		ImageView iv = (ImageView)((LinearLayout)lv.getChildAt(1)).getChildAt(1);
//		if(isChecked)
//			iv.setImageResource(R.drawable.btn_check_on);
//		else
//			iv.setImageResource(R.drawable.btn_check_off);

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
        //Log.d( LOG_TAG,"onGroupExpand: "+groupPosition );
        imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//        isShowedKeypad = false;
    }

/**
  * Creates the group list out of the colors[] array according to
  * the structure required by SimpleExpandableListAdapter. The resulting
  * List contains Maps. Each Map contains one entry with key "colorName" and
  * value of an entry in the colors[] array.
  */
	private List createGroupList() {
		ArrayList result = new ArrayList();
		for (int i = 0; i < ThemeName.length; ++i) {
			HashMap m = new HashMap();
			m.put("ThemeName", ThemeName[i]);
			result.add(m);
		}
		return (List) result;
	}

/**
  * Creates the child list out of the shades[] array according to the
  * structure required by SimpleExpandableListAdapter. The resulting List
  * contains one list for each group. Each such second-level group contains
  * Maps. Each such Map contains two keys: "shadeName" is the name of the
  * shade and "rgb" is the RGB value for the shade.
  */
	private List createChildList() {
		ArrayList result = new ArrayList();
		for (int i = 0; i < ThemeName.length; ++i) {
			// Second-level lists
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

			ArrayList secList = new ArrayList();
			for (int n = 0; n < themearray.length; n++) {
				HashMap child = new HashMap();
				child.put("Name", themearray[n].getName());
				child.put("Address", themearray[n].getAddress());
				child.put("Checked", false);
				secList.add(child);
			}
			result.add(secList);
		}
		return result;
	}

	@Override
    protected void onActivityResult(int requestCode, int result, Intent data) {
	    if (RESULT_OK == result){
	    	setResult(RESULT_OK, (new Intent()).setAction(""));
	    	finish();
	    } else if(RESULT_CANCELED == result) {
//	    	setResult(RESULT_CANCELED, (new Intent()).setAction(""));
//	    	finish();
//	    } else {
//	    	edittext.setText("");
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

	@Override
    protected void onPause() {
    	if(imm.isActive()) {
//    		isShowedKeypad = true;
    		imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
    	}
        super.onPause();
    }
}