package com.benbenTaxi.v1.function;

import com.benbenTaxi.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CallAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private String[] mContent, mTitle;
	private int[] mImgIdLst;
	
	public CallAdapter(String[] contents, Context con) {
		mInflater = LayoutInflater.from(con);
		mContent = contents;
		if ( mContent == null ) {
			mContent = new String[4];
		}
			
    	mImgIdLst = new int[4];
    	mImgIdLst[0] = R.drawable.user;
    	mImgIdLst[1] = R.drawable.phone_13;
    	mImgIdLst[2] = R.drawable.location;
    	//mImgIdLst[3] = R.drawable.location2;
    	mImgIdLst[3] = R.drawable.time_07;
    	
    	mTitle = new String[4];
    	mTitle[0] = "打车ID";
    	mTitle[1] = "乘客电话";
    	mTitle[2] = "当前位置";
    	mTitle[3] = "乘客确认";
	}
	
	@Override
	public int getCount() {
		return mTitle.length;
	}

	@Override
	public Object getItem(int position) {
		if ( position >=0 && position < mTitle.length ) {
			return mContent[position];
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListHolder lh = null;
		
		if ( convertView == null ) {
			lh = new ListHolder();
			convertView = mInflater.inflate(R.layout.list_item1, null);
			lh.img = (ImageView) convertView.findViewById(R.id.lst_imgView);
			lh.content = (TextView) convertView.findViewById(R.id.lst_textView_content);
			lh.title = (TextView) convertView.findViewById(R.id.lst_textView_title);
			convertView.setTag(lh);
		} else {
			lh = (ListHolder) convertView.getTag();
		}
		
		lh.img.setImageResource(mImgIdLst[position]);
		lh.content.setText(mContent[position]);
		lh.title.setText(mTitle[position]);
		
		return convertView;
	}
	
	public final class ListHolder {
		public ImageView img;
		public TextView content;
		public TextView title;
	}
}
