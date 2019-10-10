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
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class MifareS50TagActivity extends Activity implements OnClickListener
{
	private Button btn_S50_inventory = null;
	private Button btn_s50_connect = null;
	private Button btn_s50_disconnect = null;
	private Button btn_s50_authenticate = null;
	private Spinner sn_s50_blkAddr = null;
	private Spinner sn_s50_keyType = null;
	private EditText ed_s50_key = null;
	private Button btn_s50_read = null;
	private Button btn_s50_write = null;
	private EditText ed_s50_data = null;
	private EditText ed_s50_value = null;
	private Button btn_s50_format = null;
	private Button btn_s50_restore = null;
	private Button btn_s50_increase = null;
	private Button btn_s50_decrease = null;

	private ArrayList<CharSequence> uidList = new ArrayList<CharSequence>();
	private ArrayAdapter<CharSequence> m_adaUidList = null;
	private Spinner sn_s50_uidList = null;

	private ISO14443AInterface mTag = new ISO14443AInterface();

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag_s50);

		btn_S50_inventory = (Button) findViewById(R.id.btn_S50_inventory);
		sn_s50_uidList = (Spinner) findViewById(R.id.sn_s50_uidList);
		btn_s50_connect = (Button) findViewById(R.id.btn_s50_connect);
		btn_s50_disconnect = (Button) findViewById(R.id.btn_s50_disconnect);
		btn_s50_authenticate = (Button) findViewById(R.id.btn_s50_authenticate);
		sn_s50_blkAddr = (Spinner) findViewById(R.id.sn_s50_blkAddr);
		sn_s50_keyType = (Spinner) findViewById(R.id.sn_s50_keyType);
		ed_s50_key = (EditText) findViewById(R.id.ed_s50_key);
		btn_s50_read = (Button) findViewById(R.id.btn_s50_read);
		btn_s50_write = (Button) findViewById(R.id.btn_s50_write);
		ed_s50_data = (EditText) findViewById(R.id.ed_s50_data);
		ed_s50_value = (EditText) findViewById(R.id.ed_s50_value);
		btn_s50_format = (Button) findViewById(R.id.btn_s50_format);
		btn_s50_restore = (Button) findViewById(R.id.btn_s50_restore);
		btn_s50_increase = (Button) findViewById(R.id.btn_s50_increase);
		btn_s50_decrease = (Button) findViewById(R.id.btn_s50_decrease);

		btn_s50_connect.setOnClickListener(this);
		btn_S50_inventory.setOnClickListener(this);
		btn_s50_disconnect.setOnClickListener(this);
		btn_s50_authenticate.setOnClickListener(this);
		btn_s50_read.setOnClickListener(this);
		btn_s50_write.setOnClickListener(this);
		btn_s50_format.setOnClickListener(this);
		btn_s50_restore.setOnClickListener(this);
		btn_s50_increase.setOnClickListener(this);
		btn_s50_decrease.setOnClickListener(this);

		m_adaUidList = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_dropdown_item, uidList);
		sn_s50_uidList.setAdapter(m_adaUidList);
		
		UiVisible(false);
	}

	private void UiInventory()
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

	private void UiConnect()
	{
		byte connectUid[] = null;
		if (sn_s50_uidList.getSelectedItem() != null)
		{
			connectUid = GFunction.decodeHex(sn_s50_uidList.getSelectedItem()
					.toString());
		} else
		{
			return;
		}

		int iret = mTag.MFCL_Connect(MainActivity.m_reader, (byte) 0,
				connectUid);
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

	private void UiAuthenticate()
	{
		byte blkAddr = (byte) sn_s50_blkAddr.getSelectedItemPosition();
		byte keyType = (byte) sn_s50_keyType.getSelectedItemPosition();
		String strKey = ed_s50_key.getText().toString();
		byte byKey[] = null;
		byKey = GFunction.decodeHex(strKey);

		if (byKey == null || byKey.length != 6)
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage(getString(R.string.msg_data_err))
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		}

		int iret = mTag.MFCL_Authenticate(blkAddr, keyType, byKey);
		if (iret == ApiErrDefinition.NO_ERROR)
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage("success")
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		} else
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage("Failed")
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		}

	}

	private void UIReadBlk()
	{
		byte blkAddr = (byte) sn_s50_blkAddr.getSelectedItemPosition();
		byte[] blkData = new byte[16];
		int iret = mTag.MFCL_ReadBlock(blkAddr, blkData);
		if (iret != ApiErrDefinition.NO_ERROR)
		{
			if (iret != ApiErrDefinition.NO_ERROR)
			{
				new AlertDialog.Builder(this)
						.setTitle("")
						.setMessage(getString(R.string.tx_msg_operate_fail))
						.setPositiveButton(getString(R.string.tx_msg_certain),
								null).show();
				return;
			}
		}

		String strData = GFunction.encodeHexStr(blkData);
		ed_s50_data.setText(strData);
	}

	private void UIWriteBlk()
	{
		byte blkAddr = (byte) sn_s50_blkAddr.getSelectedItemPosition();
		String strData = ed_s50_data.getText().toString();
		byte blkData[] = null;
		blkData = GFunction.decodeHex(strData);

		if (blkData == null || blkData.length != 16)
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage(getString(R.string.msg_data_err))
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		}

		int iret = mTag.MFCL_WriteBlock(blkAddr, blkData);
		if (iret == ApiErrDefinition.NO_ERROR)
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage("success")
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		} else
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage("Failed")
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		}
	}

	private void UiFormatValue()
	{
		String strValue = ed_s50_value.getText().toString();
		if (strValue.equals(""))
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage("Input the value first.")
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		}
		byte blkAddr = (byte) sn_s50_blkAddr.getSelectedItemPosition();
		long initValue = Long.parseLong(strValue);
		int iret = mTag.MFCL_FormatValueBlock(blkAddr, initValue);
		if (iret == ApiErrDefinition.NO_ERROR)
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage("success")
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		} else
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage("Failed")
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		}
	}

	private void UiRestore()
	{
		byte blkAddr = (byte) sn_s50_blkAddr.getSelectedItemPosition();
		int iret = mTag.MFCL_Restore(blkAddr);
		if (iret == ApiErrDefinition.NO_ERROR)
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage("success")
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		} else
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage("Failed")
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		}
	}

	private void UiIncrease()
	{
		String strValue = ed_s50_value.getText().toString();
		if (strValue.equals(""))
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage("Input the value first.")
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		}
		byte blkAddr = (byte) sn_s50_blkAddr.getSelectedItemPosition();
		long initValue = Long.parseLong(strValue);
		int iret = mTag.MFCL_Increment(blkAddr, initValue);
		if (iret == ApiErrDefinition.NO_ERROR)
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage("success")
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		} else
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage("Failed")
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		}
	}

	private void UiDecrease()
	{
		String strValue = ed_s50_value.getText().toString();
		if (strValue.equals(""))
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage("Input the value first.")
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		}
		byte blkAddr = (byte) sn_s50_blkAddr.getSelectedItemPosition();
		long initValue = Long.parseLong(strValue);
		int iret = mTag.MFCL_Decrement(blkAddr, initValue);
		if (iret == ApiErrDefinition.NO_ERROR)
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage("success")
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		} else
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage("Failed")
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		}
	}
	
	private void UiVisible(boolean bConnect)
	{
		btn_S50_inventory.setEnabled(!bConnect);
		btn_s50_connect.setEnabled(!bConnect);
		btn_s50_disconnect.setEnabled(bConnect);	
		btn_s50_authenticate.setEnabled(bConnect);
		sn_s50_blkAddr.setEnabled(bConnect);
		sn_s50_keyType.setEnabled(bConnect);
		ed_s50_key.setEnabled(bConnect);
		btn_s50_read.setEnabled(bConnect);
		btn_s50_write.setEnabled(bConnect);
		ed_s50_data.setEnabled(bConnect);
		ed_s50_value.setEnabled(bConnect);
		btn_s50_format.setEnabled(bConnect);
		btn_s50_restore.setEnabled(bConnect);
		btn_s50_increase.setEnabled(bConnect);
		btn_s50_decrease.setEnabled(bConnect);
	}
	private void UiDisconnect()
	{
		mTag.ISO14443A_Disconnect();
		UiVisible(false);
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btn_S50_inventory:
			UiInventory();
			break;
		case R.id.btn_s50_connect:
			UiConnect();
			break;
		case R.id.btn_s50_disconnect:
			UiDisconnect();
			break;
		case R.id.btn_s50_authenticate:
			UiAuthenticate();
			break;
		case R.id.btn_s50_read:
			UIReadBlk();
			break;
		case R.id.btn_s50_write:
			UIWriteBlk();
			break;
		case R.id.btn_s50_format:
			UiFormatValue();
			break;
		case R.id.btn_s50_restore:
			UiRestore();
			break;
		case R.id.btn_s50_increase:
			UiIncrease();
			break;
		case R.id.btn_s50_decrease:
			UiDecrease();
			break;
		}
	}
}
