package com.benbenTaxi.v1;

import com.benbenTaxi.R;
import com.benbenTaxi.v1.function.CallAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class ListDetail extends Activity {
	private ListView mLv;
	private Button mBtn;
	private String[] mContents;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_dialog);
		
		mLv = (ListView) findViewById(R.id.listView);
		mBtn = (Button) findViewById(R.id.btnListOk);
		
		mLv.setAdapter(new CallAdapter(mContents, this));
		mBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
}
