package com.benbenTaxi.v1;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

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
import com.benbenTaxi.v1.function.BaseLocationActivity;
import com.benbenTaxi.v1.function.BenbenOverlay;
import com.benbenTaxi.v1.function.DataPreference;
import com.benbenTaxi.v1.function.PopupWindowSize;
import com.benbenTaxi.v1.function.ShowDetail;
import com.benbenTaxi.v1.function.StatusMachine;
import com.benbenTaxi.v1.function.WaitingShow;
import com.benbenTaxi.v1.function.api.JsonHelper;
import com.benbenTaxi.v1.function.index.TaxiRequestIndexActivity;

public class BenbenLocationTest extends BaseLocationActivity {	
	static MapView mMapView = null;
	public MKMapViewListener mMapListener = null;
	private MapController mMapController = null;
	FrameLayout mMapViewContainer = null;
	
	MyLocationOverlay myLocationOverlay = null;
	BenbenOverlay ov = null;
	// 存放overlay图片
	public List<Drawable>  res = new ArrayList<Drawable>();
	private Drawable mDrvMarker;
	// 存放overlayitem 
	public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	private Drawable mOldMarker = null; // 保存更新前使用的标记
	
	Button testUpdateButton = null;
	
	private final static int CODE_SHOW_DETAIL = 0x101;
	private static final int CODE_CHANGE_MODE = 0x102;
	public final static int MSG_HANDLE_MAP_MOVE = 1;
	public final static int MSG_HANDLE_POS_REFRESH = 2;
	public final static int MSG_HANDLE_REQ_TIMEOUT = 3;
	public final static int MSG_HANDLE_ITEM_TOUCH = 10000;
	
	private DataPreference mData;
	private boolean mIsDriver = false; // 是否是司机
	
