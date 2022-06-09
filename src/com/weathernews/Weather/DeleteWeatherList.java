package com.weathernews.Weather;

import java.util.ArrayList;

import com.weathernews.Weather.SearchCity.CityList3;
import com.weathernews.Weather.SearchCity.CityList3Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class DeleteWeatherList extends ListActivity {
	private Button confirm;
	private Button cancel;
	private int nCityCnt;
	private List2 sortlist;
	private List2[] arrayList;
	private ArrayList<List2> data = new ArrayList<List2>();
	private SortListAdapter mAdapter;
	//private CheckedTextView selectAll;
	private LinearLayout selectAll;
	private CheckedTextView check;
	private AlertDialog.Builder alert = null;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.delweatherlist);
        confirm = (Button) findViewById(R.id.delweatherlist_confirm);
        cancel = (Button) findViewById(R.id.delweatherlist_cancel);

        check = (CheckedTextView) findViewById(R.id.delete_check);
        confirm.setEnabled(false);

        confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//delList();

//				SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();


				int selectCnt = getSelectedCount();
				//Log.d("myTag", "selectCnt=" + selectCnt);
				if (selectCnt > 0) {
//					for (int i = 0; i < nCityCnt; ++i)
//						prefs1.remove(Const.CITY_LIST + i);
//
//					String Temp;
//					for(int i = 0, j = 0 ; i < nCityCnt ; i ++) {
//						//Log.d("myTag", "checked=" + arrayList[i].getisChecked());
//						if(arrayList[i].getisChecked() == false){
//							prefs1.putString(Const.CITY_LIST + j, arrayList[i].getDataToString());
//							j++;
//						}
//					}
//					prefs1.putInt(Const.CITY_CNT, nCityCnt - selectCnt);
//					prefs1.commit();
//					setResult(RESULT_OK, (new Intent()).setAction(""));
//					finish();
					if(alert != null) return;
//			    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//			    		alert = new AlertDialog.Builder(DeleteWeatherList.this, AlertDialog.THEME_HOLO_DARK);
//			    	else
			    		alert = new AlertDialog.Builder(DeleteWeatherList.this);

				    alert.setTitle( "삭제" );
				    alert.setIcon(R.drawable.ic_dialog_menu_generic);
				    alert.setMessage("삭제하시겠습니까?");

				    alert.setPositiveButton( "예", new DialogInterface.OnClickListener() {
					    @Override
						public void onClick( DialogInterface dialog, int which) {
					    	int selectCnt = getSelectedCount();
					    	SharedPreferences.Editor prefs1 = getSharedPreferences(Const.PREFS_NAME, 0).edit();

					    	for (int i = 0; i < nCityCnt; ++i)
								prefs1.remove(Const.CITY_LIST + i);
	
							String Temp;
							for(int i = 0, j = 0 ; i < nCityCnt ; i ++) {
							//Log.d("myTag", "checked=" + arrayList[i].getisChecked());
								if(arrayList[i].getisChecked() == false){
									prefs1.putString(Const.CITY_LIST + j, arrayList[i].getDataToString());
									j++;
								}
							}
							prefs1.putInt(Const.CITY_CNT, nCityCnt - selectCnt);
							prefs1.commit();
					    	dialog.dismiss();
							setResult(RESULT_OK, (new Intent()).setAction(""));
							finish();
					    }
					});
				    alert.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
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
				} else {
					AlertDialog.Builder alert = null;
//			    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//			    		alert = new AlertDialog.Builder(DeleteWeatherList.this, AlertDialog.THEME_HOLO_DARK);
//			    	else
			    		alert = new AlertDialog.Builder(DeleteWeatherList.this);
				    alert.setTitle( "알림" );
				    alert.setIcon(R.drawable.ic_dialog_menu_generic);
				    alert.setMessage("선택한 목록이 없습니다.");

				    alert.setPositiveButton( "확인", new DialogInterface.OnClickListener() {
					    @Override
						public void onClick( DialogInterface dialog, int which) {
					    	dialog.dismiss();
					    }
					});
				    alert.show();
				}
			}
		});

        cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(RESULT_CANCELED, (new Intent()).setAction(""));
				finish();
			}
		});

        selectAll = (LinearLayout) findViewById(R.id.delete_select);
        selectAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try{
					check.setChecked(!check.isChecked());
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				for (int i = 0 ; i < nCityCnt ; i ++) {
					try {
						arrayList[i].setisChecked(check.isChecked());
					} catch (NullPointerException e) {
						e.printStackTrace();
					} catch (ArrayIndexOutOfBoundsException e1) {
						e1.printStackTrace();
					}
				}
				if(check.isChecked())
					confirm.setEnabled(true);
				else
					confirm.setEnabled(false);

				RedrawList();
			}
		});

		String Temp = null;
		String[] szTemp;
		SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);

		if(prefs == null){
			nCityCnt = 0;
		}

		nCityCnt = prefs.getInt(Const.CITY_CNT, 0);
		
		if(nCityCnt == 0) {
			selectAll.setClickable(false);
		}

		arrayList = new List2[nCityCnt];

		for(int i = 0 ; i < nCityCnt ; i++){
			Temp = prefs.getString(Const.CITY_LIST + i, "");
			szTemp = Temp.split("\t");
			if(szTemp.length >= 8) {
				sortlist = new List2(szTemp[0], szTemp[1], szTemp[2], szTemp[3], szTemp[4], szTemp[5], szTemp[6], szTemp[7], false);
				arrayList[i] = sortlist;
				data.add(sortlist);
			}
		}

        mAdapter = new SortListAdapter(getApplicationContext(), R.layout.search_row, data);
        setListAdapter(mAdapter);
    }

    protected int getSelectedCount() {
    	int nCnt = 0;
    	for (int i = 0 ; i < nCityCnt ; i ++)
	    if ((null != arrayList[i]) && arrayList[i].getisChecked())
		++nCnt;
	return nCnt;
    }

	private void RedrawList(){
		if(nCityCnt == 0) {
			selectAll.setClickable(false);
		}
    	data = new ArrayList<List2>();
    	mAdapter = new SortListAdapter(getApplicationContext(), R.layout.search_row, data);
		setListAdapter(mAdapter);

		for(int i = 0 ; i < nCityCnt ; i++){
			data.add(arrayList[i]);
		}
    }


	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		boolean isCheckAll = true;
		LinearLayout listview = null;
		try{
			listview = (LinearLayout) v;
		} catch (ClassCastException e) {
			return;
		}
		//Log.d("myTag", "position=" + position + " id=" + id);

		CheckedTextView ctv = (CheckedTextView)listview.getChildAt(0);

		if(ctv == null)
			return;
		ctv.setChecked(!ctv.isChecked());
		//Log.d("myTag", "SelectedText is " + ctv.getText());

		try {
			arrayList[position].setisChecked(!arrayList[position].getisChecked());
			//Log.d("myTag", "Select CityID=" + arrayList[position].getCityCode());
			for(int i = 0 ; i < arrayList.length ; i ++) {
				if(arrayList[i].getisChecked() == false){
					isCheckAll = false;
					break;
				}
	
			}
			check.setChecked(isCheckAll);
			int nSelected = getSelectedCount();
			if(nSelected > 0)
				confirm.setEnabled(true);
			else
				confirm.setEnabled(false);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
    }
	
	@Override
	public void onResume() {
		super.onResume();
//		try{
//			check.setChecked(false);
//		} catch (NullPointerException e) {
//			e.printStackTrace();
//		}
//		for (int i = 0 ; i < nCityCnt ; i ++) {
//			try {
//				arrayList[i].setisChecked(false);
//			} catch (NullPointerException e) {
//				e.printStackTrace();
//			} catch (ArrayIndexOutOfBoundsException e1) {
//				e1.printStackTrace();
//			}
//		}
//		confirm.setEnabled(false);
//
//		RedrawList();
	}

	class List2 {
		private CheckedTextView City;
    	private String szCode;
    	private String szCity;
    	private String szTimeZone;
    	private String szWeatherComment;
    	private String szIcon;
    	private String szTempCur;
    	private String szTempMax;
    	private String szTempMin;

    	public List2( String code, String CityName, String TimeZone, String szWC, String Icon,
    			String Tcur, String TMax, String TMin, boolean isChecked ){
    		this.szCode = code;
    		this.szCity = CityName;
    		this.szTimeZone = TimeZone;
    		this.szWeatherComment = szWC;
    		this.szIcon = Icon;
    		this.szTempCur = Tcur;
    		this.szTempMax = TMax;
    		this.szTempMin = TMin;
    		City = new CheckedTextView(getBaseContext());
    		this.City.setText(CityName);
    		this.City.setChecked(isChecked);
    	}

    	public String getCityCode(){
    		return szCode;
    	}

    	public String getCityName(){
    		return szCity;
    	}

    	public String getTimezone(){
    		return szTimeZone;
    	}

    	public String getComment(){
    		return szWeatherComment;
    	}

    	public String getTempCur(){
    		return szTempCur;
    	}

    	public String getIcon(){
    		return szIcon;
    	}

    	public String getTempMax(){
    		return szTempMax;
    	}

    	public String getTempMin(){
    		return szTempMin;
    	}

    	public boolean getisChecked(){
    		return City.isChecked();
    	}

    	public void setisChecked(boolean isChecked){
    		City.setChecked(isChecked);
    	}

    	public String getDataToString() {
    		return szCode + "\t" + szCity + "\t" + szTimeZone + "\t" + szWeatherComment + "\t" + szIcon + "\t" + szTempCur + "\t" + szTempMax + "\t" + szTempMin;
    	}
    }

	public class SortListAdapter extends ArrayAdapter<List2> {
		private ArrayList<List2> items;

    	public SortListAdapter(Context context, int textViewResourceId,
				ArrayList<List2> objects) {
			super(context, textViewResourceId, objects);
			this.items = objects;
		}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.search_row, null);
                }
                List2 p = items.get(position);
                if (p != null) {
                	String szCity = p.getCityName();
                    CheckedTextView tv = (CheckedTextView) v.findViewById(R.id.text);
                    tv.setText(szCity);
                    tv.setChecked(p.getisChecked());
                }
                return v;
        }
    }
}
