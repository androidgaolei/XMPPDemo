package com.example.tabexample.activity;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import com.example.tabexample.R;
import com.example.tabexample.utils.XMPPTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity implements OnClickListener{
	
	private EditText username;
	private EditText password;
	private Button login;
	private Button regedit;
	XMPPConnection connection = XMPPTool.getConnection();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		initView();
	}
	private void initView() {
		username = (EditText)findViewById(R.id.login_user_edit);
		password = (EditText)findViewById(R.id.login_passwd_edit);
		login = (Button)findViewById(R.id.login_login_btn);
		login.setOnClickListener(this);
		
		regedit = (Button)findViewById(R.id.login_regedit_btn);
		regedit.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.login_login_btn:
			final String userId = username.getText().toString();
			final String pwd = password.getText().toString();
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					try {
						//连接openfire 服务器
						XMPPTool.getConnection().login(userId, pwd);
						Presence presence = new Presence(Presence.Type.available);
						XMPPTool.getConnection().sendPacket(presence);
						
						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						
						intent.putExtra("userId", userId);
						LoginActivity.this.startActivity(intent);
						LoginActivity.this.finish();
					} catch (Exception e) {
						// TODO: handle exception
						XMPPTool.closeConnection();
						
					}
				}
			}).start();
			break;
		case R.id.login_regedit_btn:
			Intent intent = new Intent(LoginActivity.this,RegeditActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}
	
	
}
