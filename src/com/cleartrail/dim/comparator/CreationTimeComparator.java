package com.cleartrail.dim.comparator;

import java.util.Comparator;

import com.cleartrail.dim.model.FileModel;

public class CreationTimeComparator implements Comparator<FileModel> {

	public int compare(FileModel fm1, FileModel fm2) {
		
		return (int)(fm1.getCreationTime() - fm2.getCreationTime());
	}
	
}