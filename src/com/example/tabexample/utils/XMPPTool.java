package com.example.tabexample.utils;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;

public class XMPPTool {
	private static XMPPConnection con = null;
	
	private static void openConnection(){
		try {
			ConnectionConfiguration config = new ConnectionConfiguration("192.168.102.123", 5222);
			con = new XMPPConnection(config);
			con.connect();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static XMPPConnection getConnection(){
		if(con==null){
			openConnection();
		}
		return con;
	}
	
	public static void closeConnection(){
		con.disconnect();
		con = null;
	}
}
