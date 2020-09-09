package com.cleartrail.dim.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown=true)
@org.codehaus.jackson.annotate.JsonIgnoreProperties(ignoreUnknown=true)
public class JSONSelectedPathsRequest {
	
	public List<Path> getSelPaths() {
		return selPaths;
	}

	public void setSelPaths(List<Path> selPaths) {
		this.selPaths = selPaths;
	}

	@Override
	public String toString() {
		return "JSONSelectedPathsRequest [selPaths=" + selPaths + "]";
	}

	private List<Path> selPaths;
	
}
