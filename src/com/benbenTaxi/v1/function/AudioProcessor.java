package com.benbenTaxi.v1.function;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

public class AudioProcessor {  
    private AudioRecord mAudioRecord; //  ³Ë¿ÍÉùÒô
	private AudioTrack mAudioTrack; // ²¥·Å³Ë¿ÍÉùÒô
	private int mAudioBufSize = 0;
	private byte[] mAudioBuffer;
	
	private boolean mIsPlay = true;
	
	public AudioProcessor(boolean play) {
		mIsPlay = play;
		initAudio();
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
