package com.example.tabexample.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import com.example.tabexample.R;
import com.example.tabexample.adapter.MsgAdapter;
import com.example.tabexample.entity.FriendInfo;
import com.example.tabexample.entity.Msg;
import com.example.tabexample.utils.XMPPTool;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NewChatActivity extends Activity implements OnClickListener{
	private TextView title;
	private FriendInfo info;
	private ListView chat_list;
	private MsgAdapter adapter;
	private List<Msg> mDataArrays = new ArrayList<Msg>();
	private EditText et_sendmessage;
	private XMPPConnection connection = null;
	private Map<String, Chat> chatManage = new HashMap<String, Chat>();// 聊天窗口管理map集合
	ChatManager cm = XMPPTool.getConnection().getChatManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chat_xiaohei);
	//	chat_list.setSelection(adapter.getCount() - 1);
		if (getIntent().getExtras() != null
				&& getIntent().getExtras().getSerializable("info") != null) {
			info = (FriendInfo) getIntent().getExtras().getSerializable("info");
		} else {
			Toast.makeText(this, "获取好友信息失败", Toast.LENGTH_SHORT).show();
			finish();
		}
		title = (TextView) findViewById(R.id.title);
		title.setText(info.getUsername());
		
		chat_list = (ListView) findViewById(R.id.listview);
		adapter = new MsgAdapter(NewChatActivity.this,mDataArrays);
		chat_list.setAdapter(adapter);
		
		
		
		Button btn_send = (Button)findViewById(R.id.btn_send);
		btn_send.setOnClickListener(this);
		
		
		
		// 接受消息

				cm.addChatListener(new ChatManagerListener() {
					@Override
					public void chatCreated(Chat chat, boolean able) {
						chat.addMessageListener(new MessageListener() {
							@Override
							public void processMessage(Chat chat2, Message message) {
								if (message.getFrom().contains(
										info.getUsername() + "@pc201509182058")) {
									// 获取用户、消息
									String[] args = new String[] { info.getUsername(),
											message.getBody() };

									// 在handler里取出来显示消息
									android.os.Message msg = handler.obtainMessage();
									msg.what = 1;
									if(args[1]!=null&&args[1]!=""){
										msg.obj = args;
										System.out.println(msg);
										msg.sendToTarget();
									}
									
								}
							}
						});
					}
				});
				
				chat_list.setSelection(adapter.getCount() - 1);
	}
	@Override
	public void onClick(View v) {
		// 发送信息 获取输入的信息
		String name = XMPPTool.getConnection().getUser().substring(0,XMPPTool.getConnection().getUser().indexOf("@"));
		et_sendmessage = (EditText) findViewById(R.id.et_sendmessage);
		String message = et_sendmessage.getText().toString();

		Chat chat = getFriendChat(info.getUsername(), null);
		try {
			if (!"".equals(message)) {
				Msg msg = new Msg();
				msg.setName(name);
				msg.setMsgType(false);// 自己发送的消息
				msg.setDate(getDate());
				msg.setMessage(message);
				mDataArrays.add(msg);
				adapter.notifyDataSetChanged();
				chat_list.setSelection(mDataArrays.size());
				et_sendmessage.setText("");
				chat.sendMessage(message);
			}

		} catch (XMPPException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public Chat getFriendChat(String friend, MessageListener listenter) {
		// Toast.makeText(this, friend, Toast.LENGTH_SHORT).show();
		connection = XMPPTool.getConnection();
		if (connection == null)
			return null;
		/** 判断是否创建聊天窗口 */
		for (String fristr : chatManage.keySet()) {
			if (fristr.equals(friend)) {
				// 存在聊天窗口，则返回对应聊天窗口
				return chatManage.get(fristr);
			}
		}
		/** 创建聊天窗口 */
		Chat chat = connection.getChatManager().createChat(
				friend + "@" + connection.getServiceName(), listenter);
		/** 添加聊天窗口到chatManage */
		chatManage.put(friend, chat);
		return chat;
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				// 获取消息并显示
				String[] args = (String[]) msg.obj;
				String m = args[1];
				Msg msg1 = new Msg();
				msg1.setName(args[0]);
				msg1.setMessage(m);
				msg1.setDate(getDate());
				msg1.setMsgType(true);
				mDataArrays.add(msg1);
				// 刷新适配器
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};
	/**
	 * 发送消息时，获取当前事件
	 * 
	 * @return 当前时间
	 */
	private String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return format.format(new Date());
	}
}
