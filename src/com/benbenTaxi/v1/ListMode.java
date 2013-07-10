package com.benbenTaxi.v1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.benbenTaxi.R;
import com.benbenTaxi.v1.function.Distance;
import com.benbenTaxi.v1.function.RequestAdapter;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ListMode extends ListDetail {
	private String[] mTitle, mUrl;
	private RequestAdapter mReqAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void do_init_functions() {
		// 解析请求数据
		JSONArray req = super.mApp.getCurrentRequestList();
		int size = req.length();
		super.mContents = new String[size];
		mTitle = new String[size];
		mUrl = new String[size];
				
		for( int i=0; i<size; ++i ) {
        	try {
				JSONObject pos = req.getJSONObject(i);
				mTitle[i] = "电话: "+pos.getString("passenger_mobile");
				double lat = pos.getDouble("passenger_lat");
				double lng = pos.getDouble("passenger_lng");				
				super.mContents[i] = "距离: "+Distance.getDistanceFormat(lat, lng, mApp.getCurrentLocData().latitude, mApp.getCurrentLocData().longitude)+"公里";
				
				mUrl[i] = pos.getString("passenger_voice_url");
			} catch (JSONException e) {
				mTitle[i] = "电话: 解析错误";
				super.mContents[i] = "距离: 解析错误";
				mUrl[i] = "";
			}
		}
		
		mReqAdapter = new RequestAdapter(mTitle, super.mContents, this);
		super.mLv.setAdapter(mReqAdapter);
		
		mPosfunc = new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				if ( mReqAdapter.isLastPage() ) {
					Toast.makeText(ListMode.this, "已是最后一批乘客请求", Toast.LENGTH_SHORT).show();
				} else {
					mReqAdapter.refreshIdx();
				}
			}
		};
	}


	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_list, menu);
        return true;
    }  
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_map_mode:
			// 返回地图模式
			this.setResult(0);
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
