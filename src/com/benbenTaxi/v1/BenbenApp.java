package com.benbenTaxi.v1;

import org.json.JSONObject;

import android.app.Application;

public class BenbenApp extends Application {
	private String[] mCurrentInfo = new String[5];
	private JSONObject mCurrentObj = new JSONObject();
	
	public String[] getCurrentInfo() {
		return mCurrentInfo;
	}
	
	public JSONObject getCurrentObject() {
		return mCurrentObj;
	}
	
	public void setCurrentInfo(String[] info) {
		mCurrentInfo = info;
	}
	
	public void setCurrentObject(JSONObject obj) {
		mCurrentObj = obj;
	}
}
