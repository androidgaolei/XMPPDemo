package com.example.tabexample.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import com.example.tabexample.R;
import com.example.tabexample.utils.XMPPTool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SelectPicPopupWindow extends Activity implements OnClickListener {
	public static final int TAKE_PHOTO = 1;
	public static final int CROP_PHOTO = 2;
	private Button btn_take_photo, btn_pick_photo, btn_cancel;
	private LinearLayout layout;
	private Uri imageUri;
	private String JUID;
	private XMPPConnection connection;
	private Bitmap photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_dialog);
		btn_take_photo = (Button) this.findViewById(R.id.btn_take_photo);
		btn_pick_photo = (Button) this.findViewById(R.id.btn_pick_photo);
		btn_cancel = (Button) this.findViewById(R.id.btn_cancel);

		layout = (LinearLayout) findViewById(R.id.pop_layout);

		// 添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
		layout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
						Toast.LENGTH_SHORT).show();
			}
		});
		// 添加按钮监听
		btn_cancel.setOnClickListener(this);
		btn_pick_photo.setOnClickListener(this);
		btn_take_photo.setOnClickListener(this);
	}

	// 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_take_photo:
			 destoryBimap();
			 String state = Environment.getExternalStorageState();
			 if (state.equals(Environment.MEDIA_MOUNTED)) {
			     Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			     startActivityForResult(intent, 1);
			 } else {
			     Toast.makeText(SelectPicPopupWindow.this,
			             "没有sdcard", Toast.LENGTH_LONG).show();
			 }

			break;
		case R.id.btn_pick_photo:
			Intent intent_pick = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent_pick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
			SelectPicPopupWindow.this.startActivityForResult(intent_pick, 0);
			break;
		case R.id.btn_cancel:
			finish();
			break;
		default:
			break;
		}
		// finish();
	}


	public void sendFile(File file) {

		// 这段代码有些人说必须带，我试了试，在我的工程中发文件是带不带没啥区别
		// ServiceDiscoveryManager sdm =
		// ServiceDiscoveryManager.getInstanceFor(connection);
		// if (sdm == null)
		// sdm = new ServiceDiscoveryManager(connection);
		// sdm.addFeature("http://jabber.org/protocol/disco#info");
		// sdm.addFeature("jabber:iq:privacy");

		connection = XMPPTool.getConnection();
		FileTransferManager ftManager = new FileTransferManager(connection);
		FileTransferNegotiator.setServiceEnabled(connection, true);
		String to = connection.getRoster()
				.getPresence("gaolei@pc201509182058/spark 2.7.2").getFrom();// 获得用户状态

		// to = "xxx"+"@"+"xxx"+"/"+"xxx";//一定注意这里必须是完整JID jid = [ node "@" ]
		// domain [ "/" resource ]

		// 我是用的android客户端给spark客户端发送文件

		OutgoingFileTransfer transfer = ftManager
				.createOutgoingFileTransfer(to);
		try {
			transfer.sendFile(file, file.getName());
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		while (!transfer.isDone()) {
//			if (transfer.getStatus().equals(FileTransfer.Status.in_progress)) {
//				// 可以调用transfer.getProgress();获得传输的进度
//				Log.i("wht", "传输进度 = " + transfer.getProgress());
//
//			} else if (transfer.getStatus().equals(FileTransfer.Status.error)) {
//				System.out.println("ERROR!!! " + transfer.getError());
//			} else if (transfer.getStatus().equals(
//					FileTransfer.Status.cancelled)
//					|| transfer.getStatus().equals(FileTransfer.Status.refused)) {
//				System.out.println("Cancelled!!! " + transfer.getError());
//			}
//			
//		}
		

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 0:
				//Log.d("test",""+data.getData());
				String path = getRealPathFromURI(data.getData());
				Log.d("test", path);
				//connection = XMPPTool.getConnection();
				sendFile(new File(path));
//				boolean falg = sendFiles(connection,path);
//				if(falg==true){
//					Toast.makeText(SelectPicPopupWindow.this, "发送成功", Toast.LENGTH_SHORT).show();
//				}else{
//					Toast.makeText(SelectPicPopupWindow.this, "发送失败", Toast.LENGTH_SHORT).show();
//				}
				finish();
				break;
			
		case 1:
			Toast.makeText(SelectPicPopupWindow.this, "take a photo ", Toast.LENGTH_SHORT).show();
			String path1 = getRealPathFromURI(data.getData());
			sendFile(new File(path1));
			finish();
			break;
		default:
			break;
		}

	}

	@SuppressLint("NewApi")
	private String getRealPathFromURI(Uri contentUri) {
	    String[] proj = { MediaStore.Images.Media.DATA };
	    CursorLoader loader = new CursorLoader(SelectPicPopupWindow.this, contentUri, proj, null, null, null);
	    Cursor cursor = loader.loadInBackground();
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	
	/**
     * 销毁图片文件
     */
    private void destoryBimap() {
        if (photo != null && !photo.isRecycled()) {
            photo.recycle();
            photo = null;
        }
    }
}
