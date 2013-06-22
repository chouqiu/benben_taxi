package com.benbenTaxi.v1.function;

import com.benbenTaxi.R;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ConfirmShow 
{
	protected Context 			mA 				= null;
	protected Activity			mAct			= null;
	private View mView;
	private PopupWindow mPop;
	private TextView mTitle;
	private TextView mContent;
	private Button mBtnPos, mBtnNeg;
	private String tip_pos, tip_neg;
	private View.OnClickListener mPosfunc = null, mNegfunc = null;
	
	public ConfirmShow(String title, String content, Activity a) {
		mA = a.getApplicationContext();
		mAct = a;
		this.init(title, content);
	}
	
	public ConfirmShow(String title, String content, Context a) {
		mA = a;
		this.init(title, content);
	}
	
	public void SetPositiveOnclick(String tip, View.OnClickListener func) {
		tip_pos = tip;
		final View.OnClickListener mf = func;
		
		mPosfunc = new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				mf.onClick(v);
				if ( mPop.isShowing() ) {
					mPop.dismiss();
				}
			}
		};
	}
	
	public void SetNegativeOnclick(String tip, View.OnClickListener func) {
		tip_neg = tip;
		final View.OnClickListener mf = func;
		
		mNegfunc = new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				mf.onClick(v);
				if ( mPop.isShowing() ) {
					mPop.dismiss();
				}
			}
		};
	}
	
	public void show()
	{
		mBtnPos.setText(tip_pos);
		mBtnNeg.setText(tip_neg);
		mBtnPos.setOnClickListener(mPosfunc);
		mBtnNeg.setOnClickListener(mNegfunc);
		
		mPop.showAtLocation(mView, Gravity.CENTER, 0, 0);
	}
	
	private void init(String title, String content) {
		mView = mAct.getLayoutInflater().inflate(R.layout.confirm_dialog, null);
    	mPop = new PopupWindow(mView, 600, 400);
    	mTitle = (TextView)mView.findViewById(R.id.tvConfirmTitle);
    	mContent = (TextView)mView.findViewById(R.id.tvConfirmContent);
    	mBtnPos = (Button)mView.findViewById(R.id.btnConfirmOk);
    	mBtnNeg = (Button)mView.findViewById(R.id.btnConfirmCancel);
    	
    	mTitle.setText(title);
    	mContent.setText(content);
    	
		tip_pos = "确定";
		tip_neg = "取消";
		mPosfunc = mNegfunc = new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				if ( mPop.isShowing() ) {
					mPop.dismiss();
				}
			}
		};
	}
}
