package com.customerogo.app.model;

import com.google.gson.annotations.SerializedName;

public class Order{

	@SerializedName("ResponseCode")
	private String responseCode;

	@SerializedName("ResponseMsg")
	private String responseMsg;

	@SerializedName("Mapinfo")
	private Mapinfo mapinfo;

	@SerializedName("Result")
	private String result;

	public String getResponseCode(){
		return responseCode;
	}

	public String getResponseMsg(){
		return responseMsg;
	}

	public Mapinfo getMapinfo(){
		return mapinfo;
	}

	public String getResult(){
		return result;
	}
}