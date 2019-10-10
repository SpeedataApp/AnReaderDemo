package com.example.AnReaderDemo;
import java.util.ArrayList;

import com.example.ReaderDemo.R;
import com.rfid.api.ADReaderInterface;
import com.rfid.api.GFunction;
import com.rfid.api.ISO14443AInterface;
import com.rfid.api.ISO14443ATag;
import com.rfid.def.ApiErrDefinition;
import com.rfid.def.RfidDef;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ISO14443Ap4TransceiveActivity extends Activity implements OnClickListener
{
	private Spinner sn_iso14443ap4_uidList = null;
	private Button btn_iso14443ap4_inventory = null;
	private Button btn_iso14443ap4_connect = null;
	private Button btn_iso14443ap4_disconnect = null;
	private EditText ed_iso14443ap4_send = null;
	private EditText ed_iso14443ap4_recv = null;
	private Button btn_iso14443ap4_send = null;
	private ArrayList<CharSequence> uidList = new ArrayList<CharSequence>();
	private ArrayAdapter<CharSequence> m_adaUidList = null;
	private ArrayList<ISO14443ATag> tagList = new ArrayList<ISO14443ATag>();
	private ISO14443AInterface mTag = new ISO14443AInterface();
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_iso14443ap4_transceive);
		sn_iso14443ap4_uidList = (Spinner) findViewById(R.id.sn_iso14443ap4_uidList);
		btn_iso14443ap4_inventory = (Button) findViewById(R.id.btn_iso14443ap4_inventory);
		btn_iso14443ap4_connect=(Button) findViewById(R.id.btn_iso14443ap4_connect);
		btn_iso14443ap4_disconnect=(Button) findViewById(R.id.btn_iso14443ap4_disconnect);
		ed_iso14443ap4_send = (EditText) findViewById(R.id.ed_iso14443ap4_send);
		ed_iso14443ap4_recv = (EditText) findViewById(R.id.ed_iso14443ap4_recv);
		btn_iso14443ap4_send = (Button) findViewById(R.id.btn_iso14443ap4_send);
		btn_iso14443ap4_inventory.setOnClickListener(this);
		btn_iso14443ap4_connect.setOnClickListener(this);
		btn_iso14443ap4_disconnect.setOnClickListener(this);
		btn_iso14443ap4_send.setOnClickListener(this);
		m_adaUidList = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_dropdown_item, uidList);
		sn_iso14443ap4_uidList.setAdapter(m_adaUidList);
		sn_iso14443ap4_uidList.setEnabled(true);
		btn_iso14443ap4_inventory.setEnabled(true);
		btn_iso14443ap4_connect.setEnabled(true);
		btn_iso14443ap4_disconnect.setEnabled(false);
		ed_iso14443ap4_send.setEnabled(false);
		ed_iso14443ap4_recv.setEnabled(false);
		btn_iso14443ap4_send.setEnabled(false);
	}
	
	private void tagInventory()
	{
		uidList.clear();
		tagList.clear();
		int iret = -1;
		byte[] mAntId = null;
		Object hInvenParamSpecList = ADReaderInterface
				.RDR_CreateInvenParamSpecList();
		ISO14443AInterface.ISO14443A_CreateInvenParam(hInvenParamSpecList,
				(byte) 0);
		iret = MainActivity.m_reader.RDR_TagInventory(RfidDef.AI_TYPE_NEW,
				mAntId, 0, hInvenParamSpecList);
		if (iret != ApiErrDefinition.NO_ERROR)
		{
			return;
		}
		Object tagReport = MainActivity.m_reader
				.RDR_GetTagDataReport(RfidDef.RFID_SEEK_FIRST);
		while (tagReport != null)
		{
			ISO14443ATag tagData = new ISO14443ATag();
			ISO14443AInterface.ISO14443A_ParseTagDataReport(tagReport, tagData);
			if (iret == ApiErrDefinition.NO_ERROR)
			{
				String uidStr = GFunction.encodeHexStr(tagData.uid);
				uidList.add(uidStr);
				tagList.add(tagData);
			}
			tagReport = MainActivity.m_reader
					.RDR_GetTagDataReport(RfidDef.RFID_SEEK_NEXT);
		}
		m_adaUidList.notifyDataSetChanged();
	}
	
	private void tagConnect()
	{
		if (sn_iso14443ap4_uidList.getCount()<=0)
		{
			Toast.makeText(this, "Please inventory first.", Toast.LENGTH_LONG).show();
			return;
		}
		int idx = sn_iso14443ap4_uidList.getSelectedItemPosition();
		ISO14443ATag tag = tagList.get(idx);
		int iret = mTag.ISO14443A_Connect(MainActivity.m_reader,0, tag.uid, (byte) tag.uid.length);
	    if (iret!=ApiErrDefinition.NO_ERROR)
		{
			Toast.makeText(this, "It's failure in connecting the tag.", Toast.LENGTH_LONG).show();
			return;
		}
	    sn_iso14443ap4_uidList.setEnabled(false);
		btn_iso14443ap4_inventory.setEnabled(false);
		btn_iso14443ap4_connect.setEnabled(false);
		btn_iso14443ap4_disconnect.setEnabled(true);
		ed_iso14443ap4_send.setEnabled(true);
		ed_iso14443ap4_recv.setEnabled(true);
		btn_iso14443ap4_send.setEnabled(true);
	}
	
	private void tagDisconnect()
	{
		mTag.ISO14443A_Disconnect();
		sn_iso14443ap4_uidList.setEnabled(true);
		btn_iso14443ap4_inventory.setEnabled(true);
		btn_iso14443ap4_connect.setEnabled(true);
		btn_iso14443ap4_disconnect.setEnabled(false);
		ed_iso14443ap4_send.setEnabled(false);
		ed_iso14443ap4_recv.setEnabled(false);
		btn_iso14443ap4_send.setEnabled(false);
	}
	
	private void dataSend()
	{
		String strData = ed_iso14443ap4_send.getText().toString();
		byte []sndData = GFunction.decodeHex(strData);
		if (sndData==null)
		{
			Toast.makeText(this, "Input the data you want to send.", Toast.LENGTH_LONG).show();
			return;
		}
		byte []rcvBuff = new byte[256];
		int []nSize = new int[1];
		nSize[0]=rcvBuff.length;
		int iret = mTag.ISO14443Ap4_Transceive(sndData, sndData.length, rcvBuff, nSize);
		if (iret!=ApiErrDefinition.NO_ERROR)
		{
			Toast.makeText(this, "It's failure in the operation.", Toast.LENGTH_LONG).show();
			return;
		}
		
		String strDes = GFunction.encodeHexStr(rcvBuff, nSize[0]);
		ed_iso14443ap4_recv.setText(strDes);
		
		Toast.makeText(this, "It's successful in the operation.", Toast.LENGTH_LONG).show();
	}
	
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btn_iso14443ap4_inventory:
			tagInventory();
			break;
		case R.id.btn_iso14443ap4_connect:
			tagConnect();
			break;
		case R.id.btn_iso14443ap4_disconnect:
			tagDisconnect();
			break;
		case R.id.btn_iso14443ap4_send:
			dataSend();
			break;
		default:
			break;
		}
	}

}
