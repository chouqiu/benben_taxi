package com.benbenTaxi.v1.function;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class EquipmentId 
{
	//imeistring:imsistring:version:andriod[24]
	private final String TAG 					= EquipmentId.class.getName();
	private final String DefaultVersion			= "23";
	private final String VERSION_PREFIX			=  "android";
	private final String SPLITOR				= "@";					
	private TelephonyManager    mTelephonyManager;
	private Activity ma;
	public EquipmentId(Activity a)
	{
		this.ma 	=	a;
		this.init();
	}
	public String getId()
	{
		if (this.mTelephonyManager == null){
			Log.d(TAG, "TelephoneManager is null");
			return "null";
		}
		String imeistring = this.mTelephonyManager.getDeviceId();
		if (imeistring == null)
			imeistring = "0";
		String imsistring = this.mTelephonyManager.getSubscriberId();
		if (imsistring == null)
			imsistring = "0";
		return imeistring 						+SPLITOR +
			   imsistring 						+SPLITOR +
			   getAndroidVersion();
	}
	
	private void init()
	{
		this.mTelephonyManager =  ( TelephonyManager )this.ma.getSystemService(Context.TELEPHONY_SERVICE );
	}
	
	public String getAndroidVersion()
	{
		String v = android.os.Build.VERSION.RELEASE.replaceAll(".", "");
		if (v.length() >=2){
			return VERSION_PREFIX+v.substring(0, 1);
		}else{
			return VERSION_PREFIX+DefaultVersion;
		}
		
	}
}
