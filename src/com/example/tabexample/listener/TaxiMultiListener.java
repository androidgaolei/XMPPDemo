package com.example.tabexample.listener;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.util.Log;

/** 
 * ��������Ϣ������ 
 *  
 * @author Administrator 
 *  
 */  
public class TaxiMultiListener implements PacketListener {  
  
    @Override  
    public void processPacket(Packet packet) {  
        Message message = (Message) packet;  
        String body = message.getBody();  
        String people = message.getFrom();
        Log.d("message",people+"˵��"+body);
          
    }  
} 

