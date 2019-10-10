package com.example.AnReaderDemo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.example.ReaderDemo.R;
import com.rfid.api.ADReaderInterface;
import com.rfid.api.BluetoothCfg;
import com.rfid.api.GFunction;
import com.rfid.api.ISO14443AInterface;
import com.rfid.api.ISO14443ATag;
import com.rfid.api.ISO15693Interface;
import com.rfid.api.ISO15693Tag;
import com.rfid.def.ApiErrDefinition;
import com.rfid.def.RfidDef;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class MainActivity extends Activity implements OnClickListener
{
	private static final int INVENTORY_MSG = 1;
	private static final int GETSCANRECORD = 2;
	private static final int INVENTORY_FAIL_MSG = 4;
	private static final int THREAD_END = 3;
	private TabHost myTabhost = null;
	private Spinner sn_commType = null;// Connector
	private Spinner sn_devName = null;// Device type
	private EditText ed_ipAddr = null;// IP
	private EditText ed_port = null;// Port
	private Spinner sn_bluetooth = null;// bluetooth
	private Spinner sn_comName = null;// com
	private Spinner sn_comBaud = null;
	private Spinner sn_comFrame = null;
	private Button btn_connect = null;// connect tag
	private Button btn_disconnect = null;// disconnect
	private Button btn_getDevInfo = null;// get device information
	private Button btn_setTime = null;// set time
	private Button btn_startInventory = null;// start inventory
	private Button btn_stopInventory = null;// stop inventory
	private Button btn_setInventoryPara = null;// set para of inventory
	private Button btn_startScanf = null;// Start scanf
	private Button btn_stopScanf = null;// stop scanf
	private Button btn_openRF = null;// Open RF
	private Button btn_closeRF = null;// Close RF
	private Button btn_clearInventoryRecord = null;// Clear inventory list
	private Button btn_clearScanfRecordList = null;// clear scanf record
	private Spinner sn_RfPower = null;// RF Power
	private Button btn_readPower = null;// Get RF Power
	private Button btn_setPower = null;// Set RF Power
	private Button btn_loadDefault = null;// Reset
	private Spinner sn_overflow_time = null;// overflow_time
	private Button btn_read_overflow_time = null;// get overflow_time
	private Button btn_write_overflow_time = null;// Set overflow_time

	private ListView list_inventory_record = null;// inventory list
	private ListView list_scanf_record = null;// scanf record list
	private ListView list_tag_name = null;// tag name

	private TextView tv_inventoryInfo = null;
	private TextView tv_scanRecordInfo = null;
	private List<InventoryReport> inventoryList = new ArrayList<InventoryReport>();
	private List<ScanReport> scanfReportList = new ArrayList<ScanReport>();
	private InventoryAdapter inventoryAdapter = null;
	private ScanAdapter scanfAdapter = null;
	static ADReaderInterface m_reader = new ADReaderInterface();
	static int INVENTORY_REQUEST_CODE = 1;// requestCode
	private boolean bOnlyReadNew = false;
	private boolean bMathAFI = false;
	private byte mAFIVal = 0x00;
	private boolean bBuzzer = true;
	private boolean bUseISO15693 = false;
	private boolean bUseISO14443A = false;
	private long mAntCfg = 0x000000;
	private boolean bRealShowTag = false;
	
	private long mLoopCnt = 0;

	private Thread m_inventoryThrd = null;// The thread of inventory
	private Thread m_getScanRecordThrd = null;// The thead of scanf the record.
												// Only for rpan device.

	private boolean isLoadScanfMode = false;

	private int[] layRes = { R.id.tab_reader, R.id.tab_command,
			R.id.tab_inventory, R.id.tab_TagTypeList, R.id.tab_ScanRecord };
	private String[] layTittle = null; // { "设备", "命令", "盘点", "读写", "扫描" };

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		layTittle = new String[] { getString(R.string.tx_tab_device),
				getString(R.string.tx_tab_command),
				getString(R.string.tx_tab_inventory),
				getString(R.string.tx_tab_operate),
				getString(R.string.tx_tab_scanf) };

		sn_commType = (Spinner) findViewById(R.id.sn_commType);
		sn_devName = (Spinner) findViewById(R.id.sn_devType);
		ed_ipAddr = (EditText) findViewById(R.id.ed_ipAddr);
		ed_port = (EditText) findViewById(R.id.ed_port);
		myTabhost = (TabHost) findViewById(R.id.tabhost);
		list_inventory_record = (ListView) findViewById(R.id.list_inventory_record);
		list_scanf_record = (ListView) findViewById(R.id.list_scanf_record);
		sn_bluetooth = (Spinner) findViewById(R.id.sn_blueName);
		sn_comName = (Spinner) findViewById(R.id.sn_comName);
		sn_comBaud = (Spinner) findViewById(R.id.sn_comBaud);
		sn_comFrame = (Spinner) findViewById(R.id.sn_comFrame);
		btn_connect = (Button) findViewById(R.id.btn_connect);
		btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
		btn_getDevInfo = (Button) findViewById(R.id.btn_getDevInfo);
		btn_setTime = (Button) findViewById(R.id.btn_setTime);
		btn_startInventory = (Button) findViewById(R.id.btn_startInventory);
		btn_stopInventory = (Button) findViewById(R.id.btn_stopInventory);
		btn_setInventoryPara = (Button) findViewById(R.id.btn_paraInventory);
		btn_startScanf = (Button) findViewById(R.id.btn_startScanfRecord);
		btn_stopScanf = (Button) findViewById(R.id.btn_stopScanfRecord);
		btn_openRF = (Button) findViewById(R.id.btn_openRF);
		btn_closeRF = (Button) findViewById(R.id.btn_closeRF);
		sn_RfPower = (Spinner) findViewById(R.id.sn_powerVal);
		btn_readPower = (Button) findViewById(R.id.btn_readPower);
		btn_setPower = (Button) findViewById(R.id.btn_setPower);
		btn_clearInventoryRecord = (Button) findViewById(R.id.btn_clearList);
		btn_clearScanfRecordList = (Button) findViewById(R.id.btn_clearScanfRecordList);
		btn_loadDefault = (Button) findViewById(R.id.btn_loadDefault);
		tv_inventoryInfo = (TextView) findViewById(R.id.tv_inventoryInfo);
		tv_scanRecordInfo = (TextView) findViewById(R.id.tv_scanRecordInfo);
		list_tag_name = (ListView) findViewById(R.id.list_tagName);
		sn_overflow_time = (Spinner) findViewById(R.id.sn_overflow_time);
		btn_read_overflow_time = (Button) findViewById(R.id.btn_read_overflow_time);
		btn_write_overflow_time = (Button) findViewById(R.id.btn_write_overflow_time);
		
		sn_commType.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id)
			{
				// TODO Auto-generated method stub
				//MainActivity.com
				CommTypeChange();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
				// TODO Auto-generated method stub
				
			}
			
		});

		// Load page
		myTabhost.setup();
		for (int i = 0; i < layRes.length - 1; i++)
		{
			TabSpec myTab = myTabhost.newTabSpec("tab" + i);
			myTab.setIndicator(layTittle[i]);
			myTab.setContent(layRes[i]);
			myTabhost.addTab(myTab);
		}
		myTabhost.setCurrentTab(0);

		// Inventory list tittle
		ViewGroup InventorytableTitle = (ViewGroup) findViewById(R.id.inventorylist_title);
		InventorytableTitle.setBackgroundColor(Color.rgb(255, 100, 10));

		// Scanf list tittle
		ViewGroup ScanRecordTableTitle = (ViewGroup) findViewById(R.id.scan_record_list_tittle);
		ScanRecordTableTitle.setBackgroundColor(Color.rgb(53, 190, 106));

		scanfAdapter = new ScanAdapter(this, scanfReportList);
		list_scanf_record.setAdapter(scanfAdapter);

		inventoryAdapter = new InventoryAdapter(this, inventoryList);
		list_inventory_record.setAdapter(inventoryAdapter);

		final String[] tagName = new String[] { "ICODE SLI", "Mifare S50",
				"Mifare Ultralight" ,"ISO14443A-4 Transceive"};
		List<Map<String, Object>> tagNameListItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < tagName.length; i++)
		{
			Map<String, Object> map = new HashMap<String, Object>(); // 实例化Map对象
			map.put("image", R.drawable.arrow);
			map.put("title", tagName[i]);
			tagNameListItems.add(map); // 将map对象添加到List集合中
		}

		// 读写选项标签列表
		SimpleAdapter tagNamadapter = new SimpleAdapter(this, tagNameListItems,
				R.xml.tag_name_items, new String[] { "title", "image" },
				new int[] { R.id.tagListtitle, R.id.tagListimage }); // 创建SimpleAdapter
		list_tag_name.setAdapter(tagNamadapter);
		list_tag_name.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)
			{
				Intent intent = null;
				switch (arg2)
				{
				case 0:
					if (!m_reader
							.RDR_IsAirProtocolSupport(RfidDef.RFID_APL_ISO15693_ID))
					{
						Toast.makeText(MainActivity.this, "Unsupported",
								Toast.LENGTH_SHORT).show();
						return;
					}
					intent = new Intent(MainActivity.this,
							IcodesliTagActivity.class);
					startActivity(intent);
					break;
				case 1:
					if (!m_reader
							.RDR_IsAirProtocolSupport(RfidDef.RFID_APL_ISO14443A_ID))
					{
						Toast.makeText(MainActivity.this, "Unsupported",
								Toast.LENGTH_SHORT).show();
						return;
					}
					intent = new Intent(MainActivity.this,
							MifareS50TagActivity.class);
					startActivity(intent);
					break;
				case 2:
					if (!m_reader
							.RDR_IsAirProtocolSupport(RfidDef.RFID_APL_ISO14443A_ID))
					{
						Toast.makeText(MainActivity.this, "Unsupported",
								Toast.LENGTH_SHORT).show();
						return;
					}
					intent = new Intent(MainActivity.this,
							UltralightTagActivity.class);
					startActivity(intent);
					break;
				case 3:
					if (!m_reader
							.RDR_IsAirProtocolSupport(RfidDef.RFID_APL_ISO14443A_ID))
					{
						Toast.makeText(MainActivity.this, "Unsupported",
								Toast.LENGTH_SHORT).show();
						return;
					}
					intent = new Intent(MainActivity.this,
							ISO14443Ap4TransceiveActivity.class);
					startActivity(intent);
					break;
				default:
					break;
				}
			}
		});

		// 列举已配对的蓝牙设备
		// Get the bluetooth
		ArrayList<CharSequence> m_bluetoolNameList = null;
		ArrayAdapter<CharSequence> m_adaBluetoolName = null;
		m_bluetoolNameList = new ArrayList<CharSequence>();
		ArrayList<BluetoothCfg> m_blueList = ADReaderInterface
				.GetPairBluetooth();
		if (m_blueList != null)
		{
			for (BluetoothCfg bluetoolCfg : m_blueList)
			{
				m_bluetoolNameList.add(bluetoolCfg.GetName());
			}
		}

		m_adaBluetoolName = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_dropdown_item,
				m_bluetoolNameList);
		sn_bluetooth.setAdapter(m_adaBluetoolName);

		// 列举所有串口
		// Get the Serial port
		ArrayList<CharSequence> m_comNameList = new ArrayList<CharSequence>();
		ArrayList<CharSequence> m_comBaudList = new ArrayList<CharSequence>();
		ArrayList<CharSequence> m_comFrameList = new ArrayList<CharSequence>();
		ArrayAdapter<CharSequence> m_adaComName = null;
		ArrayAdapter<CharSequence> m_adaComBaud = null;
		ArrayAdapter<CharSequence> m_adaComFrame = null;

		String m_comList[] = ADReaderInterface.GetSerialPortPath();
		for (String s : m_comList)
		{
			m_comNameList.add(s);
		}

		m_comBaudList.add("9600");
		m_comBaudList.add("38400");
		m_comBaudList.add("57600");
		m_comBaudList.add("115200");

		m_comFrameList.add("8N1");
		m_comFrameList.add("8E1");
		m_comFrameList.add("8O1");

		m_adaComName = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_dropdown_item, m_comNameList);
		m_adaComBaud = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_dropdown_item, m_comBaudList);

		m_adaComFrame = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_dropdown_item, m_comFrameList);

		sn_comName.setAdapter(m_adaComName);
		sn_comBaud.setAdapter(m_adaComBaud);
		sn_comFrame.setAdapter(m_adaComFrame);

		ArrayList<CharSequence> overflowTime = new ArrayList<CharSequence>();
		for (int i = 0; i < 256; i++)
		{
			overflowTime.add(i + "");
		}
		ArrayAdapter<CharSequence> overflowTimeAda = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_dropdown_item,
				overflowTime);
		sn_overflow_time.setAdapter(overflowTimeAda);

		btn_connect.setOnClickListener(this);
		btn_disconnect.setOnClickListener(this);
		btn_getDevInfo.setOnClickListener(this);
		btn_setTime.setOnClickListener(this);
		btn_openRF.setOnClickListener(this);
		btn_closeRF.setOnClickListener(this);
		btn_startInventory.setOnClickListener(this);
		btn_stopInventory.setOnClickListener(this);
		btn_clearInventoryRecord.setOnClickListener(this);
		btn_startScanf.setOnClickListener(this);
		btn_stopScanf.setOnClickListener(this);
		btn_clearScanfRecordList.setOnClickListener(this);
		btn_setPower.setOnClickListener(this);
		btn_readPower.setOnClickListener(this);
		btn_loadDefault.setOnClickListener(this);
		btn_setInventoryPara.setOnClickListener(this);
		btn_read_overflow_time.setOnClickListener(this);
		btn_write_overflow_time.setOnClickListener(this);

		btn_connect.setEnabled(true);
		btn_disconnect.setEnabled(false);
		btn_getDevInfo.setEnabled(false);
		btn_setTime.setEnabled(false);
		btn_openRF.setEnabled(false);
		btn_closeRF.setEnabled(false);
		btn_startInventory.setEnabled(false);
		btn_stopInventory.setEnabled(false);
		btn_setInventoryPara.setEnabled(false);
		btn_startScanf.setEnabled(false);
		btn_stopScanf.setEnabled(false);
		sn_RfPower.setEnabled(false);
		btn_readPower.setEnabled(false);
		btn_setPower.setEnabled(false);
		btn_loadDefault.setEnabled(false);
		list_tag_name.setEnabled(false);
		sn_overflow_time.setEnabled(false);
		btn_write_overflow_time.setEnabled(false);
		btn_read_overflow_time.setEnabled(false);

		LoadActivityByHistory();
		
		CommTypeChange();
	}
	
	
	private void CommTypeChange()
	{
		LinearLayout bluetoothView = (LinearLayout) findViewById(R.id.group_bluetooth);
		RelativeLayout netView = (RelativeLayout) findViewById(R.id.group_net);
		RelativeLayout comView = (RelativeLayout) findViewById(R.id.group_com);
		switch (sn_commType.getSelectedItemPosition())
		{
		case 0:
			bluetoothView.setVisibility(View.VISIBLE);
			netView.setVisibility(View.GONE);
			comView.setVisibility(View.GONE);
			break;
		case 1:
			bluetoothView.setVisibility(View.GONE);
			netView.setVisibility(View.GONE);
			comView.setVisibility(View.VISIBLE);
			break;
		case 2:
			bluetoothView.setVisibility(View.GONE);
			netView.setVisibility(View.VISIBLE);
			comView.setVisibility(View.GONE);
			break;
		case 3:
			bluetoothView.setVisibility(View.GONE);
			netView.setVisibility(View.GONE);
			comView.setVisibility(View.GONE);
			break;
		case 4:
			bluetoothView.setVisibility(View.GONE);
			netView.setVisibility(View.GONE);
			comView.setVisibility(View.VISIBLE);
			break;
		default:
			bluetoothView.setVisibility(View.GONE);
			netView.setVisibility(View.GONE);
			comView.setVisibility(View.GONE);
			break;
		}
	}

	@Override
	protected void onDestroy()
	{
		// soundPool.unload(soundID);
		if (m_reader.isReaderOpen())
		{
			// 如果盘点标签线程正在运行，则关闭该线程
			// If thread of inventory is running,stop the thread before exit the
			// application.
			if (m_inventoryThrd != null && m_inventoryThrd.isAlive())
			{
				b_inventoryThreadRun = false;
				m_reader.RDR_SetCommuImmeTimeout();
				try
				{
					m_inventoryThrd.join();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}

			// 如果获取扫描记录线程正在运行，则关闭该线程
			// If thread of scannig is running,stop the thread before exit the
			// application.
			if (m_getScanRecordThrd != null && m_getScanRecordThrd.isAlive())
			{
				bGetScanRecordFlg = false;
				m_reader.RDR_SetCommuImmeTimeout();
				try
				{
					m_getScanRecordThrd.join();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			CloseDev();
		}

		super.onDestroy();
	}

	public void onClick(View v)
	{
		int nret = -1;
		String str = "";
		switch (v.getId())
		{
		case R.id.btn_connect:// connect the device
			OpenDev();
			break;
		case R.id.btn_disconnect:// disconnect the device
			CloseDev();
			break;
		case R.id.btn_getDevInfo:// get the informatin of the device
			GetInformation();
			break;
		case R.id.btn_setTime:
			SetSysTime();
			break;
		case R.id.btn_openRF:// Open RF
			nret = m_reader.RDR_OpenRFTransmitter((byte) 1);
			if (nret == ApiErrDefinition.NO_ERROR)
			{
				str = getString(R.string.tx_openRF_ok);
			}
			else
			{
				str = getString(R.string.tx_openRF_fail);
			}
			MessageBox(getString(R.string.tx_openRF), str);
			break;
		case R.id.btn_setPower:// Set the power
			byte powerIndex = (byte) (sn_RfPower.getSelectedItemPosition() + 1);
			nret = m_reader.RDR_SetRFPower(powerIndex);
			if (nret == ApiErrDefinition.NO_ERROR)
			{
				str = getString(R.string.tx_setPower_ok);
			}
			else
			{
				str = getString(R.string.tx_setPower_fail);
			}
			MessageBox(getString(R.string.tx_setPower), str);
			break;
		case R.id.btn_loadDefault:// reset
			nret = m_reader.RDR_LoadFactoryDefault();
			if (nret == ApiErrDefinition.NO_ERROR)
			{
				str = getString(R.string.tx_loadDefault_ok);
			}
			else
			{
				str = getString(R.string.tx_loadDefault_fail);
			}
			MessageBox(getString(R.string.tx_loadDefault), str);
			break;
		case R.id.btn_readPower:// Get RF Power
			Byte mPower = new Byte((byte) 0);
			nret = m_reader.RDR_GetRFPower(mPower);
			if (nret != ApiErrDefinition.NO_ERROR)
			{
				MessageBox(getString(R.string.tx_getRFPower),
						getString(R.string.tx_getRFPower_fail) + nret);
				break;
			}
			sn_RfPower.setSelection(mPower.byteValue() - 1);
			MessageBox(getString(R.string.tx_getRFPower),
					getString(R.string.tx_getRFPower_ok));
			break;
		case R.id.btn_closeRF:// 关闭射频
			nret = m_reader.RDR_CloseRFTransmitter();
			if (nret == ApiErrDefinition.NO_ERROR)
			{
				str = getString(R.string.tx_CloseRF_ok);// "关闭射频成功！";
			}
			else
			{
				str = getString(R.string.tx_CloseRF_fail);// "关闭射频失败";
			}
			MessageBox(getString(R.string.tx_CloseRF), str);
			break;
		case R.id.btn_startInventory:// 开始盘点
			btn_connect.setEnabled(false);
			btn_disconnect.setEnabled(false);
			btn_getDevInfo.setEnabled(false);
			btn_setTime.setEnabled(false);
			btn_openRF.setEnabled(false);
			btn_closeRF.setEnabled(false);
			btn_startInventory.setEnabled(false);
			btn_stopInventory.setEnabled(true);
			btn_setInventoryPara.setEnabled(false);
			btn_clearInventoryRecord.setEnabled(false);
			btn_startScanf.setEnabled(false);
			btn_stopScanf.setEnabled(false);
			sn_RfPower.setEnabled(false);
			btn_readPower.setEnabled(false);
			btn_setPower.setEnabled(false);
			btn_loadDefault.setEnabled(false);
			list_tag_name.setEnabled(false);
			sn_overflow_time.setEnabled(false);
			btn_read_overflow_time.setEnabled(false);
			btn_write_overflow_time.setEnabled(false);
			inventoryList.clear();
			inventoryAdapter.notifyDataSetChanged();
			tv_inventoryInfo.setText(getString(R.string.tv_inventoryInfo));
			m_inventoryThrd = new Thread(new InventoryThrd());
			m_inventoryThrd.start();
			break;
		case R.id.btn_stopInventory:// 停止盘点
			btn_stopInventory.setEnabled(false);
			m_reader.RDR_SetCommuImmeTimeout();
			b_inventoryThreadRun = false;
			break;
		case R.id.btn_paraInventory:// 盘点参数设置
			Intent intent = new Intent(this, InventoryParaActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean("bUseISO15693", this.bUseISO15693);
			bundle.putBoolean("bUseISO14443A", this.bUseISO14443A);
			bundle.putBoolean("OnlyReadNew", this.bOnlyReadNew);
			bundle.putBoolean("MathAFI", this.bMathAFI);
			bundle.putByte("AFI", this.mAFIVal);
			bundle.putBoolean("bBuzzer", this.bBuzzer);
			bundle.putLong("mAntCfg", mAntCfg);
			bundle.putBoolean("bRealShowTag", bRealShowTag);
			intent.putExtras(bundle);
			startActivityForResult(intent, INVENTORY_REQUEST_CODE);
			break;
		case R.id.btn_clearList:// 清空列表
			inventoryList.clear();
			this.inventoryAdapter.notifyDataSetChanged();
			tv_inventoryInfo.setText(getString(R.string.tx_inventory_sum0));
			break;
		case R.id.btn_startScanfRecord:// 开始扫描
			btn_connect.setEnabled(false);
			btn_disconnect.setEnabled(false);
			btn_getDevInfo.setEnabled(false);
			btn_setTime.setEnabled(false);
			btn_openRF.setEnabled(false);
			btn_closeRF.setEnabled(false);
			btn_startInventory.setEnabled(false);
			btn_stopInventory.setEnabled(false);
			btn_setInventoryPara.setEnabled(false);
			btn_clearInventoryRecord.setEnabled(false);
			btn_startScanf.setEnabled(false);
			btn_stopScanf.setEnabled(true);
			btn_clearScanfRecordList.setEnabled(false);
			sn_RfPower.setEnabled(false);
			btn_readPower.setEnabled(false);
			btn_setPower.setEnabled(false);
			btn_loadDefault.setEnabled(false);
			list_tag_name.setEnabled(false);
			sn_overflow_time.setEnabled(false);
			btn_read_overflow_time.setEnabled(false);
			btn_write_overflow_time.setEnabled(false);
			scanfReportList.clear();
			scanfAdapter.notifyDataSetChanged();
			tv_scanRecordInfo.setText(getString(R.string.tx_scanf_sum0));
			m_getScanRecordThrd = new Thread(new GetScanRecordThrd());
			m_getScanRecordThrd.start();
			break;
		case R.id.btn_stopScanfRecord:// 停止采集记录
			btn_stopScanf.setEnabled(false);
			bGetScanRecordFlg = false;
			break;
		case R.id.btn_clearScanfRecordList:// 清空扫描记录
			scanfReportList.clear();
			tv_scanRecordInfo.setText(getString(R.string.tx_scanf_sum0));
			this.scanfAdapter.notifyDataSetChanged();
			break;
		case R.id.btn_read_overflow_time:// 获取溢出时间
			Integer mTime = 0;
			nret = m_reader.RDR_GetOverflowTime(mTime);
			if (nret != ApiErrDefinition.NO_ERROR)
			{
				MessageBox(getString(R.string.tx_getOverflowTime),
						getString(R.string.tx_getOverflowTime_fail) + nret);
				break;
			}
			sn_overflow_time.setSelection(mTime.intValue());
			MessageBox(getString(R.string.tx_getOverflowTime),
					getString(R.string.tx_getOverflowTime_ok));
			break;
		case R.id.btn_write_overflow_time:// 设置溢出时间
			nret = m_reader.RDR_SetOverflowTime(sn_overflow_time
					.getSelectedItemPosition());
			if (nret != ApiErrDefinition.NO_ERROR)
			{
				MessageBox(getString(R.string.tx_setOverflowTime),
						getString(R.string.tx_setOverflowTime_fail) + nret);
				break;
			}
			MessageBox(getString(R.string.tx_setOverflowTime),
					getString(R.string.tx_setOverflowTime_ok) + nret);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (data == null)
		{
			return;
		}
		if (requestCode != INVENTORY_REQUEST_CODE)
		{
			return;
		}
		if (resultCode != InventoryParaActivity.RESULT_OK)
		{
			return;
		}
		Bundle bundle = data.getExtras();
		bUseISO15693 = bundle.getBoolean("bUseISO15693");
		bUseISO14443A = bundle.getBoolean("bUseISO14443A");
		bOnlyReadNew = bundle.getBoolean("OnlyReadNew");
		bMathAFI = bundle.getBoolean("MathAFI");
		mAFIVal = bundle.getByte("AFI");
		bBuzzer = bundle.getBoolean("bBuzzer");
		mAntCfg = bundle.getLong("mAntCfg");
		bRealShowTag = bundle.getBoolean("bRealShowTag");

		saveHistory("AntCfg", mAntCfg);
		saveHistory("bRealShowTag", bRealShowTag);
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void MessageBox(String sTittle, String msg)
	{
		new AlertDialog.Builder(this).setTitle(sTittle).setMessage(msg)
				.setPositiveButton(getString(R.string.tx_msg_certain), null)
				.show();
	}

	private void OpenDev()
	{
		String conStr = "";
		String devName = "";
		devName = sn_devName.getSelectedItem().toString();
		int mIdx = sn_commType.getSelectedItemPosition();
		if (mIdx == 0)
		{
			if (sn_bluetooth.getAdapter().isEmpty())
			{
				MessageBox(getString(R.string.tx_select_device),
						getString(R.string.tx_select_bluetooth));
				return;
			}
			String bluetoolName = sn_bluetooth.getSelectedItem().toString();
			if (bluetoolName == "")
			{
				MessageBox(getString(R.string.tx_select_device),
						getString(R.string.tx_select_bluetooth));
				return;
			}
			conStr = String.format("RDType=%s;CommType=BLUETOOTH;Name=%s",
					devName, bluetoolName);
		}
		else if (mIdx == 1)// 串口
		{
			if (sn_comName.getAdapter().isEmpty())
			{
				MessageBox(getString(R.string.tx_msg_selectCom),
						getString(R.string.tx_msg_selectComTip));
				return;
			}
			conStr = String
					.format("RDType=%s;CommType=COM;ComPath=%s;Baund=%s;Frame=%s;Addr=255",
							devName, sn_comName.getSelectedItem().toString(),
							sn_comBaud.getSelectedItem().toString(),
							sn_comFrame.getSelectedItem().toString());
		}
		else if (mIdx == 2)// (commTypeStr.equals(getString(R.string.tx_type_net)))//
							// 网络
		{
			String sRemoteIp = ed_ipAddr.getText().toString();
			String sRemotePort = ed_port.getText().toString();
			conStr = String.format(
					"RDType=%s;CommType=NET;RemoteIp=%s;RemotePort=%s",
					devName, sRemoteIp, sRemotePort);
		}
		else if (mIdx == 3)// (commTypeStr.equals("USB"))
		{
			// 注意：使用USB方式时，必须先要枚举所有USB设备
			// Note: Before using USB, you must enumerate all USB devices first.
			int usbCnt = ADReaderInterface.EnumerateUsb(this);	
			if (usbCnt <= 0)
			{
				Toast.makeText(this, getString(R.string.tx_msg_noUsb),
						Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (!ADReaderInterface.HasUsbPermission("0"))
			{
				Toast.makeText(this,
						getString(R.string.tx_msg_noUsbPermission),
						Toast.LENGTH_SHORT).show();
				ADReaderInterface.RequestUsbPermission("0");
				return;
			}
			conStr = String.format("RDType=%s;CommType=USB;Description=0",
					devName);
		}
		else if (mIdx == 4)// (commTypeStr.equals(getString(R.string.tx_type_usb_com)))
		{
			// Attention: Only support Z-TEK
			// 注意：目录只支持Z-TEK型号的USB转串口线
			int mUsbCnt = ADReaderInterface.EnumerateZTEK(this, 0x0403, 0x6001);
			if (mUsbCnt <= 0)
			{
				Toast.makeText(this, getString(R.string.tx_msg_noUsb),
						Toast.LENGTH_SHORT).show();
				return;
			}
			conStr = String
					.format("RDType=%s;CommType=Z-TEK;port=1;Baund=%s;Frame=%s;Addr=255",
							devName, sn_comBaud.getSelectedItem().toString(),
							sn_comFrame.getSelectedItem().toString());

		}
		else
		{
			return;
		}
		int iret = m_reader.RDR_Open(conStr);
		if (iret == ApiErrDefinition.NO_ERROR)
		{
			// ///////////////////////只有RPAN设备支持扫描模式/////////////////////////////
			if (!isLoadScanfMode && devName.equals("RPAN"))
			{
				findViewById(layRes[4]).setVisibility(View.VISIBLE);
				TabSpec myTab = myTabhost.newTabSpec("tab" + 4);
				myTab.setIndicator(layTittle[4]);
				myTab.setContent(layRes[4]);
				myTabhost.addTab(myTab);
				isLoadScanfMode = true;
			}

			// ///////////////////////////////////////////////////
			Toast.makeText(this, getString(R.string.tx_msg_openDev_ok),
					Toast.LENGTH_SHORT).show();
			SaveActivity();
			mAntCfg = GetHistoryLong("AntCfg");
			bRealShowTag = GetHistoryBool("bRealShowTag");
			sn_devName.setEnabled(false);
			sn_commType.setEnabled(false);
			sn_bluetooth.setEnabled(false);
			ed_ipAddr.setEnabled(false);
			ed_port.setEnabled(false);
			sn_comName.setEnabled(false);
			sn_comBaud.setEnabled(false);
			sn_comFrame.setEnabled(false);
			btn_connect.setEnabled(false);
			btn_disconnect.setEnabled(true);
			btn_getDevInfo.setEnabled(true);
			btn_setTime.setEnabled(true);
			btn_openRF.setEnabled(true);
			btn_closeRF.setEnabled(true);
			btn_startInventory.setEnabled(true);
			btn_stopInventory.setEnabled(false);
			btn_setInventoryPara.setEnabled(true);
			btn_clearInventoryRecord.setEnabled(true);
			btn_startScanf.setEnabled(true);
			btn_stopScanf.setEnabled(false);
			sn_RfPower.setEnabled(true);
			btn_readPower.setEnabled(true);
			btn_setPower.setEnabled(true);
			btn_loadDefault.setEnabled(true);
			list_tag_name.setEnabled(true);
			sn_overflow_time.setEnabled(true);
			btn_write_overflow_time.setEnabled(true);
			btn_read_overflow_time.setEnabled(true);
			sn_overflow_time.setEnabled(true);
		}
		else
		{
			Toast.makeText(this, getString(R.string.tx_msg_openDev_fail),
					Toast.LENGTH_SHORT).show();
		}
	}

	// 播放声音池声音
	// private void playVoice()
	// {
	// AudioManager am = (AudioManager) this
	// .getSystemService(Context.AUDIO_SERVICE);
	// float audioCurrentVolume = am
	// .getStreamVolume(AudioManager.STREAM_MUSIC);
	// soundPool
	// .play(soundID, audioCurrentVolume, audioCurrentVolume, 1, 0, 1);
	// VoicePlayer.GetInst(this).Play();
	// }

	private void CloseDev()
	{
		if (m_inventoryThrd != null && m_inventoryThrd.isAlive())
		{
			MessageBox("", getString(R.string.tx_msg_stopInventory_tip));
			return;
		}
		if (m_getScanRecordThrd != null && m_getScanRecordThrd.isAlive())
		{
			MessageBox("", getString(R.string.tx_msg_stopScanf_tip));
			return;
		}
		m_reader.RDR_Close();
		sn_devName.setEnabled(true);
		sn_commType.setEnabled(true);
		sn_bluetooth.setEnabled(true);
		ed_ipAddr.setEnabled(true);
		ed_port.setEnabled(true);
		sn_comName.setEnabled(true);
		sn_comBaud.setEnabled(true);
		sn_comFrame.setEnabled(true);
		btn_connect.setEnabled(true);
		btn_disconnect.setEnabled(false);
		btn_getDevInfo.setEnabled(false);
		btn_setTime.setEnabled(false);
		btn_openRF.setEnabled(false);
		btn_closeRF.setEnabled(false);
		btn_startInventory.setEnabled(false);
		btn_stopInventory.setEnabled(false);
		btn_setInventoryPara.setEnabled(false);
		btn_startScanf.setEnabled(false);
		btn_stopScanf.setEnabled(false);
		sn_RfPower.setEnabled(false);
		btn_readPower.setEnabled(false);
		btn_setPower.setEnabled(false);
		btn_loadDefault.setEnabled(false);
		list_tag_name.setEnabled(false);
		btn_write_overflow_time.setEnabled(false);
		btn_read_overflow_time.setEnabled(false);
		sn_overflow_time.setEnabled(false);
	}

	private void GetInformation()
	{
		int iret = -1;
		StringBuffer buffer = new StringBuffer();
		iret = m_reader.RDR_GetReaderInfor(buffer);
		if (iret == ApiErrDefinition.NO_ERROR)
		{
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.tx_msg_getDevInfo))
					.setMessage(buffer.toString())
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
		}
		else
		{
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.tx_msg_getDevInfo))
					.setMessage(
							getString(R.string.tx_msg_getDevInfo_fail) + iret)
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
		}
	}

	private void SetSysTime()
	{
		int iret = -1;
		Time t = new Time();
		t.setToNow();
		iret = m_reader.RDR_SetSysTime(t.year, t.month, t.monthDay, t.hour,
				t.minute, t.second);
		if (iret == ApiErrDefinition.NO_ERROR)
		{
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.tx_msg_setTime))
					.setMessage(getString(R.string.tx_msg_setTime_ok))
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
		}
		else
		{
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.tx_msg_setTime))
					.setMessage(getString(R.string.tx_msg_setTime_fail) + iret)
					.setPositiveButton(getString(R.string.tx_msg_certain), null)
					.show();
		}
	}

	private void FinishInventory()
	{
		btn_connect.setEnabled(false);
		btn_disconnect.setEnabled(true);
		btn_getDevInfo.setEnabled(true);
		btn_setTime.setEnabled(true);
		btn_openRF.setEnabled(true);
		btn_closeRF.setEnabled(true);
		btn_startInventory.setEnabled(true);
		btn_stopInventory.setEnabled(false);
		btn_setInventoryPara.setEnabled(true);
		btn_clearInventoryRecord.setEnabled(true);
		btn_startScanf.setEnabled(true);
		btn_stopScanf.setEnabled(false);
		btn_clearScanfRecordList.setEnabled(true);
		sn_RfPower.setEnabled(true);
		btn_readPower.setEnabled(true);
		btn_setPower.setEnabled(true);
		btn_loadDefault.setEnabled(true);
		list_tag_name.setEnabled(true);
		sn_overflow_time.setEnabled(true);
		btn_read_overflow_time.setEnabled(true);
		btn_write_overflow_time.setEnabled(true);
	}

	private Handler mHandler = new MyHandler(this);

	private static class MyHandler extends Handler
	{
		private final WeakReference<MainActivity> mActivity;

		public MyHandler(MainActivity activity)
		{
			mActivity = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg)
		{
			MainActivity pt = mActivity.get();
			if (pt == null)
			{
				return;
			}
			boolean b_find = false;
			switch (msg.what)
			{
			case INVENTORY_MSG:// 盘点到标签
				@SuppressWarnings("unchecked")
				Vector<Object> tagList = (Vector<Object>) msg.obj;
				if (pt.bRealShowTag && !pt.inventoryList.isEmpty())
				{
					pt.inventoryList.clear();
				}
				if (!tagList.isEmpty() && pt.bBuzzer)
				{
					VoicePlayer.GetInst(pt).Play();
				}
				for (int i = 0; i < tagList.size(); i++)
				{
					b_find = false;

					// ISO15693 TAG
					if (tagList.get(i) instanceof ISO15693Tag)
					{
						ISO15693Tag tagData = (ISO15693Tag) tagList.get(i);
						String uidStr = GFunction.encodeHexStr(tagData.uid);
						for (int j = 0; j < pt.inventoryList.size(); j++)
						{
							InventoryReport mReport = pt.inventoryList.get(j);
							if (mReport.getUidStr().equals(uidStr))
							{
								mReport.setFindCnt(mReport.getFindCnt() + 1);
								b_find = true;
								break;
							}
						}
						if (!b_find)
						{
							long mCnt = pt.bRealShowTag?0:1;
							String tagName = ISO15693Interface
									.GetTagNameById(tagData.tag_id);
							pt.inventoryList.add(new InventoryReport(uidStr,
									tagName,mCnt));

						}
					}
					else if (tagList.get(i) instanceof ISO14443ATag)
					{
						ISO14443ATag tagData = (ISO14443ATag) tagList.get(i);
						String uidStr = GFunction.encodeHexStr(tagData.uid);
						for (int j = 0; j < pt.inventoryList.size(); j++)
						{
							InventoryReport mReport = pt.inventoryList.get(j);
							if (mReport.getUidStr().equals(uidStr))
							{
								mReport.setFindCnt(mReport.getFindCnt() + 1);
								b_find = true;
								break;
							}
						}
						if (!b_find)
						{
							long mCnt = pt.bRealShowTag?0:1;
							String tagName = ISO14443AInterface
									.GetTagNameById(tagData.tag_id);
							pt.inventoryList.add(new InventoryReport(uidStr,
									tagName,mCnt));

						}
					}

				}
				pt.tv_inventoryInfo.setText(pt
						.getString(R.string.tx_info_tagCnt)
						+ pt.inventoryList.size()
						+ pt.getString(R.string.tx_info_loopCnt)+ pt.mLoopCnt
						+ pt.getString(R.string.tx_info_failCnt) + msg.arg1);
				pt.inventoryAdapter.notifyDataSetChanged();
				break;
			case INVENTORY_FAIL_MSG:
				pt.tv_inventoryInfo.setText(pt
						.getString(R.string.tx_info_tagCnt)
						+ pt.inventoryList.size()
						+ pt.getString(R.string.tx_info_loopCnt)+ pt.mLoopCnt
						+ pt.getString(R.string.tx_info_failCnt) + msg.arg1);
				break;
			case GETSCANRECORD:// 扫描到记录
				@SuppressWarnings("unchecked")
				Vector<String> dataList = (Vector<String>) msg.obj;
				for (String str : dataList)
				{
					b_find = false;
					for (int i = 0; i < pt.scanfReportList.size(); i++)
					{
						ScanReport mReport = pt.scanfReportList.get(i);
						if (str.equals(mReport.getDataStr()))
						{
							mReport.setFindCnt(mReport.getFindCnt() + 1);
							b_find = true;
						}
					}
					if (!b_find)
					{
						pt.scanfReportList.add(new ScanReport(str));
					}

				}
				pt.tv_scanRecordInfo.setText(pt
						.getString(R.string.tx_info_scanfCnt)
						+ pt.scanfReportList.size());
				pt.scanfAdapter.notifyDataSetChanged();
				break;
			case THREAD_END:// 线程结束
				pt.FinishInventory();
				break;
			default:
				break;
			}
		}
	}

	private boolean b_inventoryThreadRun = false;

	private class InventoryThrd implements Runnable
	{
		public void run()
		{
			int failedCnt = 0;// 操作失败次数
			Object hInvenParamSpecList = null;
			byte newAI = RfidDef.AI_TYPE_NEW;
			byte useAnt[] = null;
			if (bOnlyReadNew)
			{
				newAI = RfidDef.AI_TYPE_CONTINUE;
			}

			if (mAntCfg != 0)
			{
				Vector<Byte> vAntList = new Vector<Byte>();
				for (int i = 0; i < 32; i++)
				{
					if ((mAntCfg & (1 << i)) != 0)
					{
						vAntList.add((byte) (i + 1));
					}
				}

				useAnt = new byte[vAntList.size()];
				for (int i = 0; i < useAnt.length; i++)
				{
					useAnt[i] = vAntList.get(i);
				}
			}

			if (bUseISO14443A || bUseISO15693)
			{
				hInvenParamSpecList = ADReaderInterface
						.RDR_CreateInvenParamSpecList();
				if (bUseISO15693)
				{
					ISO15693Interface.ISO15693_CreateInvenParam(
							hInvenParamSpecList, (byte) 0, bMathAFI, mAFIVal,
							(byte) 0);
				}
				if (bUseISO14443A)
				{
					ISO14443AInterface.ISO14443A_CreateInvenParam(
							hInvenParamSpecList, (byte) 0);
				}
			}

			mLoopCnt = 0;
			b_inventoryThreadRun = true;
			while (b_inventoryThreadRun)
			{
				if (mHandler.hasMessages(INVENTORY_MSG))
				{
					continue;
				}
				int iret = m_reader.RDR_TagInventory(newAI, useAnt, 0,
						hInvenParamSpecList);
				if (iret == ApiErrDefinition.NO_ERROR
						|| iret == -ApiErrDefinition.ERR_STOPTRRIGOCUR)
				{
					Vector<Object> tagList = new Vector<Object>();
					newAI = RfidDef.AI_TYPE_NEW;
					if (bOnlyReadNew
							|| iret == -ApiErrDefinition.ERR_STOPTRRIGOCUR)
					{
						newAI = RfidDef.AI_TYPE_CONTINUE;
					}

					Object tagReport = m_reader
							.RDR_GetTagDataReport(RfidDef.RFID_SEEK_FIRST);
					while (tagReport != null)
					{
						ISO15693Tag ISO15693TagData = new ISO15693Tag();
						iret = ISO15693Interface.ISO15693_ParseTagDataReport(
								tagReport, ISO15693TagData);
						if (iret == ApiErrDefinition.NO_ERROR)
						{
							// ISO15693 TAG
							tagList.add(ISO15693TagData);
							tagReport = m_reader
									.RDR_GetTagDataReport(RfidDef.RFID_SEEK_NEXT);
							continue;
						}

						ISO14443ATag ISO14444ATagData = new ISO14443ATag();
						iret = ISO14443AInterface.ISO14443A_ParseTagDataReport(
								tagReport, ISO14444ATagData);
						if (iret == ApiErrDefinition.NO_ERROR)
						{
							// ISO14443A TAG
							tagList.add(ISO14444ATagData);
							tagReport = m_reader
									.RDR_GetTagDataReport(RfidDef.RFID_SEEK_NEXT);
							continue;
						}
					}

					mLoopCnt++;
					Message msg = mHandler.obtainMessage();
					msg.what = INVENTORY_MSG;
					msg.obj = tagList;
					msg.arg1 = failedCnt;
					mHandler.sendMessage(msg);
				}
				else
				{
					mLoopCnt++;
					newAI = RfidDef.AI_TYPE_NEW;
					if (b_inventoryThreadRun)
					{
						failedCnt++;
					}
					Message msg = mHandler.obtainMessage();
					msg.what = INVENTORY_FAIL_MSG;
					msg.arg1 = failedCnt;
					mHandler.sendMessage(msg);
				}
			}
			b_inventoryThreadRun = false;
			m_reader.RDR_ResetCommuImmeTimeout();
			mHandler.sendEmptyMessage(THREAD_END);// 盘点结束
		}
	};

	private boolean bGetScanRecordFlg = false;

	private class GetScanRecordThrd implements Runnable
	{
		public void run()
		{
			int nret = 0;
			bGetScanRecordFlg = true;
			byte gFlg = 0x00;// 初次采集数据或者上一次采集数据失败时，标志位为0x00
			Object dnhReport = null;
			while (bGetScanRecordFlg)
			{
				if (mHandler.hasMessages(GETSCANRECORD))
				{
					continue;
				}
				nret = m_reader.RDR_BuffMode_FetchRecords(gFlg);
				if (nret != ApiErrDefinition.NO_ERROR)
				{
					gFlg = 0x00;
					continue;
				}
				gFlg = 0x01;
				dnhReport = m_reader
						.RDR_GetTagDataReport(RfidDef.RFID_SEEK_FIRST);
				Vector<String> dataList = new Vector<String>();
				while (dnhReport != null)
				{
					String strData = "";
					byte[] byData = new byte[32];
					int[] len = new int[1];
					len[0] = byData.length;
					if (ADReaderInterface.RDR_ParseTagDataReportRaw(dnhReport, byData,
							len) == 0)
					{
						if (len[0] > 0)
						{
							strData = GFunction.encodeHexStr(byData,len[0]);
							dataList.add(strData);
						}
					}
					dnhReport = m_reader
							.RDR_GetTagDataReport(RfidDef.RFID_SEEK_NEXT);
				}
				if (!dataList.isEmpty())
				{
					Message msg = mHandler.obtainMessage();
					msg.what = GETSCANRECORD;
					msg.obj = dataList;
					mHandler.sendMessage(msg);
				}
			}
			bGetScanRecordFlg = false;
			mHandler.sendEmptyMessage(THREAD_END);// 结束
		}
	};

	private void saveHistory(String sKey, String val)
	{
		@SuppressWarnings("deprecation")
		SharedPreferences preferences = this.getSharedPreferences(sKey,
				Context.MODE_WORLD_READABLE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(sKey, val);
		editor.commit();
	}

	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	private void saveHistory(String sKey, int val)
	{
		SharedPreferences preferences = this.getSharedPreferences(sKey,
				Context.MODE_WORLD_READABLE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(sKey, val);
		editor.commit();
	}

	private int GetHistoryInt(String sKey)
	{
		@SuppressWarnings("deprecation")
		@SuppressLint("WorldReadableFiles")
		SharedPreferences preferences = this.getSharedPreferences(sKey,
				Context.MODE_WORLD_READABLE);
		return preferences.getInt(sKey, -1);
	}

	private void saveHistory(String sKey, long val)
	{
		@SuppressWarnings("deprecation")
		SharedPreferences preferences = this.getSharedPreferences(sKey,
				Context.MODE_WORLD_READABLE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(sKey, val);
		editor.commit();
	}
	
	private void saveHistory(String sKey, boolean val)
	{
		@SuppressWarnings("deprecation")
		SharedPreferences preferences = this.getSharedPreferences(sKey,
				Context.MODE_WORLD_READABLE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(sKey, val);
		editor.commit();
	}
	
	private boolean GetHistoryBool(String sKey)
	{
		@SuppressWarnings("deprecation")
		@SuppressLint("WorldReadableFiles")
		SharedPreferences preferences = this.getSharedPreferences(sKey,
				Context.MODE_WORLD_READABLE);
		return preferences.getBoolean(sKey, false);
	}

	private long GetHistoryLong(String sKey)
	{
		@SuppressWarnings("deprecation")
		@SuppressLint("WorldReadableFiles")
		SharedPreferences preferences = this.getSharedPreferences(sKey,
				Context.MODE_WORLD_READABLE);
		return preferences.getLong(sKey, 0);
	}

	private String GetHistoryString(String sKey)
	{
		@SuppressWarnings("deprecation")
		@SuppressLint("WorldReadableFiles")
		SharedPreferences preferences = this.getSharedPreferences(sKey,
				Context.MODE_WORLD_READABLE);
		return preferences.getString(sKey, "");
	}

	private void SaveActivity()
	{
		int devItem = 0;
		int commItem = 0;
		int blueToolItem = 0;
		String ipStr = ed_ipAddr.getText().toString();
		String portStr = ed_port.getText().toString();
		int comNameItem = 0;
		int comBaudItem = 0;
		int comFrameItem = 0;

		if (!sn_devName.getAdapter().isEmpty())
		{
			devItem = sn_devName.getSelectedItemPosition();
		}
		if (!sn_commType.getAdapter().isEmpty())
		{
			commItem = sn_commType.getSelectedItemPosition();
		}
		if (!sn_bluetooth.getAdapter().isEmpty())
		{
			blueToolItem = sn_bluetooth.getSelectedItemPosition();
		}

		if (!sn_comName.getAdapter().isEmpty())
		{
			comNameItem = sn_comName.getSelectedItemPosition();
		}
		if (!sn_comBaud.getAdapter().isEmpty())
		{
			comBaudItem = sn_comBaud.getSelectedItemPosition();
		}
		if (!sn_comFrame.getAdapter().isEmpty())
		{
			comFrameItem = sn_comFrame.getSelectedItemPosition();
		}

		saveHistory("DEVNAME", devItem);
		saveHistory("COMMTYPE", commItem);
		saveHistory("COMBAUD", comBaudItem);
		saveHistory("COMFRAME", comFrameItem);
		saveHistory("BLUETOOL", blueToolItem);
		saveHistory("COMNAME", comNameItem);
		saveHistory("DEVIPADDR", ipStr);
		saveHistory("DEVPORT", portStr);
	}

	private void LoadActivityByHistory()
	{
		int devItem = GetHistoryInt("DEVNAME");
		if (devItem < sn_devName.getCount())
		{
			sn_devName.setSelection(devItem);
		}

		int commItem = GetHistoryInt("COMMTYPE");
		if (commItem < sn_commType.getCount())
		{
			sn_commType.setSelection(commItem);
		}

		int blueToolItem = GetHistoryInt("BLUETOOL");
		if (blueToolItem < sn_bluetooth.getCount())
		{
			sn_bluetooth.setSelection(blueToolItem);
		}

		int comNameItem = GetHistoryInt("COMNAME");
		if (comNameItem < sn_comName.getCount())
		{
			sn_comName.setSelection(comNameItem);
		}

		int comBaudItem = GetHistoryInt("COMBAUD");
		if (comBaudItem < sn_comBaud.getCount() && comBaudItem >= 0)
		{
			sn_comBaud.setSelection(comBaudItem);
		}
		else
		{
			sn_comBaud.setSelection(1);
		}

		int comFrameItem = GetHistoryInt("COMFRAME");
		if (comFrameItem < sn_comFrame.getCount() && comFrameItem >= 0)
		{
			sn_comFrame.setSelection(comFrameItem);
		}
		else
		{
			sn_comFrame.setSelection(1);
		}

		String sIp = GetHistoryString("DEVIPADDR");
		if (sIp != "")
		{
			ed_ipAddr.setText(sIp);
		}

		String sPort = GetHistoryString("DEVPORT");
		if (sPort != "")
		{
			ed_port.setText(sPort);
		}
	}

	static public class InventoryAdapter extends BaseAdapter
	{
		private List<InventoryReport> list;
		private LayoutInflater inflater;

		public InventoryAdapter(Context context, List<InventoryReport> list)
		{
			this.list = list;
			inflater = LayoutInflater.from(context);
		}

		public int getCount()
		{
			return list.size();
		}

		public Object getItem(int position)
		{
			return list.get(position);
		}

		public long getItemId(int position)
		{
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			InventoryReport inventoryReport = (InventoryReport) this
					.getItem(position);
			ViewHolder viewHolder;
			if (convertView == null)
			{
				viewHolder = new ViewHolder();
				convertView = inflater
						.inflate(R.xml.inventorylist_tittle, null);
				viewHolder.mTextUid = (TextView) convertView
						.findViewById(R.id.tv_inventoryUid);
				viewHolder.mTextTagType = (TextView) convertView
						.findViewById(R.id.tv_inventoryTagType);
				viewHolder.mTextFindCnt = (TextView) convertView
						.findViewById(R.id.tv_inventoryCnt);
				convertView.setTag(viewHolder);
			}
			else
			{
				viewHolder = (ViewHolder) convertView.getTag();
			}

			long mCnt = inventoryReport.getFindCnt();
			String strCnt = mCnt>0?(mCnt+""):"---";
			viewHolder.mTextUid.setText(inventoryReport.getUidStr());
			viewHolder.mTextTagType.setText(inventoryReport.getTagTypeStr());
			viewHolder.mTextFindCnt.setText(strCnt);

			return convertView;
		}

		private class ViewHolder
		{
			public TextView mTextUid;
			public TextView mTextTagType;
			public TextView mTextFindCnt;
		}
	}

	public static class InventoryReport
	{
		private String uidStr;
		private String TagTypeStr;
		private long findCnt = 0;

		public InventoryReport()
		{
			super();
		}

		public InventoryReport(String uid, String tayType,long cnt)
		{
			super();
			this.setUidStr(uid);
			this.setTagTypeStr(tayType);
			this.setFindCnt(cnt);
		}

		public String getUidStr()
		{
			return uidStr;
		}

		public void setUidStr(String uidStr)
		{
			this.uidStr = uidStr;
		}

		public String getTagTypeStr()
		{
			return TagTypeStr;
		}

		public void setTagTypeStr(String tagTypeStr)
		{
			TagTypeStr = tagTypeStr;
		}

		public long getFindCnt()
		{
			return findCnt;
		}

		public void setFindCnt(long findCnt)
		{
			this.findCnt = findCnt;
		}
	}

	public static class ScanAdapter extends BaseAdapter
	{
		private List<ScanReport> list;
		private LayoutInflater inflater;

		public ScanAdapter(Context context, List<ScanReport> list)
		{
			this.list = list;
			inflater = LayoutInflater.from(context);
		}

		public int getCount()
		{
			return list.size();
		}

		public Object getItem(int position)
		{
			return list.get(position);
		}

		public long getItemId(int position)
		{
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			ScanReport scanfReport = (ScanReport) this.getItem(position);
			ViewHolder viewHolder;
			if (convertView == null)
			{
				viewHolder = new ViewHolder();
				convertView = inflater.inflate(R.xml.scan_record_list_tittle,
						null);
				viewHolder.mTextData = (TextView) convertView
						.findViewById(R.id.tv_scanfData);
				viewHolder.mTextFindCnt = (TextView) convertView
						.findViewById(R.id.tv_scanfCnt);
				convertView.setTag(viewHolder);
			}
			else
			{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.mTextData.setText(scanfReport.getDataStr());
			viewHolder.mTextFindCnt.setText(scanfReport.getFindCnt() + "");
			return convertView;
		}

		public static class ViewHolder
		{
			public TextView mTextData;
			public TextView mTextFindCnt;
		}

	}

	static public class ScanReport
	{
		private String dataStr;
		private long findCnt = 0;

		public ScanReport()
		{
			super();
		}

		public ScanReport(String data)
		{
			super();
			this.setDataStr(data);
			this.setFindCnt(1);
		}

		public long getFindCnt()
		{
			return findCnt;
		}

		public void setFindCnt(long findCnt)
		{
			this.findCnt = findCnt;
		}

		public String getDataStr()
		{
			return dataStr;
		}

		public void setDataStr(String dataStr)
		{
			this.dataStr = dataStr;
		}
	}
}
