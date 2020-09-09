package com.cleartrail.dim.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cleartrail.dim.dao.DIMDao;
import com.cleartrail.dim.model.FileModel;
import com.cleartrail.dim.model.JSONFileModelMapResponse;
import com.cleartrail.dim.model.JSONNotificationResponse;
import com.cleartrail.dim.model.JSONSearchResultResponse;

@Service
public class DIMServiceImpl implements DIMService {

	@Autowired
	private DIMDao dao;
	
	
	public boolean validatePath(File f) {
		
		return dao.validatePath(f);
	}
	
	
	public Map<String, FileModel> getAll() {
		
		return dao.getAll();
	}


	@Override
	public int saveIndexFilePath(String path) {
		// TODO Auto-generated method stub
		
		return dao.saveIndexFilePath(path);
		
	}


	@Override
	public int addPath(File f, Map<String, String> map) {
		// TODO Auto-generated method stub
		return dao.addPath(f,map);
	}

	@Override
	public Map<String, String> loadPaths() {
		// TODO Auto-generated method stub
		return dao.loadPaths();
	}

	@Override
	public JSONFileModelMapResponse getMetaForPaths(List<String> paths) {
		// TODO Auto-generated method stub
		return dao.getMetaForPaths(paths);
	}

	@Override
	public JSONFileModelMapResponse getMetaForSubPaths(String path) {
		// TODO Auto-generated method stub
		return dao.getMetaForSubPaths(path);
	}


	@Override
	public JSONNotificationResponse addStopWord(String word) {
		// TODO Auto-generated method stub
		return dao.addStopWord(word);
	}


	@Override
	public List<JSONSearchResultResponse> searchToken(String word) {
		// TODO Auto-generated method stub
		return dao.searchToken(word);
	}


	@Override
	public String getContent(String path) {
		// TODO Auto-generated method stub
		return dao.getContent(path);
	}


	@Override
	public Map<String, Map<String,Integer>> getAllTokens() {
		// TODO Auto-generated method stub
		return dao.getAllTokens();
	}


	@Override
	public void download(HttpServletRequest request, HttpServletResponse response, String path) {
		// TODO Auto-generated method stub
		dao.download(request,response,path);
	}
}