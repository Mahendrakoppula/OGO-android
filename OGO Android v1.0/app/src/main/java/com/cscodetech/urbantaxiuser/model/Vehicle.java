package com.customerogo.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Vehicle{

	@SerializedName("ResponseCode")
	private String responseCode;

	@SerializedName("wallet")
	private String wallet;

	@SerializedName("paymentdata")
	private List<PaymentdataItem> paymentdata;

	@SerializedName("ResponseMsg")
	private String responseMsg;

	@SerializedName("VehicleData")
	private List<VehicleDataItem> vehicleData;

	@SerializedName("Result")
	private String result;

	public String getResponseCode(){
		return responseCode;
	}

	public String getWallet(){
		return wallet;
	}

	public List<PaymentdataItem> getPaymentdata(){
		return paymentdata;
	}

	public String getResponseMsg(){
		return responseMsg;
	}

	public List<VehicleDataItem> getVehicleData(){
		return vehicleData;
	}

	public String getResult(){
		return result;
	}
}