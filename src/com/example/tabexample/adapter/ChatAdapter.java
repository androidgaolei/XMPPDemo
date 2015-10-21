package com.example.tabexample.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tabexample.R;
import com.example.tabexample.entity.ChatHis;

public class ChatAdapter extends BaseAdapter {
	private List<ChatHis> chatHis;
	private LayoutInflater mInflater;

	public ChatAdapter(Context context, List<ChatHis> chatHis) {
		this.chatHis = chatHis;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatHis chat = chatHis.get(position);
		ViewHolder viewHolder;

		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.chat_item_history, null);
			viewHolder = new ViewHolder();
			viewHolder.friendName = (TextView) convertView
					.findViewById(R.id.name);
			viewHolder.content = (TextView) convertView
					.findViewById(R.id.content);
			viewHolder.date = (TextView) convertView.findViewById(R.id.date);
			convertView.setTag(viewHolder); // 将ViewHolder存储在View中
		} else {
			viewHolder = (ViewHolder) convertView.getTag(); // 重新获取ViewHolder
		}
		viewHolder.friendName.setText(chat.getUsername());
		viewHolder.content.setText(chat.getContent());
		viewHolder.date.setText(chat.getDate());
		return convertView;
	}

	static class ViewHolder {

		TextView friendName;
		TextView content;
		TextView date;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return chatHis.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return chatHis.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
}
