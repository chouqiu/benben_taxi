package com.benbenTaxi.v1.function;

import android.content.Context;
import android.content.SharedPreferences;


public class DataPreference {
	private static final String PREFS_NAME = "benben_taxi_pref";
	private Context mCon;
	
	public DataPreference(Context con) {
		mCon = con;
	}
	
	public String LoadString(String key) {
		SharedPreferences settings = mCon.getSharedPreferences(PREFS_NAME, 0);  
		return settings.getString(key, "");
	}
	
	public int LoadInt(String key) {
		SharedPreferences settings = mCon.getSharedPreferences(PREFS_NAME, 0);  
		return settings.getInt(key, 0);
	}
	
	public double LoadDouble(String key) {
		SharedPreferences settings = mCon.getSharedPreferences(PREFS_NAME, 0);  
		return settings.getFloat(key, 0.0f);
	}
	
	public boolean LoadBool(String key) {
		SharedPreferences settings = mCon.getSharedPreferences(PREFS_NAME, 0);  
		return settings.getBoolean(key, true);
	}
	
	public boolean SaveData(String key, String val) {
		// 保存账号信息
		try {
			SharedPreferences settings = mCon.getSharedPreferences(PREFS_NAME, 0);  
			SharedPreferences.Editor editor = settings.edit();  
			editor.putString(key, val);
			
			// Don't forget to commit your edits!!!  
			editor.commit();
			return true;
		} catch (Exception e) {
			// 处理异常
			return false;
		}
	}
	
	public boolean SaveData(String key, int val) {
		// 保存账号信息
		try {
			SharedPreferences settings = mCon.getSharedPreferences(PREFS_NAME, 0);  
			SharedPreferences.Editor editor = settings.edit();  
			editor.putInt(key, val);
			
			// Don't forget to commit your edits!!!  
			editor.commit();
			return true;
		} catch (Exception e) {
			// 处理异常
			return false;
		}
	}
	
	public boolean SaveData(String key, float val) {
		// 保存账号信息
		try {
			SharedPreferences settings = mCon.getSharedPreferences(PREFS_NAME, 0);  
			SharedPreferences.Editor editor = settings.edit();  
			editor.putFloat(key, val);
			
			// Don't forget to commit your edits!!!  
			editor.commit();
			return true;
		} catch (Exception e) {
			// 处理异常
			return false;
		}
	}
	
	public boolean SaveData(String key, boolean val) {
		// 保存账号信息
		try {
			SharedPreferences settings = mCon.getSharedPreferences(PREFS_NAME, 0);  
			SharedPreferences.Editor editor = settings.edit();  
			editor.putBoolean(key, val);
			
			// Don't forget to commit your edits!!!  
			editor.commit();
			return true;
		} catch (Exception e) {
			// 处理异常
			return false;
		}
	}
}

