package com.customerogo.app.model;

import com.google.gson.annotations.SerializedName;

public class OrderHistoryItem{

	@SerializedName("ride_id")
	private String rideId;

	@SerializedName("o_status")
	private String oStatus;

	@SerializedName("pick_long")
	private String pickLong;

	@SerializedName("drop_address")
	private String dropAddress;

	@SerializedName("rider_rate")
	private Object riderRate;

	@SerializedName("drop_long")
	private String dropLong;

	@SerializedName("is_rate")
	private String isRate;

	@SerializedName("car_type")
	private String carType;

	@SerializedName("pick_lat")
	private String pickLat;

	@SerializedName("drop_lat")
	private String dropLat;

	@SerializedName("pick_address")
	private String pickAddress;

	@SerializedName("rider_text")
	private String riderText;

	@SerializedName("order_total")
	private String orderTotal;

	@SerializedName("car_img")
	private String carImg;

	@SerializedName("order_date")
	private String orderDate;

	public String getRideId(){
		return rideId;
	}

	public String getOStatus(){
		return oStatus;
	}

	public String getPickLong(){
		return pickLong;
	}

	public String getDropAddress(){
		return dropAddress;
	}

	public Object getRiderRate(){
		return riderRate;
	}

	public String getDropLong(){
		return dropLong;
	}

	public String getIsRate(){
		return isRate;
	}

	public String getCarType(){
		return carType;
	}

	public String getPickLat(){
		return pickLat;
	}

	public String getDropLat(){
		return dropLat;
	}

	public String getPickAddress(){
		return pickAddress;
	}

	public String getRiderText(){
		return riderText;
	}

	public String getOrderTotal(){
		return orderTotal;
	}

	public String getCarImg(){
		return carImg;
	}

	public String getOrderDate() {
		return orderDate;
	}
}