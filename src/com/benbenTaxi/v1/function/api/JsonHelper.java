package com.benbenTaxi.v1.function.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonHelper {
	public static String getString(JSONObject o,String k)
	{
		if (o == null)
			return "invalid, null";

		try {
			return o.getString(k);
		} catch (JSONException e) {
			return "error, null";
		}
	}
	
	public static float getFloat(JSONObject o,String k)
	{
		if (o == null)
			return -1f;
		
		try {
			return (float) o.getDouble(k);
		} catch (JSONException e) {
			return -1f;
		}
	}
	
	public static long getLong(JSONObject o,String k)
	{
		if (o == null)
			return -1l;
		try {
			return o.getLong(k);
		} catch (JSONException e) {
			return -1;
		}
	}
	public static int getInt(JSONObject o,String k)
	{
		if (o == null)
			return -1;
		try {
			return o.getInt(k);
		} catch (JSONException e) {
			return -1;
		}
	}
	public static double getDouble(JSONObject o,String k)
	{
		if (o == null)
			return -1f;
		
		try {
			return  o.getDouble(k);
		} catch (JSONException e) {
			return -1f;
		}
	}

	public static JSONObject getJsonObj(JSONArray arr, int idx) {
		if ( arr == null || idx < 0 || idx >= arr.length() ) {
			return null;
		}
		
		try {
			return arr.getJSONObject(idx);
		} catch (JSONException e) {
			return null;
		}
	}
}
