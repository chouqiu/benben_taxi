package com.benbenTaxi.v1.function;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.benbenTaxi.v1.BenbenApplication;
import com.benbenTaxi.v1.BenbenLocationTest;
import com.benbenTaxi.v1.ListDetail;

public class ShowDetail {
	private final static DecimalFormat mDF = new DecimalFormat("#.##");
	
	static public void showPassengerRequestInfo(BenbenApplication app, Activity con, final JSONObject obj, int code) {
    	String[] voiceUrl = new String[5];
    	int id = 0;
    	
		try {
			id = obj.getInt("id");
			voiceUrl[0] = "ID"+id;
			voiceUrl[1] = obj.getString("passenger_mobile");
			voiceUrl[2] = mDF.format(obj.getDouble("passenger_lat"))+"/"+mDF.format(obj.getDouble("passenger_lng"));
			voiceUrl[3] = "大连西路120号";
			voiceUrl[4] = "2013-06-25 00:44:22";
			//voiceUrl[3] = obj.getString("passenger_voice_url");
		} catch (JSONException e) {
			voiceUrl[0] = "未知";
			voiceUrl[1] = "未知";
			voiceUrl[2] = "未知";
			voiceUrl[3] = "未知";
			voiceUrl[4] = "未知";
			//voiceUrl[3] = "乘客信息获取错误: "+e.toString();
		}
				
		app.setCurrentInfo(voiceUrl);
		app.setRequestID(id);
		app.setCurrentObject(obj);
		
		Bundle tips = new Bundle();
		tips.putString("pos", "确认乘客");
		tips.putString("neg", "换一个");
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
    	String mobile;
		try {
			mobile = obj.getString("passenger_mobile");
			//mobile = "12345";
		} catch (JSONException e) {
			mobile = "000000";
		}
		
		Uri uri = Uri.parse("tel:"+mobile);
	    Intent incall = new Intent(Intent.ACTION_DIAL, uri);
	    con.startActivity(incall);
    }
}
