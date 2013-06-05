package com.benbenTaxi.demo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.benbenTaxi.R;
import com.benbenTaxi.v1.function.DataPreference;
import com.benbenTaxi.v1.function.GetInfoTask;
public class LocationOverlayDemo extends Activity {
	
	static MapView mMapView = null;
	
	private MapController mMapController = null;

	public MKMapViewListener mMapListener = null;
	FrameLayout mMapViewContainer = null;
	
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
    public NotifyLister mNotifyer=null;
	
	Button testUpdateButton = null;
	
	EditText indexText = null;
	MyLocationOverlay myLocationOverlay = null;
	int index =0;
	LocationData locData = null;
	
	Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Toast.makeText(LocationOverlayDemo.this, "msg:" +msg.what, Toast.LENGTH_SHORT).show();
        };
    };
    
    private String mTokenKey, mTokenVal;
	private static final String mTestHost = "42.121.55.211:8081";
	
	OverlayTest ov = null;
	// 存放overlayitem 
	public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	// 存放overlay图片
	public List<Drawable>  res = new ArrayList<Drawable>();
	private Drawable mDrvMarker;
	
	private DataPreference mData;
	private String mUserMobile;
	private int mReqId = -1;
	private boolean mNeedTaxi = false; // 判断是否已发起打车请求
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DemoApplication app = (DemoApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            app.mBMapManager.init(DemoApplication.strKey,new DemoApplication.MyGeneralListener());
        }
        setContentView(R.layout.activity_locationoverlay);
        mMapView = (MapView)findViewById(R.id.bmapView);
        mMapController = mMapView.getController();
        
        mData = new DataPreference(this.getApplicationContext());
        mTokenKey = mData.LoadString("token_key");
        mTokenVal = mData.LoadString("token_value");
        mUserMobile = mData.LoadString("user");
        
        initMapView();
        
        mLocClient = new LocationClient( this );
        mLocClient.registerLocationListener( myListener );
        
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setCoorType("bd09ll");     //设置坐标类型
        option.setScanSpan(5000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);
        
        mMapView.setBuiltInZoomControls(true);
        mMapListener = new MKMapViewListener() {
			
			@Override
			public void onMapMoveFinish() {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				// TODO Auto-generated method stub
				String title = "";
				if (mapPoiInfo != null){
					title = mapPoiInfo.strText;
					Toast.makeText(LocationOverlayDemo.this,title,Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onGetCurrentMap(Bitmap b) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onMapAnimationFinish() {
				// TODO Auto-generated method stub
				
			}
		};
		mMapView.regMapViewListener(DemoApplication.getInstance().mBMapManager, mMapListener);
		
	    // 初始化出租车位置列表
	    mDrvMarker = this.getResources().getDrawable(R.drawable.icon_marka);
	    ov = new OverlayTest(mDrvMarker, this,mMapView); 
	    mMapView.getOverlays().add(ov);
	    res.add(getResources().getDrawable(R.drawable.icon_marka));
    	res.add(getResources().getDrawable(R.drawable.icon_markb));
	    
		myLocationOverlay = new MyLocationOverlay(mMapView);
		locData = new LocationData();
	    myLocationOverlay.setData(locData);
		mMapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		mMapView.refresh();
		
		testUpdateButton = (Button)findViewById(R.id.btn_callTaxi);
		OnClickListener clickListener = new OnClickListener(){
				public void onClick(View v) {
					testUpdateClick();
					mNeedTaxi = true;
				}
	        };
	    testUpdateButton.setOnClickListener(clickListener);
	    
	    Toast.makeText(this.getApplicationContext(), mTokenKey+": "+mTokenVal, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }
    
    
    @Override
    protected void onDestroy() {
        if (mLocClient != null)
            mLocClient.stop();
        mMapView.destroy();
        DemoApplication app = (DemoApplication)this.getApplication();
        if (app.mBMapManager != null) {
            app.mBMapManager.destroy();
            app.mBMapManager = null;
        }
        super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mMapView.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mMapView.onRestoreInstanceState(savedInstanceState);
    }
    
    public void testUpdateClick(){
        mLocClient.requestLocation();
    }
    private void initMapView() {
        mMapView.setLongClickable(true);
        //mMapController.setMapClickEnable(true);
        //mMapView.setSatellite(false);
    }
   

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	
	/**
     * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            
            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            locData.accuracy = location.getRadius();
            locData.direction = location.getDerect();
            myLocationOverlay.setData(locData);
            mMapView.refresh();
            mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)), mHandler.obtainMessage(1));
            
            // 获取周边Taxi
            GetTaxiTask gtt = new GetTaxiTask();
            gtt.getTaxi(locData.longitude, locData.latitude);
            
            // 发起打车请求
            if ( mNeedTaxi && mReqId < 0 ) {
	            GetTaxiTask reqtt = new GetTaxiTask();
	            reqtt.requestTaxi(locData.longitude, locData.latitude);
	            mNeedTaxi = false;
            } else if ( mReqId > 0 ) {
            // 发起轮询
            	GetTaxiTask getrr = new GetTaxiTask();
            	getrr.getRequest(mReqId);
            }
        }
        
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null){
                return ;
            }
        }
    }
    
    public class NotifyLister extends BDNotifyListener{
        public void onNotify(BDLocation mlocation, float distance) {
        }
    }

    
	private class GetTaxiTask extends GetInfoTask {
		private static final int TYPE_GET_TAXI = 0;
		private static final int TYPE_REQ_TAXI = 1;
		private static final int TYPE_GET_REQ = 2;
		private String _useragent = "ning@benbentaxi";
		private JSONObject _json_data;
		private int _type = -1;
		double _lat = 0.0, _lng = 0.0;
		
		public void getTaxi(double lng, double lat) {
			_lat = lat;
			_lng = lng;
			_type = TYPE_GET_TAXI;
			String url =  "http://"+mTestHost+"/api/v1/users/nearby_driver?lat="+_lat+"&lng="+_lng;
			super.initCookies(mTokenKey, mTokenVal, "42.121.55.211");
			execute(url, _useragent, GetInfoTask.TYPE_GET);
		}
		
		public void getRequest(int id) {
			_type = TYPE_GET_REQ;
			String url = "http://"+mTestHost+"/api/v1/taxi_requests/"+id;
			super.initCookies(mTokenKey, mTokenVal, "42.121.55.211");
			execute(url, _useragent, GetInfoTask.TYPE_GET);
		}
		
		public void requestTaxi(double lng, double lat) {
			_lat = lat;
			_lng = lng;
			_type = TYPE_REQ_TAXI;
			String url = "http://"+mTestHost+"/api/v1/taxi_requests";
			
			// 一定要初始化cookie和content-type!!!!!
			super.initCookies(mTokenKey, mTokenVal, "42.121.55.211");
			super.initHeaders("Content-Type", "application/json");
			
			_json_data = new JSONObject();
			try {
				//{\"taxi_request\":{\"passenger_mobile\":\"15910676326\",\"passenger_lng\":\"8\",\"passenger_lat\":\"8\",\"waiting_time_range\":30}}" 
				JSONObject sess = new JSONObject();
				sess.put("passenger_mobile", mUserMobile);
				sess.put("passenger_lng", lng);
				sess.put("passenger_lat", lat);
				sess.put("waiting_time_range", 10);
				sess.put("passenger_voice", "aSB3aWxsIGJlIHRoZXJl");
				sess.put("passenger_voice_format", "m4a");
				_json_data.put("taxi_request", sess);
			} catch (JSONException e) {
				//_info.append("form json error: "+e.toString());
			}
			execute(url, _useragent, GetInfoTask.TYPE_POST);
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
			//_info.setText("Get "+this.getHttpCode()+"\n");
			if ( succ ) {
				String data = this.toString();
				//_info.append("get result: \n"+data);
				JSONTokener jsParser = new JSONTokener(data);
				
				try {
					switch ( _type ) {
					case TYPE_GET_TAXI:
						doGetTaxi(data);
						break;
					case TYPE_GET_REQ:
						doGetRequest(jsParser);
						break;
					default:
						break;
					} 
				} catch (JSONException e) {
					//e.printStackTrace();
					try {
						JSONObject retobj = (JSONObject)jsParser.nextValue();
						JSONObject err = retobj.getJSONObject("errors");
						Toast.makeText(LocationOverlayDemo.this.getApplicationContext(), "返回: "+retobj.toString(), Toast.LENGTH_LONG).show();
						//_info.append("errmsg \""+err.getJSONArray("base").getString(0)+"\"");
						//_info.append("\ncookies: "+_sess_key.getName()+" "+_sess_key.getValue()+"\n");
					} catch (JSONException ee) {
						//_info.append("json error: "+ee.toString()+"\n"+"ret: "+data);
					}
				} catch (Exception e) {
					Toast.makeText(LocationOverlayDemo.this.getApplicationContext(), "错误返回: "+data, Toast.LENGTH_LONG).show();
				}
			} else {
				//_info.append("get errmsg: \n"+_errmsg);
			}
		}
		
		@Override
		protected void onPostExecPost(Boolean succ) {
			String data = this.toString();
			//_info.setText("Post "+this.getHttpCode()+"\n");
			if ( succ ) {
				//_info.append("result: "+this.getHttpCode()+"\n"+this.toString());
				JSONTokener jsParser = new JSONTokener(data);
				JSONObject ret = null;

				try {
					ret = (JSONObject)jsParser.nextValue();
					mReqId = ret.getInt("id");
					//_info.append("result \n"+ret.getString("token_key")+": "+ret.getString("token_value"));
					//_sess_key = new BasicNameValuePair(ret.getString("token_key"), ret.getString("token_value"));
				} catch (JSONException e) {
					//e.printStackTrace();
					try {
						JSONObject err = ret.getJSONObject("errors");
						//_info.append("errmsg \""+err.getJSONArray("base").getString(0)+"\"");
						_errmsg = err.getJSONArray("base").getString(0);
						succ = false;
					} catch (Exception ee) {
						//_info.append("json error: "+ee.toString()+"\n");
						//_info.append("to json: "+_json_data.toString());
						_errmsg = "数据通信异常，请检查云服务器配置，或联系服务商: "+ret.toString();
						succ = false;
					}
				} catch (Exception e) {
					_errmsg = "网络错误，请检查云服务器配置，并确认网络正常后再试";
					succ = false;
				}
				
			} else {
				//_info.append("errmsg: \n"+_errmsg);
			}
			
			if( succ == false ) {
				Toast.makeText(LocationOverlayDemo.this.getApplicationContext(), "错误返回: "+_errmsg+"\n"+data, Toast.LENGTH_SHORT).show();
			}
		}
		
		private void doGetTaxi(String data) throws JSONException {
			JSONArray ret = new JSONArray(data);
				
			//清除所有添加的Overlay
	        ov.removeAll();
	        mGeoList.clear();
	        
			//添加一个item
	    	//当要添加的item较多时，可以使用addItem(List<OverlayItem> items) 接口
	        for( int i=0; i<ret.length(); ++i ) {
	        	JSONObject pos = ret.getJSONObject(i);
	        	int lat = (int)(pos.getDouble("lat")*1E6);
	        	int lng = (int)(pos.getDouble("lng")*1E6);
		        OverlayItem item= new OverlayItem(new GeoPoint(lat, lng),
		        		"司机"+pos.getInt("driver_id"),"创建时间: "+pos.getString("created_at"));
			   	item.setMarker(res.get(i%res.size()));
			   	mGeoList.add(item);
	        }
	    	if ( ov.size() < mGeoList.size()){
	    		//ov.addItem(mGeoList.get(ov.size() ));
	    		ov.addItem(mGeoList);
	    	}
		    mMapView.refresh();
		    
		    if ( mReqId < 0 ) {
		    	Toast.makeText(LocationOverlayDemo.this.getApplicationContext(), "附近有"+ret.length()+"辆出租车",
						Toast.LENGTH_SHORT).show();
		    }
		}
		
		private void doGetRequest(JSONTokener jsParser) throws JSONException {
			// {"id":28,"state":"Waiting_Driver_Response","passenger_lat":8.0,"passenger_lng":8.0,"passenger_voice_url":"/uploads/taxi_request/voice/2013-05-31/03bd766e8ecc2e2429f1610c7bf6c3ec.m4a"}
			// 用户只要处理state即可
			String stat = ((JSONObject)jsParser.nextValue()).getString("state");
			if ( stat.equals("Waiting_Driver_Response") ) {
				// 继续等待
				Toast.makeText(LocationOverlayDemo.this.getApplicationContext(), "请求["+mReqId+"]等待司机响应, 附近"+mGeoList.size()+"辆",
						Toast.LENGTH_SHORT).show();
			} else if ( stat.equals("Waiting_Passenger_Confirm") ) {
				// 司机已应答，等待用户确认
				Toast.makeText(LocationOverlayDemo.this.getApplicationContext(), "请求["+mReqId+"]已有司机应答, 附近"+mGeoList.size()+"辆",
						Toast.LENGTH_SHORT).show();
			} else if ( stat.equals("TimeOut") ) {
				// 超时
				Toast.makeText(LocationOverlayDemo.this.getApplicationContext(), "请求["+mReqId+"]已超时, 附近"+mGeoList.size()+"辆",
						Toast.LENGTH_SHORT).show();
				mReqId = -1;
			} else if ( stat.equals("Canceled_By_Passenger") ) {
				// 用户取消
				Toast.makeText(LocationOverlayDemo.this.getApplicationContext(), "请求["+mReqId+"]已被取消, 附近"+mGeoList.size()+"辆",
						Toast.LENGTH_SHORT).show();
				mReqId = -1;
			}
		}
	}
}


