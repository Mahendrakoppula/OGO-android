package com.customerogo.app.model;

import com.google.gson.annotations.SerializedName;

public class Home{

	@SerializedName("ResponseCode")
	private String responseCode;

	@SerializedName("home_data")
	private HomeData homeData;

	@SerializedName("ResponseMsg")
	private String responseMsg;

	@SerializedName("Result")
	private String result;

	public String getResponseCode(){
		return responseCode;
	}

	public HomeData getHomeData(){
		return homeData;
	}

	public String getResponseMsg(){
		return responseMsg;
	}

	public String getResult(){
		return result;
	}
}