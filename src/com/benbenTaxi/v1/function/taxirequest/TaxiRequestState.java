package com.benbenTaxi.v1.function.taxirequest;

public enum TaxiRequestState {
	Waiting_Driver_Response(0,"等待司机响应。","等待"),
	Success(2,"打车成功。","成功"),
	TimeOut(3,"用户响应超时,请重新打车！","失败"),
	Canceled_By_Passenger(4,"被乘客取消打车请求。","取消"),	
	UNKONW(5,"未知状态","未知");
	
	private int mIndex;
	private String mHumanText;
	private String mHumanBreifText;
	private TaxiRequestState(int index,String text,String bref)
	{
		this.mIndex = index;
		this.mHumanText = text;
		this.mHumanBreifText =  bref;
	}
	public int getIndex()
	{
		return this.mIndex;
	}
	public String getHumanText()
	{
		return this.mHumanText;
	}
	public String getHumanBreifText()
	{
		return this.mHumanBreifText;
	}
}
