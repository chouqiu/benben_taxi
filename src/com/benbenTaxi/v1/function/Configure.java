package com.benbenTaxi.v1.function;

public class Configure {

	public static String getService()
	{
		return getHost()+":8081";
	}
	public static String getHost()
	{
		return "42.121.55.211";
	}
	public String getEquipmentId()
	{
		return "xxxxxxxx";
	}
	public String getOsVersion()
	{
		return "android "+android.os.Build.VERSION.RELEASE;
	}
	
	public static String getClientVersion()
	{
		//这个和分支一致
		return "passenger-"+"0.1";
	}
}
