package com.cleartrail.dim.websocket;

import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/ws")
public class WebSocketServer {

	private static Set<Session> sessions=new HashSet<>();
	
	public WebSocketServer() {
		// TODO Auto-generated constructor stub
		System.out.println("ws cons----");
	}
	
	@OnOpen
	public void open(Session session)
	{
		sessions.add(session);
	}
	
	@OnClose
	public void close(Session session)
	{
		sessions.remove(session);
	}
	
	
}
