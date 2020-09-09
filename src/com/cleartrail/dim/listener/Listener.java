package com.cleartrail.dim.listener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

import com.cleartrail.dim.dao.DIMDao;



/**
 * Application Lifecycle Listener implementation class Listener
 *
 */
@WebListener
@ComponentScan
public class Listener implements ServletContextListener, HttpSessionListener, ServletRequestListener {

	Connection con;
	
	@Autowired
	DIMDao dao;
	
    /**
     * Default constructor. 
     */
    public Listener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent se)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see ServletRequestListener#requestDestroyed(ServletRequestEvent)
     */
    public void requestDestroyed(ServletRequestEvent sre)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see ServletRequestListener#requestInitialized(ServletRequestEvent)
     */
    public void requestInitialized(ServletRequestEvent sre)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent se)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
         // TODO Auto-generated method stub
    	
    	try {
			con.close();
			
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
         // TODO Auto-generated method stub
    	
    	
    	connection(sce);
    	loadStopWords(sce);
    	
    }

	private void loadStopWords(ServletContextEvent sce) {
		
		 try {
	            Set<String> stopWords=new HashSet<>();
	            PreparedStatement ps;
	            String qr="select words from stopwords";
	            ps=con.prepareStatement(qr);
	            ResultSet rs;
	            rs=ps.executeQuery();
	            while(rs.next())
	            {
	            	stopWords.add(rs.getString(1));
	            }
	            sce.getServletContext().setAttribute("stoplist", stopWords);
	        } catch (SQLException ex) {
	            Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
	        }
		
	}

	private void connection(ServletContextEvent sce) {
		
		 try {
	            Class.forName("com.mysql.jdbc.Driver");
	            System.out.println("loaded");
	        } catch (ClassNotFoundException ex) {
	            Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
	        }
		
	        try {
	        	//con = DriverManager.getConnection("jdbc:mysql://korerodb.mysql.database.azure.com:3306/majorproject?user=koreroadmin@korerodb&useSSL=false&requireSSL=false&autoReconnect=true","koreroadmin@korerodb","korero1234@");
	        	 con=DriverManager.getConnection("jdbc:mysql://localhost:3306/folderindexingdb","root","Sarthak*1");
	        //  con=DriverManager.getConnection("jdbc:mysql://localhost:54760/localdb","root","password");
	          // con=DriverManager.getConnection("jdbc:mysql://64.62.211.131:3306/korero_admindb","korero_admin","123456789");
	        //rohanazure// con=DriverManager.getConnection("jdbc:mysql://localhost:50493/localdb","root","password");
	        	//con=DriverManager.getConnection("jdbc:mysql://localhost:56245/localdb","root","password");
	            System.out.println("connected...minorproject");
	            
	        } catch (SQLException ex) {
	            Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
	        }
		
	        sce.getServletContext().setAttribute("datacon",con);
		
	}
	
}
