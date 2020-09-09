package com.cleartrail.dim.comparator;

import java.util.Comparator;

import com.cleartrail.dim.model.FileModel;

public class LastModifiedComparator implements Comparator<FileModel> {

	
	public int compare(FileModel fm1, FileModel fm2) {
		
		return (int)(fm1.getLastModifiedTime() - fm2.getLastModifiedTime());
	}
	
}