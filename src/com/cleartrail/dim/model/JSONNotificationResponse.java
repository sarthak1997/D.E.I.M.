package com.cleartrail.dim.model;

import java.util.Map;

import com.cleartrail.dim.view.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class JSONNotificationResponse {
	
	@JsonView(Views.Public.class)
	int statusCode;
	
	@JsonView(Views.Public.class)
	Map<String,String> map;

	public JSONNotificationResponse() {
		// TODO Auto-generated constructor stub
	}
	
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public JSONNotificationResponse(int statusCode) {
		super();
		this.statusCode = statusCode;
	}

	public JSONNotificationResponse(int statusCode, Map<String, String> map) {
		super();
		this.statusCode = statusCode;
		this.map = map;
	}

	
	

}
