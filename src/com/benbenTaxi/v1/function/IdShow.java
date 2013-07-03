package com.benbenTaxi.v1.function;

import com.benbenTaxi.v1.function.EquipmentId;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

public class IdShow 
{
	private final String ID_TITLE				= "设备编码";
	
	protected Context 			mA 				= null;
	protected Activity			mAct			= null;
	private AlertDialog mIdDialog				= null;
	private String mTitle;
	private String mContent;
	private String tip_pos, tip_neg;
	private AlertDialog.Builder mBuilder;
	private DialogInterface.OnClickListener mPosfunc = null, mNegfunc = null;
	private View mDialogView = null;
	
	public IdShow(Activity a)
	{
		this.mA				=	a.getApplicationContext();
		mAct = a;
		//this.mEquipmentId	=   new EquipmentId(this.mA);
		mTitle = ID_TITLE;
		mContent = new EquipmentId(a).getId();
		this.init();
	}
	
	public IdShow(String title, String content, Activity a) {
		mA = a.getApplicationContext();
		mAct = a;
		mTitle = title;
		mContent = content;
		this.init();
	}
	
	public IdShow(String title, String content, Context a) {
		mA = a;
		mTitle = title;
		mContent = content;
		this.init();
	}
	
	public void SetPositiveOnclick(String tip, DialogInterface.OnClickListener func) {
		tip_pos = tip;
		mPosfunc = func;
	}
	
	public void SetNegativeOnclick(String tip, DialogInterface.OnClickListener func) {
		tip_neg = tip;
		mNegfunc = func;
	}
	
	public AlertDialog getIdDialog() {
		if ( mIdDialog == null ) {
			create();
		}
		return mIdDialog;
	}
	
	public void create()
	{
		mBuilder.setTitle(mTitle);
		mBuilder.setMessage(mContent);
		if ( tip_pos != null ) {
			mBuilder.setPositiveButton(tip_pos, mPosfunc);
		}
		if ( tip_neg != null ) {
			mBuilder.setNegativeButton(tip_neg, mNegfunc);	
		}
		if ( mDialogView != null ) {
			mBuilder.setView(mDialogView);
		}
		this.mIdDialog = mBuilder.create();
	}
	
	public void SetView( View v ) {
		mDialogView = v;
	}
	
	public View GetView() {
		return mDialogView;
	}
	
	private void init() {
		mBuilder = new AlertDialog.Builder(this.mAct);
		tip_pos = "确定";
		tip_neg = "取消";
		mPosfunc = mNegfunc = null;
	}
}
