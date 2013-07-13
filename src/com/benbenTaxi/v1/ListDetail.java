package com.benbenTaxi.v1;

import com.benbenTaxi.R;
import com.benbenTaxi.v1.function.CallAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class ListDetail extends Activity {
	protected ListView mLv;
	protected Button mBtnPos, mBtnNeg;
	protected View.OnClickListener mPosfunc, mNegfunc;
	protected String[] mContents;
	protected BenbenApplication mApp;
	private String tip_pos, tip_neg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_dialog);
		
		mApp = (BenbenApplication) this.getApplicationContext();
		Bundle tips = getIntent().getExtras();
		if ( tips != null ) {
			tip_pos = tips.getString("pos");
			tip_neg = tips.getString("neg");
		}
		
		init();
	}
	
	private void init() {
    	mLv = (ListView)findViewById(R.id.listView);
    	mBtnPos = (Button)findViewById(R.id.btnListOk);
    	mBtnNeg = (Button)findViewById(R.id.btnListCancel);
    	
    	do_init_functions();
    	
    	if ( tip_pos!=null ) {
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
	
	protected void do_init_functions() {
		mContents = mApp.getCurrentInfo();
    	mLv.setAdapter(new CallAdapter(mContents, this));
    	
		mPosfunc = new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent it = new Intent();
				setResult(1, it);
				finish();
			}
		};
		
		mNegfunc = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent();
				setResult(0, it);
				finish();
			}
		};
	}
}
