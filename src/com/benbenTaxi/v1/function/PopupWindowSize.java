package com.benbenTaxi.v1.function;

import android.app.Activity;
import android.util.Log;
import android.view.Display;

public class PopupWindowSize {
	private final static String TAG = PopupWindowSize.class.getName();
	private final static float  WIDTH_R			=	0.9f;
	private final static float  HEIGTH_R		= 	0.5f;
	@SuppressWarnings("deprecation")
	public static int getPopupWindowHeight(Activity activity)
	{
		Display display = activity.getWindowManager().getDefaultDisplay();
		Log.d(TAG,"display hieght is "+((int) (display.getHeight() * HEIGTH_R)) + ":" + display.getHeight());
		return (int) (display.getHeight() * HEIGTH_R);
	}
	
	@SuppressWarnings("deprecation")
	public static int getPopupWindoWidth(Activity activity)
	{
		Display display = activity.getWindowManager().getDefaultDisplay();
		Log.d(TAG,"display width is "+ ((int) (display.getWidth() * WIDTH_R))+":"+display.getWidth());
		return (int) (display.getWidth() * WIDTH_R);
	}

}
