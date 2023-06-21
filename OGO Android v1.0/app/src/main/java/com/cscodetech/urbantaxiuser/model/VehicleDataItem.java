package com.customerogo.app.model;

import com.google.gson.annotations.SerializedName;

public class VehicleDataItem{

	private boolean isSelct;


	@SerializedName("img")
	private String img;

	@SerializedName("uprice")
	private int uprice;

	@SerializedName("ukms")
	private int ukms;

	@SerializedName("id")
	private String id;

	@SerializedName("title")
	private String title;

	@SerializedName("aprice")
	private int aprice;

	@SerializedName("status")
	private String status;

	@SerializedName("timetaken")
	private int timetaken;

	@SerializedName("is_available")
	private int isAvailable;

	public String getImg(){
		return img;
	}

	public int getUprice(){
		return uprice;
	}

	public int getUkms(){
		return ukms;
	}

	public String getId(){
		return id;
	}

	public String getTitle(){
		return title;
	}

	public int getAprice(){
		return aprice;
	}

	public String getStatus(){
		return status;
	}

	public int getTimetaken() {
		return timetaken;
	}

	public int getIsAvailable() {
		return isAvailable;
	}

	public boolean isSelct() {
		return isSelct;
	}

	public void setSelct(boolean selct) {
		isSelct = selct;
	}
}