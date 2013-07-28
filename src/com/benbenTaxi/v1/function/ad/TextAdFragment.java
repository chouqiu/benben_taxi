package com.benbenTaxi.v1.function.ad;


import com.benbenTaxi.R;
import com.benbenTaxi.v1.function.background.BackgroundService;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TextAdFragment extends Fragment{
	private static final String TAG								=  TextAdFragment.class.getName();
	private static final String DEFAULT_AD_STR					=  "============欢迎使用奔奔打车============";
	private AdServiceConnection	mAdServiceConnection 			=	null;
	private TextAdReceiver		mTextAdReceiver					=   null;
	private String	mCurrentTextAds								= 	null;
	private TextView mAdInfoTextView							=	null;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v =  inflater.inflate(R.layout.fragment_text_ad, container, false);
		return v;
	}
	
	public void doInitService()
	{
		mAdInfoTextView = (TextView) getActivity().findViewById(R.id.ad_info_text);
		refreshAdInfo(mCurrentTextAds);
        boundService();
        registerReceiver();
	}
	public void doPauseService()
	{
		unregisterReceiver();
		unboundService();
	}
	
	@Override
	public void onPause() {
		doPauseService();
		super.onPause();
	}

	@Override
	public void onResume() {
		doInitService();
		super.onResume();
	}

	public BackgroundService getBackgroundService()
	{
		if (mAdServiceConnection != null)
			return mAdServiceConnection.getService();
		return null;
	}
	public void refreshAdInfo(String textAds)
	{
		if (textAds == null || textAds.trim().equalsIgnoreCase("")){
			mCurrentTextAds = DEFAULT_AD_STR;
		}else{
			mCurrentTextAds = textAds;
		}
		
		if (mAdInfoTextView != null){
			//mAdInfoTextView.setText(mCurrentTextAds);
			mAdInfoTextView.setText(DEFAULT_AD_STR+mCurrentTextAds);
		}
		
		/*
		mAdInfoTextView.setSingleLine();
		mAdInfoTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		mAdInfoTextView.setMarqueeRepeatLimit(-1);
		mAdInfoTextView.setFocusable(true);
		mAdInfoTextView.setFocusableInTouchMode(true);
		mAdInfoTextView.setHorizontallyScrolling(true);
		mAdInfoTextView.requestFocus();
		*/
	}
	private void boundService()
	{
		if (mAdServiceConnection == null){
			mAdServiceConnection = new AdServiceConnection();
		}
	    Intent intent 	= new Intent(getActivity(), BackgroundService.class);
	    getActivity().bindService(intent, mAdServiceConnection, Context.BIND_AUTO_CREATE);
	    Log.i(TAG,"Bind AdService ");

	}
	private void unboundService()
	{
		if (mAdServiceConnection != null ) { //&& mAdServiceConnection.isBound()){
            getActivity().unbindService(mAdServiceConnection);
            mAdServiceConnection.close();
		}
	    Log.i(TAG,"unBind AdService!");

	}
	private void registerReceiver()
	{
		if (mTextAdReceiver == null){
			this.mTextAdReceiver 		=	 new TextAdReceiver(this);
		}
    	LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mTextAdReceiver,new IntentFilter(BackgroundService.TEXT_AD_ACTION));
	}
	private void unregisterReceiver()
	{
  	  LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mTextAdReceiver);

	}
}
