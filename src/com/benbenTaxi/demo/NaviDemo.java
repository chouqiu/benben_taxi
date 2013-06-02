package com.benbenTaxi.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;

import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.benbenTaxi.R;

public class NaviDemo extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navi_demo);
		
		double mLat1 = 39.915; 
	   	double mLon1 = 116.404; 
	   	double mLat2 = 32.032;
	   	double mLon2 = 118.799;
		
		int lat = (int) (mLat1 *1E6);
	   	int lon = (int) (mLon1 *1E6);   	
	   	GeoPoint pt1 = new GeoPoint(lat, lon);
		lat = (int) (mLat2 *1E6);
	   	lon = (int) (mLon2 *1E6);
	    GeoPoint pt2 = new GeoPoint(lat, lon);
	    
        NaviPara para = new NaviPara();
        para.startPoint = pt1;
        para.startName= "从这里开始";
        para.endPoint  = pt2;
        para.endName   = "到这里结束";
        
        try {
        	
			 BaiduMapNavigation.openBaiduMapNavi(para, this);
			 
		} catch (BaiduMapAppNotSupportNaviException e) {
			e.printStackTrace();
			  AlertDialog.Builder builder = new AlertDialog.Builder(this);
			  builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
			  builder.setTitle("提示");
			  builder.setPositiveButton("确认", new OnClickListener() {
			   @Override
			   public void onClick(DialogInterface dialog, int which) {
				 dialog.dismiss();
				 BaiduMapNavigation.GetLatestBaiduMapApp(NaviDemo.this);
			   }
			  });

			  builder.setNegativeButton("取消", new OnClickListener() {
			   @Override
			   public void onClick(DialogInterface dialog, int which) {
			    dialog.dismiss();
			   }
			  });

			  builder.create().show();
			 }
		}
		

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_navi_demo, menu);
		return true;
	}
		

}
