package com.cleartrail.dim.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cleartrail.dim.dao.DIMDao;

@Service
public class WatcherServiceImpl implements WatcherService {

	@Autowired
	private DIMDao dao;
	
	@PostConstruct
	public void startMonitor()
	{
		Thread t=new Thread(dao);
		t.start();
	}
	
	
}
