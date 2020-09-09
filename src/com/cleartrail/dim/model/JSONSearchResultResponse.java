package com.cleartrail.dim.model;

import com.cleartrail.dim.view.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class JSONSearchResultResponse {
	
	@JsonView(Views.Public.class)
	int statuscode;
	@JsonView(Views.Public.class)
	FileModel fm;
	@JsonView(Views.Public.class)
	int count;

	public JSONSearchResultResponse() {
		// TODO Auto-generated constructor stub
	}
	
	public JSONSearchResultResponse(int statuscode) {
		super();
		this.statuscode = statuscode;
	}

	public JSONSearchResultResponse(int statuscode, FileModel fm, int count) {
		super();
		this.statuscode = statuscode;
		this.fm = fm;
		this.count = count;
	}

	public int getStatuscode() {
		return statuscode;
	}

	public void setStatuscode(int statuscode) {
		this.statuscode = statuscode;
	}

	public FileModel getFm() {
		return fm;
	}

	public void setFm(FileModel fm) {
		this.fm = fm;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	

	
}
