package com.benbentaxi.remoteexception;

import org.json.JSONException;
import org.json.JSONObject;

import com.benbentaxi.Configure;

public class RemoteExceptionRequest {

	private final static String CONTENT					=	"content";
	private final static String CLIENT_EXCEPTION		=	"client_exception";
	private final static String ANDROID_VERSION			=	"android_version";
	private final static String CLIENT_VERSION			=	"client_version";
	private String mTrace								= 	null;
	private Configure mConfigure						=	null;
	
	public RemoteExceptionRequest(String trace)
	{
		this.mTrace			= trace;
		mConfigure 			= new Configure();
	}
	
	public JSONObject toJson()
	{
		JSONObject json_data = new JSONObject();
		
		JSONObject json = new JSONObject();		
		try {
			json.put(CONTENT, mTrace);
			json.put(ANDROID_VERSION,mConfigure.getOsVersion());
			json.put(CLIENT_VERSION,mConfigure.getClientVersion());
			json_data.put(CLIENT_EXCEPTION,json);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json_data;
		
	}
}
