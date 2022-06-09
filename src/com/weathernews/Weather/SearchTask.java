package com.weathernews.Weather;

import com.weathernews.Weather.SearchCity.CityList3;


public class SearchTask implements Runnable {
	private String Query;
	private final SearchCity search;
	private CityList3[] CityNameList;
	private boolean IsKorean = false;
	
	SearchTask(SearchCity search, String query) {
		this.search = search;
		this.Query = query;
		CityNameList = search.getCityNameList();
	}
	
	public void run() {
		CityList3[] SearchResult;
		CityList3[] TempList = new CityList3[CityNameList.length];
		int nMatchedCount = 0;
		
		int nStart, nEnd;
		
		IsKorean = HangulUtils.isQueryKorean(Query);
    	
		if(IsKorean) {
			nStart = 0;
			nEnd = 163;
		} else {
			nStart = 160;
			nEnd = CityNameList.length;
		}
    	//Log.d("myTag", "[" + Query + "]");
    	if( Query == null || Query.length() == 0 ){
    		search.setSearchResult(CityNameList);
    		return;
    	}
    	
		for( int i = nStart ; i < nEnd ; i ++) {
			if(CityNameList[i].getCityName().toUpperCase().matches(Query.toUpperCase() + ".*")){
				//Log.d("myTag", "CityName = " + CityNameList[i].getCityName());
				TempList[nMatchedCount] = CityNameList[i];
				nMatchedCount++;
			}
		}
		
		if(TempList.length != 0){
			SearchResult = new CityList3[nMatchedCount];
			System.arraycopy(TempList, 0, SearchResult, 0, nMatchedCount);
			//Log.d("myTag", "MatchCount = " + SearchResult.length);
		} else{
			SearchResult = null;
		}
		
		search.setSearchResult(SearchResult);
	}

	
}
