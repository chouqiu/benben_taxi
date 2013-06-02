package com.benbenTaxi.v1.function;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

public class UrlConfigure extends IdShow {
	private final String URL_PREFER_NAME 			= "CHECK_APP_PRE";
	private final String URL_PREFER_ITEM_URL		= "SERVER_URL";
	private final static String URL_PREFER_ITEM_URL_DEFAULT = "www.365check.net";//"42.121.13.219";
	private final static String URL_TITLE		 = "输入服务器地址";
	private final String TAG			 = UrlConfigure.class.getName();
	//private View mUrlSettingView;
	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;
	private AlertDialog mUrlDialog;
	
	

	

	public UrlConfigure(Activity a)
	{
		super(URL_TITLE, URL_PREFER_ITEM_URL_DEFAULT, a);
		init();
	}
	
	public AlertDialog getUrlDialog() {
		return super.getIdDialog();
	}
	public String getUrl()
	{
		String url = "http://"+this.mPreferences.getString(URL_PREFER_ITEM_URL, URL_PREFER_ITEM_URL_DEFAULT)+"/?format=mobile";
		Log.d(TAG,"get url\t"+url);
		return url;
	}
	public String getHost()
	{
		return this.mPreferences.getString(URL_PREFER_ITEM_URL, URL_PREFER_ITEM_URL_DEFAULT);
	}
	private void init()
	{
		this.mPreferences 		= this.mA.getSharedPreferences(URL_PREFER_NAME, android.content.Context.MODE_WORLD_WRITEABLE);
		this.mEditor 			= this.mPreferences.edit();
		
		super.SetView(new EditText(super.mA.getApplicationContext()));
		
		super.SetPositiveOnclick("确定", new OnClickListener()
		{
			public void onClick(DialogInterface dialog,
				int which)
			{
				//EditText url_edit = (EditText)UrlConfigure.this.mUrlSettingView.findViewById(R.id.url_edit);
				EditText url_edit = (EditText)UrlConfigure.this.GetView();
				String myUrl = url_edit.getText().toString();
				Toast.makeText(UrlConfigure.this.mA, "已经保存服务器地址: "+myUrl, Toast.LENGTH_SHORT).show();
				Toast.makeText(UrlConfigure.this.mA, "正在加载...", Toast.LENGTH_SHORT).show();
				UrlConfigure.this.mEditor.putString(URL_PREFER_ITEM_URL, myUrl);
				UrlConfigure.this.mEditor.commit();
				Log.d(TAG,"LOAD URL\t"+UrlConfigure.this.getUrl());
			}
		});
	}

}
