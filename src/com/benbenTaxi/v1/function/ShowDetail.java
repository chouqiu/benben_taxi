package com.benbenTaxi.v1.function;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.benbenTaxi.v1.BenbenApplication;
import com.benbenTaxi.v1.ListDetail;
import com.benbenTaxi.v1.function.api.JsonHelper;
import com.benbenTaxi.v1.function.taxirequest.TaxiRequest;

public class ShowDetail {
	public final static int NOT_SHOW = -1;
	
	//private final static DecimalFormat mDF = new DecimalFormat("#.##");
	
	static public void showPassengerRequestInfo(BenbenApplication app, Activity con, final JSONObject obj, int code) {
    	String[] voiceUrl = new String[5];
    	int id = JsonHelper.getInt(obj, "id");
    	
    	voiceUrl[0] = ""+id;
    	voiceUrl[1] = JsonHelper.getString(obj, "passenger_mobile");
    	voiceUrl[2] = JsonHelper.getString(obj, "source");
    	voiceUrl[3] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    	voiceUrl[4] = JsonHelper.getString(obj, "passenger_voice_url");
				
		app.setCurrentInfo(voiceUrl);
		app.setCurrentObject(obj);
		app.setCurrentStat(JsonHelper.getString(obj, "state"));
		
		if ( code != NOT_SHOW ) {
			Bundle tips = new Bundle();
			tips.putString("pos", "确认");
			tips.putString("neg", "返回");
			Intent detail = new Intent(con, ListDetail.class);
			detail.putExtras(tips);
			con.startActivityForResult(detail, code);
		}
    }
	
	static public void showCurrentPassengerRequest(TaxiRequest tx, Activity con, BenbenApplication app, int code) {
		String[] txInfo = new String[5];
    	
    	txInfo[0] = ""+tx.getID();
    	txInfo[1] = tx.getPassengerMobile();
    	txInfo[2] = tx.getSource();
    	txInfo[3] = tx.getCreatedAt();
    	txInfo[4] = tx.getURI();
		
    	app.setCurrentShowTaxiRequest(tx);
		app.setCurrentInfo(txInfo);
		
		Bundle tips = new Bundle();
		tips.putString("pos", "电话");
		tips.putString("neg", "返回");
		Intent detail = new Intent(con, ListDetail.class);
		detail.putExtras(tips);
		con.startActivityForResult(detail, code);
    }
	
	static public void showPassengerConfirmInfo(Activity con, int code) {
		Bundle tips = new Bundle();
		tips.putString("pos", "电话");
		tips.putString("neg", "完成");
		Intent detail = new Intent(con, ListDetail.class);
		detail.putExtras(tips);
		con.startActivityForResult(detail, code);
	}
	
    static public void showDriverInfo(Activity con, int idx, JSONObject obj) {
    	int drvid = 0;
    	double drv_lat = 0.0, drv_lng = 0.0;
    	
    	try {
			drvid = obj.getInt("driver_id");
			drv_lat = obj.getDouble("lat");
			drv_lng = obj.getDouble("lng");
    	} catch ( JSONException e ) {
    	}
		
		IdShow confirm = new IdShow("司机信息", "ID: "+drvid+"\n经度: "+drv_lng+"\n纬度: "+drv_lat, con);

    	confirm.SetNegativeOnclick(null, null);
    	confirm.SetPositiveOnclick("关闭", null);
    	confirm.getIdDialog().show();
    }
    
    static public void showCall(Activity con, JSONObject obj) {
    	String mobile = JsonHelper.getString(obj, "passenger_mobile");
		doCall(con, mobile);
    }

	public static void showCall(Activity con, TaxiRequest tx) {
		String mobile = tx.getPassengerMobile();
		doCall(con, mobile);
	}
	
	private static void doCall(Activity con, String mobile) {
		Uri uri = Uri.parse("tel:"+mobile);
	    Intent incall = new Intent(Intent.ACTION_DIAL, uri);
	    con.startActivity(incall);
	}
}
