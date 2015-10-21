package com.example.tabexample.entity;

import java.io.Serializable;

public class ChatHis implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username ;
	private String content ;
	private String date ;
	public ChatHis() {
		// TODO Auto-generated constructor stub
	}
	public ChatHis(String username,String content,String date) {
		// TODO Auto-generated constructor stub
		this.username=username;
		this.content=content;
		this.date=date;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}
