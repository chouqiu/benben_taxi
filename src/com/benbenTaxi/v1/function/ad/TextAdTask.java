package com.benbenTaxi.v1.function.ad;

import com.benbenTaxi.v1.function.DataPreference;
import com.benbenTaxi.v1.function.GetInfoTask;
import com.benbenTaxi.v1.function.background.BackgroundService;

import android.os.Handler;



public class TextAdTask extends GetInfoTask {
	private final static String	TAG					= TextAdTask.class.getName();
	public String mApiUrl							= "/api/v1/advertisements";
	private DataPreference mData					= null;
	private final static String mUA					= "ning@benbentaxi";
	private Handler	mH								= null;
	
	public TextAdTask(DataPreference data, Handler h)
	{
		mData				= data;
		mH					= h;
	}
	
	public void send() {
		String url =  "http://"+mData.LoadString("host")+mApiUrl;
		super.initCookies(mData.LoadString("token_key"), mData.LoadString("token_value"), "42.121.55.211");
		execute(url, mUA, GetInfoTask.TYPE_GET);
	}
	
	@Override
	protected void onPostExecGet(Boolean succ) {
		/*
		 * [{"content":"测试维幄通达，阳泉交通集团，加油！！！！！"},{"content":"北京维幄通达网络科技有限公司"}]
		 */
		String data = this.toString();
		if ( succ ) {			
			TextAds ads = new TextAds(data);
			mH.dispatchMessage(mH.obtainMessage(BackgroundService.MSG_TEXT_AD_RECV, ads));
			
		} else if ( mH != null ) {
			mH.dispatchMessage(mH.obtainMessage(BackgroundService.MSG_TEXT_AD_ERROR, _errmsg));
		}
	}

}
