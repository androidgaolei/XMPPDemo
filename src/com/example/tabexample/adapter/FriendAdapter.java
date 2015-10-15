package com.example.tabexample.adapter;

import java.util.List;

import com.example.tabexample.R;
import com.example.tabexample.entity.FriendInfo;




import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;



public class FriendAdapter extends ArrayAdapter<FriendInfo>{
	private int resourceId;
	public FriendAdapter(Context context, int textViewResourceId,
			List<FriendInfo> objects) {
		// TODO Auto-generated constructor stub
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
	}
		public View getView(int position, View convertView, ViewGroup parent){
			FriendInfo friend = getItem(position);
			View view;
			ViewHolder viewHolder;
			if (convertView == null) {
				view = LayoutInflater.from(getContext()).inflate(resourceId, null);
				viewHolder = new ViewHolder();
				viewHolder.friendName = (TextView) view.findViewById
				(R.id.friend_name);
				view.setTag(viewHolder); // 将ViewHolder存储在View中
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
			}
			viewHolder.friendName.setText(friend.getUsername());
			return view;
	}
		class ViewHolder {
			
			TextView friendName;
		}
}
