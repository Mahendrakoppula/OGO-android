package com.customerogo.app.model;

import com.google.gson.annotations.SerializedName;

public class Mapinfo{

	@SerializedName("pick_long")
	private double pickLong;

	@SerializedName("rider_longs")
	private double riderLongs;

	@SerializedName("orderid")
	private String orderid;

	@SerializedName("drop_address")
	private String dropAddress;

	@SerializedName("title")
	private String title;

	@SerializedName("drop_long")
	private double dropLong;

	@SerializedName("rider_img")
	private String riderImg;

	@SerializedName("rider_id")
	private String riderid;

	@SerializedName("request_step")
	private int requestStep;

	@SerializedName("pick_lat")
	private double pickLat;

	@SerializedName("drop_lat")
	private double dropLat;

	@SerializedName("rider_name")
	private String riderName;

	@SerializedName("subtitle")
	private String subtitle;

	@SerializedName("pick_address")
	private String pickAddress;

	@SerializedName("rider_mobile")
	private String riderMobile;

	@SerializedName("request_arrive_seconds")
	private int requestArriveSeconds;

	@SerializedName("otp")
	private int otp;

	@SerializedName("vehicle_number")
	private String vehicleNumber;

	@SerializedName("rider_lats")
	private double riderLats;

	public double getPickLong(){
		return pickLong;
	}

	public double getRiderLongs(){
		return riderLongs;
	}

	public String getOrderid(){
		return orderid;
	}

	public String getDropAddress(){
		return dropAddress;
	}

	public String getTitle(){
		return title;
	}

	public double getDropLong(){
		return dropLong;
	}

	public String getRiderImg(){
		return riderImg;
	}

	public int getRequestStep(){
		return requestStep;
	}

	public double getPickLat(){
		return pickLat;
	}

	public double getDropLat(){
		return dropLat;
	}

	public String getRiderName(){
		return riderName;
	}

	public String getSubtitle(){
		return subtitle;
	}

	public String getPickAddress(){
		return pickAddress;
	}

	public String getRiderMobile(){
		return riderMobile;
	}

	public int getRequestArriveSeconds(){
		return requestArriveSeconds;
	}

	public double getRiderLats(){
		return riderLats;
	}

	public String getRiderid() {
		return riderid;
	}

	public int getOtp() {
		return otp;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}
}