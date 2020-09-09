package com.cleartrail.dim.model;

import java.util.List;

import com.cleartrail.dim.view.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class JSONFileModelMapResponse {
	
	@JsonView(Views.Public.class)
	int code;
	
	@JsonView(Views.Public.class)
	List<FileModel> result;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public List<FileModel> getResult() {
		return result;
	}

	public void setResult(List<FileModel> result) {
		this.result = result;
	}

}
