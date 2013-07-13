package com.benbenTaxi.v1.function;

import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.LocationData;
import com.benbenTaxi.v1.BenbenApplication;
import com.benbenTaxi.v1.BenbenLocationMain.NotifyLister;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public abstract class BaseLocationActivity extends Activity {
	// 定位相关
	private LocationClient mLocClient;
	private MyLocationListenner myListener = new MyLocationListenner();
    private NotifyLister mNotifyer=null;
    
    protected LocationData locData = null;
    protected MsgHandler mH = null;
    protected BenbenApplication mApp = null;
    protected DataPreference mData;
    
	//protected String mStatus;
    
    protected boolean mIsDriver = true;
    protected AudioProcessor mAp = null;
    
	public final static int MSG_HANDLE_POS_REFRESH = 2;
	public final static int MSG_HANDLE_REQ_TIMEOUT = 3;
	public final static int MSG_HANDLE_ITEM_TOUCH = 10000;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		locData = new LocationData();
		mApp = (BenbenApplication) this.getApplicationContext();
		mData = new DataPreference(this.getApplicationContext());
		
		mH = new MsgHandler();
		
		mLocClient = new LocationClient( this );
        mLocClient.registerLocationListener( myListener );
        
        // 初始化声音组件
	    mAp = new AudioProcessor(mIsDriver);
	}
    
    @Override
	protected void onDestroy() {
    	if (mLocClient != null)
    		mLocClient.stop();
		super.onDestroy();
	}

	protected void setLocationStart() {
    	LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setCoorType("bd09ll");     //设置坐标类型
        option.setScanSpan(5000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }
    
    protected void setLocationStop() {
    	if ( mLocClient != null ) {
    		mLocClient.stop();
    	}
    }
    
    protected void setLocationRequest() {
    	mLocClient.requestLocation();
    }
    
    protected void refreshProcess() {
    	// 上报司机位置
    	StatusMachine dtt = new StatusMachine(mH, mData, null);
    	dtt.driverReport(locData.longitude, locData.latitude, locData.accuracy, "gsm");
    	
    	// 获取taxirequest
    	if ( mApp.getRequestID() < 0 ) {
    		StatusMachine drvreq = new StatusMachine(mH, mData, null);
    		drvreq.driverGetRequest(locData.longitude, locData.latitude, locData.accuracy);       	
    	} else {
    	// 轮询request
    		StatusMachine drvask = new StatusMachine(mH, mData, mApp.getCurrentObject());
    		drvask.driverAskRequest(mApp.getRequestID());
    	}
    }
    
    protected abstract void doProcessMsg(Message msg);
    
    protected void resetStatus() {
    	mApp.setRequestID(-1);
    	mApp.setCurrentReqIdx(-1);
    }

	/**
     * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
     */
    public class MyLocationListenner implements BDLocationListener {
        //private int mCountFactor = 0; // 计数器，控制执行频率
        
    	@Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            
            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            locData.accuracy = location.getRadius();
            locData.direction = location.getDerect();
            
            mApp.setCurrentLocData(locData);
            refreshProcess();
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
    
    public class MsgHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			doProcessMsg(msg);
		}
    }
    
    
}
