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
import com.example.tabexample.db.MyDatabaseHelper;
import com.example.tabexample.entity.ChatHis;
import com.example.tabexample.entity.FriendInfo;
import com.example.tabexample.entity.Msg;
import com.example.tabexample.utils.XMPPTool;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NewChatActivity extends Activity implements OnClickListener{
	private TextView title;
	private FriendInfo info;
	private ListView chat_list;
	private MsgAdapter adapter;
	private List<Msg> mDataArrays = new ArrayList<Msg>();
	
	public static String NAME;
	public static String CONTENT;
	public static String DATE;
	public static boolean FLAG = true;
	
	private EditText et_sendmessage;
	private ImageButton chatting_keyboard_btn;
	private ImageButton pic1;
	private ImageButton pic2;
	private ImageButton pic3;
	private ImageButton pic4;
	private XMPPConnection connection = null;
	private Map<String, Chat> chatManage = new HashMap<String, Chat>();// ���촰�ڹ���map����
	ChatManager cm = XMPPTool.getConnection().getChatManager();
	private MyDatabaseHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chat_xiaohei);
	//	chat_list.setSelection(adapter.getCount() - 1);x`
		if (getIntent().getExtras() != null
				&& getIntent().getExtras().getSerializable("info") != null) {
			info = (FriendInfo) getIntent().getExtras().getSerializable("info");
		} else {
			Toast.makeText(this, "��ȡ������Ϣʧ��", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		dbHelper = new MyDatabaseHelper(this, "chatHis.db", null, 1);
		dbHelper.getWritableDatabase();
		
		
		title = (TextView) findViewById(R.id.title);
		title.setText(info.getUsername());
		
		
		
		
		chat_list = (ListView) findViewById(R.id.listview);
		adapter = new MsgAdapter(NewChatActivity.this,mDataArrays);
		chat_list.setAdapter(adapter);
		showHistory(XMPPTool.getConnection().getUser().substring(0,XMPPTool.getConnection().getUser().indexOf("@")));
		chatting_keyboard_btn = (ImageButton)findViewById(R.id.chatting_keyboard_btn);
		chatting_keyboard_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				LinearLayout layout = (LinearLayout)findViewById(R.id.page_select);
				if(layout.getVisibility()==View.GONE){
					layout.setVisibility(View.VISIBLE);
				}else{
					layout.setVisibility(View.GONE);
				}
				
				
			}
		});
		
		
		pic1 = (ImageButton)findViewById(R.id.pic1);
		pic1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View menu) {
				//Toast.makeText(NewChatActivity.this, "pic1", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(NewChatActivity.this,SelectPicPopupWindow.class)); 
			}
		});
		Button btn_send = (Button)findViewById(R.id.btn_send);
		btn_send.setOnClickListener(this);
		
		
		
		// ������Ϣ

				cm.addChatListener(new ChatManagerListener() {
					@Override
					public void chatCreated(Chat chat, boolean able) {
						chat.addMessageListener(new MessageListener() {
							@Override
							public void processMessage(Chat chat2, Message message) {
								if (message.getFrom().contains(
										info.getUsername() + "@pc201509182058")) {
									// ��ȡ�û�����Ϣ
									String[] args = new String[] { info.getUsername(),
											message.getBody() };

									// ��handler��ȡ������ʾ��Ϣ
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
		// ������Ϣ ��ȡ�������Ϣ
		String name = XMPPTool.getConnection().getUser().substring(0,XMPPTool.getConnection().getUser().indexOf("@"));
		et_sendmessage = (EditText) findViewById(R.id.et_sendmessage);
		String message = et_sendmessage.getText().toString();

		Chat chat = getFriendChat(info.getUsername(), null);
		try {
			if (!"".equals(message)) {
				Msg msg = new Msg();
				msg.setName(name);
				msg.setMsgType(false);// �Լ����͵���Ϣ
				msg.setDate(getDate());
				msg.setMessage(message);
				mDataArrays.add(msg);
				adapter.notifyDataSetChanged();
				chat_list.setSelection(mDataArrays.size());
				et_sendmessage.setText("");
				chat.sendMessage(message);
				
				NAME = info.getUsername();
				CONTENT = message;
				DATE = getDate();
				
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				ContentValues values = new ContentValues();
				// ��ʼ��װ��һ������
				values.put("fromuser", name);
				values.put("touser", info.getUsername());
				values.put("content", message);
				values.put("type","1");
				values.put("date",getDate());
				values.put("man", name);
				values.put("thischat", info.getUsername());
				db.insert("chatHis", null, values); // �����һ������
				values.clear();
				
				
				
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
		/** �ж��Ƿ񴴽����촰�� */
		for (String fristr : chatManage.keySet()) {
			if (fristr.equals(friend)) {
				// �������촰�ڣ��򷵻ض�Ӧ���촰��
				return chatManage.get(fristr);
			}
		}
		/** �������촰�� */
		Chat chat = connection.getChatManager().createChat(
				friend + "@" + connection.getServiceName(), listenter);
		/** ������촰�ڵ�chatManage */
		chatManage.put(friend, chat);
		return chat;
	}
	public void showHistory(String from){
		 SQLiteDatabase db=dbHelper.getWritableDatabase();
		 Cursor cursor = db.query("chatHis", new String[] {"fromuser","touser","content","date","type"}, "man=? and thischat=?",new String[]{XMPPTool.getConnection().getUser().substring(0,XMPPTool.getConnection().getUser().indexOf("@")),info.getUsername()}, null, null, null);
		 if(cursor.moveToFirst()){
			 do{
				 String name = cursor.getString(cursor.getColumnIndex("fromuser"));
				 String toname = cursor.getString(cursor.getColumnIndex("touser"));
				 String content = cursor.getString(cursor.getColumnIndex("content"));
				 String date = cursor.getString(cursor.getColumnIndex("date"));
				 String type = cursor.getString(cursor.getColumnIndex("type"));
				 
				 Log.d("db", name);
				 Log.d("db", toname);
				 Log.d("db", content);
				 Log.d("db", date);
				 Log.d("db", type);
				 Msg msg = new Msg();
				 if(type.equals("1")){
					 msg.setMessage(content);
					 msg.setName(name);
					 msg.setDate(date);
					 msg.setMsgType(false);
				 }else{
					 msg.setMessage(content);
					 msg.setName(name);
					 msg.setDate(date);
					 msg.setMsgType(true);
				 }
				
				 mDataArrays.add(msg);
					// ˢ��������
				adapter.notifyDataSetChanged();
				 
			 }while(cursor.moveToNext());
		 }
		 cursor.close();
	}
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				// ��ȡ��Ϣ����ʾ
				String[] args = (String[]) msg.obj;
				String m = args[1];
				Msg msg1 = new Msg();
				msg1.setName(args[0]);
				msg1.setMessage(m);
				msg1.setDate(getDate());
				msg1.setMsgType(true);
			
				mDataArrays.add(msg1);
				// ˢ��������
				adapter.notifyDataSetChanged();
				
				NAME = info.getUsername();
				CONTENT = m;
				DATE = getDate();
		         
				//
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				ContentValues values = new ContentValues();
				// ��ʼ��װ��һ������
				values.put("fromuser", args[0]);
				values.put("touser",XMPPTool.getConnection().getUser().substring(0,XMPPTool.getConnection().getUser().indexOf("@")));
				values.put("content", m);
				values.put("type","0");
				values.put("date",getDate());
				values.put("man",XMPPTool.getConnection().getUser().substring(0,XMPPTool.getConnection().getUser().indexOf("@")));
				values.put("thischat", args[0]);
				db.insert("chatHis", null, values); // ��������
				values.clear();
				break;
			default:
				break;
			}
		};
	};
	/**
	 * ������Ϣʱ����ȡ��ǰ�¼�
	 * 
	 * @return ��ǰʱ��
	 */
	private String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return format.format(new Date());
	}

}
