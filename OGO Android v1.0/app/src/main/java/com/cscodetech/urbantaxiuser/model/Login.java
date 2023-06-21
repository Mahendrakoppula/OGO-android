package com.customerogo.app.model;

import com.google.gson.annotations.SerializedName;

public class Login{

	@SerializedName("ResponseCode")
	private String responseCode;

	@SerializedName("ResponseMsg")
	private String responseMsg;

	@SerializedName("currency")
	private String currency;

	@SerializedName("UserLogin")
	private User userLogin;

	@SerializedName("Result")
	private String result;

	public String getResponseCode(){
		return responseCode;
	}

	public String getResponseMsg(){
		return responseMsg;
	}

	public String getCurrency(){
		return currency;
	}

	public User getUserLogin(){
		return userLogin;
	}

	public String getResult(){
		return result;
	}
}