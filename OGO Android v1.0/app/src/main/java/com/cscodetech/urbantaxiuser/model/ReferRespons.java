package com.customerogo.app.model;

import com.google.gson.annotations.SerializedName;

public class ReferRespons{

	@SerializedName("refercredit")
	private String refercredit;

	@SerializedName("ResponseCode")
	private String responseCode;

	@SerializedName("code")
	private String code;

	@SerializedName("signupcredit")
	private String signupcredit;

	@SerializedName("ResponseMsg")
	private String responseMsg;

	@SerializedName("Result")
	private String result;

	public String getRefercredit(){
		return refercredit;
	}

	public String getResponseCode(){
		return responseCode;
	}

	public String getCode(){
		return code;
	}

	public String getSignupcredit(){
		return signupcredit;
	}

	public String getResponseMsg(){
		return responseMsg;
	}

	public String getResult(){
		return result;
	}
}