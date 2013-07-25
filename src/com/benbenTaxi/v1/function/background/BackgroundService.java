package com.benbenTaxi.v1.function.background;

import com.benbenTaxi.v1.function.ad.TextAdTask;
import com.benbenTaxi.v1.function.ad.TextAds;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class BackgroundService extends Service{
	private static final int	MSG_NEAR_BY_DRIVERS							= 0;
	private static final int	MSG_TEXT_AD									= 1;
	private static final long	REFRESH_NEARBY_DRIVER_INTERVAL   			= 60000;
	private static final long	REFRESH_TEXT_AD_INTERVAL					= 10000;
	public  static final String	NEARYBY_DRIVER_ACTION						= "nearbydrvier_action";
	public  static final String TEXT_AD_ACTION								= "text_ad_action";
	private static final String TAG 										= BackgroundService.class.getName();
	private BackgroundServiceBinder mBackgroundServiceBinder 				= null;
    private Looper			   mLooper			   							= null;
    private ServiceHandler	   mHandler										= null;
    private TextAds					  mTextAds								= null;
    private HandlerThread mThread 											= null;


	@Override
	public void onCreate()
	{
		mThread 								= new HandlerThread(TAG,android.os.Process.THREAD_PRIORITY_BACKGROUND);
		mThread.start();
		mLooper 			 					= mThread.getLooper();
		mBackgroundServiceBinder				= new BackgroundServiceBinder(this);
		mHandler								= new ServiceHandler(mLooper);
	}
	@Override
	public IBinder onBind(Intent intent) {
		return mBackgroundServiceBinder;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		mThread.quit();
		this.mHandler.removeMessages(MSG_NEAR_BY_DRIVERS);
		this.mHandler.removeMessages(MSG_TEXT_AD);
    }
	public void startRefreshNearByDriver()
	{
		
		this.mHandler.sendMessage(this.mHandler.obtainMessage(MSG_NEAR_BY_DRIVERS));
	}
	
	public void startTextAd()
	{
		this.mHandler.sendMessage(this.mHandler.obtainMessage(MSG_TEXT_AD));
	}
	
	public TextAds getTextAds()
	{
		return mTextAds;
	}
	
	private final class ServiceHandler extends Handler
	{
		private Intent mNearbyDriverIntent = new Intent(NEARYBY_DRIVER_ACTION);
		private Intent mTextAdIntent	   = new Intent(TEXT_AD_ACTION);
		 public ServiceHandler(Looper looper) {
	          super(looper);
	      }
		 public void handleMessage(Message msg) {
				switch (msg.what)
				{
					case MSG_TEXT_AD:
						TextAdTask	textAdTask			= new TextAdTask((DemoApplication) BackgroundService.this.getApplication());
						mTextAds						=  textAdTask.send();
						LocalBroadcastManager.getInstance(BackgroundService.this).sendBroadcast(mTextAdIntent);
					 	if (mHandler.getLooper().getThread().getState() != Thread.State.TERMINATED){
					 		mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_TEXT_AD), REFRESH_TEXT_AD_INTERVAL);
					 	}
						break;
					default:
						break;
				}
			}


	}
	
	
	
}
