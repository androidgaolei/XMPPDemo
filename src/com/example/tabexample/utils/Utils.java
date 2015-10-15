package com.example.tabexample.utils;


public class Utils {


	public static String getJidToUsername(String jid){
		
		return jid.split("@")[0];
	}
	
	public static String getUserNameToJid(String username){
		return username + "@pc201509182058";
	}
}
