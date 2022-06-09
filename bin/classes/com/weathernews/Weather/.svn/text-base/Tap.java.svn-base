package com.weathernews.Weather;

import android.graphics.Point;

public class Tap {
	private Point posDown = new Point();
	private Point posUp = new Point();
	
	public Tap(){
		clear();
	}
	
	public void clear(){
		posDown.set(0, 0);
		posUp.set(0, 0);
	}
	
	public boolean Down(int x, int y){
		posDown.x = posUp.x = x;
		posDown.y = posUp.y = y;
		
		return true;
	}
	
	public boolean Up(int x, int y){
		posUp.x = x;
		posUp.y = y;
		
		if(posDown.x == posUp.x && posDown.y == posUp.y)
			return true;
		else
			return false;
	}
}