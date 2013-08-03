package com.benbenTaxi.v1.function;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.benbenTaxi.R;
import com.benbenTaxi.v1.BenbenApplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RequestAdapter extends BaseAdapter {
	public final static int ITEM_STAT_ORG = 0;
	public final static int ITEM_STAT_WAIT = 1;
	public final static int ITEM_STAT_OK = 2;
	public final static int ITEM_STAT_PLAY = 3;
	
	private LayoutInflater mInflater;
	private String[] mContent, mTitle, mUrl, mStat;
	private int[] mImgIdLst;
	private static final int mMaxSize = 7;
	private int mFromIdx = 0, mSelect = -1;
	private boolean mIsLast = false;
	private BenbenApplication mApp;
	private JSONArray mReqList;
	private ListView mLV;
	private int mColorWait = Color.rgb(255, 0, 0), mColorOK = Color.rgb(0, 255, 0);
	private int mColorPlay = Color.rgb(255, 201, 14), mColor = Color.rgb(60, 145, 170); // 保存原来的颜色
	private AudioProcessor mAp = null;
	
	public RequestAdapter(Context con, ListView vv, BenbenApplication app, AudioProcessor ap) {
		mInflater = LayoutInflater.from(con);
		mApp = app;
		mLV = vv;
		
		setAudioProcessor(ap);
			
    	mImgIdLst = new int[5];
    	mImgIdLst[0] = R.drawable.user;
    	mImgIdLst[1] = R.drawable.phone_13;
    	mImgIdLst[2] = R.drawable.location;
    	mImgIdLst[3] = R.drawable.location2;
    	mImgIdLst[4] = R.drawable.time_07;
    	
    	updateList();
	}
	
	public void setAudioProcessor( AudioProcessor mp ) {
		mAp = mp;
	}
	
	@Override
	public int getCount() {
		if ( mTitle.length - mFromIdx > mMaxSize ) {
			return mMaxSize;
		} else if ( mTitle.length - mFromIdx > 0 ) {
			mIsLast = true;
			return mTitle.length - mFromIdx;
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		if ( getCount() > 0 ) {
			try {
				return mReqList.get(position+mFromIdx);
			} catch (JSONException e) {
				return null;
			}
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position+mFromIdx;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListHolder lh = null;
		
		if ( convertView == null ) {
			lh = new ListHolder();
			convertView = mInflater.inflate(R.layout.list_item_request, null);
			lh.img = (ImageView) convertView.findViewById(R.id.lst_req_imgView);
			lh.content = (TextView) convertView.findViewById(R.id.lst_req_tv_content);
			lh.title = (TextView) convertView.findViewById(R.id.lst_req_tv_title);
			convertView.setTag(lh);
		} else {
			lh = (ListHolder) convertView.getTag();
		}
		
		lh.img.setImageResource(mImgIdLst[0]);
		lh.content.setText(mContent[position+mFromIdx]);
		lh.title.setText(mTitle[position+mFromIdx]);
		
		if ( mAp != null ) {
	    	//mAp.addAudioList(position, "http://"+mHost+mUrl[position+mFromIdx]);
	    	try {
				mAp.addAudioList(position, mReqList.getJSONObject(position+mFromIdx));
			} catch (Exception e) {
				//e.printStackTrace();
			}
	    }
		
		return convertView;
	}	
	
	public void setItemSelected(int itemid) {
		mSelect = itemid;
		updateView(mSelect, ITEM_STAT_WAIT);
	}
	
	public void setItemConfirm() {
		updateView(mSelect, ITEM_STAT_OK);
	}
	
	public void resetItemSelected() {
		updateView(mSelect, ITEM_STAT_ORG);		
		mSelect = -1;
	}
	
	public void setItemPlay(int pos) {
		updateView(pos, ITEM_STAT_PLAY);
	}
	
	public void setItemOrg(int pos) {
		updateView(pos, ITEM_STAT_ORG);
	}
	
	public void refreshIdx() {
		// 更新内容
		mFromIdx += mMaxSize;
		this.notifyDataSetChanged();
	}
	
	public boolean isLastPage() {
		getCount();
		return mIsLast;
	}
	
	public void updateList() {
		JSONArray reqList = mApp.getCurrentRequestList();
		
		int size = reqList.length();
    	mContent = new String[size];
    	mTitle = new String[size];
    	mUrl = new String[size];
    	mStat = new String[size];
    	
    	mFromIdx = 0;
    	
    	/*
    	 * [{"id":13587,"state":"Waiting_Driver_Response","passenger_mobile":"15910676326","driver_mobile":null,
    	 * "passenger_lat":8.0,"passenger_lng":8.0,
    	 * "passenger_voice_url":"/uploads/taxi_request/voice/2013-06-15/a021331949144ec138de2c3857ec2538.m4a",
    	 * "driver_lat":null,"driver_lng":null}] 
    	 */
		for( int i=0; i<size; ++i ) {
        	try {
				JSONObject pos = reqList.getJSONObject(i);
				mTitle[i] = "电话: "+pos.getString("passenger_mobile");
				double lat = pos.getDouble("passenger_lat");
				double lng = pos.getDouble("passenger_lng");				
				mContent[i] = "距离: "+Distance.getDistanceFormat(lat, lng, mApp.getCurrentLocData().latitude, mApp.getCurrentLocData().longitude)+"公里";
				
				mUrl[i] = pos.getString("passenger_voice_url");
				mStat[i] = pos.getString("state");
				
			} catch (JSONException e) {
				mTitle[i] = "电话: 解析错误";
				mContent[i] = "距离: 解析错误";
				mUrl[i] = "";
			}
		}
		
		mReqList = reqList;
		mSelect = -1;
	}
	
	public final class ListHolder {
		public ImageView img;
		public TextView content;
		public TextView title;
	}
	
	private void updateView( int id, int stat ) {
		int vpos = mLV.getFirstVisiblePosition();
		View v = mLV.getChildAt(id-vpos);
		
		if ( v != null ) {
			ListHolder lh = (ListHolder) v.getTag();
			
			switch (stat) {
			case ITEM_STAT_ORG:
				lh.title.setTextColor(mColor);
				break;
			case ITEM_STAT_WAIT:
				lh.title.setTextColor(mColorWait);
				break;
			case ITEM_STAT_OK:
				lh.title.setTextColor(mColorOK);
				break;
			case ITEM_STAT_PLAY:
				lh.title.setTextColor(mColorPlay);
				break;
			default:
				break;
			}
			
		}
	}
}
