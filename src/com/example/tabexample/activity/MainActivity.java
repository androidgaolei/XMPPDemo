package com.example.tabexample.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

import com.example.tabexample.R;
import com.example.tabexample.entity.FriendInfo;
import com.example.tabexample.utils.Utils;
import com.example.tabexample.utils.XMPPTool;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements
		android.view.View.OnClickListener {

	private ViewPager mViewPager;// �������ý����л�
	private PagerAdapter mPagerAdapter;// ��ʼ��View������
	private List<View> mViews = new ArrayList<View>();// �������Tab01-04
	// �ĸ�Tab��ÿ��Tab����һ����ť
	private LinearLayout mTabWeiXin;
	private LinearLayout mTabAddress;
	private LinearLayout mTabFrd;
	private LinearLayout mTabSetting;
	// �ĸ���ť
	private ImageButton mWeiXinImg;
	private ImageButton mAddressImg;
	private ImageButton mFrdImg;
	private ImageButton mSettingImg;
	
	private LinearLayout top_layout;
	static XMPPConnection connection = null;
	private ImageButton top_add ;
	
	private ImageButton top_search;//��Ȼ�ǲ�ѯ �����Ǵ��������ҡ�����û���زĵķ��գ�������

	private List<com.example.tabexample.entity.FriendInfo> friendList;
	com.example.tabexample.entity.FriendInfo friendInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		top_search = (ImageButton)findViewById(R.id.top_search);
		top_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LayoutInflater inflater = getLayoutInflater();
				final View layout = inflater.inflate(R.layout.joinroom,null);
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("����������").setView(layout);
				builder.setPositiveButton("ȷ��",  new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						EditText roomName;
						EditText password;
						roomName =(EditText)layout.findViewById(R.id.roomname);
						password = (EditText)layout.findViewById(R.id.password);
						String roomTitle = roomName.getText().toString();
						String pwd = password.getText().toString();
						createRoom(roomTitle, pwd);
						
						
						
					}
				});
				builder.setNegativeButton("ȡ��", null);
				builder.show();
			}
		});
		 top_add = (ImageButton)findViewById(R.id.top_add);
		 top_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				LayoutInflater inflater = getLayoutInflater();
				final View layout = inflater.inflate(R.layout.joinroom,null);
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("��������������Ϣ").setView(layout);
				builder.setPositiveButton("ȷ��",  new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						EditText roomName;
						EditText password;
						roomName =(EditText)layout.findViewById(R.id.roomname);
						password = (EditText)layout.findViewById(R.id.password);
						String roomTitle = roomName.getText().toString();
						String pwd = password.getText().toString();
						String name = getIntent().getStringExtra("userId");
						joinMultiUserChat(name,roomTitle,pwd);
						if(joinMultiUserChat(name,roomTitle,pwd)!=null){
							Intent intent1 = new Intent(MainActivity.this,RoomChatActivity.class);
							intent1.putExtra("name", name);
							intent1.putExtra("roomname", roomTitle);
							intent1.putExtra("pass", pwd);
							startActivity(intent1);
							finish();
						}else{
							Toast.makeText(MainActivity.this, "������Ϣ����", Toast.LENGTH_SHORT).show();
						}
					}
				} );
				builder.setNegativeButton("ȡ��", null);
				builder.show();
