package com.example.tabexample.listener;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.tabexample.activity.MainActivity;
import com.example.tabexample.activity.NewChatActivity;
import com.example.tabexample.entity.ChatHis;
import com.example.tabexample.utils.XMPPTool;

import de.greenrobot.event.EventBus;

/**
 * ����������Ϣ������
 * 
 * @author Administrator
 * 
 */
public class TaxiChatManagerListener implements ChatManagerListener {
	private Context mContext;

	public TaxiChatManagerListener(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	public void chatCreated(Chat chat, boolean arg1) {
		chat.addMessageListener(new MessageListener() {
			public void processMessage(Chat arg0, Message msg) {
				// ��¼�û�
				StringUtils.parseName(XMPPTool.getConnection().getUser());
				// ������Ϣ�û�
				msg.getFrom();
				// ��Ϣ����
				String body = msg.getBody();
				if (body != null) {
					Log.d("messageTest", msg.getFrom() + "˵��" + body);
					EventBus.getDefault().post(
							new ChatHis(msg.getFrom(), body, getDate()));

				}

			}
		});
	}

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
