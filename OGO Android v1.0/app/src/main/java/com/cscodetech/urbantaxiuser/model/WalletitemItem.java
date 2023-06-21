package com.customerogo.app.model;

import com.google.gson.annotations.SerializedName;

public class WalletitemItem{

	@SerializedName("amt")
	private String amt;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	@SerializedName("tdate")
	private String tdate;

	public String getAmt(){
		return amt;
	}

	public String getMessage(){
		return message;
	}

	public String getStatus(){
		return status;
	}

	public String getTdate() {
		return tdate;
	}

	public void setTdate(String tdate) {
		this.tdate = tdate;
	}
}