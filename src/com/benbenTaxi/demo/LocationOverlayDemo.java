package com.benbenTaxi.demo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Intent;
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
import com.benbenTaxi.v1.LoginActivity;
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
        
        Bundle bs = this.getIntent().getExtras();
        mTokenKey = bs.getString("token_key");
        mTokenVal = bs.getString("token_value");
        
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
		private String _useragent = "ning@benbentaxi";
		private JSONObject _json_data;
		private int _type;
		double _lat = 0.0, _lng = 0.0;
		
		public void getTaxi(double lng, double lat) {
			_lat = lat;
			_lng = lng;
			String url =  "http://"+mTestHost+"/api/v1/users/nearby_driver?lat="+_lat+"&lng="+_lng;
			super.initCookies(mTokenKey, mTokenVal, "42.121.55.211");
			execute(url, _useragent, super.TYPE_GET);
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
				JSONArray ret = null;
				
				try {
					ret = new JSONArray(data);
					
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
				    
					Toast.makeText(LocationOverlayDemo.this.getApplicationContext(), "附近有"+ret.length()+"辆出租车",
							Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
					Toast.makeText(LocationOverlayDemo.this.getApplicationContext(), "err返回: "+data, Toast.LENGTH_LONG).show();
				}
			} else {
				//_info.append("get errmsg: \n"+_errmsg);
			}
		}
		
		@Override
		protected void onPostExecPost(Boolean succ) {
			//_info.setText("Post "+this.getHttpCode()+"\n");
			if ( succ ) {
				//_info.append("result: "+this.getHttpCode()+"\n"+this.toString());
				JSONTokener jsParser = new JSONTokener(this.toString());
				JSONObject ret = null;

				try {
					ret = (JSONObject)jsParser.nextValue();
					//_info.append("result \n"+ret.getString("token_key")+": "+ret.getString("token_value"));
					//_sess_key = new BasicNameValuePair(ret.getString("token_key"), ret.getString("token_value"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
		}
	}
}


