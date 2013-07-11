package com.benbenTaxi.v1.function;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.benbenTaxi.R;
import com.benbenTaxi.v1.BenbenApplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RequestAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private String[] mContent, mTitle, mUrl;
	private int[] mImgIdLst;
	private JSONArray mReqList;
	private static final int mMaxSize = 7;
	private int mFromIdx = 0;
	private boolean mIsLast = false;
	private BenbenApplication mApp;
	
	public RequestAdapter(JSONArray objs, Context con, BenbenApplication app) {
		mInflater = LayoutInflater.from(con);
		mApp = app;
			
    	mImgIdLst = new int[5];
    	mImgIdLst[0] = R.drawable.user;
    	mImgIdLst[1] = R.drawable.phone_13;
    	mImgIdLst[2] = R.drawable.location;
    	mImgIdLst[3] = R.drawable.location2;
    	mImgIdLst[4] = R.drawable.time_07;
    	
    	mReqList = objs;
    	int size = mReqList.length();
    	mContent = new String[size];
    	mTitle = new String[size];
    	mUrl = new String[size];
		
		for( int i=0; i<size; ++i ) {
        	try {
				JSONObject pos = mReqList.getJSONObject(i);
				mTitle[i] = "电话: "+pos.getString("passenger_mobile");
				double lat = pos.getDouble("passenger_lat");
				double lng = pos.getDouble("passenger_lng");				
				mContent[i] = "距离: "+Distance.getDistanceFormat(lat, lng, mApp.getCurrentLocData().latitude, mApp.getCurrentLocData().longitude)+"公里";
				
				mUrl[i] = pos.getString("passenger_voice_url");
			} catch (JSONException e) {
				mTitle[i] = "电话: 解析错误";
				mContent[i] = "距离: 解析错误";
				mUrl[i] = "";
			}
		}
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
		
		return convertView;
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
	
	public final class ListHolder {
		public ImageView img;
		public TextView content;
		public TextView title;
	}
}
