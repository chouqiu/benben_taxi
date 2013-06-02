package com.benbenTaxi.v1.function;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.baidu.location.*;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class LocTimerTask extends TimerTask {
	public final static int EVENT_LOCATION_OK = 0x100;
	public final static int EVENT_LOCATION_FAIL = 0x200;
	public final static int EVENT_LOCATION_ERROR = 0x400;
	
	private Timer mTimer;
	private LocationInfo mLocInfo;
	private boolean mReadyFlag;
	private Context mContext;
	
	private Handler mHandler;
	private LocationClient mLocationClient;
	private String mCookie;
	private String mUserAgent;
	private String mHost;
	private String mErrmsg;
	
	public class LocationInfo {
		public String mLocTime;
		public String mLocType;
		public int mLocTypeId;
		public double mLatitude, mLongitude;
		public float mRadius;
		
		public LocationInfo() {
			mLatitude = mLongitude = 0.0d;
			mRadius = 0.0f;
			mLocTypeId = 0;
		}
	}
	
	public LocTimerTask( Handler h, String host, String cookie, String ua, Context con ) { 
		mHandler = h;
		mCookie = cookie;
		mUserAgent = ua; 
		mContext = con;
		mHost = host;
		mReadyFlag = false;
		
		mLocInfo = new LocationInfo();
		mLocationClient = new LocationClient( mContext );
		mLocationClient.registerLocationListener( new MyLocationListenner() );
		mTimer = new Timer(true);
	}
	
	// 启动定时器
	public void Schedule() {
		//manualLocate();
		//mTimer.schedule(this, 10000);
		mTimer.schedule(this, 500, 10000);
	}
	
	public LocationInfo getLocation() {
		mReadyFlag = false;
		//this.cancel();
		return mLocInfo;
	}
	
	public void manualLocate() {
		if ( mLocationClient.isStarted() ) {
			mLocationClient.stop();
		}
		// 获取地理位置，并发送给webactivity
		setLocationOption();
		mLocationClient.start();
	}
	
	public void clean() {
		if ( mLocationClient.isStarted() ) {
			mLocationClient.stop();
		}
		
		this.cancel();
		mTimer.cancel();
		mTimer.purge();
	}
	
	public void waitForResult() {
		int cnt = 0;
		while ( mReadyFlag == false && cnt < 5 ) {
			// 休眠等待
			try {
				Thread.sleep(1000);
			} catch (Exception e) {}
			++cnt;
		}
		mLocationClient.stop();
	}
	
	public String getErrmsg() {
		return mErrmsg;
	}
	
	@Override
	public void run() {
		manualLocate();
	}
	
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);				//打开gps
		option.setCoorType("bd09ll");		//设置坐标类型
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setPoiExtraInfo(false);
		option.setAddrType("all");
		//设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
		option.setScanSpan(500);
		option.setPriority(LocationClientOption.GpsFirst);
		//option.setPriority(LocationClientOption.NetWorkFirst);      //设置网络优先
		
		option.setPoiNumber(10);
		option.disableCache(true);		
		mLocationClient.setLocOption(option);
	}

	/**
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		public void onReceiveLocation(BDLocation location) {
			if ( location != null ) {
				mReadyFlag = true;
				
				mLocInfo.mLocTime = location.getTime();
				mLocInfo.mLatitude = location.getLatitude();
				mLocInfo.mLongitude = location.getLongitude();
				mLocInfo.mRadius = location.getRadius();
				
				mLocInfo.mLocTypeId = location.getLocType();
				if (mLocInfo.mLocTypeId == BDLocation.TypeGpsLocation){
					mLocInfo.mLocType = "gps";
				} else if (mLocInfo.mLocTypeId == BDLocation.TypeNetWorkLocation){
					mLocInfo.mLocType = "gsm";
				} else {
					mLocInfo.mLocType = "fail";
				}
			} else {
				mReadyFlag = false;
			}
			
			if ( mLocInfo.mLocTypeId == BDLocation.TypeGpsLocation || 
					mLocInfo.mLocTypeId == BDLocation.TypeNetWorkLocation ) {
				(new ReportLocationTask()).reportLoc(mLocInfo, mHost, mCookie, mUserAgent);
			} else {
				mErrmsg = "errorid("+mLocInfo.mLocTypeId+")";
				Message msg = mHandler.obtainMessage(LocTimerTask.EVENT_LOCATION_ERROR);
				msg.sendToTarget();
			}
			mLocationClient.stop();
		}
		
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null){
				return ; 
			}
			/*
			StringBuffer sb = new StringBuffer(256);
			sb.append("Poi time : ");
			sb.append(poiLocation.getTime());
			sb.append("\nerror code : "); 
			sb.append(poiLocation.getLocType());
			sb.append("\nlatitude : ");
			sb.append(poiLocation.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(poiLocation.getLongitude());
			sb.append("\nradius : ");
			sb.append(poiLocation.getRadius());
			if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(poiLocation.getAddrStr());
			} 
			if(poiLocation.hasPoi()){
				sb.append("\nPoi:");
				sb.append(poiLocation.getPoi());
			}else{				
				sb.append("noPoi information");
			}
			logMsg(sb.toString());
			*/
		}
	}
	
	public class NotifyLister extends BDNotifyListener{
		public void onNotify(BDLocation mlocation, float distance){
			//mVibrator01.vibrate(1000);
		}
	}
	
	private class ReportLocationTask extends GetInfoTask {
		//private final static String _url = "http://peterwolf.cn.mu/zone_supervisor/sessions.json";
		//private final static String _url = "http://v2.365check.net/api/v1/track_points";
		//private final static String _testagent = "351554052661692@460018882023767@0.14@android42";
		private JSONObject _json_data;
		
		// 上报经纬度
		public void reportLoc(LocationInfo locinfo, String host, String cookie, String ua) {
			initHeaders("Content-Type", "application/json");
			initCookies("remember_token", cookie, host);
			
			_json_data = new JSONObject();
			try {
				//{"session":{"account":"sh_0000","password":"8"}}
				JSONObject loc = new JSONObject();
				loc.put("generated_time_of_client_version", locinfo.mLocTime);
				loc.put("radius", locinfo.mRadius);
				loc.put("lng", locinfo.mLongitude);
				loc.put("lat", locinfo.mLatitude);
				loc.put("coortype", locinfo.mLocType);
				//loc.put("coortype_id", locinfo.mLocTypeId);
				loc.put("interval_time_between_generate_and_submit", 0);
				JSONArray locarr = new JSONArray();
				locarr.put(loc);
				_json_data.put("track_points", locarr);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//_info.append("form json error: "+e.toString());
			} catch (Exception e) {
				// TODO: 异常处理
			}
			
			String url = "http://"+host+"/api/v1/track_points";
			execute(url, ua, "post");
		}
		
		@Override
		protected void initPostValues() {
			//sess_params.add(new BasicNameValuePair("","{\"session\":{\"name\":\"ceshi001\",\"password\":\"8\"}}"));
			//post_param = "{\"session\":{\"name\":\"ceshi_ning\",\"password\":\"8\"}}";
			if ( _json_data != null ) {
				post_param = _json_data.toString();
			}
		}
		
		@Override
		protected void onPostExecGet(Boolean succ) {
			if ( succ ) {
			} else {
			}
		}
		
		@Override
		protected void onPostExecPost(Boolean succ) {
			//_info.setText("Post "+this.getHttpCode()+"\n");
			Message msg;
			if ( succ ) {	
				msg = mHandler.obtainMessage(LocTimerTask.EVENT_LOCATION_OK);			
				
				//_info.append("result: "+this.getHttpCode()+"\n"+this.toString());
				JSONTokener jsParser = new JSONTokener(this.toString());
				JSONObject ret = null;

				try {
					String info = this.toString();
					ret = (JSONObject)jsParser.nextValue();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					try {
						JSONObject err = ret.getJSONObject("errors");
						//_info.append("errmsg \""+err.getJSONArray("base").getString(0)+"\"");
					} catch (Exception ee) {
						//_info.append("json error: "+ee.toString()+"\n");
						//_info.append("to json: "+_json_data.toString());
					}
				} catch (Exception e) {
					// TODO: 异常处理
				}
				
			} else {
				try {
					JSONTokener jsParser = new JSONTokener(this.toString());
					JSONObject err = ((JSONObject)jsParser.nextValue()).getJSONObject("errors");
					mErrmsg = err.toString();
				} catch (Exception ee) {
					mErrmsg = "未知错误";
				}
				msg = mHandler.obtainMessage(LocTimerTask.EVENT_LOCATION_FAIL);
			}
			msg.sendToTarget();
		}
	}
}
