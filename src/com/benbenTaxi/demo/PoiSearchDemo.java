package com.benbenTaxi.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.benbenTaxi.R;


public class PoiSearchDemo extends Activity {
	static MapView mMapView = null;
	
	public MKMapViewListener mMapListener = null;

	MKSearch mSearch = null;   // 搜索模块，也可去掉地图模块独立使用
	public static String mStrSuggestions[] = {};
	Button mBtnSearch = null;  // 搜索按钮
    Button mBtnDetailSearch = null;  // 详细搜搜按钮
    Button mSuggestionSearch = null;  //suggestion搜索
    Button nextData = null;
    public int load_Index;
    ListView mSuggestionList = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
         DemoApplication app = (DemoApplication)this.getApplication();
         if (app.mBMapManager == null) {
             app.mBMapManager = new BMapManager(this);
             app.mBMapManager.init(DemoApplication.strKey,new DemoApplication.MyGeneralListener());
         }
         setContentView(R.layout.activity_poisearch);
         mMapView = (MapView)findViewById(R.id.bmapView);
        
         initMapView();
       
         mMapListener = new MKMapViewListener() {
			
			@Override
			public void onMapMoveFinish() {
				//Log.d("hjtest", "hjtest"+"onMapMoveFinish");
			}
			
			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				String title = "";
				if (mapPoiInfo != null){
					title = mapPoiInfo.strText;
					Toast.makeText(PoiSearchDemo.this,title,Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onGetCurrentMap(Bitmap b) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onMapAnimationFinish() {
				// TODO Auto-generated method stub
				//Log.d("hjtest", "hjtest"+"onMapAnimationFinish");
				
			}
		};
		mMapView.regMapViewListener(DemoApplication.getInstance().mBMapManager, mMapListener);
		mMapView.getController().enableClick(true);
        mMapView.getController().setZoom(12);
		
		// 初始化搜索模块，注册事件监听
        mSearch = new MKSearch();
        mSearch.init(app.mBMapManager, new MKSearchListener(){

            @Override
            public void onGetPoiDetailSearchResult(int type, int error) {
                if (error != 0) {
                    Toast.makeText(PoiSearchDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(PoiSearchDemo.this, "成功，查看详情页面", Toast.LENGTH_SHORT).show();
                }
            }
            
            public void onGetPoiResult(MKPoiResult res, int type, int error) {
                // 错误号可参考MKEvent中的定义
                if (error != 0 || res == null) {
                    Toast.makeText(PoiSearchDemo.this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
                    return;
                }
                // 将地图移动到第一个POI中心点
                if (res.getCurrentNumPois() > 0) {
                    // 将poi结果显示到地图上
                    MyPoiOverlay poiOverlay = new MyPoiOverlay(PoiSearchDemo.this, mMapView, mSearch);
                    poiOverlay.setData(res.getAllPoi());
                    mMapView.getOverlays().clear();
                    mMapView.getOverlays().add(poiOverlay);
                    mMapView.refresh();
                    //当ePoiType为2（公交线路）或4（地铁线路）时， poi坐标为空
                    for( MKPoiInfo info : res.getAllPoi() ){
                    	if ( info.pt != null ){
                    		mMapView.getController().animateTo(info.pt);
                    		break;
                    	}
                    }
                } else if (res.getCityListNum() > 0) {
                    String strInfo = "在";
                    for (int i = 0; i < res.getCityListNum(); i++) {
                        strInfo += res.getCityListInfo(i).city;
                        strInfo += ",";
                    }
                    strInfo += "找到结果";
                    Toast.makeText(PoiSearchDemo.this, strInfo, Toast.LENGTH_LONG).show();
                }
            }
            public void onGetDrivingRouteResult(MKDrivingRouteResult res,
                    int error) {
            }
            public void onGetTransitRouteResult(MKTransitRouteResult res,
                    int error) {
            }
            public void onGetWalkingRouteResult(MKWalkingRouteResult res,
                    int error) {
            }
            public void onGetAddrResult(MKAddrInfo res, int error) {
            }
            public void onGetBusDetailResult(MKBusLineResult result, int iError) {
            }
            @Override
            public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
                if (arg1 != 0 || res == null) {
                    Toast.makeText(PoiSearchDemo.this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
                    return;
                }
                int nSize = res.getSuggestionNum();
                mStrSuggestions = new String[nSize];

                for (int i = 0; i < nSize; i++) {
                    mStrSuggestions[i] = res.getSuggestion(i).city + res.getSuggestion(i).key;
                }
                ArrayAdapter<String> suggestionString = new ArrayAdapter<String>(PoiSearchDemo.this, android.R.layout.simple_list_item_1,mStrSuggestions);
                mSuggestionList.setAdapter(suggestionString);
                Toast.makeText(PoiSearchDemo.this, "suggestion callback", Toast.LENGTH_LONG).show();

            }
            
        });
        mSuggestionList = (ListView) findViewById(R.id.listView1);
        // 设定搜索按钮的响应
        mBtnSearch = (Button)findViewById(R.id.search);
        
        OnClickListener clickListener = new OnClickListener(){
            public void onClick(View v) {
                SearchButtonProcess(v);
            }
        };
        mBtnSearch.setOnClickListener(clickListener); 
        
        nextData = (Button) findViewById(R.id.map_next_data);
        nextData.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //搜索下一组poi
                int flag = mSearch.goToPoiPage(++load_Index);
                if (flag != 0) {
                    Toast.makeText(PoiSearchDemo.this, "先搜索开始，然后再搜索下一组数据", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        // 设定suggestion响应
        mSuggestionSearch = (Button)findViewById(R.id.suggestionsearch);

        OnClickListener clickListener1 = new OnClickListener(){
            public void onClick(View v) {
                SuggestionSearchButtonProcess(v);
            }
        };
        mSuggestionSearch.setOnClickListener(clickListener1); 
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
        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(true);
    }
    
    void SearchButtonProcess(View v) {
        if (mBtnSearch.equals(v)) {
          EditText editCity = (EditText)findViewById(R.id.city);
          EditText editSearchKey = (EditText)findViewById(R.id.searchkey);
          mSearch.poiSearchInCity(editCity.getText().toString(), 
                  editSearchKey.getText().toString());
        }
    }

    void SuggestionSearchButtonProcess(View v) {
        EditText editSearchKey = (EditText)findViewById(R.id.suggestionkey);
        mSearch.suggestionSearch(editSearchKey.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

}