	private View mDialogView; // 录音对话框的view
	private WaitingShow mWs; // 等待响应popwin	
	private Handler mapHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case MSG_HANDLE_MAP_MOVE:
	    		break;
	    	default:
	    		if ( msg.what >= MSG_HANDLE_ITEM_TOUCH ) {
	    			int idx = msg.what-MSG_HANDLE_ITEM_TOUCH;
	        		try {
						if ( mIsDriver ) {
							if (mApp.getRequestID() < 0) {
								JSONObject obj = mApp.getCurrentRequestList().getJSONObject(idx);
								int reqid = obj.getInt("id");
								mApp.setCurrentReqIdx(idx);
								mApp.setRequestID(reqid);
								mApp.setCurrentObject(obj);
								// 更新乘客图标
								updateRequestIcon(idx, false);
								ShowDetail.showPassengerRequestInfo(mApp, BenbenLocationTest.this, obj, CODE_SHOW_DETAIL);
							} else {
								// 当前已有请求在处理，不能响应
								Toast.makeText(BenbenLocationTest.this, "当前已有请求在处理，请点击查看了解详情", Toast.LENGTH_LONG).show();
							}
						}
					} catch (JSONException e) {
						resetStatus();
						// 下标异常
		        		Toast.makeText(BenbenLocationTest.this.getApplicationContext(), 
		        				"请求状态异常: "+idx+"/"+mApp.getCurrentRequestList().length(), Toast.LENGTH_SHORT).show();
					}
	    		}
	    		break;
			}
		}
		
	};
	private Handler waitingHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case WaitingShow.MSG_HANDLE_REQ_TIMEOUT:
	    		Toast.makeText(BenbenLocationTest.this, "请求超时，请重新选择", Toast.LENGTH_SHORT).show();
	    		requestAbandon();
	    		resetStatus();
	    		break;
			case WaitingShow.MSG_HANDLE_REQ_CANCEL:
				Toast.makeText(BenbenLocationTest.this, "请求取消，请重新选择", Toast.LENGTH_SHORT).show();
	    		requestAbandon();
	    		resetStatus();
	    		break;
	    	default:
	    		break;
			}
		}
		
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BenbenApplication app = (BenbenApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            app.mBMapManager.init(BenbenApplication.strKey,new BenbenApplication.MyGeneralListener());
        }
        setContentView(R.layout.activity_locationoverlay);
        mMapView = (MapView)findViewById(R.id.bmapView);
        mMapController = mMapView.getController();
        
        mData = new DataPreference(this.getApplicationContext());
        mData.LoadString("user");
        mIsDriver = mData.LoadBool("isdriver");
        mApp = (BenbenApplication) getApplication();
        
        initMapView();
        
        super.setLocationRequest();
        super.setLocationStart();
        
        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);    
        mMapView.setBuiltInZoomControls(true);
        mMapListener = new MKMapViewListener() {
			
			@Override
			public void onMapMoveFinish() {
				// Auto-generated method stub
			}
			
			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				// Auto-generated method stub
				String title = "";
				if (mapPoiInfo != null){
					title = mapPoiInfo.strText;
					Toast.makeText(BenbenLocationTest.this,title,Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onGetCurrentMap(Bitmap b) {
				//  Auto-generated method stub
				
			}

			@Override
			public void onMapAnimationFinish() {
				//  Auto-generated method stub
				
			}
		};
		mMapView.regMapViewListener(BenbenApplication.getInstance().mBMapManager, mMapListener);
		
	    // 初始化出租车/乘客位置列表
		if ( mIsDriver ) {
			mDrvMarker = this.getResources().getDrawable(R.drawable.icon_marka);
			res.add(getResources().getDrawable(R.drawable.icon_marka));
			res.add(getResources().getDrawable(R.drawable.icon_markb));
			res.add(getResources().getDrawable(R.drawable.icon_markc));
			res.add(getResources().getDrawable(R.drawable.icon_markd));
			res.add(getResources().getDrawable(R.drawable.icon_marke));
			res.add(getResources().getDrawable(R.drawable.icon_markf));
			res.add(getResources().getDrawable(R.drawable.icon_markg));
			res.add(getResources().getDrawable(R.drawable.icon_markh));
			res.add(getResources().getDrawable(R.drawable.icon_marki));
			res.add(getResources().getDrawable(R.drawable.icon_markj));
		} else {
			mDrvMarker = this.getResources().getDrawable(R.drawable.steering);
			res.add(getResources().getDrawable(R.drawable.steering));
		}
	    ov = new BenbenOverlay(mDrvMarker, this,mMapView, mapHandler); 
	    mMapView.getOverlays().add(ov);
	    
		myLocationOverlay = new MyLocationOverlay(mMapView);
	    myLocationOverlay.setData(locData);
		mMapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		mMapView.refresh();
		
		testUpdateButton = (Button)findViewById(R.id.btn_callTaxi);
	    
	    if ( mIsDriver ) {
	    	testUpdateButton.setVisibility(View.GONE);
	    }
	    
    	mDialogView = getLayoutInflater().inflate(R.layout.record_dialog, null);
    	new PopupWindow(mDialogView, 600, 600);
    	
    	View vv = getLayoutInflater().inflate(R.layout.waiting_dialog, null);
    	mWs = new WaitingShow("等待乘客响应", 30, PopupWindowSize.getPopupWindoWidth(this), 
    			PopupWindowSize.getPopupWindowHeight(this), vv);
    	mWs.SetNegativeOnclick("取消请求", null);
    	mWs.setHandler(mH);
	    
	    //Toast.makeText(this.getApplicationContext(), mTokenKey+": "+mTokenVal, Toast.LENGTH_SHORT).show();
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
        BenbenApplication app = (BenbenApplication)this.getApplication();
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
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	if ( mWs.isShow() ) {
    		Toast.makeText(this, "正在等待中，请稍后再试", Toast.LENGTH_SHORT).show();
    		return super.onOptionsItemSelected(item);
    	}

		switch(item.getItemId()) {
		case R.id.menu_info:
			Intent detail = new Intent(this, ListDetail.class);
			detail.putExtra("neg", "再看看");
			if ( mApp.getCurrentStat().equals(StatusMachine.STAT_SUCCESS) ) {
				// 显示电话乘客按钮
				detail.putExtra("pos", "电话乘客");
			}
			this.startActivityForResult(detail, 3);
			break;
		case R.id.menu_mode:
			// 模式切换
			Intent lstmode = new Intent(this, ListMode.class);
			lstmode.putExtra("pos", "换一批");
			this.startActivityForResult(lstmode, CODE_CHANGE_MODE);
			break;
		case R.id.menu_map_history:
			Intent historyList = new Intent(this, TaxiRequestIndexActivity.class);
			this.startActivity(historyList);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		int reqid = mApp.getRequestID();
		JSONObject reqobj = mApp.getCurrentObject();
		
		switch(requestCode) {
		case CODE_SHOW_DETAIL:
			// 来自点击用户请求图标，司机处理用户请求
			if ( resultCode > 0 && reqid >= 0 ) {
				LocationData locData = mApp.getCurrentLocData();
				StatusMachine sm = new StatusMachine(mH, mData, reqobj);
	    		// 这里是用保存的reqid，防止被更新为无效值
	    		sm.driverConfirm(locData.longitude, locData.latitude, reqid);
	    		
	    		// 显示延迟进度条，等待30s
	    		// 问题已解决，可以使用popwin，注意不要在回调函数中dismiss当前的popwin
	    		mWs.show();

			} else if ( resultCode > 0 ) {
				Toast.makeText(this, "该请求已无效，请选取其他请求", Toast.LENGTH_SHORT).show();
			} else {
				updateRequestIcon(mApp.getCurrentReqIdx(), true);
				resetStatus();
			}
			break;
		case 2:
			// 来自Success状态，电话乘客，其余动作与查看(3)相同
			Toast.makeText(this, "乘客已确认，请前往指定地点", Toast.LENGTH_SHORT).show();
		case 3:
			// 来自查看
			if ( resultCode > 0 ) {
				String mobile;
	    		try {
	    			mobile = mApp.getCurrentObject().getString("passenger_mobile");
	    			//mobile = "12345";
	    		} catch (JSONException e) {
	    			mobile = "000000";
	    		}
	    		
				Uri uri = Uri.parse("tel:"+mobile);
			    Intent incall = new Intent(Intent.ACTION_DIAL, uri);
			    BenbenLocationTest.this.startActivity(incall);
			}
			break;
		case CODE_CHANGE_MODE:
			// 已在父类onResume中恢复定位功能
			break;
		default:
			break;
		}
	}
    
    protected void resetStatus() {
    	super.resetStatus();
    }
    
    private void requestAbandon() {
    	mWs.Dismiss();
    }

    private void updateMapView() {
    	ov.removeAll();
    	if ( ov.size() < mGeoList.size()){
    		//ov.addItem(mGeoList.get(ov.size() ));
    		ov.addItem(mGeoList);
    	}
	    mMapView.refresh();
    }
	
	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event ) {
		/*
		if(keyCode == KeyEvent.KEYCODE_BACK && 
				event.getAction() == KeyEvent.ACTION_DOWN) {
			// 这里不需要再按两次，一次退出
			if( exitTime == 0 ) {
	            Toast.makeText(getApplicationContext(), "再按一次返回键退出", Toast.LENGTH_SHORT).show();
	            exitTime = System.currentTimeMillis();
			} else {
				Toast.makeText(getApplicationContext(), "退出中...", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
				finish();
			}
	        return true;   
	    }
	    */
	    return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void refreshProcess() {
		// 在地图上定位
		myLocationOverlay.setData(locData);
        mMapView.refresh();
        mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)), 
        		mapHandler.obtainMessage(MSG_HANDLE_MAP_MOVE));
        
		super.refreshProcess();
	}
	
	private void updateListView() {
		JSONArray reqInfo = mApp.getCurrentRequestList();
		//清除所有添加的Overlay
        mGeoList.clear();
        
		//添加一个item
    	//当要添加的item较多时，可以使用addItem(List<OverlayItem> items) 接口
        for( int i=0; i<reqInfo.length(); ++i ) {
        	JSONObject pos = JsonHelper.getJsonObj(reqInfo, i);        	
        	OverlayItem item = new OverlayItem(new GeoPoint((int)(JsonHelper.getDouble(pos, "passenger_lat")*1e6), 
    				(int)(JsonHelper.getDouble(pos, "passenger_lng")*1e6)),
	        		"乘客"+JsonHelper.getInt(pos, "id"), "声音: "+JsonHelper.getString(pos, "passenger_voice_url"));
	        
        	if ( item != null ) {
			   	item.setMarker(res.get(i%res.size()));
			   	mGeoList.add(item);
        	}
        }
    	updateMapView();
	}
	
	private void updateRequestIcon(int reqidx, boolean reset) {
		if ( reset == false ) {
			OverlayItem it = mGeoList.get(reqidx);
			mOldMarker = it.getMarker();
			it.setMarker(getResources().getDrawable(R.drawable.location2));
			mGeoList.set(reqidx, it);
			updateMapView();
		} else {
			OverlayItem it = mGeoList.get(reqidx);
			it.setMarker(mOldMarker);
			mOldMarker = null;
			mGeoList.set(reqidx, it);
			updateMapView();
		}
	}

	@Override
	protected void doProcessMsg(Message msg) {
		int idx = mApp.getCurrentReqIdx();
		int reqid = mApp.getRequestID();
		
		switch (msg.what) {	
		case StatusMachine.MSG_STAT_OTHER:
			String mobile = (String)msg.obj;
			Toast.makeText(this, "乘客请求["+reqid+"]已被其他司机["+mobile+"]接受, 附近有"+mApp.getCurrentRequestList().length()+"个乘客", 
					Toast.LENGTH_SHORT).show();
			mApp.setCurrentStat(StatusMachine.STAT_CANCEL);
			requestAbandon();
			resetStatus();
			break;
    	case StatusMachine.MSG_STAT_CANCEL:
			Toast.makeText(this, "乘客请求["+idx+"]已被取消, 附近有"+mApp.getCurrentRequestList().length()+"个乘客", 
					Toast.LENGTH_SHORT).show();
			requestAbandon();
			resetStatus();
			mApp.setCurrentStat(StatusMachine.STAT_CANCEL);
			break;
		case StatusMachine.MSG_STAT_SUCCESS:
			mApp.setCurrentStat(StatusMachine.STAT_SUCCESS);
			requestAbandon();
			/*
			 * 精简流程，不再显示确认页，直接电话乘客
			 */
			Toast.makeText(this, "乘客请求["+mApp.getRequestID()+"]已确认，请前往乘客所在地！", Toast.LENGTH_SHORT).show();
			resetStatus();
			ShowDetail.showCall(this, mApp.getCurrentObject());
			break;
		case StatusMachine.MSG_STAT_TIMEOUT:
			Toast.makeText(this, "乘客请求["+idx+"]已超时, 附近有"+mApp.getCurrentRequestList().length()+"个乘客",
					Toast.LENGTH_SHORT).show();
			requestAbandon();
			resetStatus();
			mApp.setCurrentStat(StatusMachine.STAT_TIMEOUT);
			break;
		case StatusMachine.MSG_STAT_WAITING_PASS:
			Toast.makeText(this, "等待乘客确认请求["+idx+"], 附近有"+mApp.getCurrentRequestList().length()+"个乘客",
					Toast.LENGTH_SHORT).show();
			mApp.setCurrentStat(StatusMachine.STAT_WAITING_PAS_CONF);
			break;
		case StatusMachine.MSG_STAT_WAITING_DRV:
			Toast.makeText(this, "乘客请求["+idx+"]等待您接受, 附近有"+mApp.getCurrentRequestList().length()+"个乘客",
					Toast.LENGTH_SHORT).show();
			mApp.setCurrentStat(StatusMachine.STAT_WAITING_DRV_RESP);
			break;
		case StatusMachine.MSG_DATA_GETLIST:
			// 存入app中
			JSONArray obj = (JSONArray) msg.obj;
			mApp.setCurrentRequestList(obj);
			updateListView();
			Toast.makeText(this, "附近有"+obj.length()+"个乘客请求", Toast.LENGTH_SHORT).show();
			break;
		case StatusMachine.MSG_ERR_DRV_REPORT:
			Toast.makeText(this, (String)msg.obj, Toast.LENGTH_SHORT).show();
			break;
		case StatusMachine.MSG_ERR_NETWORK:
			Toast.makeText(this, (String)msg.obj, Toast.LENGTH_SHORT).show();
			requestAbandon();
			resetStatus();
			break;
    	default:
    		break;
    	}
	}
}


