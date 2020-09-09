package com.cleartrail.dim.model;

import java.io.Serializable;
import java.util.Map;

import com.cleartrail.dim.view.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class FileModel implements Serializable{

	@JsonView(Views.Public.class)
	private String name,extension,type,parent,path;
	@JsonView(Views.Public.class)
	private long length;
	@JsonView(Views.Public.class)
	private int wordCount,lineCount;
	@JsonView(Views.Public.class)
	private long lastAccessTime,lastModifiedTime,creationTime;
	
	private Map<String,FileModel> filesList;
	
	@JsonView(Views.Public.class)
	private boolean isDirectory;
	@JsonView(Views.Public.class)
	private Map<String,Integer> tokenList;
	
	
	public Map<String, Integer> getTokenList() {
		return tokenList;
	}
	public void setTokenList(Map<String, Integer> tokenList) {
		this.tokenList = tokenList;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Map<String, FileModel> getFilesList() {
		return filesList;
	}
	public void setFilesList(Map<String, FileModel> filesList) {
		this.filesList = filesList;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public boolean isDirectory() {
		return isDirectory;
	}
	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {		
		return this.getParent()+" "+this.getName() + "\t" + this.getExtension() + "\n"; /*+ this.getLength() + "\t"
				+ this.getLastAccessTime() + "\t" + this.getLastModifiedTime() + "\t" + this.getCreationTime() + "\t"
				+ this.getWordCount() + "\t" + this.getLineCount() + "\t" + "TYPE";*/
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	public int getWordCount() {
		return wordCount;
	}
	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}
	public int getLineCount() {
		return lineCount;
	}
	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}
	public long getLastAccessTime() {
		return lastAccessTime;
	}
	public void setLastAccessTime(long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}
	public long getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	public long getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
	
	
}