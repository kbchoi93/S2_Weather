package com.weathernews.Weather;

import com.weathernews.Weather.SearchTheme.ThemeList3;


public class SearchTaskTheme implements Runnable {
	private String Query;
	private final SearchTheme search;
	private ThemeList3[] CityNameList;
	private boolean IsKorean = false;
	
	SearchTaskTheme(SearchTheme search, String query) {
		this.search = search;
		this.Query = query;
		CityNameList = search.getThemeNameList();
	}
	
	public void run() {
		ThemeList3[] SearchResult;
		ThemeList3[] TempList = new ThemeList3[CityNameList.length];
		int nMatchedCount = 0;
		
		int nStart, nEnd;
		
		IsKorean = HangulUtils.isQueryKorean(Query);
    	
    	//Log.d("myTag", "[" + Query + "]");
    	if( Query == null || Query.length() == 0 ){
    		search.setSearchResult(CityNameList);
    		return;
    	}
    	
		for( int i = 0 ; i < CityNameList.length ; i ++) {
			if(CityNameList[i].getTheme().getName().matches(Query + ".*")){
				//Log.d("myTag", "CityName = " + CityNameList[i].getTheme().getName());
				TempList[nMatchedCount] = CityNameList[i];
				nMatchedCount++;
			}
		}
		
		if(TempList.length != 0){
			SearchResult = new ThemeList3[nMatchedCount];
			System.arraycopy(TempList, 0, SearchResult, 0, nMatchedCount);
			//Log.d("myTag", "MatchCount = " + SearchResult.length);
		} else{
			SearchResult = null;
		}
		
		search.setSearchResult(SearchResult);
	}

	
}
