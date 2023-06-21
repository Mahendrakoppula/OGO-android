package com.customerogo.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Faq{

	@SerializedName("ResponseCode")
	private String responseCode;

	@SerializedName("FaqData")
	private List<FaqDataItem> faqData;

	@SerializedName("ResponseMsg")
	private String responseMsg;

	@SerializedName("Result")
	private String result;

	public String getResponseCode(){
		return responseCode;
	}

	public List<FaqDataItem> getFaqData(){
		return faqData;
	}

	public String getResponseMsg(){
		return responseMsg;
	}

	public String getResult(){
		return result;
	}
}