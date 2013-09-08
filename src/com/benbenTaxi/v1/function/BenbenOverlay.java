package com.benbenTaxi.v1.function;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.widget.Toast;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.benbenTaxi.R;
import com.benbenTaxi.v1.BenbenLocationMain;
import com.benbenTaxi.v1.function.api.JsonHelper;

/**
 *  在一个圆周上添加自定义overlay. 
 */
public class BenbenOverlay extends ItemizedOverlay<OverlayItem> {
    public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	private Context mContext = null;
    static PopupOverlay pop = null;
    private Handler mH = null;

    Toast mToast = null;
    
    // 存放overlay图片
 	private List<Drawable>  res = new ArrayList<Drawable>();
 	private Drawable mDrvMarker;
 	private Drawable mOldMarker = null; // 保存更新前使用的标记
 	
 	private int mSelectedIdx = -1;

    
	public BenbenOverlay(Drawable marker,Context context, MapView mapView){
		super(marker,mapView);
		this.mContext = context;
        pop = new PopupOverlay( mapView,new PopupClickListener() {
			
			@Override
			public void onClickedPopup(int index) {
			    if (null == mToast)
			        mToast = Toast.makeText(mContext, "popup item :" + index + " is clicked.", Toast.LENGTH_SHORT);
			    else mToast.setText("popup item :" + index + " is clicked.");
			    mToast.show();
			}
		});
       // 自2.1.1 开始，使用 add/remove 管理overlay , 无需调用以下接口.
	   // populate();
		
		res.add(mContext.getResources().getDrawable(R.drawable.icon_marka));
		res.add(mContext.getResources().getDrawable(R.drawable.icon_markb));
		res.add(mContext.getResources().getDrawable(R.drawable.icon_markc));
		res.add(mContext.getResources().getDrawable(R.drawable.icon_markd));
		res.add(mContext.getResources().getDrawable(R.drawable.icon_marke));
		res.add(mContext.getResources().getDrawable(R.drawable.icon_markf));
		res.add(mContext.getResources().getDrawable(R.drawable.icon_markg));
		res.add(mContext.getResources().getDrawable(R.drawable.icon_markh));
		res.add(mContext.getResources().getDrawable(R.drawable.icon_marki));
		res.add(mContext.getResources().getDrawable(R.drawable.icon_markj));
	}
	
	public BenbenOverlay(Drawable marker,Context context, MapView mapView, Handler h){
		this(marker, context, mapView);
		mH = h;
	}
	
	public void updateOverlayView() {
		this.removeAll();
		if ( mGeoList.size() > 0 ) {
			this.addItem(mGeoList);
		}
	}
	
	public void setSelectItem(int reqidx) {
		if ( reqidx < 0 || reqidx >= mGeoList.size() )
			return;
		mSelectedIdx = reqidx;
		
		OverlayItem it = mGeoList.get(mSelectedIdx);
		mOldMarker = it.getMarker();
		it.setMarker(mContext.getResources().getDrawable(R.drawable.location2));
		mGeoList.set(mSelectedIdx, it);
	}
	
	public void resetSelectItem() {
		if ( mSelectedIdx < 0 || mSelectedIdx >= mGeoList.size() || mOldMarker == null )
			return;
		
		OverlayItem it = mGeoList.get(mSelectedIdx);
		it.setMarker(mOldMarker);
		mGeoList.set(mSelectedIdx, it);
		mOldMarker = null;
		mSelectedIdx = -1;
	}
	
	protected boolean onTap(int index) {
		//System.out.println("item onTap: "+index);
	    
	    Bitmap[] bmps = new Bitmap[3];
	    if (index %2 == 0) {
	        try {
	            bmps[0] = BitmapFactory.decodeStream(mContext.getAssets().open("marker1.png"));
	            bmps[1] = BitmapFactory.decodeStream(mContext.getAssets().open("marker2.png"));
	            bmps[2] = BitmapFactory.decodeStream(mContext.getAssets().open("marker3.png"));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    else {
	        try {
                bmps[2] = BitmapFactory.decodeStream(mContext.getAssets().open("marker1.png"));
                bmps[1] = BitmapFactory.decodeStream(mContext.getAssets().open("marker2.png"));
                bmps[0] = BitmapFactory.decodeStream(mContext.getAssets().open("marker3.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
	    }
        
	    //pop.showPopup(bmps, getItem(index).getPoint(), 32);
		//if (null == mToast)
        //  mToast = Toast.makeText(mContext, getItem(index).getTitle(), Toast.LENGTH_SHORT);
        //else mToast.setText(getItem(index).getTitle());
		//mToast.show();
	    
	    if ( mH != null ) {
	    	mH.dispatchMessage(mH.obtainMessage(BenbenLocationMain.MSG_HANDLE_ITEM_TOUCH+index));
	    }
		
		return true;
	}
	public boolean onTap(GeoPoint pt, MapView mapView){
		if (pop != null){
			pop.hidePop();
		}
		super.onTap(pt,mapView);
		return false;
	}

	public void addPreparedItem(JSONArray reqInfo) {
		//清除所有添加的Overlay
        mGeoList.clear();
        
		//添加一个item
    	//当要添加的item较多时，可以使用addItem(List<OverlayItem> items) 接口
        for( int i=0; i<reqInfo.length(); ++i ) {
        	JSONObject pos = JsonHelper.getJsonObj(reqInfo, i);        	
        	OverlayItem item = new OverlayItem(new GeoPoint((int)(JsonHelper.getDouble(pos, "passenger_lat")*1e6), 
    				(int)(JsonHelper.getDouble(pos, "passenger_lng")*1e6)),
	        		"乘客"+JsonHelper.getInt(pos, "id"), "声音: "+JsonHelper.getString(pos, "passenger_voice_url"));
	        
        	if ( item != null ) {
			   	item.setMarker(res.get(i%res.size()));
			   	mGeoList.add(item);
        	}
        }
	}
	
	// 自2.1.1 开始，使用 add/remove 管理overlay , 无需重写以下接口
	/*
	@Override
	protected OverlayItem createItem(int i) {
		return mGeoList.get(i);
	}
	
	@Override
	public int size() {
		return mGeoList.size();
	}
	*/
}
