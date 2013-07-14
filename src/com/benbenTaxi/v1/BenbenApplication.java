package com.benbenTaxi.v1;


import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;


public class BenbenApplication extends Application {
	
    private static BenbenApplication mInstance = null;
    public boolean m_bKeyRight = true;
    public BMapManager mBMapManager = null;

    public static final String strKey = "1BE33CC3A1DEBDC8FF3A8A3F23A5E208C27E5C83";
	
    private JSONArray mReqList = new JSONArray(); // 保存乘客请求列表，供列表模式引用
    private String[] mCurrentInfo = null;// 包括声音url
	private JSONObject mCurrentObj = new JSONObject();
	private int mReqId = -1, mReqIdx = -1; // 乘客发起的请求id
	private String mCurrentStat = new String("");
	//private int mStatVal = -1; // 代替字符串比较，提高性能
	private LocationData mCurrentLocData = new LocationData();
	
	public static final int STATVAL_SUCCESS = 1;
	public static final int STATVAL_CANCEL = 0;
	public static final int STATVAL_TIMEOUT = 2;
	
	public String[] getCurrentInfo() {
		return mCurrentInfo;
	}
	
	public JSONObject getCurrentObject() {
		return mCurrentObj;
	}
	
	public JSONArray getCurrentRequestList() {
		return mReqList;
	}
	
	public int getRequestID() {
		return mReqId;
	}
	
	public String getCurrentStat() {
		return mCurrentStat;
	}
	
	public int getCurrentReqIdx() {
		return mReqIdx;
	}
	
	public LocationData getCurrentLocData() {
		return mCurrentLocData;
	}
	
	public void setCurrentInfo(String[] info) {
		mCurrentInfo = info;
	}
	
	public void setCurrentObject(JSONObject obj) {
		mCurrentObj = obj;
	}
	
	public void setCurrentRequestList(JSONArray arr) {
		mReqList = arr;
	}
	
	public void setRequestID(int id) {
		mReqId = id;
	}
	
	public void setCurrentStat( String stat ) {
		mCurrentStat = stat;
	}
	
	public void setCurrentReqIdx( int idx ) {
		mReqIdx = idx;
	}
	
	public void setCurrentLocData(LocationData data) {
		mCurrentLocData.latitude = data.latitude;
        mCurrentLocData.longitude = data.longitude;
        mCurrentLocData.accuracy = data.accuracy;
        mCurrentLocData.direction = data.direction;
	}
	
	@Override
    public void onCreate() {
	    super.onCreate();
		mInstance = this;
		initEngineManager(this);
	}
	
	@Override
	//建议在您app的退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
	public void onTerminate() {
		// TODO Auto-generated method stub
	    if (mBMapManager != null) {
            mBMapManager.destroy();
            mBMapManager = null;
        }
		super.onTerminate();
	}
	
	public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(BenbenApplication.getInstance().getApplicationContext(), 
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
	}
	
	public static BenbenApplication getInstance() {
		return mInstance;
	}
	
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
    public static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(BenbenApplication.getInstance().getApplicationContext(), "您的网络出错啦！",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(BenbenApplication.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //授权Key错误：
                Toast.makeText(BenbenApplication.getInstance().getApplicationContext(), 
                        "请在 DemoApplication.java文件输入正确的授权Key！", Toast.LENGTH_LONG).show();
                BenbenApplication.getInstance().m_bKeyRight = false;
            }
        }
    }
}