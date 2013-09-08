package com.benbenTaxi.v1.function.index;



import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.benbenTaxi.v1.BenbenApplication;
import com.benbenTaxi.v1.function.Configure;
import com.benbenTaxi.v1.function.DataPreference;
import com.benbenTaxi.v1.function.GetInfoTask;


public class TaxiRequestIndexTask extends GetInfoTask{
	private final String API1 						="/api/v1/taxi_requests";
	private final String TAG			     		= TaxiRequestIndexTask.class.getName();

	private DataPreference mSession 				= null;
	private BenbenApplication mApp 					= null;
	private Handler mHandler						= null;

	private TaxiRequestIndexTask(Context context,BenbenApplication app)
	{	
		this.mApp = app;
		this.mSession 	  = mApp.getSession();
		
		if (this.mSession != null){
			super.initCookies(mSession.getTokenKey(), mSession.getTokenValue(),Configure.getHost());
			Log.d(TAG,mSession.getTokenKey()+":"+mSession.getTokenValue());
		}else{
			Log.e(TAG,"Session 获取出错!");
		}
	}
	
	public TaxiRequestIndexTask(Context context,BenbenApplication app,Handler handler)
	{
		this(context,app);
		mHandler = handler;
	}
	
		
	public void go()
	{
		mHandler.sendMessage(mHandler.obtainMessage(TaxiRequestIndexActivity.MSG_HANDLE_INDEX_TASK_START));
		super.executeGET();
	}
	
	protected String getApiUrl()
	{
		return "http://"+Configure.getService()+API1;
	}

	@Override
	protected void onPostExecGet(Boolean succ) {
		// TODO Auto-generated method stub
		TaxiRequestIndexResponse taxiRequestIndexResponse = new TaxiRequestIndexResponse(this.toString());
		if (!succ){
			taxiRequestIndexResponse.setSysErrorMessage(this.getErrorMsg());
			mHandler.sendMessage(mHandler.obtainMessage(TaxiRequestIndexActivity.MSG_HANDLE_INDEX_TASK_ERROR,this.getErrorMsg()));
		}
		if (!taxiRequestIndexResponse.hasError()){	
			mHandler.sendMessage(mHandler.obtainMessage(TaxiRequestIndexActivity.MSG_HANDLE_INDEX_TASK_SUCCESS,taxiRequestIndexResponse));
		}else{
			mHandler.sendMessage(mHandler.obtainMessage(TaxiRequestIndexActivity.MSG_HANDLE_INDEX_TASK_ERROR,"获取历史打车列表失败，访问服务器出错！"));
		}
	}

	@Override
	protected void onPostExecPost(Boolean succ) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onPostExecError(String type, int code) {
		// TODO Auto-generated method stub
		
	}
	
    
}
