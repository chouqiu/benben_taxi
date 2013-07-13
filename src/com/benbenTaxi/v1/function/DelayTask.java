package com.benbenTaxi.v1.function;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;

public class DelayTask extends AsyncTask<Integer, Integer, Boolean> {
	public final static int MSG_DELAY_OK = 0x10001;
	
	private int _type;
	private Handler mH;
	
	public DelayTask( int type, Handler h ) {
		_type = type;
		mH = h;
	}
	
	@Override
	protected Boolean doInBackground(Integer... params) {
		// 获取延迟时间, ms
		int delay = params[0];
		SystemClock.sleep(delay);
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if ( mH != null ) {
			mH.dispatchMessage(mH.obtainMessage(MSG_DELAY_OK, _type, 0));
		}
	}
}
