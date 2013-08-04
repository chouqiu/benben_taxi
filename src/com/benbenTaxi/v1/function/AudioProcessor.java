package com.benbenTaxi.v1.function;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Log;

public class AudioProcessor {
	public final static int MSG_ERROR_ARG = 0x501;
	public final static int MSG_ERROR_STAT = 0x502;
	public final static int MSG_ERROR_SEC = 0x503;
	public final static int MSG_ERROR_IO = 0x504;
	public final static int MSG_ERROR_PRE_STAT = 0x505;
	public final static int MSG_ERROR_PRE_IO = 0x506;
	
	public final static int MSG_PLAY_COMPLETE = 0x601;
	public final static int MSG_PLAY_ERROR = 0x602;
	public final static int MSG_PLAY_READY = 0x603;
	public final static int MSG_PLAY_STOP = 0x604;
	public final static int MSG_PLAY_REPLAY = 0x605;
	
	private final static int FLAG_PLAY_STOP = 0xa01;
	private final static int FLAG_PLAY_REPLAY = 0xa02;
	public final static int FLAG_MODE_PLAY = 0xb01;
	public final static int FLAG_MODE_RECORD = 0xb02;
	
	
    //private AudioRecord mAudioRecord; //  录制乘客声音
	//private AudioTrack mAudioTrack; // 播放乘客声音
	private MediaPlayer mMediaPlayer; // 使用mediaplayer方案
	//private int mAudioBufSize = 0;
	//private byte[] mAudioBuffer;
	private Handler mH = null;
	
	private boolean mPlaying = false;
	
	private ArrayList< LinkedHashMap<Integer, JSONObject> > mPlayListPingPang = null;
	private int mPlayListIdx = 0;
	private Iterator<Integer> mIt = null;
	private int mCurrentKey = -1, mCurrentReqID = -1;
	private int mPlayFlag = 0, mPlayMode = 0;
	
	private String mHost = null;
	
	public AudioProcessor(String host, int play) {
		mHost = host;
		mPlayMode = play;
		mPlayListPingPang = new ArrayList< LinkedHashMap<Integer, JSONObject> >();
		mPlayListPingPang.add( new LinkedHashMap<Integer, JSONObject>() );
		mPlayListPingPang.add( new LinkedHashMap<Integer, JSONObject>() );
		initAudio();
	}
	
	public void setHandler(Handler h) {
		mH = h;
	}
	
	//public void addAudioList(int pos, String uri) {
		//getBackupPlayList().put(pos, uri);
	//}
	
	public void addAudioList(int pos, JSONObject obj) {
		getBackupPlayList().put(pos, obj);
	}
	
	public void batchPlay(boolean playCurrent) {	
		if ( mPlaying ) {
			return;
		}
	
		if ( ! playCurrent )
			exchangePlayList();
		
		LinkedHashMap<Integer, JSONObject> pl = getCurrentPlayList();
		
		if ( pl.size() <= 0 ) {
			return;
		}
		
		// 排序，找到最后播放的下一个节点
		mIt = pl.keySet().iterator();
		
		JSONObject obj = null;
		int key = -1, rid = -1;
		boolean got = false;
		while ( mIt.hasNext() ) {
			key = (Integer) mIt.next();
			obj = pl.get(key);
			rid = getReqID(obj);
			if ( mCurrentReqID < 0 || mCurrentReqID > rid ) {
				got = true;
				break;
			}
		}
		if ( got == true ) {
			setKeyAndReqID(key, rid);
			playAudioUri( getUri(obj) );
		} else {
			// 上次播放结束已是最后一个，重头开始
			mIt = pl.keySet().iterator();
			int nkey = (Integer)mIt.next();
			JSONObject nobj = pl.get(nkey);
			setKeyAndReqID( nkey, getReqID(nobj) );
			playAudioUri( getUri(nobj) );
		}
		Log.d("AudioProc", "curreq: "+mCurrentReqID+" curkey: "+mCurrentKey);
	}
	
	public int getPlayListSize() {
		return getCurrentPlayList().size();
	}
	
	private void resetPlayStatus() {
		if ( mMediaPlayer.isPlaying() ) {
			mMediaPlayer.stop();
		}
		mMediaPlayer.reset();
		mPlaying = false;
		mPlayFlag = 0;
		//mPlayList.clear();
	}
	
	private void resetPlayList() {
		mIt = null;
		//mCurrentKey = -1;
	}
	
	public void resetBackupPlayList() {
		getBackupPlayList().clear();
	}
	
	public void release() {
		resetPlayStatus();
		mMediaPlayer.release();
	}
	
	public void setStopPlay() {
		mPlayFlag = FLAG_PLAY_STOP;
		resetPlayStatus();
		resetPlayList();
		if ( mPlaying ) { 
			mH.dispatchMessage(mH.obtainMessage(MSG_PLAY_STOP, -1, 0));
		}
	}
	
