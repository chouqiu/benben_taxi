package com.benbenTaxi.v1.function.api;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;


public abstract class Response {
	private final String TAG			     = "Response"; //FormResponse.class.getName();

	enum RESULT_JSON_TYPE {JSONObject,JSONArray};

	private String mRStr;
	private Object mRjson = null;
	private RESULT_JSON_TYPE mRJsonType = null;
	private String mSysErrorMessage = null;
	public  abstract void parser();
	protected abstract void dealError(String key,String val);

	
	public Response(String r)
	{
		this.mRStr = r;
		init();
	}
	public boolean hasError()
	{
		if (hasSysError()){
			dealAllError();
			return true;
		}
		if (hasAppError()){
			dealAllError();
			return true;
		}
		parser();
		return false;
	}
	private void dealAllError()
	{
		if (hasSysError()){
			dealError(ApiConstant.BASE,getSysErrorMesssage());
			return;
		}
		if (hasAppError()){
			JSONObject err;
			try {
				err = ((JSONObject)getJsonResult()).getJSONObject(ApiConstant.ERROR);
				@SuppressWarnings("rawtypes")
				Iterator i = err.keys();
				while(i.hasNext()){
					String k = (String)i.next();
					String v = err.getJSONArray(k).getString(0);
					dealError(k,v);
					Log.d(TAG,k+":"+v);
				}
			} catch (JSONException e) {
				Log.e(TAG, "解析应用层错误数据出错(JSON)!");
			} catch (Exception e) {
				Log.e(TAG, "解析应用层错误数据出错!");
			}
		}
	}
	private void init()
	{
		if (mRStr  == null)
			return;
		JSONTokener jsParser = new JSONTokener(mRStr);
		try {
			if (mRStr.startsWith("[")){
				mRJsonType = RESULT_JSON_TYPE.JSONArray;
			}else if (mRStr.startsWith("{")){
				mRJsonType = RESULT_JSON_TYPE.JSONObject;
			}
			mRjson = jsParser.nextValue();
			
		} catch (JSONException e) {
			setSysErrorMessage(SysErrorMessage.ERROR_API_DATA_ERROR);
		}catch (Exception e){
			setSysErrorMessage(SysErrorMessage.ERROR_NET_WORK);
		}
	}
	private RESULT_JSON_TYPE getResponseJsonType()
	{
		return mRJsonType;
	}
	public Object getJsonResult()
	{
		return mRjson;
	}
	public void setSysErrorMessage(String m)
	{
		this.mSysErrorMessage = m;
	}
	public String getSysErrorMesssage()
	{
		return this.mSysErrorMessage;
	}
	protected boolean hasSysError()
	{
		if (mSysErrorMessage != null){
			return true;
		}
		return false;
	}
	protected boolean hasAppError()
	{
		if (getResponseJsonType() == RESULT_JSON_TYPE.JSONObject && ((JSONObject)getJsonResult()).has(ApiConstant.ERROR)){
			Log.d(TAG,"has app error!");
			return true;
		}
		return false;
	}
}
