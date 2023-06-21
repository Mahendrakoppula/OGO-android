package com.customerogo.app.model;

import com.google.gson.annotations.SerializedName;

public class HomeData{

	@SerializedName("app_lan")
	private String appLan;

	@SerializedName("currency")
	private String currency;

	public String getAppLan(){
		return appLan;
	}

	public String getCurrency(){
		return currency;
	}
}