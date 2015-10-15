package com.example.tabexample.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

import com.example.tabexample.R;
import com.example.tabexample.adapter.MsgAdapter;
import com.example.tabexample.entity.Msg;
import com.example.tabexample.listener.TaxiMultiListener;
import com.example.tabexample.utils.XMPPTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class RoomChatActivity extends Activity implements OnClickListener{
	private TextView title;
	
	private EditText et_sendmessage;
	private Button btn_send;
	private ListView chat_list;
	private MsgAdapter adapter;
	private List<Msg> msgList = new ArrayList<Msg>();
	MultiUserChat muc = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chat_xiaohei);

		title = (TextView) findViewById(R.id.title);
		title.setText("群聊");
		
		chat_list = (ListView) findViewById(R.id.listview);
		adapter = new MsgAdapter(RoomChatActivity.this,
				R.layout.chat_item, msgList);
		chat_list.setAdapter(adapter);
		
		btn_send = (Button)findViewById(R.id.btn_send);
		btn_send.setOnClickListener(this);
		
		Intent intent = getIntent();
		 final String user = intent.getStringExtra("name");
		 final String roomsName = intent.getStringExtra("roomname");
		 final String password = intent.getStringExtra("pass");
		 muc = joinMultiUserChat(user,roomsName,password);
		 //muc.addMessageListener(new TaxiMultiListener());
		 muc.addMessageListener(new PacketListener(){
			   @Override  
			    public void processPacket(Packet packet) {  
			        Message message = (Message) packet;  
			        String body = message.getBody();  
			        String people = message.getFrom();
			        if (!message.getFrom().contains(
			        		user)){
			        	// 获取用户、消息
						String[] args = new String[] { people,
								body };
						// 在handler里取出来显示消息
						android.os.Message msg = handler.obtainMessage();
						msg.what = 1;
						if(args[1]!=null&&args[1]!=""){
							msg.obj = args;
							msg.sendToTarget();
						}
			        }
			    	
			    }  
		 });
		 getAllMember();
		
	}
	
	@Override
	public void onClick(View v) {
		// 发送消息
		et_sendmessage = (EditText)findViewById(R.id.et_sendmessage);
		Intent intent = getIntent();
		final String user = intent.getStringExtra("name");
		final String roomsName = intent.getStringExtra("roomname");
		final String password = intent.getStringExtra("pass");
		//获取对象
		muc = joinMultiUserChat(user,roomsName,password);
		try {
			String message = et_sendmessage.getText().toString();
 			Msg msg = new Msg(message, 1);
			msgList.add(msg);
			adapter.notifyDataSetChanged();
			chat_list.setSelection(msgList.size());
			muc.sendMessage(et_sendmessage.getText().toString());
			et_sendmessage.setText("");
		} catch (XMPPException e) { 	
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** 
	 * 加入会议室 
	 *  
	 * @param user 
	 *            昵称 
	 * @param password 
	 *            会议室密码 
	 * @param roomsName 
	 *            会议室名 
	 */  
	public MultiUserChat joinMultiUserChat(String user, String roomsName,  
	        String password) {  
		 XMPPConnection conn = XMPPTool.getConnection();
		
	    if (conn == null)  
	        return null;  
	    try {  
	        // 使用XMPPConnection创建一个MultiUserChat窗口  
	        MultiUserChat muc = new MultiUserChat(conn, roomsName  
	                + "@conference." + conn.getServiceName());  
	        // 聊天室服务将会决定要接受的历史记录数量  
	        DiscussionHistory history = new DiscussionHistory();  
	        history.setMaxChars(0);  
	        // history.setSince(new Date());  
	        // 用户加入聊天室  
	        muc.join(user, password, history,  
	                SmackConfiguration.getPacketReplyTimeout());  
	        return muc;  
	    } catch (XMPPException e) {  
	        e.printStackTrace();  
	        return null;  
	    }  
	} 

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				// 获取消息并显示
				String[] args = (String[]) msg.obj;
				msgList.add(new Msg(args[1], 0));
				// 刷新适配器
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};
	
	/** 
     * 获取聊天室的所有成员 
     */  
    private void getAllMember() {  
        //Log.i(TAG, "获取聊天室的所有成员");  
       // affiliates.clear();  
          
        new Thread(new Runnable() {  
            @Override  
            public void run() {  
                try {  
                    Iterator<String> it = muc.getOccupants();  
                    while (it.hasNext()) {  
                        String name = it.next();  
                        name = name.substring(name.indexOf("/") + 1);  
                      //  affiliates.add(name);  
                        Log.i("mem", "成员名字;" + name);  
                    }  
  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
  
        
            }  
        }).start();  
    } 
}
