package com.benbenTaxi.demo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.benbenTaxi.R;

public class GeoCoderDemo extends Activity {
	Button mBtnReverseGeoCode = null;	// 将坐标反编码为地址
	Button mBtnGeoCode = null;	// 将地址编码为坐标
	
	MapView mMapView = null;	// 地图View
	MKSearch mSearch = null;	// 搜索模块，也可去掉地图模块独立使用
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        DemoApplication app = (DemoApplication)this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			app.mBMapManager.init(DemoApplication.strKey,new DemoApplication.MyGeneralListener());
		}
		setContentView(R.layout.geocoder);
        mMapView = (MapView)findViewById(R.id.bmapView);
        mMapView.getController().enableClick(true);
        mMapView.getController().setZoom(12);
        mMapView.displayZoomControls(true);
        mMapView.setDoubleClickZooming(true);
        
        // 初始化搜索模块，注册事件监听
        mSearch = new MKSearch();
        mSearch.init(app.mBMapManager, new MKSearchListener() {
            @Override
            public void onGetPoiDetailSearchResult(int type, int error) {
            }
            
			public void onGetAddrResult(MKAddrInfo res, int error) {
				if (error != 0) {
					String str = String.format("错误号：%d", error);
					Toast.makeText(GeoCoderDemo.this, str, Toast.LENGTH_LONG).show();
					return;
				}
				mMapView.getController().animateTo(res.geoPt);
					
				String strInfo = String.format("纬度：%f 经度：%f\r\n", res.geoPt.getLatitudeE6()/1e6, res.geoPt.getLongitudeE6()/1e6);

				Toast.makeText(GeoCoderDemo.this, strInfo, Toast.LENGTH_LONG).show();
				Drawable marker = getResources().getDrawable(R.drawable.icon_markf);  //得到需要标在地图上的资源
				marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());   //为maker定义位置和边界
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(new OverItemT(marker, GeoCoderDemo.this, res.geoPt, res.strAddr,mMapView));
				mMapView.refresh();
			}
			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				if (error != 0 || res == null) {
					Toast.makeText(GeoCoderDemo.this, "解析失败", Toast.LENGTH_LONG).show();
					return;
				}
				if (res != null && res.getCurrentNumPois() > 0) {
					GeoPoint ptGeo = res.getAllPoi().get(0).pt;
					// 移动地图到该点：
					mMapView.getController().animateTo(ptGeo);
					
					String strInfo = String.format("纬度：%f 经度：%f\r\n", ptGeo.getLatitudeE6()/1e6, ptGeo.getLongitudeE6()/1e6);
					strInfo += "\r\n附近有：";
					for (int i = 0; i < res.getAllPoi().size(); i++) {
						strInfo += (res.getAllPoi().get(i).name + ";");
					}
					Toast.makeText(GeoCoderDemo.this, strInfo, Toast.LENGTH_LONG).show();
				}
			}
			public void onGetDrivingRouteResult(MKDrivingRouteResult res, int error) {
			}
			public void onGetTransitRouteResult(MKTransitRouteResult res, int error) {
			}
			public void onGetWalkingRouteResult(MKWalkingRouteResult res, int error) {
			}
			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}
			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
			}

        });
        
        // 设定地理编码及反地理编码按钮的响应
        mBtnReverseGeoCode = (Button)findViewById(R.id.reversegeocode);
        mBtnGeoCode = (Button)findViewById(R.id.geocode);
        
        OnClickListener clickListener = new OnClickListener(){
			public void onClick(View v) {
					SearchButtonProcess(v);
			}
        };
        
        mBtnReverseGeoCode.setOnClickListener(clickListener); 
        mBtnGeoCode.setOnClickListener(clickListener); 
	}
	
	@Override
    protected void onDestroy() {
        mMapView.destroy();
        super.onDestroy();
    }
	
	void SearchButtonProcess(View v) {
		if (mBtnReverseGeoCode.equals(v)) {
			GeoPoint ptCenter = new GeoPoint(39904965, 116327764);
			mSearch.reverseGeocode(ptCenter);
		} else if (mBtnGeoCode.equals(v)) {
			EditText editCity = (EditText)findViewById(R.id.city);
			EditText editGeoCodeKey = (EditText)findViewById(R.id.geocodekey);
			mSearch.geocode(editGeoCodeKey.getText().toString(), editCity.getText().toString());
		}
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
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mMapView.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mMapView.onRestoreInstanceState(savedInstanceState);
    }

	class OverItemT extends ItemizedOverlay {
		private List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();

		public OverItemT(Drawable marker, Context context, GeoPoint pt, String title,MapView mMapView) {
			super(marker,mMapView);
			OverlayItem item = new OverlayItem(pt, title, null);
			mGeoList.add(item);
			addItem(item);
			//populate();
		}
        /*
		@Override
		protected OverlayItem createItem(int i) {
			return mGeoList.get(i);
		}

		@Override
		public int size() {
			return mGeoList.size();
		}
		*/

	}

}