//				Intent intent = getIntent();
//				String name = intent.getStringExtra("userId");
//				joinMultiUserChat(name,"ff","ff");
//				if(joinMultiUserChat(name,"ff","ff")!=null){
//					Intent intent1 = new Intent(MainActivity.this,RoomChatActivity.class);
//					intent1.putExtra("name", name);
//					intent1.putExtra("roomname", "ff");
//					intent1.putExtra("pass", "ff");
//					startActivity(intent1);
//					finish();
//				}
			}
		});
		initView();
		initViewPage();
		initEvent();
	}

	private void initEvent() {
		mTabWeiXin.setOnClickListener(this);
		mTabAddress.setOnClickListener(this);
		mTabFrd.setOnClickListener(this);
		mTabSetting.setOnClickListener(this);
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			/**
			 * ViewPage���һ���ʱ
			 */
			@Override
			public void onPageSelected(int arg0) {
				int currentItem = mViewPager.getCurrentItem();
				switch (currentItem) {
				case 0:
					resetImg();
					mWeiXinImg.setImageResource(R.drawable.tab_weixin_pressed);
					break;
				case 1:
					resetImg();
					mAddressImg
							.setImageResource(R.drawable.tab_address_pressed);
					initAddress();
					break;
				case 2:
					resetImg();
					mFrdImg.setImageResource(R.drawable.tab_find_frd_pressed);
					break;
				case 3:
					resetImg();
					mSettingImg
							.setImageResource(R.drawable.tab_settings_pressed);
					break;
				default:
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	/**
	 * ��ʼ������
	 */
	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.id_viewpage);
		// ��ʼ���ĸ�LinearLayout
		mTabWeiXin = (LinearLayout) findViewById(R.id.id_tab_weixin);
		mTabAddress = (LinearLayout) findViewById(R.id.id_tab_address);
		mTabFrd = (LinearLayout) findViewById(R.id.id_tab_frd);
		mTabSetting = (LinearLayout) findViewById(R.id.id_tab_settings);
		
		top_layout = (LinearLayout) findViewById(R.layout.top_layout);
		// ��ʼ���ĸ���ť
		mWeiXinImg = (ImageButton) findViewById(R.id.id_tab_weixin_img);
		mAddressImg = (ImageButton) findViewById(R.id.id_tab_address_img);
		mFrdImg = (ImageButton) findViewById(R.id.id_tab_frd_img);
		mSettingImg = (ImageButton) findViewById(R.id.id_tab_settings_img);
		
		
	}

	/**
	 * ��ʼ��ViewPage
	 */
	private void initViewPage() {

		// ���軯�ĸ�����
		LayoutInflater mLayoutInflater = LayoutInflater.from(this);
		View tab01 = mLayoutInflater.inflate(R.layout.tab01, null);
		View tab02 = mLayoutInflater.inflate(R.layout.tab02, null);
		View tab03 = mLayoutInflater.inflate(R.layout.tab03, null);
		View tab04 = mLayoutInflater.inflate(R.layout.tab04, null);

		mViews.add(tab01);
		mViews.add(tab02);
		mViews.add(tab03);
		mViews.add(tab04);

		// ��������ʼ��������
		mPagerAdapter = new PagerAdapter() {

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView(mViews.get(position));

			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				View view = mViews.get(position);
				container.addView(view);
				return view;
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {

				return arg0 == arg1;
			}

			@Override
			public int getCount() {

				return mViews.size();
			}
		};
		mViewPager.setAdapter(mPagerAdapter);
	}

	/**
	 * �ж��ĸ�Ҫ��ʾ�������ð�ťͼƬ
	 */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.id_tab_weixin:
			mViewPager.setCurrentItem(0);
			resetImg();
			mWeiXinImg.setImageResource(R.drawable.tab_weixin_pressed);
			break;
		case R.id.id_tab_address:
			mViewPager.setCurrentItem(1);
			resetImg();
			mAddressImg.setImageResource(R.drawable.tab_address_pressed);
			initAddress();
			break;
		case R.id.id_tab_frd:
			mViewPager.setCurrentItem(2);
			resetImg();
			mFrdImg.setImageResource(R.drawable.tab_find_frd_pressed);
			break;
		case R.id.id_tab_settings:
			mViewPager.setCurrentItem(3);
			resetImg();
			mSettingImg.setImageResource(R.drawable.tab_settings_pressed);
			break;
	
		default:
			break;
		}
	}

	/**
	 * ������ͼƬ�䰵
	 */
	private void resetImg() {
		mWeiXinImg.setImageResource(R.drawable.tab_weixin_normal);
		mAddressImg.setImageResource(R.drawable.tab_address_normal);
		mFrdImg.setImageResource(R.drawable.tab_find_frd_normal);
		mSettingImg.setImageResource(R.drawable.tab_settings_normal);
	}

	private void initAddress() {

		try {
			loadfriend();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
			Toast.makeText(this, "������", 0).show();
			return;
		}
		com.example.tabexample.adapter.FriendAdapter adapter = new com.example.tabexample.adapter.FriendAdapter(
				MainActivity.this, R.layout.frienditem, friendList);
		ListView listView = (ListView) findViewById(R.id.list_view);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				FriendInfo friendInfo = friendList.get(position);
				String name = friendInfo.getUsername();
				Intent intent = new Intent(MainActivity.this, NewChatActivity.class);
				intent.putExtra("info", friendInfo);
				startActivity(intent);
				
			}
		});
	}

	public void loadfriend() {
		//TaxiChatManagerListener chatManagerListener = new TaxiChatManagerListener();  
		//XMPPTool.getConnection().getChatManager().addChatListener(chatManagerListener);  
	
		
		try {
			connection = XMPPTool.getConnection();
			Roster roster = connection.getRoster();
			Collection<RosterGroup> groups = roster.getGroups();
			for (RosterGroup group : groups) {

				friendList = new ArrayList<FriendInfo>();

				Collection<RosterEntry> entries = group.getEntries();
				for (RosterEntry entry : entries) {
					if ("both".equals(entry.getType().name())) {// ֻ���˫�ߺ���
						friendInfo = new FriendInfo();
						friendInfo.setUsername(Utils.getJidToUsername(entry
								.getUser()));
						friendList.add(friendInfo);
						// Toast.makeText(this, friendInfo.getUsername(),
						// Toast.LENGTH_SHORT).show();
						friendInfo = null;

					}
				}

			}
		} catch (Exception e) {
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
		connection = XMPPTool.getConnection();
	    if (connection == null)  
	        return null;  
	    try {  
	        // ʹ��XMPPConnection����һ��MultiUserChat����  
	        MultiUserChat muc = new MultiUserChat(connection, roomsName  
	                + "@conference." + connection.getServiceName());  
	        // �����ҷ��񽫻����Ҫ���ܵ���ʷ��¼����  
	        DiscussionHistory history = new DiscussionHistory();  
	        history.setMaxChars(0);  
	        // history.setSince(new Date());  
	        // �û�����������  
	        muc.join(user, password, history,  
	                SmackConfiguration.getPacketReplyTimeout());  
	        Toast.makeText(MainActivity.this, "����"+roomsName+"�ɹ�", Toast.LENGTH_SHORT).show();
	      //  Log.i("MultiUserChat", "�����ҡ�"+roomsName+"������ɹ�........");  
	        return muc;  
	    } catch (XMPPException e) {  
	        e.printStackTrace();  
	        Toast.makeText(MainActivity.this, "����"+roomsName+"ʧ��", Toast.LENGTH_SHORT).show();
	       // Log.i("MultiUserChat", "�����ҡ�"+roomsName+"������ʧ��........");  
	        return null;  
	    }  
	} 
	
	/** 
     * �������� 
     *  
     * @param roomName �������� 
     */  
    public static void createRoom(String roomName,String password) {  
    	connection = XMPPTool.getConnection();
        if (connection == null) {  
            return;  
        }  
        try {  
            // ����һ��MultiUserChat  
            MultiUserChat muc = new MultiUserChat(connection, roomName  
                    + "@conference." + connection.getServiceName());  
            // ����������  
            muc.create(roomName); // roomName���������  
            // ��������ҵ����ñ�  
            Form form = muc.getConfigurationForm();  
            // ����ԭʼ������һ��Ҫ�ύ���±���  
            Form submitForm = form.createAnswerForm();  
            // ��Ҫ�ύ�ı����Ĭ�ϴ�  
            for (Iterator<FormField> fields = form.getFields(); fields  
                    .hasNext();) {  
                FormField field = (FormField) fields.next();  
                if (!FormField.TYPE_HIDDEN.equals(field.getType())  
                        && field.getVariable() != null) {  
                    // ����Ĭ��ֵ��Ϊ��  
                    submitForm.setDefaultAnswer(field.getVariable());  
                }  
            }  
            // ���������ҵ���ӵ����  
            List<String> owners = new ArrayList<String>();  
            owners.add(connection.getUser());// �û�JID  
            submitForm.setAnswer("muc#roomconfig_roomowners", owners);  
            // �����������ǳ־������ң�����Ҫ����������  
            submitForm.setAnswer("muc#roomconfig_persistentroom", false);  
            // ������Գ�Ա����  
            submitForm.setAnswer("muc#roomconfig_membersonly", false);  
            // ����ռ��������������  
            submitForm.setAnswer("muc#roomconfig_allowinvites", true);  
            // �����Ƿ���Ҫ����  
            submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);  
            // ���ý�������  
            submitForm.setAnswer("muc#roomconfig_roomsecret", password);  
            // �ܹ�����ռ������ʵ JID �Ľ�ɫ  
            // submitForm.setAnswer("muc#roomconfig_whois", "anyone");  
            // ��¼����Ի�  
            submitForm.setAnswer("muc#roomconfig_enablelogging", true);  
            // ������ע����ǳƵ�¼  
            submitForm.setAnswer("x-muc#roomconfig_reservednick", true);  
            // ����ʹ�����޸��ǳ�  
            submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);  
            // �����û�ע�᷿��  
            submitForm.setAnswer("x-muc#roomconfig_registration", false);  
            // ��������ɵı�����Ĭ��ֵ����������������������  
            submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);           
            // ��������ɵı�����Ĭ��ֵ����������������������  
            muc.sendConfigurationForm(submitForm);  
        } catch (XMPPException e) {  
            e.printStackTrace();  
        }  
    }  
}
