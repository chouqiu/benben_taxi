package com.benbenTaxi.v1.function.taxirequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.util.Log;

import com.benbenTaxi.v1.function.Distance;
import com.benbenTaxi.v1.function.api.JsonHelper;

public class TaxiRequest {
	private JSONObject mTaxiRequestJson = null;
	private long mId;
	private String mPassengerMobile, mDriverMobile, mSource, mUri;
	private double mDriverLat, mDriverLng, mPassengerLat, mPassengerLng, mDistance;
	private String mCreatedAt;
	private Date mCreatedDate;
	private TaxiRequestState mTaxiRequestState;
	
	public TaxiRequest(JSONObject obj) {
		init(obj);
	}

	public void init(JSONObject obj)
	{
		this.mTaxiRequestJson 	= obj;
		mTaxiRequestState 		= TaxiRequestApiConstant.getState(JsonHelper.getString(obj, TaxiRequestApiConstant.STATE));
		mId 				 	= JsonHelper.getLong(obj, TaxiRequestApiConstant.ID);
		mPassengerMobile		= JsonHelper.getString(obj, TaxiRequestApiConstant.PASSENGER_MOBILE);
		mDriverMobile			= JsonHelper.getString(obj, TaxiRequestApiConstant.DRIVER_MOBILE);
		
		mDriverLat 				= JsonHelper.getFloat(obj, TaxiRequestApiConstant.DRIVER_LAT);
		mDriverLng				= JsonHelper.getFloat(obj, TaxiRequestApiConstant.DRIVER_LNG);
		mPassengerLat			= JsonHelper.getFloat(obj, TaxiRequestApiConstant.PASSENGER_LAT);
		mPassengerLng			= JsonHelper.getFloat(obj, TaxiRequestApiConstant.PASSENGER_LNG);
		mSource					= JsonHelper.getString(obj, TaxiRequestApiConstant.SOURCE);
		mUri					= JsonHelper.getString(obj, TaxiRequestApiConstant.URI);
		if (mDriverLat > 0 && mDriverLng > 0 && mPassengerLat>0 && mPassengerLng>0){
			mDistance = Distance.getDistance(mDriverLat, mDriverLng, mPassengerLat, mPassengerLng);
		}

		mCreatedAt						=JsonHelper.getString(obj, TaxiRequestApiConstant.CREATED_AT);
		
		mCreatedAt = mCreatedAt.replaceAll("\\+0([0-9]){1}\\:00", "+0$100");
		String ISO8601String = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		SimpleDateFormat formatter = new SimpleDateFormat(ISO8601String,Locale.CHINESE);
		
		Date strtodate;
		try {
			SimpleDateFormat formatter1= new SimpleDateFormat("yyyy-MM-dd");
			strtodate = formatter.parse(mCreatedAt);
			mCreatedDate=strtodate;
			mCreatedAt = formatter1.format(strtodate);
		} catch (ParseException e) {			
			e.printStackTrace();
			Log.e("TaxiRequest","CreateAt is Wrong....."+mCreatedAt);
		}
		
	}
	
	public Long getID() {
		return mId;
	}
	
	public String getURI() {
		return mUri;
	}
	
	public String getPassengerMobile() {
		return mPassengerMobile;
	}

	public String getCreatedAt() {
		return mCreatedAt;
	}
	
	public String getCreatedAt(String fmt) {
		SimpleDateFormat formatter1= new SimpleDateFormat(fmt);	
		return formatter1.format(mCreatedDate);	
	}

	public String getSource() {
		return mSource;
	}

	public String getHumanBreifTextState() {
		return mTaxiRequestState.getHumanBreifText();
	}

	public boolean isTaxiRequestSuccess() {
		if(this.mTaxiRequestState == TaxiRequestState.Success){
			return true;
		}
		return false;
	}
}
