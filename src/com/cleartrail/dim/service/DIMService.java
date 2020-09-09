package com.cleartrail.dim.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cleartrail.dim.model.JSONSearchResultResponse;
import com.cleartrail.dim.model.FileModel;
import com.cleartrail.dim.model.JSONFileModelMapResponse;
import com.cleartrail.dim.model.JSONNotificationResponse;
import com.cleartrail.dim.model.JSONSelectedPathsRequest;

public interface DIMService {

	int addPath(File f, Map<String, String> map);

	boolean validatePath(File f);

	Map<String,FileModel> getAll();

	int saveIndexFilePath(String path);

	Map<String, String> loadPaths();

	JSONFileModelMapResponse getMetaForPaths(List<String> paths);

	JSONFileModelMapResponse getMetaForSubPaths(String path);

	JSONNotificationResponse addStopWord(String word);

	List<JSONSearchResultResponse> searchToken(String word);

	String getContent(String path);

	Map<String, Map<String,Integer>> getAllTokens();

	void download(HttpServletRequest request, HttpServletResponse response, String path);

	
}