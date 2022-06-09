package com.weathernews.Weather;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.AutoCompleteTextView;

public class MyAutoCompleteTextView extends AutoCompleteTextView {
	
	private static CityGroup citygroup = null;

	public MyAutoCompleteTextView(Context context) {
		super(context);
	}
	
	public MyAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setActivity(CityGroup act) {
		citygroup = act;
	}
	
	@Override
	public boolean dispatchKeyEventPreIme(KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
			if(citygroup != null)
				citygroup.onBackPressInEditView();
		}
		return super.dispatchKeyEventPreIme(event);
	}
}
