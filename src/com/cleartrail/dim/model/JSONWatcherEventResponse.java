package com.cleartrail.dim.model;

import com.cleartrail.dim.view.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class JSONWatcherEventResponse {
	
	@JsonView(Views.Public.class)
	int type=2;
	
	@JsonView(Views.Public.class)
	int status;
	@JsonView(Views.Public.class)
	String event;
	@JsonView(Views.Public.class)
	String Path;
	@JsonView(Views.Public.class)
	FileModel fm;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public JSONWatcherEventResponse() {
		// TODO Auto-generated constructor stub
	}
	
	public JSONWatcherEventResponse(int status) {
		super();
		this.status = status;
	}

	public JSONWatcherEventResponse(int status, String event, String path, FileModel fm) {
		super();
		this.status = status;
		this.event = event;
		Path = path;
		this.fm = fm;
	}

	public JSONWatcherEventResponse(int status, String event, String path) {
		super();
		this.status = status;
		this.event = event;
		Path = path;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getPath() {
		return Path;
	}

	public void setPath(String path) {
		Path = path;
	}

	public FileModel getFm() {
		return fm;
	}

	public void setFm(FileModel fm) {
		this.fm = fm;
	}
	
}
