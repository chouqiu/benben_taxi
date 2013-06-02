package com.benbenTaxi.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Symbol;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.benbenTaxi.R;

public class GeometryDemo extends Activity{

	final static String TAG = "MainActivty";
	static MapView mMapView = null;
	
	public MKMapViewListener mMapListener = null;
	FrameLayout mMapViewContainer = null;
	
	Button lineButton = null;
	Button polygenButton = null;
	Button circleButton = null;
	EditText indexText = null;
	Button pointButton = null;
	Button removeButton = null;
	Button clearButton = null;
	
	 long envelopeId = 0;
	int index =0;
	
    double mLat1 = 39.90923; // point1纬度
   	double mLon1 = 116.357428; // point1经度
   	double mLat2 = 39.90923;
   	double mLon2 = 116.397428;
	double mLat3 = 39.94923;
	double mLon3 = 116.437428;

	
	GraphicsOverlay graphicsOverlay = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	DemoApplication app = (DemoApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            app.mBMapManager.init(DemoApplication.strKey,new DemoApplication.MyGeneralListener());
        }
        setContentView(R.layout.activity_geometry);
        mMapView = (MapView)findViewById(R.id.bmapView);
        initMapView();
        mMapView.getController().setZoom(13);
        mMapView.getController().enableClick(true);
        
        lineButton = (Button)findViewById(R.id.button1);
        polygenButton = (Button)findViewById(R.id.button2);
        circleButton = (Button)findViewById(R.id.button3);
        pointButton = (Button)findViewById(R.id.button4);
        removeButton = (Button)findViewById(R.id.button5);
        clearButton = (Button)findViewById(R.id.button6);
        
        graphicsOverlay = new GraphicsOverlay(mMapView);
        mMapView.getOverlays().add(graphicsOverlay);
        
       
        OnClickListener drawLineListener = new OnClickListener(){
			public void onClick(View v) {
				testLineClick();
			}
        };
        OnClickListener drawPolygenListener = new OnClickListener(){
        	public void onClick(View v){
        		testEnvelopeClick();
        	}
        };
        OnClickListener drawCircleListener = new OnClickListener(){
            public void onClick(View v){
                testCircleClick();
            }
        };
        OnClickListener removeListener = new OnClickListener(){
            public void onClick(View v){
                testRemoveClick();
            }
        };
        OnClickListener drawpointListener = new OnClickListener(){
            public void onClick(View v){
                testPointClick();
            }
        };
        OnClickListener clearListener = new OnClickListener(){
            public void onClick(View v){
                testClearClick();
            }
        };
        
        lineButton.setOnClickListener(drawLineListener);
        polygenButton.setOnClickListener(drawPolygenListener);
        circleButton.setOnClickListener(drawCircleListener);
        pointButton.setOnClickListener(drawpointListener);
        removeButton.setOnClickListener(removeListener);
        clearButton.setOnClickListener(clearListener);
        
        mMapListener = new MKMapViewListener() {
			
			@Override
			public void onMapMoveFinish() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				// TODO Auto-generated method stub
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

    private void initMapView() {
        mMapView.setLongClickable(true);
        //mMapController.setMapClickEnable(true);
        //mMapView.setSatellite(false);
    }
    public void testRemoveClick(){
    	graphicsOverlay.removeGraphic(envelopeId);
    	mMapView.refresh();
    }
    public void testClearClick(){
    	graphicsOverlay.removeAll();
    	mMapView.refresh();
    }
    public void testLineClick(){
   
    	int lat = (int) (mLat1*1E6);
	   	int lon = (int) (mLon1*1E6);   	
	   	GeoPoint pt1 = new GeoPoint(lat, lon);
	   	lat = (int) (mLat2*1E6);
	   	lon = (int) (mLon2*1E6);
	   	GeoPoint pt2 = new GeoPoint(lat, lon);
		lat = (int) (mLat3*1E6);
	   	lon = (int) (mLon3*1E6);
	    GeoPoint pt3 = new GeoPoint(lat, lon);
	    
	  //构建点并显示

  		Geometry lineGeometry = new Geometry();
  		GeoPoint[] linePoints = new GeoPoint[3];
  		linePoints[0] = pt1;
  		linePoints[1] = pt2;
  		linePoints[2] = pt3;
  		lineGeometry.setPolyLine(linePoints);
  		
  		Symbol lineSymbol = new Symbol();
  		Symbol.Color lineColor = lineSymbol.new Color();
  		lineColor.red = 255;
  		lineColor.green = 0;
  		lineColor.blue = 0;
  		lineColor.alpha = 126;
  		lineSymbol.setLineSymbol(lineColor, 10);
  		
  		Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
  		
  		graphicsOverlay.setData(lineGraphic);
  		mMapView.refresh();
    	
    }
   
    public void testEnvelopeClick(){
    	int lat = (int) (mLat1*1E6);
	   	int lon = (int) (mLon1*1E6);   	
	   	GeoPoint pt1 = new GeoPoint(lat, lon);
		lat = (int) (mLat3*1E6);
	   	lon = (int) (mLon3*1E6);
	    GeoPoint pt3 = new GeoPoint(lat, lon);
	    
	  //构建点并显示
  		Geometry envelopeGeometry = new Geometry();
  	
  		envelopeGeometry.setEnvelope(pt1, pt3);
  		
  		Symbol envelopeSymbol = new Symbol();
 		Symbol.Color envelopeColor = envelopeSymbol.new Color();
 		envelopeColor.red = 0;
 		envelopeColor.green = 0;
 		envelopeColor.blue = 255;
 		envelopeColor.alpha = 126;
  		envelopeSymbol.setSurface(envelopeColor,1,3);
  		
  		Graphic envelopeGraphic = new Graphic(envelopeGeometry, envelopeSymbol);
  		
  		envelopeId = graphicsOverlay.setData(envelopeGraphic);
  		mMapView.refresh();
    }
    public void testPointClick(){
    	int lat = (int) (mLat3*1E6);
	   	int lon = (int) (mLon3*1E6);   	
	   	GeoPoint pt1 = new GeoPoint(lat, lon);
	   	
	    
	  //构建点并显示
  		Geometry pointGeometry = new Geometry();
  	
  		pointGeometry.setPoint(pt1, 10);
  		
  		Symbol pointSymbol = new Symbol();
 		Symbol.Color pointColor = pointSymbol.new Color();
 		pointColor.red = 0;
 		pointColor.green = 255;
 		pointColor.blue = 255;
 		pointColor.alpha = 126;
 		pointSymbol.setPointSymbol(pointColor);
  		
  		Graphic pointGraphic = new Graphic(pointGeometry, pointSymbol);
  		
  		graphicsOverlay.setData(pointGraphic);
  		mMapView.refresh();
    }
    public void testCircleClick() {
    	int lat = (int) (mLat1*1E6);
	   	int lon = (int) (mLon1*1E6);   	
	   	GeoPoint pt1 = new GeoPoint(lat, lon);
	   	
	    
	  //构建点并显示
  		Geometry circleGeometry = new Geometry();
  	
  		circleGeometry.setCircle(pt1, 2000);
  		
  		Symbol circleSymbol = new Symbol();
 		Symbol.Color circleColor = circleSymbol.new Color();
 		circleColor.red = 0;
 		circleColor.green = 255;
 		circleColor.blue = 0;
 		circleColor.alpha = 126;
  		circleSymbol.setSurface(circleColor,1,3);
  		
  		Graphic circleGraphic = new Graphic(circleGeometry, circleSymbol);
  		
  		graphicsOverlay.setData(circleGraphic);
  		mMapView.refresh();

   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}


