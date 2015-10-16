package com.example.tabexample.activity;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/** 
     * 会议室状态监听事件 
     *  
     * @author Administrator 
     *  
     */  
    class ParticipantStatus implements ParticipantStatusListener {  
    	
    	private Context mContext;
    	public ParticipantStatus(Context context) {
			// TODO Auto-generated constructor stub
    		mContext = context;
    		
		}
        @Override  
        public void adminGranted(String arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void adminRevoked(String arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void banned(String arg0, String arg1, String arg2) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void joined(String participant) {  
            System.out.println(StringUtils.parseResource(participant)+ " has joined the room.");  
            Log.d("static", StringUtils.parseResource(participant)+ " has joined the room.");
            Toast.makeText(mContext,StringUtils.parseResource(participant)+ " has joined the room.", Toast.LENGTH_SHORT).show();
        }  
  
        @Override  
        public void kicked(String arg0, String arg1, String arg2) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void left(String participant) {  
            // TODO Auto-generated method stub  
            System.out.println(StringUtils.parseResource(participant)+ " has left the room.");  
            Log.d("static", StringUtils.parseResource(participant)+ " has left the room.");
            Toast.makeText(mContext,StringUtils.parseResource(participant)+ " has left the room.", Toast.LENGTH_SHORT).show();
        }  
  
        @Override  
        public void membershipGranted(String arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void membershipRevoked(String arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void moderatorGranted(String arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void moderatorRevoked(String arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void nicknameChanged(String participant, String newNickname) {  
            System.out.println(StringUtils.parseResource(participant)+ " is now known as " + newNickname + ".");  
        }  
  
        @Override  
        public void ownershipGranted(String arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void ownershipRevoked(String arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void voiceGranted(String arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void voiceRevoked(String arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
    }  