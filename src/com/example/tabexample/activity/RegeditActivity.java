package com.example.tabexample.activity;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;

import com.example.tabexample.R;
import com.example.tabexample.utils.XMPPTool;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegeditActivity extends Activity implements OnClickListener {
	private XMPPConnection connection = XMPPTool.getConnection();
	private EditText username;
	private EditText password;
	private EditText repassword;
	private Button ok;
	private Button cancle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.regedit);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		repassword = (EditText) findViewById(R.id.repassword);
		ok = (Button) findViewById(R.id.regedit_ok);
		cancle = (Button) findViewById(R.id.regedit_cancle);
		ok.setOnClickListener(this);
		cancle.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.regedit_ok:
			String user = username.getText().toString();
			String pwd = password.getText().toString();
			String repwd = repassword.getText().toString();
			if (!pwd.equals(repwd)) {
				Toast.makeText(RegeditActivity.this, "两次密码输入不同",
						Toast.LENGTH_SHORT).show();
				return;
			}
			String result = regist(user, pwd);
			if (result == "1") {
				Toast.makeText(RegeditActivity.this, "注册成功", Toast.LENGTH_SHORT)
						.show();
				finish();
			} else if (result == "2") {
				Toast.makeText(RegeditActivity.this, "帐号已经存在",
						Toast.LENGTH_SHORT).show();
			} else if (result == "0") {
				Toast.makeText(RegeditActivity.this, "服务器没有相应",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(RegeditActivity.this, "注册失败", Toast.LENGTH_SHORT)
						.show();
				finish();
			}
 
			break;
		case R.id.regedit_cancle:
			finish();
			break;
		default:
			break;
		}
	}

	/** 
     * 注册 
     *  
     * @param account 
     *            注册帐号 
     * @param password 
     *            注册密码 
     * @return 1、注册成功 0、服务器没有返回结果2、这个账号已经存在3、注册失败 
     */  
    public String regist(String account, String password) {  
        if (XMPPTool.getConnection() == null)  
            return "0";  
        Registration reg = new Registration();  
        reg.setType(IQ.Type.SET);  
        reg.setTo(XMPPTool.getConnection().getServiceName());  
        // 注意这里createAccount注册时，参数是UserName，不是jid，是"@"前面的部分。  
        reg.setUsername(account);  
        reg.setPassword(password);  
        // 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！  
        reg.addAttribute("android", "geolo_createUser_android");  
        PacketFilter filter = new AndFilter(new PacketIDFilter(  
                reg.getPacketID()), new PacketTypeFilter(IQ.class));  
        PacketCollector collector = XMPPTool.getConnection().createPacketCollector(  
                filter);  
        XMPPTool.getConnection().sendPacket(reg);  
        IQ result = (IQ) collector.nextResult(SmackConfiguration  
                .getPacketReplyTimeout());  
        // Stop queuing results停止请求results（是否成功的结果）  
        collector.cancel();  
        if (result == null) {  
            Log.e("regist", "No response from server.");  
            return "0";  
        } else if (result.getType() == IQ.Type.RESULT) {  
            Log.v("regist", "regist success.");  
            return "1";  
        } else { // if (result.getType() == IQ.Type.ERROR)  
            if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {  
                Log.e("regist", "IQ.Type.ERROR: "  
                        + result.getError().toString());  
                return "2";  
            } else {  
                Log.e("regist", "IQ.Type.ERROR: "  
                        + result.getError().toString());  
                return "3";  
            }  
        }  
    } 
}
