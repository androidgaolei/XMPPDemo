package com.example.tabexample.entity;

import java.io.Serializable;

import android.text.TextUtils;



public class FriendInfo implements Serializable{
	private String username;
	private String nickname;
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getNickname() {
		if(TextUtils.isEmpty(nickname))
			return username;
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public String getJid(){
		if (username == null) return null;
		return username + "@pc201509182058";
	}
}
