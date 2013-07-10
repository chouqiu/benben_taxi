package com.benbenTaxi.v1.function;

import com.benbenTaxi.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RequestAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private String[] mContent, mTitle;
	private int[] mImgIdLst;
	private static final int mMaxSize = 7;
	private int mFromIdx = 0;
	private boolean mIsLast = false;
	
	public RequestAdapter(String[] title, String[] contents, Context con) {
		mInflater = LayoutInflater.from(con);
		mContent = contents;
			
    	mImgIdLst = new int[5];
    	mImgIdLst[0] = R.drawable.user;
    	mImgIdLst[1] = R.drawable.phone_13;
    	mImgIdLst[2] = R.drawable.location;
    	mImgIdLst[3] = R.drawable.location2;
    	mImgIdLst[4] = R.drawable.time_07;
    	
    	mTitle = title;
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
			return mContent[position+mFromIdx];
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
		// ¸üÐÂÄÚÈÝ
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
