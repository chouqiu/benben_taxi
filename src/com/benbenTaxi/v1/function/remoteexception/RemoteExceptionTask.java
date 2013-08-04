package com.benbentaxi.remoteexception;


import android.util.Log;

import com.benbentaxi.Configure;
import com.benbentaxi.api.PostTask;

public class RemoteExceptionTask extends PostTask{
	private final static String API						=	"/api/v1/client_exceptions";
	private final String TAG			     			= RemoteExceptionTask.class.getName();
	private Configure       mConfigure					=  null;
	private RemoteExceptionRequest	mExceptionRequest		= null;
	
	public RemoteExceptionTask(String exception)
	{
		mExceptionRequest		= new RemoteExceptionRequest(exception);
		mConfigure				= new Configure();
	}
	protected String getPostParams() {
		String str = mExceptionRequest.toJson().toString();
		Log.d(TAG,"get exception data " + str);
		return str;
	}

	protected String getApiUrl() {
		return "http://"+mConfigure.getService()+API;
	}
	protected void onPostExecute(Boolean succ) 
	{
		
		//Log.d(TAG,"the result is "+this.getResult() + "|"+succ);
	}

	public void go() 
	{
		execute();
		//boolean s = mJsonHttpRequest.post(getApiUrl(), getPostParams());
		//Log.d(TAG,"============|"+s);
		
	}
}
