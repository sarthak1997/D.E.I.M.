package com.cleartrail.dim.comparator;

import java.util.Comparator;

import com.cleartrail.dim.model.FileModel;

public class WordCountComparator implements Comparator<FileModel> {

	@Override
	public int compare(FileModel fm1, FileModel fm2) {
		
		return (fm1.getWordCount()-fm2.getWordCount());
	}
	
}
