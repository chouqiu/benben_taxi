package com.benbenTaxi.v1.function;

import com.benbenTaxi.R;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ListShow 
{
	protected Context 			mA 				= null;
	protected Activity			mAct			= null;
	private View mView;
	private PopupWindow mPop;
	private ListView mList;
	private Button mBtnPos;
	private String tip_pos;
	private View.OnClickListener mPosfunc = null;
	
	private String[] mContents;
	
	public ListShow(String[] contents, Activity a) {
		mA = a.getApplicationContext();
		mAct = a;
		mContents = contents;
		this.init();
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
	
	public void show()
	{
		mBtnPos.setText(tip_pos);
		mBtnPos.setOnClickListener(mPosfunc);
		
		mPop.showAtLocation(mView, Gravity.CENTER, 0, 0);
	}
	
	private void init() {
		mView = mAct.getLayoutInflater().inflate(R.layout.list_dialog, null);
    	mPop = new PopupWindow(mView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

    	mList = (ListView)mView.findViewById(R.id.listView);
    	mBtnPos = (Button)mView.findViewById(R.id.btnListOk);
    	
    	mList.setAdapter(new CallAdapter(mContents, mAct));  
    	
		tip_pos = "È·¶¨";
		mPosfunc = new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				if ( mPop.isShowing() ) {
					mPop.dismiss();
				}
			}
		};
	}
}