	public void setRePlay() {
		mPlayFlag = FLAG_PLAY_REPLAY;
		if ( mIt == null && ! mPlaying ) {
			resetPlayStatus();
			mH.dispatchMessage(mH.obtainMessage(MSG_PLAY_REPLAY, -1, 0));
		}
	}
	
	public void playAudioUri(String uri) {
		int msg = -1;
		String info = null;
		
		try {
			mMediaPlayer.setDataSource("http://"+mHost+uri);
		} catch (IllegalArgumentException e) {
			msg = MSG_ERROR_ARG;
			info = e.toString();
		} catch (SecurityException e) {
			msg = MSG_ERROR_SEC;
			info = e.toString();
		} catch (IllegalStateException e) {
			msg = MSG_ERROR_STAT;
			info = e.toString();
		} catch (IOException e) {
			msg = MSG_ERROR_IO;
			info = e.toString();
		}
		
		if ( msg > 0 && mH != null ) {
			mH.dispatchMessage(mH.obtainMessage(msg, info));
		}
		
		msg = -1;
		info = null;
		try {
			mMediaPlayer.prepareAsync();
		} catch (IllegalStateException e) {
			msg = MSG_ERROR_PRE_STAT;
			info = e.toString();
		}
		
		if ( msg > 0 && mH != null ) {
			mH.dispatchMessage(mH.obtainMessage(msg, info));
		} else if ( msg <= 0 && mH != null ) {
			mPlaying = true;
			mH.dispatchMessage(mH.obtainMessage(MSG_PLAY_READY, mCurrentKey, 0));
		}
	}
	
	private void initAudio() {
		initAudioTrack(mPlayMode);
    	
    	mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				switch ( mPlayFlag ) {
				case FLAG_PLAY_STOP:
					resetPlayStatus();
					resetPlayList();
					if ( mH != null )
						mH.dispatchMessage(mH.obtainMessage(MSG_PLAY_STOP, mCurrentKey, 0));
					break;
				case FLAG_PLAY_REPLAY:
					resetPlayStatus();
					resetPlayList();
					if ( mH != null )
						mH.dispatchMessage(mH.obtainMessage(MSG_PLAY_REPLAY, mCurrentKey, 0));
					break;
				default:
					resetPlayStatus();
					if ( mH != null )
						mH.dispatchMessage(mH.obtainMessage(MSG_PLAY_COMPLETE, mCurrentKey, 0));
					
					if ( mIt != null && mIt.hasNext() ) {
						int nkey = (Integer) mIt.next();
						JSONObject nobj = getCurrentPlayList().get(nkey);
						setKeyAndReqID(nkey, getReqID(nobj));
						playAudioUri( getUri(nobj) );
					} else {
						resetPlayList();
						mCurrentReqID = -1;
						batchPlay(true);
					}
					break;
				}
			}
		});
		
		mMediaPlayer.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				mMediaPlayer.reset();
				if ( mH != null ) {
					mH.dispatchMessage(mH.obtainMessage(MSG_PLAY_ERROR, mCurrentKey, 0));
				}
				return false;
			}
		});
		
		mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.start();
			}
		});
    }
	
	private void initAudioTrack(int mode) {
    	/*
		switch ( mode ) {
    	case FLAG_MODE_PLAY:
        	mAudioBufSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        	break;
    	case FLAG_MODE_RECORD:
    		mAudioBufSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
    		break;
		default:
			return;
    	}

    	mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, 
    			AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, mAudioBufSize);
    	
    	mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, 
    			AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, mAudioBufSize, AudioTrack.MODE_STREAM);    	
    	
    	mAudioBuffer = new byte[mAudioBufSize];
    	*/
	}
    
	/*
    private void doRecordAudio() {
    	mAudioRecord.startRecording();
    	mAudioRecord.read(mAudioBuffer, 0, mAudioBufSize);
    	mAudioRecord.stop();
    }

    private void doPlayAudio() {
    	mAudioTrack.play();
    	mAudioTrack.write(mAudioBuffer, 0, mAudioBufSize);
    	mAudioTrack.stop();
    }
    */
    
    private LinkedHashMap<Integer, JSONObject> getBackupPlayList() {
    	return mPlayListPingPang.get((mPlayListIdx+1) % mPlayListPingPang.size());
    }
    
    private LinkedHashMap<Integer, JSONObject> getCurrentPlayList() {
    	return mPlayListPingPang.get(mPlayListIdx);
    }
    
    private void exchangePlayList() {
		mPlayListIdx = (mPlayListIdx+1) % mPlayListPingPang.size();
		getBackupPlayList().clear();
    }
    
    private String getUri(JSONObject obj) {
    	try {
    		return obj.getString("passenger_voice_url");
    	} catch (JSONException e) {
    		return null;
    	}
    }
    
    private int getReqID( JSONObject obj ) {
    	try {
    		return obj.getInt("id");
    	} catch (JSONException e) {
    		return -1;
    	}
    }
    
    private void setKeyAndReqID(int key, int rid) {
    	mCurrentKey = key;
    	mCurrentReqID = rid;
    }
}
