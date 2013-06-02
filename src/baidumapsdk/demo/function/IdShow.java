package com.android.task.main.function;

import com.android.task.tools.EquipmentId;

import android.app.Activity;
import android.app.AlertDialog;

public class IdShow 
{
	private final String ID_TITLE				= "设备编码";
	
	private Activity 			mA 				= null;
	private EquipmentId mEquipmentId			= null;
	private AlertDialog mIdDialog				= null;
	private String mTitle;
	private String mContent;

	
	public IdShow(Activity a)
	{
		this.mA				=	a;
		//this.mEquipmentId	=   new EquipmentId(this.mA);
		mTitle = ID_TITLE;
		mContent = new EquipmentId(this.mA).getId();
		this.init();
	}
	
	public IdShow(String title, String content, Activity a) {
		mA = a;
		mTitle = title;
		mContent = content;
		this.init();
	}
	
	public AlertDialog getIdDialog() {
		return mIdDialog;
	}
	private void init()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this.mA);
		builder.setTitle(mTitle);
		builder.setMessage(mContent);
		builder.setPositiveButton("确定", null);
		this.mIdDialog = builder.create();
	}
}
