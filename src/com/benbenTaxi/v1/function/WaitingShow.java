package com.benbenTaxi.v1.function;

import com.benbenTaxi.R;
import com.benbenTaxi.v1.BenbenLocationMain;

import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WaitingShow 
{
	private View mView;
	private PopupWindow mPop;
	private TextView mTitle;
	private Chronometer mMeter;
	private ProgressBar mBar;
	private Button mBtnNeg;
	private String title, tip_neg;
	private View.OnClickListener mNegfunc = null;
	private int mSecs = 0;
	private Handler mH = null;
	
	public WaitingShow(String title, int secs, View vv) {
		mView = vv;
		mSecs = secs;
		this.title = title;
		this.init();
	}
	
	public void SetNegativeOnclick(String tip, View.OnClickListener func) {
		tip_neg = tip;
		final View.OnClickListener mf = func;
		
		if ( func != null ) {
			mNegfunc = new View.OnClickListener() {		
				@Override
				public void onClick(View v) {
					mf.onClick(v);
					doClean();
				}
			};
		}
	}
	
	public void setHandler( Handler h ) {
		mH = h;
	}
	
	public void show()
	{
    	mPop.showAtLocation(mView, Gravity.CENTER, 0, 0);
    	mMeter.setBase(SystemClock.elapsedRealtime());
    	mMeter.start();
	}
	
	public void Dismiss() {
		doClean();
	}
	
	private void init() {
    	mPop = new PopupWindow(mView, 640, 480);
    	mTitle = (TextView)mView.findViewById(R.id.tvWaitingTitle);
    	mBar = (ProgressBar)mView.findViewById(R.id.waitingProgress);
    	mMeter = (Chronometer) mView.findViewById(R.id.waitingMeter);
    	mBtnNeg = (Button)mView.findViewById(R.id.btnWaitingCancel);
    	
		tip_neg = "取消";
		mNegfunc = new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				doClean();		
			}
		};
		
		mBtnNeg.setText(tip_neg);
		mBtnNeg.setOnClickListener(mNegfunc);
		
		mTitle.setText(title);
    	mBar.setProgress(0);
    	mBar.setMax(mSecs);
    	mBar.setIndeterminate(false);
    	
    	mMeter.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
			
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				long tick = SystemClock.elapsedRealtime() - mMeter.getBase();
				
				// IMPORTANT: 不可再回调函数中结束定时器或popwin，否则会挂掉！！
				// 更新定时器滴答
				if ( tick > mSecs*1000 ) {
					// 超时
					//Toast.makeText(mAct, "乘客超时未响应，请重新选择请求", Toast.LENGTH_SHORT).show();
					//doClean();
					if ( mH != null ) {
						mH.dispatchMessage(mH.obtainMessage(BenbenLocationMain.MSG_HANDLE_REQ_TIMEOUT));
					}
				} else {
					// 更新进度条
					mBar.setProgress((int) (tick/1000.0));		
				}
			}
		});
	}
	
	private void doClean() {
		mMeter.stop();
		if ( mPop.isShowing() ) {
			mPop.dismiss();
		}
	}
}
