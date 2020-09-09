package com.cleartrail.dim.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
@org.codehaus.jackson.annotate.JsonIgnoreProperties(ignoreUnknown=true)
public class Path {
	
	
	String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public static List<String> toStringList(List<Path> selPath,List<String> paths) {
		
		for(Path p : selPath)
		{
			paths.add(p.getPath());
		}
		
		return paths;
	}

}
