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
				Toast.makeText(RegeditActivity.this, "�����������벻ͬ",
						Toast.LENGTH_SHORT).show();
				return;
			}
			String result = regist(user, pwd);
			if (result == "1") {
				Toast.makeText(RegeditActivity.this, "ע��ɹ�", Toast.LENGTH_SHORT)
						.show();
				finish();
			} else if (result == "2") {
				Toast.makeText(RegeditActivity.this, "�ʺ��Ѿ�����",
						Toast.LENGTH_SHORT).show();
			} else if (result == "0") {
				Toast.makeText(RegeditActivity.this, "������û����Ӧ",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(RegeditActivity.this, "ע��ʧ��", Toast.LENGTH_SHORT)
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
     * ע�� 
     *  
     * @param account 
     *            ע���ʺ� 
     * @param password 
     *            ע������ 
     * @return 1��ע��ɹ� 0��������û�з��ؽ��2������˺��Ѿ�����3��ע��ʧ�� 
     */  
    public String regist(String account, String password) {  
        if (XMPPTool.getConnection() == null)  
            return "0";  
        Registration reg = new Registration();  
        reg.setType(IQ.Type.SET);  
        reg.setTo(XMPPTool.getConnection().getServiceName());  
        // ע������createAccountע��ʱ��������UserName������jid����"@"ǰ��Ĳ��֡�  
        reg.setUsername(account);  
        reg.setPassword(password);  
        // ���addAttribute����Ϊ�գ������������������־��android�ֻ������İɣ���������  
        reg.addAttribute("android", "geolo_createUser_android");  
        PacketFilter filter = new AndFilter(new PacketIDFilter(  
                reg.getPacketID()), new PacketTypeFilter(IQ.class));  
        PacketCollector collector = XMPPTool.getConnection().createPacketCollector(  
                filter);  
        XMPPTool.getConnection().sendPacket(reg);  
        IQ result = (IQ) collector.nextResult(SmackConfiguration  
                .getPacketReplyTimeout());  
        // Stop queuing resultsֹͣ����results���Ƿ�ɹ��Ľ����  
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
