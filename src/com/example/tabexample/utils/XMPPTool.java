package com.example.tabexample.utils;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

public class XMPPTool {
	private static XMPPConnection con = null;
	
	private static void openConnection(){
		try {
			//ConnectionConfiguration config = new ConnectionConfiguration("192.168.102.123", 5222);
			ConnectionConfiguration config = new ConnectionConfiguration("192.168.191.1", 5222);
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
    /** 
     * µÇÂ¼ 
     *  
     * @param account 
     *            µÇÂ¼ÕÊºÅ 
     * @param password 
     *            µÇÂ¼ÃÜÂë 
     * @return 
     */  
    public static boolean login(String account, String password) {  
        try {  
            if (XMPPTool.getConnection() == null)  
                return false;  
            XMPPTool.getConnection().login(account, password);  
            
            Presence presence = new Presence(Presence.Type.available);
			XMPPTool.getConnection().sendPacket(presence);
         
           
            return true;  
        } catch (XMPPException xe) {  
            xe.printStackTrace();  
        }  
        return false;  
    }
	
}
