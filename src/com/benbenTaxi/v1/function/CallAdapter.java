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
	private String[] mContent;
	private int[] mImgIdLst;
	
	public CallAdapter(int[] idLst, String[] contents, Context con) {
		mInflater = LayoutInflater.from(con);
		mImgIdLst = idLst;
		mContent = contents;
	}
	
	@Override
	public int getCount() {
		return mContent.length;
	}

	@Override
	public Object getItem(int position) {
		if ( position >=0 && position < mContent.length ) {
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
			convertView = mInflater.inflate(R.layout.list_item, null);
			lh.img = (ImageView) convertView.findViewById(R.id.lst_imgView);
			lh.content = (TextView) convertView.findViewById(R.id.lst_textView);
			convertView.setTag(lh);
		} else {
			lh = (ListHolder) convertView.getTag();
		}
		
		lh.img.setImageResource(mImgIdLst[position]);
		lh.content.setText(mContent[position]);
		
		return convertView;
	}
	
	public final class ListHolder {
		public ImageView img;
		public TextView content;
	}
}
