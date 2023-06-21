package com.customerogo.app.model;

import com.google.gson.annotations.SerializedName;

public class PaymentdataItem{

	@SerializedName("img")
	private String img;

	@SerializedName("p_show")
	private String pShow;

	@SerializedName("subtitle")
	private String subtitle;

	@SerializedName("attributes")
	private String attributes;

	@SerializedName("id")
	private String id;

	@SerializedName("title")
	private String title;

	@SerializedName("status")
	private String status;

	public String getImg(){
		return img;
	}

	public String getPShow(){
		return pShow;
	}

	public String getSubtitle(){
		return subtitle;
	}

	public String getAttributes(){
		return attributes;
	}

	public String getId(){
		return id;
	}

	public String getTitle(){
		return title;
	}

	public String getStatus(){
		return status;
	}
}