package com.benbenTaxi.v1.function;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.os.Handler;

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
	
    private AudioRecord mAudioRecord; //  录制乘客声音
	private AudioTrack mAudioTrack; // 播放乘客声音
	private MediaPlayer mMediaPlayer; // 使用mediaplayer方案
	private int mAudioBufSize = 0;
	private byte[] mAudioBuffer;
	private Handler mH = null;
	
	private boolean mIsPlay = true, mPlaying = false;
	
	private HashMap<Integer, String> mPlayList = null;
	private Iterator mIt = null;
	private int mCurrentKey = -1;
	private boolean mStop = false;
	
	public AudioProcessor(boolean play) {
		mIsPlay = play;
		mPlayList = new HashMap<Integer, String>();
		initAudio();
	}
	
	public void setHandler(Handler h) {
		mH = h;
	}
	
	public void addAudioList(int pos, String uri) {
		mPlayList.put(pos, uri);
	}
	
	public void batchPlay() {
		mStop = false;
		
		if ( getPlayListSize() <= 0 ) {
			return;
		}
		mIt = mPlayList.keySet().iterator();
		if ( mIt.hasNext() ) {
			mCurrentKey = (Integer) mIt.next();
			playAudioUri( mPlayList.get(mCurrentKey) );
		}
	}
	
	public int getPlayListSize() {
		return mPlayList.size();
	}
	
	public void resetPlay() {
		if ( mMediaPlayer.isPlaying() ) {
			mMediaPlayer.stop();
		}
		if ( mH != null ) {
			mH.dispatchMessage(mH.obtainMessage(MSG_PLAY_COMPLETE, mCurrentKey, 0));
		}
		mPlaying = false;
	}
	
	public void resetPlayList() {
		mIt = null;
		mCurrentKey = -1;
		mPlayList.clear();
		
	}
	
	public void release() {
		mMediaPlayer.release();
	}
	
	public boolean isPlayingList() {
		return mPlaying;
	}
	
	public void setStopPlay() {
		mStop = true;
	}
	
	public void playAudioUri(String uri) {
		int msg = -1;
		String info = null;
		
		try {
			mMediaPlayer.setDataSource(uri);
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
    	if ( mIsPlay ) {
        	mAudioBufSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
    	} else {
        	mAudioBufSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
    	}

    	mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, 
    			AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, mAudioBufSize);
    	
    	mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, 
    			AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, mAudioBufSize, AudioTrack.MODE_STREAM);    	
    	
    	mAudioBuffer = new byte[mAudioBufSize];
    	
    	mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mMediaPlayer.stop();
				mMediaPlayer.reset();
				mPlaying = false;
				if ( mH != null ) {
					mH.dispatchMessage(mH.obtainMessage(MSG_PLAY_COMPLETE, mCurrentKey, 0));
				}
				
				if ( mIt != null && mIt.hasNext() ) {
					mCurrentKey = (Integer) mIt.next();
					playAudioUri( mPlayList.get(mCurrentKey) );
				} else if ( mStop == false ) {
					// 循环播放
					resetPlay();
					batchPlay();
				} else {
					mH.dispatchMessage(mH.obtainMessage(MSG_PLAY_STOP, mCurrentKey, 0));
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
}
