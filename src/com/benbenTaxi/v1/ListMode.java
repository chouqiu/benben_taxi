package com.benbenTaxi.v1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.benbenTaxi.R;
import com.benbenTaxi.v1.function.Distance;
import com.benbenTaxi.v1.function.RequestAdapter;
import com.benbenTaxi.v1.function.ShowDetail;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ListMode extends ListDetail {
	private RequestAdapter mReqAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void do_init_functions() {
		// 解析请求数据
		JSONArray req = super.mApp.getCurrentRequestList();
		
		mReqAdapter = new RequestAdapter(req, this);
		super.mLv.setAdapter(mReqAdapter);
		
		super.mLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				ShowDetail.showPassengerRequestInfo(mApp, BenbenLocationMain.this, mReqId, mConfirmObj);
				
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
