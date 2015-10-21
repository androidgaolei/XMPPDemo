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
 * 单人聊天信息监听类
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
				// 登录用户
				StringUtils.parseName(XMPPTool.getConnection().getUser());
				// 发送消息用户
				msg.getFrom();
				// 消息内容
				String body = msg.getBody();
				if (body != null) {
					Log.d("messageTest", msg.getFrom() + "说了" + body);
					EventBus.getDefault().post(
							new ChatHis(msg.getFrom(), body, getDate()));

				}

			}
		});
	}

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
