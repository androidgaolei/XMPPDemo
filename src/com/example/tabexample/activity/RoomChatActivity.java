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
		title.setText("Ⱥ��");
		
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
			        	// ��ȡ�û�����Ϣ
						String[] args = new String[] { people,
								body };
						// ��handler��ȡ������ʾ��Ϣ
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
		// ������Ϣ
		et_sendmessage = (EditText)findViewById(R.id.et_sendmessage);
		Intent intent = getIntent();
		final String user = intent.getStringExtra("name");
		final String roomsName = intent.getStringExtra("roomname");
		final String password = intent.getStringExtra("pass");
		//��ȡ����
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
	 * ��������� 
	 *  
	 * @param user 
	 *            �ǳ� 
	 * @param password 
	 *            ���������� 
	 * @param roomsName 
	 *            �������� 
	 */  
	public MultiUserChat joinMultiUserChat(String user, String roomsName,  
	        String password) {  
		 XMPPConnection conn = XMPPTool.getConnection();
		
	    if (conn == null)  
	        return null;  
	    try {  
	        // ʹ��XMPPConnection����һ��MultiUserChat����  
	        MultiUserChat muc = new MultiUserChat(conn, roomsName  
	                + "@conference." + conn.getServiceName());  
	        // �����ҷ��񽫻����Ҫ���ܵ���ʷ��¼����  
	        DiscussionHistory history = new DiscussionHistory();  
	        history.setMaxChars(0);  
	        // history.setSince(new Date());  
	        // �û�����������  
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
				// ��ȡ��Ϣ����ʾ
				String[] args = (String[]) msg.obj;
				msgList.add(new Msg(args[1], 0));
				// ˢ��������
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};
	
	/** 
     * ��ȡ�����ҵ����г�Ա 
     */  
    private void getAllMember() {  
        //Log.i(TAG, "��ȡ�����ҵ����г�Ա");  
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
                        Log.i("mem", "��Ա����;" + name);  
                    }  
  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
  
        
            }  
        }).start();  
    } 
}
