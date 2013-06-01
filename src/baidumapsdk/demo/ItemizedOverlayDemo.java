package baidumapsdk.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.utils.CoordinateConvert;
import com.baidu.platform.comapi.basestruct.GeoPoint;
/**
 *  在一个圆周上添加自定义overlay. 
 */
public class ItemizedOverlayDemo extends Activity {

	final static String TAG = "MainActivty";
	static MapView mMapView = null;
	private MapController mMapController = null;
	public MKMapViewListener mMapListener = null;
	Button testItemButton = null;
	Button removeItemButton = null;
	Button removeAllItemButton = null;
	EditText indexText = null;
	OverlayTest ov = null;
	/**
	 *  圆心经纬度坐标 
	 */
	int cLat = 39909230 ;
	int cLon = 116397428 ;
	// 存放overlayitem 
	public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	// 存放overlay图片
	public List<Drawable>  res = new ArrayList<Drawable>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	DemoApplication app = (DemoApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            app.mBMapManager.init(DemoApplication.strKey,new DemoApplication.MyGeneralListener());
        }
        setContentView(R.layout.activity_itemizedoverlay);
        mMapView = (MapView)findViewById(R.id.bmapView);
        mMapController = mMapView.getController();
        initMapView();
        mMapView.getController().setZoom(13);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(true);
        testItemButton = (Button)findViewById(R.id.button1);
        removeItemButton = (Button)findViewById(R.id.button2);
        removeAllItemButton = (Button)findViewById(R.id.button3);
        
        Drawable marker = ItemizedOverlayDemo.this.getResources().getDrawable(R.drawable.icon_marka);
	    mMapView.getOverlays().clear();
	    ov = new OverlayTest(marker, this,mMapView); 
	    mMapView.getOverlays().add(ov);
       
        OnClickListener clickListener = new OnClickListener(){
			public void onClick(View v) {
				testItemClick();
			}
        };
        OnClickListener removeListener = new OnClickListener(){
        	public void onClick(View v){
        		testRemoveItemClick();
        	}
        };
        OnClickListener removeAllListener = new OnClickListener(){
            public void onClick(View v){
                testRemoveAllItemClick();
            }
        };
        
        testItemButton.setOnClickListener(clickListener);
        removeItemButton.setOnClickListener(removeListener);
        removeAllItemButton.setOnClickListener(removeAllListener);
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
					Toast.makeText(ItemizedOverlayDemo.this,title,Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onGetCurrentMap(Bitmap b) {
			}

			@Override
			public void onMapAnimationFinish() {
				// TODO Auto-generated method stub
				
			}
		};
		mMapView.regMapViewListener(DemoApplication.getInstance().mBMapManager, mMapListener);
		
		res.add(getResources().getDrawable(R.drawable.icon_marka));
    	res.add(getResources().getDrawable(R.drawable.icon_markb));
		res.add(getResources().getDrawable(R.drawable.icon_markc));
		res.add(getResources().getDrawable(R.drawable.icon_markd));
		res.add(getResources().getDrawable(R.drawable.icon_markf));
		res.add(getResources().getDrawable(R.drawable.icon_markg));
		res.add(getResources().getDrawable(R.drawable.icon_markh));
		res.add(getResources().getDrawable(R.drawable.icon_marki));
		
		// overlay 数量 
		int iSize = 9;
		double pi = 3.1415926 ;
		// overlay半径
		int r = 50000;
		// 准备overlay 数据
		for (int i=0; i<iSize ; i++){
		   	int lat = (int) (cLat + r*Math.cos(2*i*pi/iSize));
		   	int lon = (int) (cLon + r*Math.sin(2*i*pi/iSize));
		   	OverlayItem item= new OverlayItem(new GeoPoint(lat,lon),"item"+i,"item"+i);
		   	item.setMarker(res.get(i%(res.size())));
		   	mGeoList.add(item);
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
    
    @Override
    protected void onDestroy() {
        mMapView.destroy();
        super.onDestroy();
    }
    
    private void initMapView() {
        mMapView.setLongClickable(true);
        //mMapController.setMapClickEnable(true);
        //mMapView.setSatellite(false);
    }
    public void testRemoveAllItemClick(){
    	//清除所有添加的Overlay
        ov.removeAll();	
        mMapView.refresh();
    	
    }
    public void testRemoveItemClick(){
    	//删除最后添加的overlay
    	if ( ov.size() > 0)
    	    ov.removeItem(ov.getItem(ov.size() -1));
	    mMapView.refresh();
    }
    
    public void testItemClick() {
    	//添加一个item
    	//当要添加的item较多时，可以使用addItem(List<OverlayItem> items) 接口
    	if ( ov.size() < mGeoList.size()){
    		ov.addItem(mGeoList.get(ov.size() ));
    		//ov.addItem(mGeoList);
    	}
	    mMapView.refresh();
    	
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

}

class OverlayTest extends ItemizedOverlay<OverlayItem> {
    public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	private Context mContext = null;
    static PopupOverlay pop = null;

    Toast mToast = null;
    
	public OverlayTest(Drawable marker,Context context, MapView mapView){
		super(marker,mapView);
		this.mContext = context;
        pop = new PopupOverlay( ItemizedOverlayDemo.mMapView,new PopupClickListener() {
			
			@Override
			public void onClickedPopup(int index) {
			    if (null == mToast)
			        mToast = Toast.makeText(mContext, "popup item :" + index + " is clicked.", Toast.LENGTH_SHORT);
			    else mToast.setText("popup item :" + index + " is clicked.");
			    mToast.show();
			}
		});
       // 自2.1.1 开始，使用 add/remove 管理overlay , 无需调用以下接口.
	   // populate();
		
	}
	protected boolean onTap(int index) {
		System.out.println("item onTap: "+index);
	    
	    Bitmap[] bmps = new Bitmap[3];
	    if (index %2 == 0) {
	        try {
	            bmps[0] = BitmapFactory.decodeStream(mContext.getAssets().open("marker1.png"));
	            bmps[1] = BitmapFactory.decodeStream(mContext.getAssets().open("marker2.png"));
	            bmps[2] = BitmapFactory.decodeStream(mContext.getAssets().open("marker3.png"));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    else {
	        try {
                bmps[2] = BitmapFactory.decodeStream(mContext.getAssets().open("marker1.png"));
                bmps[1] = BitmapFactory.decodeStream(mContext.getAssets().open("marker2.png"));
                bmps[0] = BitmapFactory.decodeStream(mContext.getAssets().open("marker3.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
	    }
        
	    pop.showPopup(bmps, getItem(index).getPoint(), 32);
		if (null == mToast)
            mToast = Toast.makeText(mContext, getItem(index).getTitle(), Toast.LENGTH_SHORT);
        else mToast.setText(getItem(index).getTitle());
		mToast.show();
		
		
		return true;
	}
	public boolean onTap(GeoPoint pt, MapView mapView){
		if (pop != null){
			pop.hidePop();
		}
		super.onTap(pt,mapView);
		return false;
	}
	
	// 自2.1.1 开始，使用 add/remove 管理overlay , 无需重写以下接口
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
