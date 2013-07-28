package com.benbenTaxi.v1;

import com.benbenTaxi.R;
import com.benbenTaxi.v1.function.AudioProcessor;
import com.benbenTaxi.v1.function.CallAdapter;
import com.benbenTaxi.v1.function.DataPreference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ListDetail extends Activity {
	protected ListView mLv;
	protected Button mBtnPos, mBtnNeg;
	protected View.OnClickListener mPosfunc, mNegfunc;
	protected String[] mContents;
	protected BenbenApplication mApp;
	private String tip_pos, tip_neg;
    private AudioProcessor mAp = null;
    
    private Handler mediaHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case AudioProcessor.MSG_ERROR_ARG:
				Toast.makeText(ListDetail.this, "����������������: "+(String)msg.obj, Toast.LENGTH_SHORT).show();
				break;
			case AudioProcessor.MSG_ERROR_IO:
				Toast.makeText(ListDetail.this, "��������IO����: "+(String)msg.obj, Toast.LENGTH_SHORT).show();
				break;
			case AudioProcessor.MSG_ERROR_SEC:
				Toast.makeText(ListDetail.this, "��������Ȩ�޴���: "+(String)msg.obj, Toast.LENGTH_SHORT).show();
				break;
			case AudioProcessor.MSG_ERROR_STAT:
				Toast.makeText(ListDetail.this, "��������״̬����: "+(String)msg.obj, Toast.LENGTH_SHORT).show();
				break;
			case AudioProcessor.MSG_ERROR_PRE_IO:
				Toast.makeText(ListDetail.this, "׼����ƵIO����: "+(String)msg.obj, Toast.LENGTH_SHORT).show();
				break;
			case AudioProcessor.MSG_ERROR_PRE_STAT:
				Toast.makeText(ListDetail.this, "׼����Ƶ״̬����: "+(String)msg.obj, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_dialog);
		
		mApp = (BenbenApplication) this.getApplicationContext();
		Bundle tips = getIntent().getExtras();
		if ( tips != null ) {
			tip_pos = tips.getString("pos");
			tip_neg = tips.getString("neg");
		}
		
		init();
		
		DataPreference data = new DataPreference(this);
		String host = data.LoadString("host");
		
		// ��ʼ���������, ����url�����һ��
		if ( mContents!=null ) {
			int idx = mContents.length - 1;
			if ( mContents[idx]!=null && mContents[idx].length()>0 ) {
			    mAp = new AudioProcessor(true);
			    mAp.playAudioUri("http://"+host+mContents[idx]);
			}
			//Toast.makeText(this, "��������["+idx+"]: "+host+mContents[idx], Toast.LENGTH_SHORT).show();
		}
	}
	
	private void init() {
    	mLv = (ListView)findViewById(R.id.listView);
    	mBtnPos = (Button)findViewById(R.id.btnListOk);
    	mBtnNeg = (Button)findViewById(R.id.btnListCancel);
    	
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
	
	protected void do_init_functions() {
		mContents = mApp.getCurrentInfo();
    	mLv.setAdapter(new CallAdapter(mContents, this));
    	
		mPosfunc = new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent it = new Intent();
				setResult(1, it);
				finish();
			}
		};
		
		mNegfunc = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent();
				setResult(0, it);
				finish();
			}
		};
	}
	
	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event ) {
		if(keyCode == KeyEvent.KEYCODE_BACK && 
				event.getAction() == KeyEvent.ACTION_DOWN) {
			// ���ﲻ��Ҫ�����ؼ��˳�
	        //return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
