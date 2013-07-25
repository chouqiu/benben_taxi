package com.benbenTaxi.v1.function.ad;

import org.json.JSONArray;
import org.json.JSONException;

import com.benbenTaxi.v1.function.api.JsonHelper;
import com.benbenTaxi.v1.function.api.Response;

import android.util.Log;

public class TextAds extends  Response{
	private final static String TAG			=	TextAds.class.getName();
	private JSONArray mRes 					= null;

	public TextAds(String r) {
		super(r);
	}
	public String getContent(int index)
	{
		if (index >= getSize())
			return "";
		try {
			return JsonHelper.getString(mRes.getJSONObject(index),TextAdApiConstant.CONTENT);
		} catch (JSONException e) {
			Log.e(TAG, "数组越界！");
			return "";
		}
	}
	public int getSize()
	{
		if (mRes == null)
			return 0;
		return mRes.length();
	}
	@Override
	public void parser() {
		mRes = (JSONArray) this.getJsonResult();
	}

	@Override
	protected void dealError(String key, String val) {
		Log.e(TAG,key+":"+val);
	}

}
