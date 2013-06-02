package com.benbenTaxi.demo;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKOLSearchRecord;
import com.baidu.mapapi.map.MKOLUpdateElement;
import com.baidu.mapapi.map.MKOfflineMap;
import com.baidu.mapapi.map.MKOfflineMapListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.benbenTaxi.R;

public class OfflineDemo extends Activity implements MKOfflineMapListener {
	
	private MapView mMapView = null;
	private MKOfflineMap mOffline = null;
	private EditText mEditCityName;
	private EditText mEditCityId;
	private TextView mText;
	final static String TAG = "MainActivty";
	private MapController mMapController = null;
	LinearLayout mMapViewContainer = null;
	Button testItemButton = null;
	
	int cityid = -1;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        DemoApplication app = (DemoApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            app.mBMapManager.init(DemoApplication.strKey,new DemoApplication.MyGeneralListener());
        }
        setContentView(R.layout.activity_offline);
        mMapView = (MapView)findViewById(R.id.bmapView);
        mMapController = mMapView.getController();
        initMapView();
        
        mOffline = new MKOfflineMap();    
        // init offlinemap 
        mOffline.init(mMapController, this);
        ArrayList<MKOLUpdateElement> info = mOffline.getAllUpdateInfo();
        if (info != null) {
        	// Log.d("OfflineDemo", String.format("has %d city info", info.size()));
        	for ( MKOLUpdateElement e : info ){
        		// Log.d("OfflineDemo",String.format("updateinfo: %s %d %d", e.cityName,e.cityID,e.status));
        	}
        }
        
        ArrayList<MKOLSearchRecord> records = mOffline.getOfflineCityList();
        if (records != null) {
        	//Log.d("OfflineDemo", String.format("has %d hot city", records.size()));
        	for (MKOLSearchRecord r : records ){
        		 Log.d("OfflineDemo","hot city: "+r.cityName+" "+r.cityID + "  size:"+ r.size);
        	}
        }
         
        mEditCityName = (EditText)findViewById(R.id.city);
        mEditCityId = (EditText)findViewById(R.id.cityid);
        mText = (TextView)findViewById(R.id.text);
        mText.setText("welcome to baidu map");
        
        Button btn = (Button)findViewById(R.id.start);
        btn.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				
				try {
					cityid = Integer.parseInt(mEditCityId.getText().toString());
				} catch (Exception e) {					
				}
		        if (mOffline.start(cityid)) {
		        	// Log.d("OfflineDemo", String.format("start cityid:%d", cityid));
		        } else {
		        	// Log.d("OfflineDemo", String.format("not start cityid:%d", cityid));
		        }
			}
		});
        
        btn = (Button)findViewById(R.id.stop);
        btn.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				int cityid = -1;
				try {
					cityid = Integer.parseInt(mEditCityId.getText().toString());
				} catch (Exception e) {
					
				}
		        if (mOffline.pause(cityid)) {
		        	// Log.d("OfflineDemo", String.format("stop cityid:%d", cityid));
		        } else {
		        	// Log.d("OfflineDemo", String.format("not pause cityid:%d", cityid));
		        }
			}
		}); 
        
        btn = (Button)findViewById(R.id.search);
        btn.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				ArrayList<MKOLSearchRecord> records = mOffline.searchCity(mEditCityName.getText().toString());
				if (records == null || records.size() != 1)
					return;
				mEditCityId.setText(String.valueOf(records.get(0).cityID));
				MKOLUpdateElement element = mOffline.getUpdateInfo(Integer.parseInt(mEditCityId.getText().toString()));
			    if ( element != null ){
				    if ( element.geoPt != null )
			            mMapController.setCenter(element.geoPt);
			    }
			}
			
		}); 
        
        btn = (Button)findViewById(R.id.del);
        btn.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				int cityid = -1;
				try {
					cityid = Integer.parseInt(mEditCityId.getText().toString());
				} catch (Exception e) {
					
				}
		        if (mOffline.remove(cityid)) {
		        	// Log.d("OfflineDemo", String.format("del cityid:%d", cityid));
		        } else {
		        	// Log.d("OfflineDemo", String.format("not del cityid:%d", cityid));
		        }
			}
		}); 
        
        btn = (Button)findViewById(R.id.scan);
        btn.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				int num = mOffline.scan();
				if (num != 0)
					mText.setText(String.format("此次扫描共导入%d个离线包", num));
				// Log.d("OfflineDemo", String.format("scan offlinemap num:%d", num));
				ArrayList<MKOLUpdateElement> infos = mOffline.getAllUpdateInfo();
				// Log.d("OfflineDemo", String.format("scan offlinemap num:%d", num));
			}
		}); 
        
        btn = (Button)findViewById(R.id.get);
        btn.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				int cityid = -1;
				try {
					cityid = Integer.parseInt(mEditCityId.getText().toString());
				} catch (Exception e) {
					
				}
				MKOLUpdateElement element = mOffline.getUpdateInfo(cityid);
				if (element != null) {
					new AlertDialog.Builder(OfflineDemo.this)
					.setTitle(element.cityName)
					.setMessage(String.format("离线包大小: %.2fMB 已下载  %d%%", ((double)element.size)/1000000, element.ratio))
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int whichButton) {
							
						}
					}).show();
				}
				else {
					new AlertDialog.Builder(OfflineDemo.this)
					.setTitle(mEditCityName.getText().toString())
					.setMessage("该 城市离线地图未安装")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int whichButton) {
							
						}
					}).show();
				}
			}
		}); 
	}
	
	
	private void initMapView() {
	    mMapView.setLongClickable(true);
	    mMapView.setBuiltInZoomControls(true);
	    mMapView.getController().setZoom(14);
	}

	@Override
    protected void onPause() {
	    mOffline.pause(cityid);
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
        mOffline.destroy();
        mMapView.destroy();
        DemoApplication app = (DemoApplication)this.getApplication();
        if (app.mBMapManager != null) {
            app.mBMapManager.destroy();
            app.mBMapManager = null;
        }
        super.onDestroy();
    }

	@Override
	public void onGetOfflineMapState(int type, int state) {
		switch (type) {
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
			{
				// Log.d("OfflineDemo", String.format("cityid:%d update", state));
				MKOLUpdateElement update = mOffline.getUpdateInfo(state);
				if ( update != null )
				    mText.setText(String.format("%s : %d%%", update.cityName, update.ratio));
			}
			break;
		case MKOfflineMap.TYPE_NEW_OFFLINE:
			// Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
			mText.setText(String.format("新安装%d个离线地图",state));
			break;
		case MKOfflineMap.TYPE_VER_UPDATE:
			MKOLUpdateElement e = mOffline.getUpdateInfo(state);
			if ( e != null ){
			    // Log.d("OfflineDemo", String.format("%d has new offline map: ",e.cityID));
			    mText.setText(String.format("%s 有离线地图更新",e.cityName));
			}
			break;
		}
		 
	}

}