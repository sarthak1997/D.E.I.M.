package com.cleartrail.dim.websocket;

import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.cleartrail.dim.model.JSONProgressResponse;
import com.cleartrail.dim.model.JSONWatcherEventResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class SocketHandler extends TextWebSocketHandler {
	
	private static Set<WebSocketSession> sessions=new HashSet<>();
	
	 
	public SocketHandler() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void handleTextMessage(WebSocketSession session,TextMessage msg) throws Exception {
		  
		sessions.add(session);
		System.out.println("hiii");
		
		
	  }
	@Override
	public void afterConnectionClosed(WebSocketSession session,CloseStatus status) {
		
		System.out.println("diconnecting "+session);
		sessions.remove(session);
		
	}
	public void setAction(JSONWatcherEventResponse action) {
		ObjectMapper mapper=new ObjectMapper();
		String message;
		try {
			message = mapper.writeValueAsString(action);
			for(WebSocketSession session:sessions)
				try {
					session.sendMessage(new TextMessage(message));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void setAction(JSONProgressResponse action)
	{
		ObjectMapper mapper=new ObjectMapper();
		String message;
		try {
			message = mapper.writeValueAsString(action);
			for(WebSocketSession session:sessions)
				try {
					session.sendMessage(new TextMessage(message));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	 
	 
}
