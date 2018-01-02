package com.gazorpazorp.model.dto;

public class Customer {
	private String id;
	private String stripeId;
	
	public Customer() {}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStripeId() {
		return stripeId;
	}
	public void setStripeId(String stripeId) {
		this.stripeId = stripeId;
	}
	
	
}
