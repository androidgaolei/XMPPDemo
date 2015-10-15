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

	private ViewPager mViewPager;// 用来放置界面切换
	private PagerAdapter mPagerAdapter;// 初始化View适配器
	private List<View> mViews = new ArrayList<View>();// 用来存放Tab01-04
	// 四个Tab，每个Tab包含一个按钮
	private LinearLayout mTabWeiXin;
	private LinearLayout mTabAddress;
	private LinearLayout mTabFrd;
	private LinearLayout mTabSetting;
	// 四个按钮
	private ImageButton mWeiXinImg;
	private ImageButton mAddressImg;
	private ImageButton mFrdImg;
	private ImageButton mSettingImg;
	
	private LinearLayout top_layout;
	static XMPPConnection connection = null;
	private ImageButton top_add ;
	
	private ImageButton top_search;//虽然是查询 但是是创建聊天室。。。没有素材的烦恼！！！！

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
				builder.setTitle("创建聊天室").setView(layout);
				builder.setPositiveButton("确定",  new DialogInterface.OnClickListener() {
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
				builder.setNegativeButton("取消", null);
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
				builder.setTitle("请输入聊天室信息").setView(layout);
				builder.setPositiveButton("确定",  new DialogInterface.OnClickListener() {
					
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
							Toast.makeText(MainActivity.this, "输入信息有误", Toast.LENGTH_SHORT).show();
						}
					}
				} );
				builder.setNegativeButton("取消", null);
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
			 * ViewPage左右滑动时
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
	 * 初始化设置
	 */
	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.id_viewpage);
		// 初始化四个LinearLayout
		mTabWeiXin = (LinearLayout) findViewById(R.id.id_tab_weixin);
		mTabAddress = (LinearLayout) findViewById(R.id.id_tab_address);
		mTabFrd = (LinearLayout) findViewById(R.id.id_tab_frd);
		mTabSetting = (LinearLayout) findViewById(R.id.id_tab_settings);
		
		top_layout = (LinearLayout) findViewById(R.layout.top_layout);
		// 初始化四个按钮
		mWeiXinImg = (ImageButton) findViewById(R.id.id_tab_weixin_img);
		mAddressImg = (ImageButton) findViewById(R.id.id_tab_address_img);
		mFrdImg = (ImageButton) findViewById(R.id.id_tab_frd_img);
		mSettingImg = (ImageButton) findViewById(R.id.id_tab_settings_img);
		
		
	}

	/**
	 * 初始化ViewPage
	 */
	private void initViewPage() {

		// 初妈化四个布局
		LayoutInflater mLayoutInflater = LayoutInflater.from(this);
		View tab01 = mLayoutInflater.inflate(R.layout.tab01, null);
		View tab02 = mLayoutInflater.inflate(R.layout.tab02, null);
		View tab03 = mLayoutInflater.inflate(R.layout.tab03, null);
		View tab04 = mLayoutInflater.inflate(R.layout.tab04, null);

		mViews.add(tab01);
		mViews.add(tab02);
		mViews.add(tab03);
		mViews.add(tab04);

		// 适配器初始化并设置
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
	 * 判断哪个要显示，及设置按钮图片
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
	 * 把所有图片变暗
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
			Toast.makeText(this, "出错啦", 0).show();
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
					if ("both".equals(entry.getType().name())) {// 只添加双边好友
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
	 * 加入会议室 
	 *  
	 * @param user 
	 *            昵称 
	 * @param password 
	 *            会议室密码 
	 * @param roomsName 
	 *            会议室名 
	 */  
	public MultiUserChat joinMultiUserChat(String user, String roomsName,  
	        String password) {  
		connection = XMPPTool.getConnection();
	    if (connection == null)  
	        return null;  
	    try {  
	        // 使用XMPPConnection创建一个MultiUserChat窗口  
	        MultiUserChat muc = new MultiUserChat(connection, roomsName  
	                + "@conference." + connection.getServiceName());  
	        // 聊天室服务将会决定要接受的历史记录数量  
	        DiscussionHistory history = new DiscussionHistory();  
	        history.setMaxChars(0);  
	        // history.setSince(new Date());  
	        // 用户加入聊天室  
	        muc.join(user, password, history,  
	                SmackConfiguration.getPacketReplyTimeout());  
	        Toast.makeText(MainActivity.this, "加入"+roomsName+"成功", Toast.LENGTH_SHORT).show();
	      //  Log.i("MultiUserChat", "会议室【"+roomsName+"】加入成功........");  
	        return muc;  
	    } catch (XMPPException e) {  
	        e.printStackTrace();  
	        Toast.makeText(MainActivity.this, "加入"+roomsName+"失败", Toast.LENGTH_SHORT).show();
	       // Log.i("MultiUserChat", "会议室【"+roomsName+"】加入失败........");  
	        return null;  
	    }  
	} 
	
	/** 
     * 创建房间 
     *  
     * @param roomName 房间名称 
     */  
    public static void createRoom(String roomName,String password) {  
    	connection = XMPPTool.getConnection();
        if (connection == null) {  
            return;  
        }  
        try {  
            // 创建一个MultiUserChat  
            MultiUserChat muc = new MultiUserChat(connection, roomName  
                    + "@conference." + connection.getServiceName());  
            // 创建聊天室  
            muc.create(roomName); // roomName房间的名字  
            // 获得聊天室的配置表单  
            Form form = muc.getConfigurationForm();  
            // 根据原始表单创建一个要提交的新表单。  
            Form submitForm = form.createAnswerForm();  
            // 向要提交的表单添加默认答复  
            for (Iterator<FormField> fields = form.getFields(); fields  
                    .hasNext();) {  
                FormField field = (FormField) fields.next();  
                if (!FormField.TYPE_HIDDEN.equals(field.getType())  
                        && field.getVariable() != null) {  
                    // 设置默认值作为答复  
                    submitForm.setDefaultAnswer(field.getVariable());  
                }  
            }  
            // 设置聊天室的新拥有者  
            List<String> owners = new ArrayList<String>();  
            owners.add(connection.getUser());// 用户JID  
            submitForm.setAnswer("muc#roomconfig_roomowners", owners);  
            // 设置聊天室是持久聊天室，即将要被保存下来  
            submitForm.setAnswer("muc#roomconfig_persistentroom", false);  
            // 房间仅对成员开放  
            submitForm.setAnswer("muc#roomconfig_membersonly", false);  
            // 允许占有者邀请其他人  
            submitForm.setAnswer("muc#roomconfig_allowinvites", true);  
            // 进入是否需要密码  
            submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);  
            // 设置进入密码  
            submitForm.setAnswer("muc#roomconfig_roomsecret", password);  
            // 能够发现占有者真实 JID 的角色  
            // submitForm.setAnswer("muc#roomconfig_whois", "anyone");  
            // 登录房间对话  
            submitForm.setAnswer("muc#roomconfig_enablelogging", true);  
            // 仅允许注册的昵称登录  
            submitForm.setAnswer("x-muc#roomconfig_reservednick", true);  
            // 允许使用者修改昵称  
            submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);  
            // 允许用户注册房间  
            submitForm.setAnswer("x-muc#roomconfig_registration", false);  
            // 发送已完成的表单（有默认值）到服务器来配置聊天室  
            submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);           
            // 发送已完成的表单（有默认值）到服务器来配置聊天室  
            muc.sendConfigurationForm(submitForm);  
        } catch (XMPPException e) {  
            e.printStackTrace();  
        }  
    }  
}
