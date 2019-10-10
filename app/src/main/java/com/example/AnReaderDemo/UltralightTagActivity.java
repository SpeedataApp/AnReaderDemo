package com.example.AnReaderDemo;

import java.util.ArrayList;

import com.example.ReaderDemo.R;
import com.rfid.api.ADReaderInterface;
import com.rfid.api.GFunction;
import com.rfid.api.ISO14443AInterface;
import com.rfid.api.ISO14443ATag;
import com.rfid.def.ApiErrDefinition;
import com.rfid.def.RfidDef;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class UltralightTagActivity extends Activity implements OnClickListener
{
	private Spinner sn_ultralight_uidList = null;
	private Button btn_ultralight_inventory = null;
	private Button btn_ultralight_connect = null;
	private Button btn_ultralight_disconnect = null;
	private Spinner sn_ultralight_blkAddr = null;
	private Spinner sn_ultralight_blkNum = null;
	private EditText ed_ultralight_blkData = null;
	private Button btn_ultralight_read = null;
	private Button btn_ultralight_write = null;
	private ISO14443AInterface mTag = new ISO14443AInterface();

	private ArrayList<CharSequence> uidList = new ArrayList<CharSequence>();
	private ArrayAdapter<CharSequence> m_adaUidList = null;

	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag_ultralight);
		sn_ultralight_uidList = (Spinner) findViewById(R.id.sn_ultralight_uidList);
		btn_ultralight_inventory = (Button) findViewById(R.id.btn_ultralight_inventory);
		btn_ultralight_connect = (Button) findViewById(R.id.btn_ultralight_connect);
		btn_ultralight_disconnect = (Button) findViewById(R.id.btn_ultralight_disconnect);
		sn_ultralight_blkAddr = (Spinner) findViewById(R.id.sn_ultralight_blkAddr);
		sn_ultralight_blkNum = (Spinner) findViewById(R.id.sn_ultralight_blkNum);
		ed_ultralight_blkData = (EditText) findViewById(R.id.ed_ultralight_blkData);
		btn_ultralight_read = (Button) findViewById(R.id.btn_ultralight_read);
		btn_ultralight_write = (Button) findViewById(R.id.btn_ultralight_write);
		btn_ultralight_inventory.setOnClickListener(this);
		btn_ultralight_read.setOnClickListener(this);
		btn_ultralight_write.setOnClickListener(this);
		btn_ultralight_connect.setOnClickListener(this);
		btn_ultralight_disconnect.setOnClickListener(this);

		m_adaUidList = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_dropdown_item, uidList);
		sn_ultralight_uidList.setAdapter(m_adaUidList);
		
		UiVisible(false);
	}

	private void Inventory()
	{
		uidList.clear();
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
			}
			tagReport = MainActivity.m_reader
					.RDR_GetTagDataReport(RfidDef.RFID_SEEK_NEXT);
		}
		m_adaUidList.notifyDataSetChanged();
	}

	private void Connect()
	{
		byte connectUid[] = null;
		if (sn_ultralight_uidList.getSelectedItem() != null)
		{
			connectUid = GFunction.decodeHex(sn_ultralight_uidList.getSelectedItem()
					.toString());
		} else
		{
			return;
		}

		int iret = mTag.ULTRALIGHT_Connect(MainActivity.m_reader,connectUid);
		if (iret != ApiErrDefinition.NO_ERROR)
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage(getString(R.string.tx_msg_operate_fail))
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		}
		UiVisible(true);
	}

	private void Disconnect()
	{
		mTag.ISO14443A_Disconnect();
		UiVisible(false);
	}

	@SuppressLint("UseValueOf") 
	private void Read()
	{
		int blkAddr = (int) sn_ultralight_blkAddr.getSelectedItemPosition();
		int numOfBlksToRead = (int) (sn_ultralight_blkNum.getSelectedItemPosition() + 1);
		byte bufBlocks[] = new byte[4 * numOfBlksToRead];
		Integer nSize = new Integer(bufBlocks.length);
		int iret = mTag.ULTRALIGHT_ReadMultiplePages(blkAddr, numOfBlksToRead, bufBlocks, nSize);
		if (iret != ApiErrDefinition.NO_ERROR)
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage(getString(R.string.tx_msg_readBlk_fail) + iret)
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
		}
		String strData = GFunction.encodeHexStr(bufBlocks);
		ed_ultralight_blkData.setText(strData);
	}

	private void Write()
	{
		String strData = ed_ultralight_blkData.getText().toString();
		byte byData[] = GFunction.decodeHex(strData);
		if (byData == null)
		{
			ed_ultralight_blkData
					.setError(getString(R.string.tx_msg_inputHexString));
			return;
		}
		int blkNum = sn_ultralight_blkNum.getSelectedItemPosition() + 1;
		int blkAddr = sn_ultralight_blkAddr.getSelectedItemPosition();
		if (blkNum * 4 != byData.length)
		{
			ed_ultralight_blkData.setError(getString(R.string.tx_msg_dataLenErr));
			return;
		}
		int iret = mTag.ULTRALIGHT_WriteMultiplePages(blkAddr, blkNum, byData, byData.length);
		String srtResult = "";
		if (iret == ApiErrDefinition.NO_ERROR)
		{
			srtResult = getString(R.string.tx_msg_writeBlk_ok);

		}
		else
		{
			srtResult = getString(R.string.tx_msg_writeBlk_fail) + iret;
		}
		new AlertDialog.Builder(this).setTitle("").setMessage(srtResult)
				.setPositiveButton(getString(R.string.tx_msg_certain), null)
				.show();
	}
	
	private void UiVisible(boolean bConnect)
	{	
		sn_ultralight_uidList.setEnabled(!bConnect);
		btn_ultralight_inventory.setEnabled(!bConnect);
		btn_ultralight_connect.setEnabled(!bConnect);
		btn_ultralight_disconnect.setEnabled(bConnect);	
		sn_ultralight_blkAddr.setEnabled(bConnect);
		sn_ultralight_blkNum.setEnabled(bConnect);
		ed_ultralight_blkData.setEnabled(bConnect);
		btn_ultralight_read.setEnabled(bConnect);
		btn_ultralight_write.setEnabled(bConnect);
	}

	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.btn_ultralight_inventory:
			Inventory();
			break;
		case R.id.btn_ultralight_connect:
			Connect();
			break;
		case R.id.btn_ultralight_disconnect:
			Disconnect();
			break;
		case R.id.btn_ultralight_read:
			Read();
			break;
		case R.id.btn_ultralight_write:
			Write();
			break;
		default:
			break;
		}
	}

}
