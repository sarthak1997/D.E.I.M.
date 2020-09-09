package com.cleartrail.dim.comparator;

import java.util.Comparator;

import com.cleartrail.dim.model.FileModel;

public class LineCountComparator implements Comparator<FileModel> {

	@Override
	public int compare(FileModel fm1, FileModel fm2) {
		
		return (fm1.getLineCount()-fm2.getLineCount());
	}
	
}
