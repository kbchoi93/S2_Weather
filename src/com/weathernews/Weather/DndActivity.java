package com.weathernews.Weather;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weathernews.Weather.DndListView;

public class DndActivity extends ListActivity implements DndListView.DragListener, DndListView.DropListener {
    private ArrayList<CityListValue> data = new ArrayList<CityListValue>();
    private DndListView lstInfoType;
    private boolean isDnd = false;
    private SortListAdapter mAdapter;
    private FrameLayout fram;
    private LinearLayout btnarea;
    private Button confirm;
    private Button cancel;
    private TextView dndtitle;
    private int nCityCnt;
    private CityListValue sortlist;

    class CityListValue {
    	private String szCode;
    	private String szCity;
    	private String szTimeZone;
    	private String szWeatherComment;
    	private String szIcon;
    	private String szTempCur;
    	private String szTempMax;
    	private String szTempMin;

    	public CityListValue(String code, String CityName, String TimeZone, String szWC, String Icon,
			     String Tcur, String TMax, String TMin) {
	    szCode = code;
	    szCity = CityName;
	    szTimeZone = TimeZone;
	    szWeatherComment = szWC;
	    szIcon = Icon;
	    szTempCur = Tcur;
	    szTempMax = TMax;
	    szTempMin = TMin;
    	}

    	public String getCityCode() { return szCode; }
    	public String getCityName() { return szCity; }
    	public String getTimezone() { return szTimeZone; }
    	public String getComment()  { return szWeatherComment; }
    	public String getTempCur()  { return szTempCur; }
   	public String getIcon()     { return szIcon; }
    	public String getTempMax()  { return szTempMax; }
    	public String getTempMin()  { return szTempMin; }

    	public String getDataToString() {
	    return szCode + "\t" + szCity + "\t" + szTimeZone + "\t" + szWeatherComment + "\t" + szIcon + "\t" + szTempCur + "\t" + szTempMax + "\t" + szTempMin;
    	}
    }


    public class SortListAdapter extends ArrayAdapter<CityListValue> {
	private ArrayList<CityListValue> items;

    	public SortListAdapter(Context context, int textViewResourceId,
			       ArrayList<CityListValue> objects) {
	    super(context, textViewResourceId, objects);
	    items = objects;
	}

        @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    View v = convertView;
	    if (null == v)
		v = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dragndrop_row, null);
	    CityListValue p = items.get(position);
	    if (null != p) {
		String szCity = p.getCityName();
		TextView tv = (TextView) v.findViewById(R.id.drag_text);
		tv.setText(szCity);
	    }

	    return v;
        }
    }
    
    final Handler toasthandler = new Handler() {
	    int msgid;
	    public void handleMessage(Message msg) {
        	msgid = msg.what;

        	if (msgid == 0) {
		    Toast.makeText(DndActivity.this,
				   "순서가 변경되었습니다",
				   Toast.LENGTH_SHORT).show();
        	}
	    }
	};


    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dragndrop);
        lstInfoType = (DndListView) findViewById(android.R.id.list);
        btnarea = (LinearLayout) findViewById(R.id.button_area);
        dndtitle = (TextView) findViewById(R.id.dndtitle);
        
        confirm = (Button) findViewById(R.id.dragndrop_confirm);
        confirm.setEnabled(false);
        cancel = (Button) findViewById(R.id.dragndrop_cancel);
        
        new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				DisplayMetrics displaymetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
				LinearLayout ll = (LinearLayout)findViewById(R.id.dndlayout);
				lstInfoType.setBoundary(lstInfoType.getWidth(), displaymetrics.heightPixels - ll.getHeight() + dndtitle.getHeight(), lstInfoType.getHeight());
			}
		}, 400);

        confirm.setOnClickListener(new OnClickListener() {

		@Override
		    public void onClick(View v) {
		    // TODO Auto-generated method stub
		    saveList();
		    toasthandler.sendEmptyMessage(0);
		    setResult(RESULT_OK, (new Intent()).setAction(""));
		    finish();
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

	//        mAdapter = new ArrayAdapter<String>(this,
	//                android.R.layout.simple_list_item_1, data);
	String Temp = null;
	String[] szTemp;
	SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, 0);

	if (null == prefs) nCityCnt = 0;

	nCityCnt = prefs.getInt(Const.CITY_CNT, 0);
	for(int i = 0 ; i < nCityCnt ; i++){
	    Temp = prefs.getString(Const.CITY_LIST + i, "");
	    szTemp = Temp.split("\t");
	    if(szTemp.length >= 8) {
	    	sortlist = new CityListValue(szTemp[0], szTemp[1], szTemp[2], szTemp[3], szTemp[4], szTemp[5], szTemp[6], szTemp[7]);
	    	data.add(sortlist);
	    }
	}

        mAdapter = new SortListAdapter(getApplicationContext(), R.layout.dragndrop_row, data);
        setListAdapter(mAdapter);
        if(nCityCnt > 1) {
        lstInfoType.setDragListener(this);
	lstInfoType.setDropListener(this);
        }
    }

    CityListValue dragged;
    public void drag(int which) {
	isDnd = true;
	if(which > data.size() || which < 0) {
		isDnd = false;
		return;
	}
	dragged = data.remove(which);
	mAdapter.notifyDataSetChanged();
    }

    public void drop(int from, int to) {
    	if(!confirm.isEnabled() && data.size() > 0 && from != to) confirm.setEnabled(true);
	if (isDnd) {
	    data.add(to, dragged);
	    isDnd = false;
	    mAdapter.notifyDataSetChanged();
	    lstInfoType.Pause();
	}
    }

    private void saveList(){
	SharedPreferences.Editor prefs = getSharedPreferences(Const.PREFS_NAME, 0).edit();
	for(int i = 0 ; i < data.size() ; i++){
	    prefs.remove(Const.CITY_LIST + i);
	    prefs.putString(Const.CITY_LIST + i, data.get(i).getDataToString());
	}
	prefs.commit();
    }
    
    protected void onPause() {
    	lstInfoType.Pause();
		super.onPause();
	}
}
