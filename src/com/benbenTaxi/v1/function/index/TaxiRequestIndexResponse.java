package com.benbenTaxi.v1.function.index;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.benbenTaxi.v1.function.api.Response;
import com.benbenTaxi.v1.function.taxirequest.TaxiRequest;

import android.util.Log;

public class TaxiRequestIndexResponse extends Response {

	private String TAG =   "TaxiRequestIndexResponse"; //TaxiRequestIndexResponse.class.getName();
	JSONArray mRes = null;
	ArrayList<TaxiRequest> list=null;
	
	public TaxiRequestIndexResponse(String s)
	{
		super(s);
		if(!hasError())
		{
			list=new ArrayList<TaxiRequest>(); 
			for(int i=0;i<getSize();i++)
			{
				TaxiRequest tx=Json2TaxiRequest(i);
				list.add(tx);
			}
		}
	}
	public int getSize()
	{
		if (mRes == null)
			return 0;
		return mRes.length();
	}
	private  JSONObject getJSONbyIndex(int Index){
		if(Index<getSize())
			try {
				return mRes.getJSONObject(Index);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		else
			return null;
	}
	
	public TaxiRequest getTaxiRequest(int index)
	{
		if(index>=0 && index<getSize())
			return list.get(index);
		else
			return null;
	}
	
	private TaxiRequest Json2TaxiRequest(int index)
	{
		if(getJSONbyIndex(index)!=null)
			return new TaxiRequest(getJSONbyIndex(index));
		else
			return null;
	}
	
	@Override
	public void parser() {
		// TODO Auto-generated method stub
		mRes = (JSONArray) this.getJsonResult();
	}

	@Override
	protected void dealError(String key, String val) {
		// TODO Auto-generated method stub
		Log.e(TAG,key+":"+val);
	}

}
