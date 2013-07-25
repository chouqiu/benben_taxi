package com.benbenTaxi.v1.function.ad;

import com.benbenTaxi.v1.function.background.BackgroundService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TextAdReceiver extends BroadcastReceiver{
	private static final String TAG							= TextAdReceiver.class.getName();
	private static final String SPLITOR						= "\t";
	private TextAdFragment 		mTextAdFragment				= null;
	
	public TextAdReceiver(TextAdFragment textAdFragment)
	{
		mTextAdFragment				=		textAdFragment;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		if (mTextAdFragment  != null && this.mTextAdFragment.getBackgroundService() != null){
			Log.d(TAG,"recive ad info......");
			refreshAdInfo();
		}
	}
	
	private void refreshAdInfo()
	{
		if (mTextAdFragment == null){
			return ;
		}
		BackgroundService backgroundService = mTextAdFragment.getBackgroundService();
		if (backgroundService == null){
			return;
		}
		TextAds textAds = backgroundService.getTextAds();
		if (textAds == null){
			return;
		}
		int     len		= textAds.getSize();
		String  all		= "";
		for(int i=0 ; i < len ; i++){
			all += textAds.getContent(i);
			all += SPLITOR;
		}
		mTextAdFragment.refreshAdInfo(all);
	}

}
