package com.example.AnReaderDemo;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import com.example.ReaderDemo.R;
import com.rfid.api.ADReaderInterface;
import com.rfid.api.GFunction;
import com.rfid.api.ISO15693Interface;
import com.rfid.api.ISO15693Tag;
import com.rfid.def.ApiErrDefinition;
import com.rfid.def.RfidDef;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class IcodesliTagActivity extends Activity implements OnClickListener
{
	private Button btn_inventory = null;
	private Button btn_icodesli_connect = null;
	private Button btn_icodesli_tagInfo = null;
	private Button btn_icodesli_disconnect = null;
	private Button btn_icodesli_setAnt = null;
	private Spinner sn_icodesli_antList = null;
	private Spinner sn_icodesli_uidList = null;
	private Spinner sn_icodesli_connectMode = null;
	private Spinner sn_icodesli_blkAddr = null;
	private EditText ed_icodesli_blkData = null;
	private Button btn_icodesli_read = null;
	private Button btn_icodesli_write = null;
	private Button btn_icodesli_lockBlk = null;
	private EditText ed_icodesli_dsfid = null;
	private EditText ed_icodesli_afi = null;
	private Button btn_icodesli_setDsfid = null;
	private Button btn_icodesli_lockDsfid = null;
	private Button btn_icodesli_setAFI = null;
	private Button btn_icodesli_lockAFI = null;
	private Button btn_icodesli_enEAS = null;
	private Button btn_icodesli_disEnEAS = null;
	private Button btn_icodesli_checkEAS = null;
	private Button btn_icodesli_lockEas = null;

	private Spinner sn_icodesli_pwbid = null;
	private Button btn_icodesli_authenticate = null;
	private Button btn_icodesli_updatePwb = null;
	private Button btn_icodesli_enable64bitPwb = null;
	private Button btn_icodesli_lockPwb = null;
	private Spinner sn_icodesli_protectEasOrAfi = null;
	private Button btn_icodesli_protectEasOrAfi = null;
	private Spinner sn_icodesli_protectBlkAddr = null;
	private Spinner sn_icodesli_page_L_state = null;
	private Spinner sn_icodesli_page_H_state = null;
	private Button btn_icodesli_protectPage = null;
	private Button btn_icodesli_lockPageState = null;
	private EditText ed_icodesli_pwb = null;

	private ISO15693Interface mTag = new ISO15693Interface();
	private ArrayList<CharSequence> uidList = new ArrayList<CharSequence>();
	private ArrayAdapter<CharSequence> m_adaUidList = null;
	private Spinner sn_icodesli_blkNum = null;

	// m_uid_tagType: used for saving the type of the tags.
	private Dictionary<String, Long> m_uid_tagType = new Hashtable<String, Long>();

	private int mAntCnt = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag_icodesli);
		btn_icodesli_setAnt = (Button) findViewById(R.id.btn_icodesli_setAnt);
		btn_inventory = (Button) findViewById(R.id.btn_icodesli_inventory);
		btn_icodesli_connect = (Button) findViewById(R.id.btn_icodesli_connect);
		btn_icodesli_tagInfo = (Button) findViewById(R.id.btn_icodesli_tagInfo);
		btn_icodesli_disconnect = (Button) findViewById(R.id.btn_icodesli_disconnect);
		sn_icodesli_antList = (Spinner) findViewById(R.id.sn_icodesli_antList);
		sn_icodesli_uidList = (Spinner) findViewById(R.id.sn_icodesli_uidList);
		sn_icodesli_connectMode = (Spinner) findViewById(R.id.sn_icodesli_connectMode);
		sn_icodesli_blkAddr = (Spinner) findViewById(R.id.sn_icodesli_blkAddr);
		sn_icodesli_blkNum = (Spinner) findViewById(R.id.sn_icodesli_blkNum);
		ed_icodesli_blkData = (EditText) findViewById(R.id.ed_icodesli_blkData);
		btn_icodesli_read = (Button) findViewById(R.id.btn_icodesli_read);
		btn_icodesli_write = (Button) findViewById(R.id.btn_icodesli_write);
		btn_icodesli_lockBlk = (Button) findViewById(R.id.btn_icodesli_lockBlk);
		ed_icodesli_dsfid = (EditText) findViewById(R.id.ed_icodesli_dsfid);
		ed_icodesli_afi = (EditText) findViewById(R.id.ed_icodesli_afi);
		btn_icodesli_setDsfid = (Button) findViewById(R.id.btn_icodesli_setDsfid);
		btn_icodesli_lockDsfid = (Button) findViewById(R.id.btn_icodesli_lockDsfid);
		btn_icodesli_setAFI = (Button) findViewById(R.id.btn_icodesli_setAFI);
		btn_icodesli_lockAFI = (Button) findViewById(R.id.btn_icodesli_lockAFI);
		btn_icodesli_enEAS = (Button) findViewById(R.id.btn_icodesli_enEAS);
		btn_icodesli_disEnEAS = (Button) findViewById(R.id.btn_icodesli_disEnEAS);
		btn_icodesli_checkEAS = (Button) findViewById(R.id.btn_icodesli_checkEAS);
		btn_icodesli_lockEas = (Button) findViewById(R.id.btn_icodesli_lockEas);
		sn_icodesli_pwbid = (Spinner) findViewById(R.id.sn_icodesli_pwbid);
		btn_icodesli_authenticate = (Button) findViewById(R.id.btn_icodesli_authenticate);
		btn_icodesli_updatePwb = (Button) findViewById(R.id.btn_icodesli_updatePwb);
		btn_icodesli_enable64bitPwb = (Button) findViewById(R.id.btn_icodesli_enable64bitPwb);
		btn_icodesli_lockPwb = (Button) findViewById(R.id.btn_icodesli_lockPwb);
		sn_icodesli_protectEasOrAfi = (Spinner) findViewById(R.id.sn_icodesli_protectEasOrAfi);
		btn_icodesli_protectEasOrAfi = (Button) findViewById(R.id.btn_icodesli_protectEasOrAfi);
		sn_icodesli_protectBlkAddr = (Spinner) findViewById(R.id.sn_icodesli_protectBlkAddr);
		sn_icodesli_page_L_state = (Spinner) findViewById(R.id.sn_icodesli_page_L_state);
		sn_icodesli_page_H_state = (Spinner) findViewById(R.id.sn_icodesli_page_H_state);
		btn_icodesli_protectPage = (Button) findViewById(R.id.btn_icodesli_protectPage);
		btn_icodesli_lockPageState = (Button) findViewById(R.id.btn_icodesli_lockPageState);
		ed_icodesli_pwb = (EditText) findViewById(R.id.ed_icodesli_pwb);

		btn_inventory.setOnClickListener(this);
		btn_icodesli_connect.setOnClickListener(this);
		btn_icodesli_tagInfo.setOnClickListener(this);
		btn_icodesli_disconnect.setOnClickListener(this);
		btn_icodesli_read.setOnClickListener(this);
		btn_icodesli_write.setOnClickListener(this);
		btn_icodesli_lockBlk.setOnClickListener(this);
		btn_icodesli_setDsfid.setOnClickListener(this);
		btn_icodesli_lockDsfid.setOnClickListener(this);
		btn_icodesli_setAFI.setOnClickListener(this);
		btn_icodesli_lockAFI.setOnClickListener(this);
		btn_icodesli_enEAS.setOnClickListener(this);
		btn_icodesli_disEnEAS.setOnClickListener(this);
		btn_icodesli_lockEas.setOnClickListener(this);
		btn_icodesli_checkEAS.setOnClickListener(this);
		btn_icodesli_setAnt.setOnClickListener(this);
		btn_icodesli_authenticate.setOnClickListener(this);
		btn_icodesli_updatePwb.setOnClickListener(this);
		btn_icodesli_enable64bitPwb.setOnClickListener(this);
		btn_icodesli_lockPwb.setOnClickListener(this);
		btn_icodesli_protectEasOrAfi.setOnClickListener(this);
		btn_icodesli_protectPage.setOnClickListener(this);
		btn_icodesli_lockPageState.setOnClickListener(this);
		sn_icodesli_connectMode.setSelection(1);

		ed_icodesli_blkData
				.setOnFocusChangeListener(new OnFocusChangeListener()
				{
					public void onFocusChange(View v, boolean hasFocus)
					{
						if (ed_icodesli_blkData.hasFocus())
						{
							ed_icodesli_blkData.setError(null, null);
						}
					}
				});

		ed_icodesli_dsfid.setOnFocusChangeListener(new OnFocusChangeListener()
		{
			public void onFocusChange(View arg0, boolean arg1)
			{
				if (ed_icodesli_dsfid.hasFocus())
				{
					ed_icodesli_dsfid.setError(null, null);
				}
			}
		});

		ed_icodesli_afi.setOnFocusChangeListener(new OnFocusChangeListener()
		{
			public void onFocusChange(View arg0, boolean arg1)
			{
				if (ed_icodesli_afi.hasFocus())
				{
					ed_icodesli_afi.setError(null, null);
				}
			}
		});

		m_adaUidList = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_dropdown_item, uidList);
		sn_icodesli_uidList.setAdapter(m_adaUidList);

		ArrayList<CharSequence> blkAddrList = new ArrayList<CharSequence>();
		ArrayList<CharSequence> blkNumList = new ArrayList<CharSequence>();
		ArrayAdapter<CharSequence> m_adaBlkAddrList = null;
		ArrayAdapter<CharSequence> m_adaBlkNumList = null;
		for (int i = 0; i < 80; i++)
		{
			blkAddrList.add(i + "");
			blkNumList.add((i + 1) + "");
		}
		m_adaBlkAddrList = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_dropdown_item, blkAddrList);
		m_adaBlkNumList = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_dropdown_item, blkNumList);
		sn_icodesli_blkAddr.setAdapter(m_adaBlkAddrList);
		sn_icodesli_blkNum.setAdapter(m_adaBlkNumList);
		sn_icodesli_blkAddr.setSelection(0);
		sn_icodesli_blkNum.setSelection(0);

		ArrayList<CharSequence> pwbIdList = new ArrayList<CharSequence>();
		pwbIdList.add("Read");
		pwbIdList.add("Write");
		pwbIdList.add("Private");
		pwbIdList.add("Destroy");
		pwbIdList.add("EAS/AFI");
		ArrayAdapter<CharSequence> m_adaPwbIdList = null;
		m_adaPwbIdList = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_dropdown_item, pwbIdList);
		sn_icodesli_pwbid.setAdapter(m_adaPwbIdList);
		sn_icodesli_pwbid.setSelection(0);

		ArrayList<CharSequence> easorAfiList = new ArrayList<CharSequence>();
		ArrayAdapter<CharSequence> m_adaEasOrAfi = null;
		easorAfiList.add("EAS");
		easorAfiList.add("AFI");
		m_adaEasOrAfi = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_dropdown_item, easorAfiList);
		sn_icodesli_protectEasOrAfi.setAdapter(m_adaEasOrAfi);
		sn_icodesli_protectEasOrAfi.setSelection(0);

		sn_icodesli_protectBlkAddr.setAdapter(m_adaBlkAddrList);
		sn_icodesli_protectBlkAddr.setSelection(0);

		ArrayList<CharSequence> pageStateL = new ArrayList<CharSequence>();
		ArrayAdapter<CharSequence> m_adaPageStateL = null;
		pageStateL.add("Public");
		pageStateL.add("RW protected by the R password");
		pageStateL.add("W protected by the W password");
		pageStateL
				.add("R protected by the R password and W protected by the RW password");
		m_adaPageStateL = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_dropdown_item, pageStateL);
		sn_icodesli_page_L_state.setAdapter(m_adaPageStateL);
		sn_icodesli_page_L_state.setSelection(0);

		ArrayList<CharSequence> pageStateH = new ArrayList<CharSequence>();
		ArrayAdapter<CharSequence> m_adaPageStateH = null;
		pageStateH.add("Public");
		pageStateH.add("RW protected by the R password");
		pageStateH.add("W protected by the W password");
		pageStateH
				.add("R protected by the R password and W protected by the RW password");
		m_adaPageStateH = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_dropdown_item, pageStateH);
		sn_icodesli_page_H_state.setAdapter(m_adaPageStateH);
		sn_icodesli_page_H_state.setSelection(0);

		// Load antenna configuration.
		mAntCnt = MainActivity.m_reader.RDR_GetAntennaInterfaceCount();
		ArrayList<CharSequence> antList = new ArrayList<CharSequence>();
		for (int i = 0; i < mAntCnt; i++)
		{
			int strIdx = i + 1;
			antList.add(strIdx + "");
		}
		ArrayAdapter<CharSequence> m_adaAntList = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_dropdown_item, antList);
		sn_icodesli_antList.setAdapter(m_adaAntList);

		if (mAntCnt <= 1)
		{
			findViewById(R.id.tv_icodesli_ant).setVisibility(View.GONE);
			sn_icodesli_antList.setVisibility(View.GONE);
			btn_icodesli_setAnt.setVisibility(View.GONE);
		}

		UiVisible(false);
	}

	private void UiInventory()
	{
		uidList.clear();
		m_adaUidList.clear();
		int iret = -1;
		byte[] mAntId = null;
		if (mAntCnt > 1)
		{
			mAntId = new byte[1];
			mAntId[0] = (byte) (sn_icodesli_antList.getSelectedItemPosition() + 1);
		}

		Object hInvenParamSpecList = ADReaderInterface
				.RDR_CreateInvenParamSpecList();
		ISO15693Interface.ISO15693_CreateInvenParam(hInvenParamSpecList,
				(byte) 0, false, (byte) 0, (byte) 0);

		iret = MainActivity.m_reader.RDR_TagInventory(RfidDef.AI_TYPE_NEW,
				mAntId, 0, hInvenParamSpecList);
		if (iret != ApiErrDefinition.NO_ERROR)
		{
			m_adaUidList.notifyDataSetChanged();
			return;
		}
		Object tagReport = MainActivity.m_reader
				.RDR_GetTagDataReport(RfidDef.RFID_SEEK_FIRST);
		while (tagReport != null)
		{
			ISO15693Tag tagData = new ISO15693Tag();
			iret = ISO15693Interface.ISO15693_ParseTagDataReport(tagReport,
					tagData);
			if (iret == ApiErrDefinition.NO_ERROR)
			{
				String uidStr = GFunction.encodeHexStr(tagData.uid);
				uidList.add(uidStr);

				// save the tag type.
				boolean bTagExist = false;
				Enumeration<String> keys = m_uid_tagType.keys();
				while (keys.hasMoreElements())
				{
					String keyData = keys.nextElement();
					if (keyData.equals(keyData))
					{
						bTagExist = true;
						break;
					}
				}
				if (!bTagExist)
				{
					m_uid_tagType.put(uidStr, tagData.tag_id);
				}
			}
			tagReport = MainActivity.m_reader
					.RDR_GetTagDataReport(RfidDef.RFID_SEEK_NEXT);
		}
		m_adaUidList.notifyDataSetChanged();
	}

	private void UiVisible(boolean bConnect)
	{
		btn_inventory.setEnabled(!bConnect);
		btn_icodesli_connect.setEnabled(!bConnect);
		btn_icodesli_tagInfo.setEnabled(bConnect);
		btn_icodesli_disconnect.setEnabled(bConnect);
		sn_icodesli_uidList.setEnabled(!bConnect);
		sn_icodesli_connectMode.setEnabled(!bConnect);
		sn_icodesli_blkAddr.setEnabled(bConnect);
		sn_icodesli_blkNum.setEnabled(bConnect);
		ed_icodesli_blkData.setEnabled(bConnect);
		btn_icodesli_read.setEnabled(bConnect);
		btn_icodesli_write.setEnabled(bConnect);
		btn_icodesli_lockBlk.setEnabled(bConnect);
		ed_icodesli_dsfid.setEnabled(bConnect);
		btn_icodesli_setDsfid.setEnabled(bConnect);
		btn_icodesli_lockDsfid.setEnabled(bConnect);
		btn_icodesli_setAFI.setEnabled(bConnect);
		btn_icodesli_lockAFI.setEnabled(bConnect);
		btn_icodesli_enEAS.setEnabled(bConnect);
		btn_icodesli_disEnEAS.setEnabled(bConnect);
		btn_icodesli_checkEAS.setEnabled(bConnect);
		btn_icodesli_lockEas.setEnabled(bConnect);
		ed_icodesli_afi.setEnabled(bConnect);
		sn_icodesli_pwbid.setEnabled(bConnect);
		ed_icodesli_pwb.setEnabled(bConnect);
		btn_icodesli_authenticate.setEnabled(bConnect);
		btn_icodesli_updatePwb.setEnabled(bConnect);
		btn_icodesli_enable64bitPwb.setEnabled(bConnect);
		btn_icodesli_lockPwb.setEnabled(bConnect);
		btn_icodesli_protectEasOrAfi.setEnabled(bConnect);
		sn_icodesli_protectBlkAddr.setEnabled(bConnect);
		sn_icodesli_page_L_state.setEnabled(bConnect);
		sn_icodesli_page_H_state.setEnabled(bConnect);
		btn_icodesli_protectPage.setEnabled(bConnect);
		btn_icodesli_lockPageState.setEnabled(bConnect);
		sn_icodesli_protectEasOrAfi.setEnabled(bConnect);
	}

	private void UiSetAnt()
	{
		byte mAntId = (byte) (sn_icodesli_antList.getSelectedItemPosition() + 1);
		int iret = MainActivity.m_reader.RDR_SetAcessAntenna(mAntId);
		if (iret == ApiErrDefinition.NO_ERROR)
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage(getString(R.string.tx_msg_SetAnt_OK))
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
		}
		else
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage(getString(R.string.tx_msg_SetAnt_fail))
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
		}
	}

	private void UiConnect()
	{
		byte connectMode = 1;
		int idx = (int) sn_icodesli_connectMode.getSelectedItemId();
		switch (idx)
		{
			case 0:
				connectMode = 0;// none address mode
				break;
			case 1:
				connectMode = 1;// address mode
				break;
			default:
				break;
		}

		byte connectUid[] = null;
		String strUid = "";
		if (sn_icodesli_uidList.getCount() > 0)
		{
			strUid = sn_icodesli_uidList.getSelectedItem().toString();
			if (sn_icodesli_uidList.getSelectedItem() != null)
			{
				connectUid = GFunction.decodeHex(strUid);
			}
		}

		if (connectMode == 1 && connectUid == null)
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage(getString(R.string.tx_msg_uidNoNull))
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		}

		long mTayType = RfidDef.RFID_ISO15693_PICC_ICODE_SLI_ID;
		if (connectMode != 0)
		{
			long type = m_uid_tagType.get(strUid);
			if (type > 0)
			{
				mTayType = type;
			}
		}

		int iret = mTag.ISO15693_Connect(MainActivity.m_reader, mTayType,
				connectMode, connectUid);

		if (iret != ApiErrDefinition.NO_ERROR)
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage(getString(R.string.tx_msg_operate_fail))
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		}

		if (connectMode == 0)
		{
			// If the connected mode is "address mode",we must reset the tag
			// here.
			iret = mTag.ISO15693_Reset();
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

		UiVisible(true);
	}

	@SuppressLint("DefaultLocale")
	private void UiGetTagInfo()
	{
		byte infoUid[] = new byte[8];
		byte dsfid[] = new byte[1];
		byte afi[] = new byte[1];
		long blkSize[] = new long[1] ;
		long numOfBloks[] = new long[1] ;
		byte icRef[] = new byte[1];
		int iret = mTag.ISO15693_GetSystemInfo(infoUid, dsfid, afi, blkSize,
				numOfBloks, icRef);
		if (iret != ApiErrDefinition.NO_ERROR)
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage(
							getString(R.string.tx_msg_getTagInfo_fail) + iret)
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
		}
		else
		{
			String sUid = GFunction.encodeHexStr(infoUid);
			String tagInfo = String
					.format("Uid:%s\nDSFID:0x%02X\nAFI:0x%02X\nBlkSize:%d\nNumOfBloks:%d\nIcRef:0x%02X",
							sUid, dsfid[0], afi[0], blkSize[0], numOfBloks[0], icRef[0]);
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage(tagInfo)
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
		}
	}

	@SuppressLint("UseValueOf")
	private void UiReadBlock()
	{
		int blkAddr = (int) sn_icodesli_blkAddr.getSelectedItemPosition();
		int numOfBlksToRead = (int) (sn_icodesli_blkNum
				.getSelectedItemPosition() + 1);
		Integer numOfBlksRead = new Integer(0);
		Long bytesBlkDatRead = new Long(0);
		byte bufBlocks[] = new byte[4 * numOfBlksToRead];
		int iret = mTag.ISO15693_ReadMultiBlocks(false, blkAddr,
				numOfBlksToRead, numOfBlksRead, bufBlocks, bytesBlkDatRead);
		if (iret != ApiErrDefinition.NO_ERROR)
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage(getString(R.string.tx_msg_readBlk_fail) + iret)
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
		}
		String strData = GFunction.encodeHexStr(bufBlocks);
		ed_icodesli_blkData.setText(strData);
	}

	private void UiWriteBlock()
	{
		String strData = ed_icodesli_blkData.getText().toString();
		byte byData[] = GFunction.decodeHex(strData);
		if (byData == null)
		{
			ed_icodesli_blkData
					.setError(getString(R.string.tx_msg_inputHexString));
			return;
		}
		int blkNum = sn_icodesli_blkNum.getSelectedItemPosition() + 1;
		int blkAddr = sn_icodesli_blkAddr.getSelectedItemPosition();
		if (blkNum * 4 != byData.length)
		{
			ed_icodesli_blkData.setError(getString(R.string.tx_msg_dataLenErr));
			return;
		}

		int iret = mTag.ISO15693_WriteMultipleBlocks(blkAddr, blkNum, byData);
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

	private void UiLockBlock()
	{
		int blkAddr = sn_icodesli_blkAddr.getSelectedItemPosition();
		int blkCnt = sn_icodesli_blkNum.getSelectedItemPosition() + 1;
		int iret = mTag.ISO15693_LockMultipleBlocks(blkAddr, blkCnt);
		String srtResult;
		if (iret == ApiErrDefinition.NO_ERROR)
		{
			srtResult = getString(R.string.tx_msg_lockBlk_ok);// "锁数据块成功!";
		}
		else
		{
			srtResult = getString(R.string.tx_msg_lockBlk_fail) + iret;
		}
		new AlertDialog.Builder(this).setTitle("").setMessage(srtResult)
				.setPositiveButton(getString(R.string.tx_msg_certain), null)
				.show();
	}

	private void UiWriteDsfid()
	{
		String dsfidStr = ed_icodesli_dsfid.getText().toString();
		byte dsfid[] = GFunction.decodeHex(dsfidStr);
		if (dsfid == null)
		{
			ed_icodesli_dsfid.setError(getString(R.string.tx_msg_inputDsfid));
			return;
		}
		int iret = mTag.ISO15693_WriteDSFID(dsfid[0]);
		String srtResult;
		if (iret == ApiErrDefinition.NO_ERROR)
		{
			srtResult = getString(R.string.tx_msg_setDsfid_ok);// "设置DSFID成功!";
		}
		else
		{
			srtResult = getString(R.string.tx_msg_setDsfid_fail) + iret;
		}
		new AlertDialog.Builder(this).setTitle("").setMessage(srtResult)
				.setPositiveButton(getString(R.string.tx_msg_certain), null)
				.show();
	}

	private void UiWriteAFI()
	{
		String afiStr = ed_icodesli_afi.getText().toString();
		byte afi[] = GFunction.decodeHex(afiStr);
		if (afi == null)
		{
			ed_icodesli_afi.setError(getString(R.string.tx_msg_inputAFI));
			return;
		}
		int iret = mTag.ISO15693_WriteAFI(afi[0]);
		String srtResult;
		if (iret == ApiErrDefinition.NO_ERROR)
		{
			srtResult = getString(R.string.tx_msg_setAfi_ok);// "设置AFI成功!";
		}
		else
		{
			srtResult = getString(R.string.tx_msg_setAfi_fail) + iret;
		}
		new AlertDialog.Builder(this).setTitle("").setMessage(srtResult)
				.setPositiveButton(getString(R.string.tx_msg_certain), null)
				.show();
	}

	private void UiLockDsfid()
	{
		int iret = mTag.ISO15693_LockDSFID();
		String srtResult;
		if (iret == ApiErrDefinition.NO_ERROR)
		{
			srtResult = getString(R.string.tx_msg_lockDsfid_ok);
		}
		else
		{
			srtResult = getString(R.string.tx_msg_lockDsfid_fail) + iret;
		}
		new AlertDialog.Builder(this).setTitle("").setMessage(srtResult)
				.setPositiveButton(getString(R.string.tx_msg_certain), null)
				.show();
	}

	private void UiEnableEAS()
	{
		int iret = mTag.NXPICODESLI_EableEAS();
		String srtResult;
		if (iret == ApiErrDefinition.NO_ERROR)
		{
			srtResult = getString(R.string.tx_msg_enEAS_ok);// "使能EAS成功!";
		}
		else
		{
			srtResult = getString(R.string.tx_msg_enEAS_fail) + iret;
		}
		new AlertDialog.Builder(this).setTitle("").setMessage(srtResult)
				.setPositiveButton(getString(R.string.tx_msg_certain), null)
				.show();
	}

	private void UiDisableEAS()
	{
		int iret = mTag.NXPICODESLI_DisableEAS();
		String srtResult;
		if (iret == ApiErrDefinition.NO_ERROR)
		{
			srtResult = getString(R.string.tx_msg_disEAS_ok);
		}
		else
		{
			srtResult = getString(R.string.tx_msg_disEAS_fail);
		}
		new AlertDialog.Builder(this).setTitle("").setMessage(srtResult)
				.setPositiveButton(getString(R.string.tx_msg_certain), null)
				.show();
	}

	private void UiLockEAS()
	{
		int iret = mTag.NXPICODESLI_LockEAS();
		String srtResult;
		if (iret == ApiErrDefinition.NO_ERROR)
		{
			srtResult = getString(R.string.tx_msg_lockEAS_ok);
		}
		else
		{
			srtResult = getString(R.string.tx_msg_lockEAS_fail) + iret;
		}
		new AlertDialog.Builder(this).setTitle("").setMessage(srtResult)
				.setPositiveButton(getString(R.string.tx_msg_certain), null)
				.show();
	}

	private void UiLockAFI()
	{
		int iret = mTag.ISO15693_LockAFI();
		String srtResult;
		if (iret == ApiErrDefinition.NO_ERROR)
		{
			srtResult = getString(R.string.tx_msg_lockAFI_ok);
		}
		else
		{
			srtResult = getString(R.string.tx_msg_lockAFI_fail) + iret;
		}
		new AlertDialog.Builder(this).setTitle("").setMessage(srtResult)
				.setPositiveButton(getString(R.string.tx_msg_certain), null)
				.show();
	}

	private void UiEasCheck()
	{
		Byte easFlg = new Byte((byte) 0);
		int iret = mTag.NXPICODESLI_EASCheck(easFlg);
		if (iret != ApiErrDefinition.NO_ERROR)
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage(getString(R.string.tx_msg_checkEAS_fail) + iret)
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
			return;
		}
		if (easFlg.byteValue() == 0)
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage(getString(R.string.tx_msg_EasClosed))
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
		}
		else
		{
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage(getString(R.string.tx_msg_EasOpen))
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
		}
	}

	private void UiAuthenticatePassword()
	{
		byte pwdId = 0;
		int idx = sn_icodesli_pwbid.getSelectedItemPosition();
		switch (idx)
		{
			case 0:
				pwdId = 0x01;
				break;
			case 1:
				pwdId = 0x02;
				break;
			case 2:
				pwdId = 0x04;
				break;
			case 3:
				pwdId = 0x08;
				break;
			case 4:
				pwdId = 0x10;
				break;
			default:
				return;
		}
		String strPwb = ed_icodesli_pwb.getText().toString();
		if (strPwb.length() != 8)
		{
			new AlertDialog.Builder(this).setTitle("")
					.setMessage("Password error.")
					.setPositiveButton("OK", null).show();
			return;
		}
		long mPassord = Long.parseLong(strPwb, 16);
		String strErrInfo = "Authenticate the password successfully";
		int iret = mTag.NXPICODESLI_GetRandomAndSetPassword(pwdId, mPassord);

		if (iret != ApiErrDefinition.NO_ERROR)
		{
			strErrInfo = "Fail to authenticate the password.";
		}
		new AlertDialog.Builder(this).setTitle("").setMessage(strErrInfo)
				.setPositiveButton("OK", null).show();
	}

	private void UiUpdatePassword()
	{
		byte pwdId = 0;
		int idx = sn_icodesli_pwbid.getSelectedItemPosition();
		switch (idx)
		{
			case 0:
				pwdId = 0x01;
				break;
			case 1:
				pwdId = 0x02;
				break;
			case 2:
				pwdId = 0x04;
				break;
			case 3:
				pwdId = 0x08;
				break;
			case 4:
				pwdId = 0x10;
				break;
			default:
				return;
		}
		String strPwb = ed_icodesli_pwb.getText().toString();
		if (strPwb.length() != 8)
		{
			new AlertDialog.Builder(this).setTitle("")
					.setMessage("Password error.")
					.setPositiveButton("OK", null).show();
			return;
		}
		long mPassord = Long.parseLong(strPwb, 16);

		String strErrInfo = "Update the password successfully";
		int iret = mTag.NXPICODESLI_WritePassword(pwdId, mPassord);

		if (iret != ApiErrDefinition.NO_ERROR)
		{
			strErrInfo = "Fail to update the password.";
		}
		new AlertDialog.Builder(this).setTitle("").setMessage(strErrInfo)
				.setPositiveButton("OK", null).show();
	}

	private void UiEnable64BitPwd()
	{
		String strErrInfo = "Enable 64-bit password successfully";
		int iret = mTag.NXPICODESLI_Enable64BitPwd();

		if (iret != ApiErrDefinition.NO_ERROR)
		{
			strErrInfo = "Fail to enable 64-bit password the password.";
		}
		new AlertDialog.Builder(this).setTitle("").setMessage(strErrInfo)
				.setPositiveButton("OK", null).show();
	}

	private void UiLockPassword()
	{
		byte pwdId = 0;
		int idx = sn_icodesli_pwbid.getSelectedItemPosition();
		switch (idx)
		{
			case 0:
				pwdId = 0x01;
				break;
			case 1:
				pwdId = 0x02;
				break;
			case 2:
				pwdId = 0x04;
				break;
			case 3:
				pwdId = 0x08;
				break;
			case 4:
				pwdId = 0x10;
				break;
			default:
				return;
		}

		String strErrInfo = "Lock password successfully";
		int iret = mTag.NXPICODESLI_LockPassword(pwdId);

		if (iret != ApiErrDefinition.NO_ERROR)
		{
			strErrInfo = "Failed to lock password the password.";
		}
		new AlertDialog.Builder(this).setTitle("").setMessage(strErrInfo)
				.setPositiveButton("OK", null).show();
	}

	private void UiProtectEasOrAfi()
	{
		byte bandType = 0;
		int idx = sn_icodesli_protectEasOrAfi.getSelectedItemPosition();
		switch (idx)
		{
			case 0:
				bandType = 0;
				break;
			case 1:
				bandType = 1;
				break;
			default:
				return;
		}

		String strErrInfo = "Protect EAS or AFI successfully";
		int iret = mTag.NXPICODESLI_PasswordProtect(bandType);

		if (iret != ApiErrDefinition.NO_ERROR)
		{
			strErrInfo = "Failed to Protect EAS or AFI.";
		}
		new AlertDialog.Builder(this).setTitle("").setMessage(strErrInfo)
				.setPositiveButton("OK", null).show();
	}

	private void UiProtectPage()
	{
		byte PPPointer = (byte) sn_icodesli_protectBlkAddr
				.getSelectedItemPosition();
		byte HPSta = (byte) sn_icodesli_page_H_state.getSelectedItemPosition();
		byte LPSta = (byte) sn_icodesli_page_L_state.getSelectedItemPosition();
		byte PageSta = (byte) ((LPSta & 0x0f) | (HPSta << 4 & 0xf0));

		String strErrInfo = "Protect pages successfully";
		int iret = mTag.NXPICODESLI_ProtectPage(PPPointer, PageSta);

		if (iret != ApiErrDefinition.NO_ERROR)
		{
			strErrInfo = "Failed to Protect pages.";
		}
		new AlertDialog.Builder(this).setTitle("").setMessage(strErrInfo)
				.setPositiveButton("OK", null).show();
	}

	private void UiLockPageState()
	{
		byte pageAddr = (byte) sn_icodesli_protectBlkAddr
				.getSelectedItemPosition();
		String strErrInfo = "Lock pages state successfully";
		int iret = mTag.NXPICODESLI_LockPageProtection(pageAddr);

		if (iret != ApiErrDefinition.NO_ERROR)
		{
			strErrInfo = "Failed to lock pages state.";
		}
		new AlertDialog.Builder(this).setTitle("").setMessage(strErrInfo)
				.setPositiveButton("OK", null).show();
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_icodesli_setAnt:
				UiSetAnt();
				break;
			case R.id.btn_icodesli_inventory:
				UiInventory();
				break;
			case R.id.btn_icodesli_connect:
				UiConnect();
				break;
			case R.id.btn_icodesli_tagInfo:
				UiGetTagInfo();
				break;
			case R.id.btn_icodesli_disconnect:
				mTag.ISO15693_Disconnect();
				UiVisible(false);
				break;
			case R.id.btn_icodesli_read:
				UiReadBlock();
				break;
			case R.id.btn_icodesli_write:
				UiWriteBlock();
				break;
			case R.id.btn_icodesli_lockBlk:
				UiLockBlock();
				break;
			case R.id.btn_icodesli_setDsfid:
				UiWriteDsfid();
				break;
			case R.id.btn_icodesli_lockDsfid:
				UiLockDsfid();
				break;
			case R.id.btn_icodesli_setAFI:
				UiWriteAFI();
				break;
			case R.id.btn_icodesli_lockAFI:
				UiLockAFI();
				break;
			case R.id.btn_icodesli_enEAS:
				UiEnableEAS();
				break;
			case R.id.btn_icodesli_disEnEAS:
				UiDisableEAS();
				break;
			case R.id.btn_icodesli_lockEas:
				UiLockEAS();
				break;
			case R.id.btn_icodesli_checkEAS:
				UiEasCheck();
				break;
			case R.id.btn_icodesli_authenticate:
				UiAuthenticatePassword();
				break;
			case R.id.btn_icodesli_updatePwb:
				UiUpdatePassword();
				break;
			case R.id.btn_icodesli_enable64bitPwb:
				UiEnable64BitPwd();
				break;
			case R.id.btn_icodesli_lockPwb:
				UiLockPassword();
				break;
			case R.id.btn_icodesli_protectEasOrAfi:
				UiProtectEasOrAfi();
				break;
			case R.id.btn_icodesli_protectPage:
				UiProtectPage();
				break;
			case R.id.btn_icodesli_lockPageState:
				UiLockPageState();
				break;
			default:
				break;
		}
	}
}
