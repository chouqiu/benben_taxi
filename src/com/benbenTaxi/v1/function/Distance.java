package com.benbenTaxi.v1.function;

import java.text.DecimalFormat;

import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class Distance {
	private final static DecimalFormat mDF = new DecimalFormat("#.##");
	
	public static double getDistance(double lat0, double lng0, double lat1, double lng1) {
		GeoPoint gp1 = new GeoPoint((int)(lat0* 1e6), (int)(lng0 *  1e6));
    	GeoPoint gp2 = new GeoPoint((int)(lat1* 1e6), (int)(lng1 *  1e6));
    	
    	return DistanceUtil.getDistance(gp1, gp2);
	}
	
	public static String getDistanceFormat(double lat0, double lng0, double lat1, double lng1) {
		return mDF.format(getDistance(lat0, lng0, lat1, lng1));
	}
}
