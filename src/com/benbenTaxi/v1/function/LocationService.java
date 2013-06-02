package com.benbenTaxi.v1.function;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

public class LocationService extends Service {
	private LocTimerTask mLocTask;
	private Handler mLocHandler;
	private String mHost, mSessKey, mEid;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		mLocHandler = new Handler() {
			@Override
        	public void handleMessage(Message msg) {
        		if (msg.what == LocTimerTask.EVENT_LOCATION_OK) {
        			// 显示地理位置信息
        			//LocationInfo info = mLocTask.getLocation();
        			Toast.makeText(LocationService.this.getApplicationContext(), 
        					"svr位置信息成功上报！", Toast.LENGTH_SHORT).show();
        		} else if (msg.what == LocTimerTask.EVENT_LOCATION_FAIL) {
        			String err = mLocTask.getErrmsg();
        			Toast.makeText(LocationService.this.getApplicationContext(),
        					"svr位置信息上报失败: "+err, Toast.LENGTH_SHORT).show();
        		}else if (msg.what == LocTimerTask.EVENT_LOCATION_ERROR ){
        			String err = mLocTask.getErrmsg();
        			Toast.makeText(LocationService.this.getApplicationContext(),
        					"svr位置信息获取错误: "+err, Toast.LENGTH_SHORT).show();
        		} else {
        			Toast.makeText(LocationService.this.getApplicationContext(),
        					"svr got unknown msg!", Toast.LENGTH_SHORT).show();
        		}
        	}
        };
        
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		mLocTask.clean();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle bs = intent.getExtras();
		if ( bs != null ) {
			mHost = bs.getString("host");
			mSessKey = bs.getString("sessid");
			mEid = bs.getString("eid");
		}
		
		if ( mLocTask != null ) {
			mLocTask.clean();
			mLocTask = null;
		}
		
		// 启动定时器
        if ( mHost != null ) {
        	mLocTask = new LocTimerTask(mLocHandler, mHost, mSessKey, mEid, this.getApplicationContext());
        	mLocTask.Schedule();
        } else {
    		Toast.makeText(LocationService.this.getApplicationContext(),
					"服务启动参数错误！", Toast.LENGTH_SHORT).show();
        }
        
		return super.onStartCommand(intent, flags, startId);
	}
	
}
