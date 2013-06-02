package com.benbenTaxi.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.platform.comapi.map.Projection;
import com.benbenTaxi.R;

public class BaseMapDemo extends Activity {

	final static String TAG = "MainActivty";
	private MapView mMapView = null;
	
	private MapController mMapController = null;

	FrameLayout mMapViewContainer = null;
	MKMapViewListener mMapListener = null;
	Button button1 = null;
	Button buttonRotate = null;
	Button buttonOverlook = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DemoApplication app = (DemoApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            app.mBMapManager.init(DemoApplication.strKey,new DemoApplication.MyGeneralListener());
        }
        setContentView(R.layout.activity_main);
        mMapView = (MapView)findViewById(R.id.bmapView);
        mMapController = mMapView.getController();
        initMapView();
        mMapController.enableClick(true);
        mMapController.setZoom(12);
        mMapView.displayZoomControls(true);
//        mMapView.setTraffic(true);
//        mMapView.setSatellite(true);
        mMapView.setDoubleClickZooming(true);
        mMapView.setOnTouchListener(null);
       
        mMapListener = new MKMapViewListener() {
			
			@Override
			public void onMapMoveFinish() {
				// 在此处理地图移动完成消息回调
			}
			
			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				String title = "";
				if (mapPoiInfo != null){
					title = mapPoiInfo.strText;
					Toast.makeText(BaseMapDemo.this,title,Toast.LENGTH_SHORT).show();
					mMapController.animateTo(mapPoiInfo.geoPt);
				}
			}

			@Override
			public void onGetCurrentMap(Bitmap b) {
				// 回调图片数据，保存在"/mnt/sdcard/test"目录下
				Log.d("test", "test"+"onGetCurrentMap");
				 File file = new File("/mnt/sdcard/test.png");
	                FileOutputStream out;
	                try{
	                        out = new FileOutputStream(file);
	                        if(b.compress(Bitmap.CompressFormat.PNG, 70, out)) 
	                        {
	                                out.flush();
	                                out.close();
	                        }
	                } 
	                catch (FileNotFoundException e) 
	                {
	                        e.printStackTrace();
	                } 
	                catch (IOException e) 
	                {
	                        e.printStackTrace(); 
	                }
			}

			@Override
			public void onMapAnimationFinish() {
				// 在此处理地图动画完成回调

			}
		};
		mMapView.regMapViewListener(DemoApplication.getInstance().mBMapManager, mMapListener);
		
		OnClickListener captureListener = new OnClickListener(){
			public void onClick(View v) {
				captureMapClick();
			}
        };
        OnClickListener rotateListener = new OnClickListener(){
			public void onClick(View v) {
				setMapRotateClick();
			}
        };
        OnClickListener overlookListener = new OnClickListener(){
			public void onClick(View v) {
				setMapOverlookingClick();
			}
        };
		button1 = (Button)findViewById(R.id.button1);
		button1.setOnClickListener(captureListener);
		buttonRotate = (Button)findViewById(R.id.button2);
		buttonRotate.setOnClickListener(rotateListener);
		buttonOverlook = (Button)findViewById(R.id.button3);
		buttonOverlook.setOnClickListener(overlookListener);
		
    }
    
    private void initMapView() {
        GeoPoint centerpt = mMapView.getMapCenter();
        int maxLevel = mMapView.getMaxZoomLevel();
        int zoomlevel = mMapView.getZoomLevel();
        boolean isTraffic = mMapView.isTraffic();
        boolean isSatillite = mMapView.isSatellite();
        boolean isDoubleClick = mMapView.isDoubleClickZooming();
        mMapView.setLongClickable(true);
        //mMapController.setMapClickEnable(true);
       // mMapView.setSatellite(false);
    }
    //截图，异步方法
    public void captureMapClick() {
    	mMapView.getCurrentMap();
    }
    //设置地图旋转角度
    public void setMapRotateClick(){
    	EditText rotate = (EditText)findViewById(R.id.rotateangle);
    	mMapView.getController().setRotation(Integer.parseInt(rotate.getText().toString()));
    }
    //设置地图俯视角度
    public void setMapOverlookingClick(){
    	EditText overlooking = (EditText)findViewById(R.id.overlookangle);
    	mMapView.getController().setOverlooking(Integer.parseInt(overlooking.getText().toString()));
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	}

}
