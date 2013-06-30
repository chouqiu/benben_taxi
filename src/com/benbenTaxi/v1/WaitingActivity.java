package com.benbenTaxi.v1;

import com.benbenTaxi.R;
import com.benbenTaxi.demo.DemoApplication;
import com.benbenTaxi.demo.LocationOverlayDemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WaitingActivity extends Activity {	
	private DemoApplication mA;
	private TextView mTitle;
	private Chronometer mMeter;
	private ProgressBar mBar;
	private Button mBtnNeg;
	private String title, tip_neg;
	private View.OnClickListener mNegfunc = null;
	private int mSecs = 0;
	private boolean mRun = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.waiting_dialog);
		
		mA = (DemoApplication) getApplicationContext();
		Intent it = this.getIntent();
		mSecs = it.getIntExtra("timeout", 30);
		title = it.getStringExtra("title");
		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	
	public void SetNegativeOnclick(String tip, View.OnClickListener func) {
		tip_neg = tip;
		final View.OnClickListener mf = func;
		
		if ( func != null ) {
			mNegfunc = new View.OnClickListener() {		
				@Override
				public void onClick(View v) {
					mf.onClick(v);
					doClean(DemoApplication.STATVAL_CANCEL);
				}
			};
		}
	}
	
	private void init() {
    	mTitle = (TextView)findViewById(R.id.tvWaitingTitle);
    	mBar = (ProgressBar)findViewById(R.id.waitingProgress);
    	mMeter = (Chronometer)findViewById(R.id.waitingMeter);
    	mBtnNeg = (Button)findViewById(R.id.btnWaitingCancel);
    	
		tip_neg = "取消";
		mNegfunc = new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				doClean(DemoApplication.STATVAL_CANCEL);		
			}
		};
		
		mBtnNeg.setText(tip_neg);
		mBtnNeg.setOnClickListener(mNegfunc);
		
		mTitle.setText(title);
    	mBar.setProgress(0);
    	mBar.setMax(mSecs);
    	mBar.setIndeterminate(false);
    	
    	mMeter.setBase(SystemClock.elapsedRealtime());
    	mMeter.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
			
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				long tick = SystemClock.elapsedRealtime() - mMeter.getBase();
				
				// 更新定时器滴答
				if ( tick > mSecs*1000 ) {
					// 超时
					//Toast.makeText(mAct, "乘客超时未响应，请重新选择请求", Toast.LENGTH_SHORT).show();
					doClean(DemoApplication.STATVAL_TIMEOUT);
				} else {
					// 更新进度条
					mBar.setProgress((int) (tick/1000.0));
					int ret = mA.getCurrentStatVal();
					
					if ( ret == DemoApplication.STATVAL_SUCCESS || 
							ret == DemoApplication.STATVAL_CANCEL )
					{
						// 响应成功或取消，结束waiting
						doClean(ret);
					}
				}
			}
		});
    	
    	mMeter.start();
	}
	
	private void doClean( int result ) {
		mMeter.stop();
		mA.resetStatVal();
		this.setResult(result);
		finish();
	}
}
