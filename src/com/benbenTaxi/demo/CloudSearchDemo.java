package com.benbenTaxi.demo;

import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.cloud.Bounds;
import com.baidu.mapapi.cloud.BoundsSearchInfo;
import com.baidu.mapapi.cloud.CustomPoiInfo;
import com.baidu.mapapi.cloud.DetailResult;
import com.baidu.mapapi.cloud.DetailSearchInfo;
import com.baidu.mapapi.cloud.GeoSearchListener;
import com.baidu.mapapi.cloud.GeoSearchManager;
import com.baidu.mapapi.cloud.GeoSearchResult;
import com.baidu.mapapi.cloud.NearbySearchInfo;
import com.baidu.mapapi.cloud.RegionSearchInfo;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.benbenTaxi.R;

public class CloudSearchDemo extends Activity implements GeoSearchListener {
    
    MapView mMapView;
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        DemoApplication app = (DemoApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            app.mBMapManager.init(DemoApplication.strKey,new DemoApplication.MyGeneralListener());
        }
        setContentView(R.layout.lbssearch);
        GeoSearchManager.getInstance().init(CloudSearchDemo.this);
        
        mMapView = (MapView)findViewById(R.id.bmapView);
        mMapView.getController().enableClick(true);
        mMapView.getController().setZoom(12);
        mMapView.displayZoomControls(true);
        mMapView.setDoubleClickZooming(true);
        findViewById(R.id.regionSearch).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RegionSearchInfo r = new RegionSearchInfo();
                r.queryWords = "北京市五中";
                r.ak = "请输入你的ak";
                r.cityName = "北京";
                r.filter.put("databox", 848);
                r.scope = 2;
                GeoSearchManager.getInstance().searchRegion(r);
            }
        });
        findViewById(R.id.nearbySearch).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NearbySearchInfo r = new NearbySearchInfo();
                r.queryWords = "北京";
                r.ak = "请输入你的ak";
                r.location = new GeoPoint(39956948, 116412214);
                r.radius = 10000000;
                r.filter.put("databox", 848);
                r.scope = 2;
                GeoSearchManager.getInstance().searchNearby(r);
            }
        });
        
        
        findViewById(R.id.boundsSearch).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BoundsSearchInfo r = new BoundsSearchInfo();
                r.queryWords = "五中";
                r.ak = "请输入你的ak";
                r.bounds = new Bounds(39843895,116402214,40956948,116431457);
                r.filter.put("databox", 848);
                r.scope = 2;
                GeoSearchManager.getInstance().searchBounds(r);
            }
        });
        findViewById(R.id.detailsSearch).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailSearchInfo r = new DetailSearchInfo();
                r.id = 81217;
                r.ak = "请输入你的ak";
                r.scope = 2;
                GeoSearchManager.getInstance().searchDetail(r);
            }
        });
    }
    
    
    @Override
    protected void onDestroy() {
        mMapView.destroy();
        super.onDestroy();
    }
    
    @Override
    public void onGetGeoDetailsResult(DetailResult result, int type, int iError) {
        if (result != null) {
            if (result.content != null) {
                Toast.makeText(CloudSearchDemo.this, result.content.name, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(CloudSearchDemo.this, "status:" + result.status, Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    public void onGetGeoResult(GeoSearchResult result, int type, int iError) {
        if (result != null && result.poiList!= null && result.poiList.size() > 0) {
            CloudOverlay poiOverlay = new CloudOverlay(this,mMapView);
            poiOverlay.setData(result.poiList);
            mMapView.getOverlays().clear();
            mMapView.getOverlays().add(poiOverlay);
            mMapView.refresh();
            mMapView.getController().animateTo(new GeoPoint((int)(result.poiList.get(0).latitude * 1e6), (int)(result.poiList.get(0).longitude * 1e6)));
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
}
class CloudOverlay extends ItemizedOverlay {

    List<CustomPoiInfo> mLbsPoints;
    Activity mContext;
    
    public CloudOverlay(Activity context ,MapView mMapView) {
        super(null,mMapView);
        mContext = context;
    }

    public void setData(List<CustomPoiInfo> lbsPoints) {
        if (lbsPoints != null) {
            mLbsPoints = lbsPoints;
           // super.populate();
        }
        for ( CustomPoiInfo rec : mLbsPoints ){
            GeoPoint pt = new GeoPoint((int)(rec.latitude * 1e6), (int)(rec.longitude * 1e6));
            OverlayItem item = new OverlayItem(pt , rec.name, rec.address);
            Drawable marker1 = this.mContext.getResources().getDrawable(R.drawable.icon_marka);
            item.setMarker(marker1);
            addItem(item);
        }
    }
    /*
    @Override
    protected OverlayItem createItem(int i) {
        CustomPoiInfo rec = mLbsPoints.get(i);
        GeoPoint pt = new GeoPoint((int)(rec.latitude * 1e6), (int)(rec.longitude * 1e6));
        OverlayItem item = new OverlayItem(pt , rec.name, rec.address);
        Drawable marker1 = this.mContext.getResources().getDrawable(R.drawable.icon_marka);
        item.setMarker(marker1);
        return item;
    }
    
    @Override
    public int size() {
        if (mLbsPoints != null)
            return mLbsPoints.size();
        else
            return 0;
    }
    */
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return super.clone();
    }
    
    @Override
    protected boolean onTap(int arg0) {
        CustomPoiInfo item = mLbsPoints.get(arg0);
        Toast.makeText(mContext, item.name,Toast.LENGTH_LONG).show();
        return super.onTap(arg0);
    }
    
}
