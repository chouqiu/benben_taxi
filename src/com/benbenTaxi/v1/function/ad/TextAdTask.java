package com.benbenTaxi.v1.function.ad;

import android.util.Log;

import com.benbentaxi.Configure;
import com.benbentaxi.Session;
import com.benbentaxi.api.JsonHttpRequest;
import com.benbentaxi.passenger.location.DemoApplication;

public class TextAdTask {
	private final static String	TAG					= TextAdTask.class.getName();
	public String mApiUrl							= "/api/v1/advertisements";
	public Configure  mConfigure					= null;
	private DemoApplication mApp 					= null;
	private Session			mSession 				= null;
	
	private JsonHttpRequest mJsonHttpRequest		= null;
	
	public TextAdTask(DemoApplication app)
	{
		mApp				=	app;
		mSession 	  		= 	mApp.getCurrentSession();
		mConfigure 			= new Configure();
	}
	
	public TextAds send() {
		mJsonHttpRequest 	= new JsonHttpRequest();
		setCookie();
		boolean succ 	 	= mJsonHttpRequest.get(getApiUrl());
		TextAds textAds 	= new TextAds(mJsonHttpRequest.getResult());
		if (!succ){
			textAds.setSysErrorMessage(mJsonHttpRequest.getErrorMsg());
		}
		if (!textAds.hasError()){
				Log.d(TAG,mJsonHttpRequest.getResult());
				return textAds;
		}else{
				//閿欒宸茬粡鍦╤asError涓鐞嗚繃浜嗭紝鎵�互杩欓噷涓嶅啀澶勭悊
		}
		return null;
	}
	private void setCookie()
	{
		if (this.mSession != null){
			mJsonHttpRequest.setCookie(mSession.getTokenKey(), mSession.getTokenVal(),mConfigure.getHost());
		}else{
			Log.e(TAG,"Session 鑾峰彇鍑洪敊!");
		}
	}
	protected String getApiUrl() {
		return "http://"+mConfigure.getService()+mApiUrl;
	}

}
