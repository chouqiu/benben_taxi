package com.benbenTaxi.v1;

import com.benbenTaxi.R;
import com.benbenTaxi.demo.DemoApplication;
import com.benbenTaxi.v1.function.CallAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.LinearLayout.LayoutParams;

public class ListDetail extends Activity {
	private ListView mLv;
	private Button mBtnPos, mBtnNeg;
	private String tip_pos, tip_neg;
	private View.OnClickListener mPosfunc, mNegfunc;
	private String[] mContents;
	private DemoApplication mApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_dialog);
		
		mApp = (DemoApplication) this.getApplicationContext();
		mContents = mApp.getCurrentInfo();
		init();
	}
	
	private void init() {
    	mLv = (ListView)findViewById(R.id.listView);
    	mBtnPos = (Button)findViewById(R.id.btnListOk);
    	mBtnNeg = (Button)findViewById(R.id.btnListCancel);
    	
    	mLv.setAdapter(new CallAdapter(mContents, this));  
    	
		tip_pos = "电话乘客";
		mPosfunc = new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent it = new Intent();
				setResult(1, it);
				finish();
			}
		};
		
		tip_neg = "再看看";
		mNegfunc = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent();
				setResult(0, it);
				finish();
			}
		};
		
		if ( mApp.getRequestID() > 0 ) {
			mBtnPos.setText(tip_pos);
			mBtnPos.setOnClickListener(mPosfunc);
		} else {
			mBtnPos.setVisibility(View.GONE);
		}
		if ( tip_neg != null ) {
			mBtnNeg.setText(tip_neg);
			mBtnNeg.setOnClickListener(mNegfunc);
		} else {
			mBtnNeg.setVisibility(View.GONE);
		}
	}
}
