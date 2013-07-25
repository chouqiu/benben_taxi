package com.benbenTaxi.v1.function.background;


import android.content.ComponentName;
import android.content.ServiceConnection;

public abstract class BackgroundServiceConnection implements ServiceConnection{
	protected BackgroundService mBackgroundService					=	null;
	protected boolean			  mIBound							= 	false;
	

	@Override
	public void onServiceDisconnected(ComponentName name) {
		mIBound = false;
	}
	
	public BackgroundService getService()
	{
		return mBackgroundService;
	}
	
	public boolean isBound()
	{
		return mIBound;
	}
	
	public void close()
	{
		mIBound			   = false;
		mBackgroundService = null;
	}
}
