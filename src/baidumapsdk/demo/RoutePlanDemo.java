package baidumapsdk.demo;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class RoutePlanDemo extends Activity {

	Button mBtnDrive = null;	// 驾车搜索
	Button mBtnTransit = null;	// 公交搜索
	Button mBtnWalk = null;	// 步行搜索
	Button mBtnCusRoute = null; //自定义路线
	
	MapView mMapView = null;	// 地图View
	MKSearch mSearch = null;	// 搜索模块，也可去掉地图模块独立使用
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        DemoApplication app = (DemoApplication)this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			app.mBMapManager.init(DemoApplication.strKey,new DemoApplication.MyGeneralListener());
		}
		setContentView(R.layout.routeplan);
        mMapView = (MapView)findViewById(R.id.bmapView);
        initMapView();
        // 初始化搜索模块，注册事件监听
        mSearch = new MKSearch();
        mSearch.init(app.mBMapManager, new MKSearchListener(){

            @Override
            public void onGetPoiDetailSearchResult(int type, int error) {
            }
            
			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
				// 错误号可参考MKEvent中的定义
				if (error != 0 || res == null) {
					Toast.makeText(RoutePlanDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
					return;
				}
				RouteOverlay routeOverlay = new RouteOverlay(RoutePlanDemo.this, mMapView);
			    // 此处仅展示一个方案作为示例
			    routeOverlay.setData(res.getPlan(0).getRoute(0));
			    mMapView.getOverlays().clear();
			    mMapView.getOverlays().add(routeOverlay);
			    mMapView.refresh();
			    // 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			    mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
			    mMapView.getController().animateTo(res.getStart().pt);
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				if (error != 0 || res == null) {
					Toast.makeText(RoutePlanDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
					return;
				}
				TransitOverlay  routeOverlay = new TransitOverlay (RoutePlanDemo.this, mMapView);
			    // 此处仅展示一个方案作为示例
			    routeOverlay.setData(res.getPlan(0));
			    mMapView.getOverlays().clear();
			    mMapView.getOverlays().add(routeOverlay);
			    mMapView.refresh();
			    // 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			    mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
			    mMapView.getController().animateTo(res.getStart().pt);
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				if (error != 0 || res == null) {
					Toast.makeText(RoutePlanDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
					return;
				}
				RouteOverlay routeOverlay = new RouteOverlay(RoutePlanDemo.this, mMapView);
			    // 此处仅展示一个方案作为示例
				routeOverlay.setData(res.getPlan(0).getRoute(0));
			    mMapView.getOverlays().clear();
			    mMapView.getOverlays().add(routeOverlay);
			    mMapView.refresh();
			    // 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			    mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
			    mMapView.getController().animateTo(res.getStart().pt);
			    
			}
			public void onGetAddrResult(MKAddrInfo res, int error) {
			}
			public void onGetPoiResult(MKPoiResult res, int arg1, int arg2) {
			}
			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
			}

        });
        
        // 设定搜索按钮的响应
        mBtnDrive = (Button)findViewById(R.id.drive);
        mBtnTransit = (Button)findViewById(R.id.transit);
        mBtnWalk = (Button)findViewById(R.id.walk);
        mBtnCusRoute = (Button)findViewById(R.id.cusroute);
        
        OnClickListener clickListener = new OnClickListener(){
			public void onClick(View v) {
					SearchButtonProcess(v);
			}
        };
        
        mBtnDrive.setOnClickListener(clickListener); 
        mBtnTransit.setOnClickListener(clickListener); 
        mBtnWalk.setOnClickListener(clickListener);
        mBtnCusRoute.setOnClickListener(clickListener);
	}
	
	private void initMapView() {
	    mMapView.getController().enableClick(true);
	    mMapView.getController().setZoom(12);
	    mMapView.setBuiltInZoomControls(true);
	    mMapView.setDoubleClickZooming(true);  
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
	
	void SearchButtonProcess(View v) {
		// 处理搜索按钮响应
		EditText editSt = (EditText)findViewById(R.id.start);
		EditText editEn = (EditText)findViewById(R.id.end);
		
		// 对起点终点的name进行赋值，也可以直接对坐标赋值，赋值坐标则将根据坐标进行搜索
		MKPlanNode stNode = new MKPlanNode();
		stNode.name = editSt.getText().toString();
		MKPlanNode enNode = new MKPlanNode();
		enNode.name = editEn.getText().toString();

		// 实际使用中请对起点终点城市进行正确的设定
		if (mBtnDrive.equals(v)) {
			mSearch.drivingSearch("北京", stNode, "北京", enNode);
		} else if (mBtnTransit.equals(v)) {
			mSearch.transitSearch("北京", stNode, enNode);
		} else if (mBtnWalk.equals(v)) {
			mSearch.walkingSearch("北京", stNode, "北京", enNode);
		} else if (mBtnCusRoute.equals(v)){
			/** 演示自定义路线使用方法	
			 *  在北京地图上画一个北斗七星
			 *  想知道某个点的百度经纬度坐标请点击：http://api.map.baidu.com/lbsapi/getpoint/index.html	
			 */
			GeoPoint p1 = new GeoPoint((int)(39.9411 * 1E6),(int)(116.3714 * 1E6));
			GeoPoint p2 = new GeoPoint((int)(39.9498 * 1E6),(int)(116.3785 * 1E6));
			GeoPoint p3 = new GeoPoint((int)(39.9436 * 1E6),(int)(116.4029 * 1E6));
			GeoPoint p4 = new GeoPoint((int)(39.9329 * 1E6),(int)(116.4035 * 1E6));
			GeoPoint p5 = new GeoPoint((int)(39.9218 * 1E6),(int)(116.4115 * 1E6));
			GeoPoint p6 = new GeoPoint((int)(39.9144 * 1E6),(int)(116.4230 * 1E6));
			GeoPoint p7 = new GeoPoint((int)(39.9126 * 1E6),(int)(116.4387 * 1E6));
		    //起点坐标
			GeoPoint start = p1;
			//终点坐标
			GeoPoint stop  = p7;
			//第一站，站点坐标为p3,经过p1,p2
			GeoPoint[] step1 = new GeoPoint[3];
			step1[0] = p1;
			step1[1] = p2 ;
			step1[2] = p3;
			//第二站，站点坐标为p5,经过p4
			GeoPoint[] step2 = new GeoPoint[2];
			step2[0] = p4;
			step2[1] = p5;
			//第三站，站点坐标为p7,经过p6
			GeoPoint[] step3 = new GeoPoint[2];
			step3[0] = p6;
			step3[1] = p7;
			//站点数据保存在一个二维数据中
			GeoPoint [][] routeData = new GeoPoint[3][];
			routeData[0] = step1;
			routeData[1] = step2;
			routeData[2] = step3;
			//用站点数据构建一个MKRoute
			MKRoute route = new MKRoute();
			route.customizeRoute(start, stop, routeData);	
			//将包含站点信息的MKRoute添加到RouteOverlay中
			RouteOverlay routeOverlay = new RouteOverlay(RoutePlanDemo.this, mMapView);		
			routeOverlay.setData(route);
			//向地图添加构造好的RouteOverlay
			mMapView.getOverlays().clear();
			mMapView.getOverlays().add(routeOverlay);
		    mMapView.refresh();
			// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());					
			mMapView.getController().setCenter(p5);
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
