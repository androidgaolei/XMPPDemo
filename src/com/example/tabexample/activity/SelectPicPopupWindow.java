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

		// ���ѡ�񴰿ڷ�Χ�����������Ȼ�ȡ���㣬������ִ��onTouchEvent()��������������ط�ʱִ��onTouchEvent()��������Activity
		layout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "��ʾ����������ⲿ�رմ��ڣ�",
						Toast.LENGTH_SHORT).show();
			}
		});
		// ��Ӱ�ť����
		btn_cancel.setOnClickListener(this);
		btn_pick_photo.setOnClickListener(this);
		btn_take_photo.setOnClickListener(this);
	}

	// ʵ��onTouchEvent���������������Ļʱ���ٱ�Activity
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
			             "û��sdcard", Toast.LENGTH_LONG).show();
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

		// ��δ�����Щ��˵��������������ԣ����ҵĹ����з��ļ��Ǵ�����ûɶ����
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
				.getPresence("gaolei@pc201509182058/spark 2.7.2").getFrom();// ����û�״̬

		// to = "xxx"+"@"+"xxx"+"/"+"xxx";//һ��ע���������������JID jid = [ node "@" ]
		// domain [ "/" resource ]

		// �����õ�android�ͻ��˸�spark�ͻ��˷����ļ�

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
//				// ���Ե���transfer.getProgress();��ô���Ľ���
//				Log.i("wht", "������� = " + transfer.getProgress());
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
//					Toast.makeText(SelectPicPopupWindow.this, "���ͳɹ�", Toast.LENGTH_SHORT).show();
//				}else{
//					Toast.makeText(SelectPicPopupWindow.this, "����ʧ��", Toast.LENGTH_SHORT).show();
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
     * ����ͼƬ�ļ�
     */
    private void destoryBimap() {
        if (photo != null && !photo.isRecycled()) {
            photo.recycle();
            photo = null;
        }
    }
}
