package com.benbenTaxi.v1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mapapi.map.LocationData;
import com.benbenTaxi.R;
import com.benbenTaxi.v1.function.AudioProcessor;
import com.benbenTaxi.v1.function.BaseLocationActivity;
import com.benbenTaxi.v1.function.DelayTask;
import com.benbenTaxi.v1.function.PopupWindowSize;
import com.benbenTaxi.v1.function.RequestAdapter;
import com.benbenTaxi.v1.function.ShowDetail;
import com.benbenTaxi.v1.function.StatusMachine;
import com.benbenTaxi.v1.function.WaitingShow;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ListMode extends BaseLocationActivity {
	protected ListView mLv;
	protected Button mBtnPos, mBtnNeg;
	private RequestAdapter mReqAdapter;
	private WaitingShow mWs; // 等待响应popwin
	private Handler waitingHandler, playHandler, delayHandler;
	
	private String tip_pos, tip_neg;
	protected View.OnClickListener mPosfunc, mNegfunc;
	
	private final static int CODE_SHOW_DETAIL = 0x101;
	private final static int CODE_SHOW_INFO = 0x102;
	private final static int CODE_SHOW_CONFIRM_INFO = 0x103;
	private final static int CODE_DELAY = 0x104;
	
	private AudioProcessor mAp = null;
	private int mDelayRetry = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listmode);
		
		Bundle tips = getIntent().getExtras();
		if ( tips != null ) {
			tip_pos = tips.getString("pos");
			tip_neg = tips.getString("neg");
		}
		
    	mAp = new AudioProcessor(true);
    	playHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
				case AudioProcessor.MSG_PLAY_READY:
					if ( msg.arg1 >=0 ) {
						mReqAdapter.setItemPlay(msg.arg1);
					}
					break;
				case AudioProcessor.MSG_PLAY_COMPLETE:
				case AudioProcessor.MSG_PLAY_ERROR:
					if ( msg.arg1 >=0 ) {
						mReqAdapter.setItemOrg(msg.arg1);
					}
					break;
				default:
					Toast.makeText(ListMode.this, "播放信息错误["+msg.what+"]: "+(String)msg.obj, Toast.LENGTH_SHORT).show();
					break;
				}
			}
    	};
    	mAp.setHandler(playHandler);
    	
		init();
		
		waitingHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
				case WaitingShow.MSG_HANDLE_REQ_TIMEOUT:
					Toast.makeText(ListMode.this, "请求已超时，请重新选取！", Toast.LENGTH_SHORT).show();
					mReqAdapter.resetItemSelected();
					mWs.Dismiss();
					resetStatus();
					break;
				case WaitingShow.MSG_HANDLE_REQ_CANCEL:
					Toast.makeText(ListMode.this, "请求已取消，请重新选取！", Toast.LENGTH_SHORT).show();
					mReqAdapter.resetItemSelected();
					mWs.Dismiss();
					resetStatus();
					break;
				default:
					mWs.Dismiss();
					break;
				}
			}
		};
		mWs = new WaitingShow("等待乘客响应", 30, PopupWindowSize.getPopupWindoWidth(this), 
				PopupWindowSize.getPopupWindowHeight(this), getLayoutInflater().inflate(R.layout.waiting_dialog, null));
    	mWs.SetNegativeOnclick("取消请求", null);
    	mWs.setHandler(waitingHandler);
    	
    	super.setLocationRequest();
    	super.setLocationStart();
    	
    	mReqAdapter.registerDataSetObserver(new DataSetObserver(){  
    		
            public void onChanged() {  
            	//Toast.makeText(ListMode.this, "播放列表: "+mAp.getPlayListSize(), Toast.LENGTH_SHORT).show();
				//mAp.resetPlay();
				//mAp.batchPlay();
            }  
        });
    	//mAp.batchPlay();
    	
    	delayHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
				case DelayTask.MSG_DELAY_OK:
					int ss = mAp.getPlayListSize();
					if ( msg.arg1 == CODE_DELAY && ss > 0 ) {
						mDelayRetry = 0;
						//Toast.makeText(ListMode.this, "播放列表: "+mAp.getPlayListSize()+":"+mAp.isPlayingList(), Toast.LENGTH_SHORT).show();
						if ( ! mAp.isPlayingList() ) {
							// 避免播放线程冲突
							mAp.batchPlay();
						}
					} else if ( msg.arg1 == CODE_DELAY && ss <= 0 && mDelayRetry < 1 ) {
						// 延迟，方便reqadapter刷新
						++mDelayRetry;
						DelayTask dt = new DelayTask(CODE_DELAY, delayHandler);
						dt.execute(200);
					} else if ( msg.arg1 == CODE_DELAY ) {
						mDelayRetry = 0;
					}
					break;
				default:
					break;
				}
			}
    	};
	}
	
	private void init() {
    	mLv = (ListView)findViewById(R.id.listView_listmode);
    	mBtnPos = (Button)findViewById(R.id.btnListOk_listmode);
    	mBtnNeg = (Button)findViewById(R.id.btnListCancel_listmode);
    	
    	do_init_functions();
    	
    	if ( tip_pos!=null ) {
			mBtnPos.setText(tip_pos);
			mBtnPos.setOnClickListener(mPosfunc);
		} else {
			mBtnPos.setVisibility(View.GONE);
		}
		if ( tip_neg != null ) {
			mBtnNeg.setText(tip_neg);
			mBtnNeg.setOnClickListener(mNegfunc);
		} else {
			mBtnNeg.setVisibility(View.GONE);
		}
	}

	@Override
	protected void resetStatus() {
		resetAudioProcessor();
		super.resetStatus();
	}
	
	private void resetAudioProcessor() {
		mAp.resetPlay();
		mAp.resetPlayList();
	}

	protected void do_init_functions() {
		// 解析请求数据	
		mReqAdapter = new RequestAdapter(this, mLv, mApp, mAp);
		mLv.setAdapter(mReqAdapter);
		
		mLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int reqid = mApp.getRequestID();
				JSONObject lastobj = mApp.getCurrentObject();
				int lastid = -1, newid = -1;
				
				if ( lastobj != null ) {
					try {
						lastid = lastobj.getInt("id");
					} catch (JSONException e) {
						lastid = -1;
					}
				}
				
				JSONObject obj = (JSONObject) mReqAdapter.getItem(arg2);
				if ( obj != null ) {
					try {
						newid = obj.getInt("id");
					} catch (JSONException e) {
						newid = -1;
					}
				} else {
					Toast.makeText(ListMode.this, "无效的请求信息，请重试", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if ( reqid >= 0 || (lastid>=0 && lastid==newid && (mApp.getCurrentStat().equals(StatusMachine.STAT_SUCCESS) ||
						mApp.getCurrentStat().equals(StatusMachine.STAT_CANCEL) ||
						mApp.getCurrentStat().equals(StatusMachine.STAT_TIMEOUT))) )
				{
					Toast.makeText(ListMode.this, "已有请求["+reqid+"]在处理中, 或请求["+lastid+"]已被处理过", 
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				resetAudioProcessor();
				mReqAdapter.setItemSelected(arg2);
				ShowDetail.showPassengerRequestInfo(mApp, ListMode.this, obj, CODE_SHOW_DETAIL);
			}
		});
		
		mPosfunc = new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				if ( mReqAdapter.isLastPage() ) {
					Toast.makeText(ListMode.this, "已是最后一批乘客请求", Toast.LENGTH_SHORT).show();
				} else {
					mReqAdapter.refreshIdx();
				}
				resetAudioProcessor();
			}
		};
	}


	@Override
	protected void onResume() {
		super.onResume();
	}
	

	@Override
	protected void onDestroy() {
		mAp.release();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		int reqid = mApp.getRequestID();
		JSONObject reqobj = mApp.getCurrentObject();
		
		switch(requestCode) {
		case CODE_SHOW_DETAIL:
			// 来自点击用户请求图标，司机处理用户请求
			if ( resultCode > 0 ) {
				LocationData locData = mApp.getCurrentLocData();
				StatusMachine sm = new StatusMachine(mH, mData, reqobj);
	    		// 这里是用保存的reqid，防止被更新为无效值
	    		sm.driverConfirm(locData.longitude, locData.latitude, reqid);
	    		
	    		// 显示延迟进度条，等待30s
	    		// 问题已解决，可以使用popwin，注意不要在回调函数中dismiss当前的popwin
	    		mWs.show();
			} else {
				mReqAdapter.resetItemSelected();
				resetStatus();
			}
			break;
		case CODE_SHOW_INFO:
			if ( resultCode > 0 ) {
				ShowDetail.showCall(this, reqobj);
			}
			break;
		case CODE_SHOW_CONFIRM_INFO:
			if ( resultCode > 0 ) {
				ShowDetail.showCall(this, mApp.getCurrentObject());
			}
			Toast.makeText(this, "乘客请求["+mApp.getRequestID()+"]已确认，请前往乘客所在地！", Toast.LENGTH_SHORT).show();
			resetStatus();
			super.setLocationRequest();
			super.setLocationStart();
			break;
		default:
			break;
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_list, menu);
        return super.onCreateOptionsMenu(menu);
    }  
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	if ( mWs.isShow() ) {
    		Toast.makeText(this, "正在等待中，请稍后再试", Toast.LENGTH_SHORT).show();
    		return super.onOptionsItemSelected(item);
    	}
    	
		switch(item.getItemId()) {
		case R.id.menu_list_info:
			Intent detail = new Intent(this, ListDetail.class);
			detail.putExtra("neg", "再看看");
			if ( mApp.getCurrentStat().equals(StatusMachine.STAT_SUCCESS) ) {
				// 显示电话乘客按钮
				detail.putExtra("pos", "电话乘客");
			}
			this.startActivityForResult(detail, CODE_SHOW_INFO);
			break;
		case R.id.menu_map_mode:
			// 返回地图模式
			super.setLocationStop();
			this.setResult(0);
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
    
	@Override
	protected void doProcessMsg(Message msg) {
		int reqid = mApp.getRequestID();
		
		switch(msg.what) {
		case StatusMachine.MSG_STAT_CANCEL:
			Toast.makeText(this, "乘客请求["+reqid+"]已被取消, 附近有"+mApp.getCurrentRequestList().length()+"个乘客", 
					Toast.LENGTH_SHORT).show();
			mApp.setCurrentStat((String) msg.obj);
			mWs.Dismiss();
			resetStatus();
			break;
		case StatusMachine.MSG_STAT_SUCCESS:
			// 司机态，显示详情
			mApp.setCurrentStat((String) msg.obj);
			mReqAdapter.setItemConfirm();
			mWs.Dismiss();
			// 先暂停轮训，防止反复调用ListDetail
			super.setLocationStop();
			ShowDetail.showPassengerConfirmInfo(this, CODE_SHOW_CONFIRM_INFO);
			break;
		case StatusMachine.MSG_STAT_TIMEOUT:
			Toast.makeText(this, "乘客请求["+reqid+"]已超时, 附近有"+mApp.getCurrentRequestList().length()+"个乘客",
					Toast.LENGTH_SHORT).show();
			mApp.setCurrentStat((String) msg.obj);
			mWs.Dismiss();
			resetStatus();
			break;
		case StatusMachine.MSG_STAT_WAITING_PASS:
			Toast.makeText(this, "等待乘客确认请求["+reqid+"], 附近有"+mApp.getCurrentRequestList().length()+"个乘客",
					Toast.LENGTH_SHORT).show();
			mApp.setCurrentStat((String) msg.obj);
			break;
		case StatusMachine.MSG_STAT_WAITING_DRV:
			Toast.makeText(this, "乘客请求["+reqid+"]等待您接受, 附近有"+mApp.getCurrentRequestList().length()+"个乘客",
					Toast.LENGTH_SHORT).show();
			mApp.setCurrentStat((String) msg.obj);
			break;
		case StatusMachine.MSG_DATA_GETLIST:
			// 存入app中
			JSONArray obj = (JSONArray) msg.obj;
			resetAudioProcessor();
			mApp.setCurrentRequestList(obj);
			mReqAdapter.updateList();
			mReqAdapter.notifyDataSetChanged();
			Toast.makeText(this, "已刷新，附近有"+obj.length()+"个乘客请求", Toast.LENGTH_SHORT).show();
			//mAp.resetPlay();
			//mAp.batchPlay();
			
			// 延迟，方便reqadapter刷新
			DelayTask dt = new DelayTask(CODE_DELAY, delayHandler);
			dt.execute(500);
			break;
		default:
			break;
		}
		
	}
}
