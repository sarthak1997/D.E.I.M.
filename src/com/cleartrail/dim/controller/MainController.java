package com.cleartrail.dim.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ServletContextAware;

import com.cleartrail.dim.model.FileModel;
import com.cleartrail.dim.model.JSONFileModelMapResponse;
import com.cleartrail.dim.model.JSONNotificationResponse;
import com.cleartrail.dim.model.JSONSearchResultResponse;
import com.cleartrail.dim.model.JSONSelectedPathsRequest;
import com.cleartrail.dim.model.Path;
import com.cleartrail.dim.service.DIMService;
import com.cleartrail.dim.service.WatcherService;
import com.cleartrail.dim.view.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Controller
@RestController
@RequestMapping
public class MainController{
	
	private String path;
	private Map<String,FileModel> filesList;
	private File f;
	
	@Autowired
	private DIMService service;

	@Autowired
	private WatcherService watcher;
	
	@ResponseBody
	@RequestMapping("/allTokens")
	public Map<String,Map<String,Integer>> getAllTokens()
	{
		return service.getAllTokens();
	}
	
	@ResponseBody
	@RequestMapping("/content")
	public String getContent(@RequestParam("path") String path)
	{
		return service.getContent(path);
	}
	
	@ResponseBody
	@RequestMapping("/onload")
	public JSONNotificationResponse loadPaths()
	{
		return new JSONNotificationResponse(0,service.loadPaths());
	}
	
	@ResponseBody
	@RequestMapping("/path")
	public JSONNotificationResponse addPath(@RequestParam("path") String path)
	{
		System.out.println(System.currentTimeMillis());
		System.out.println(path);
		f=new File(path);
		Map<String,String> map = new HashMap<String,String>();
		int code=service.addPath(f,map);
		System.out.println(service.getAll().values());
		System.out.println(System.currentTimeMillis());
		return new JSONNotificationResponse(code,map);
	}

	@ResponseBody
	@RequestMapping("/indexfilepath")
	public JSONNotificationResponse saveIndexFilePath(@RequestParam("indexfilepath") String path)
	{
		
		System.out.println(path);
		f=new File(path);
		
		int code=service.saveIndexFilePath(path);
		return new JSONNotificationResponse(code);
		
	}
	
	@JsonView(Views.Public.class)
	@ResponseBody
	@RequestMapping("/filesList")
	public JSONFileModelMapResponse getMetaForPaths(@RequestBody JSONSelectedPathsRequest paths)
	{
		List<Path> selPath=paths.getSelPaths();
		List<String> selPaths=new ArrayList<>();
		System.out.println(selPaths.size()+" "+selPath.size()+" "+paths.toString()+" "+selPath.get(0).getPath());
		selPaths=Path.toStringList(selPath,selPaths);
		System.out.println(selPaths.size()+" "+selPath.size()+" "+paths.toString()+" "+selPaths.get(0));
		return service.getMetaForPaths(selPaths);
		//System.out.println("here");
		//return null;
	}
	
	@ResponseBody
	@RequestMapping("/subFilesList")
	public JSONFileModelMapResponse getMetaForSubPaths(@RequestParam("path") String path)
	{
		return service.getMetaForSubPaths(path);
	}
	
	@ResponseBody
	@RequestMapping("/stopword")
	public JSONNotificationResponse addStopWord(@RequestParam("word") String word)
	{
		return service.addStopWord(word);
	}
		
	
	@ResponseBody
	@RequestMapping("/searchquery")
	public List<JSONSearchResultResponse> searchToken(@RequestParam("word") String word)
	{
		return service.searchToken(word);
	}
	
	@RequestMapping("/download")
    public void download( HttpServletRequest request,
                                     HttpServletResponse response,
                                     @RequestParam("path") String path)
    {
        service.download(request,response,path);
    }
	
	
}