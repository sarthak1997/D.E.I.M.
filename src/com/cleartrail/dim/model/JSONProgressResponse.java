package com.cleartrail.dim.model;

import com.cleartrail.dim.view.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class JSONProgressResponse {
	
	@JsonView(Views.Public.class)
	int type=1;
	
	@JsonView(Views.Public.class)
	long TotalFiles;
	
	@JsonView(Views.Public.class)
	long currentFileNo;

	@JsonView(Views.Public.class)
	String Path;

	public long getTotalFiles() {
		return TotalFiles;
	}

	public void setTotalFiles(long totalFiles) {
		TotalFiles = totalFiles;
	}

	public long getCurrentFileNo() {
		return currentFileNo;
	}

	public void setCurrentFileNo(long currentFileNo) {
		this.currentFileNo = currentFileNo;
	}

	public JSONProgressResponse(long currentFileNo, String path) {
		super();
		this.currentFileNo = currentFileNo;
		Path = path;
	}

	public JSONProgressResponse(long totalFiles, long currentFileNo) {
		super();
		TotalFiles = totalFiles;
		this.currentFileNo = currentFileNo;
	}
	

	
}