package com.example.AnReaderDemo;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.ReaderDemo.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class InventoryParaActivity extends Activity implements OnClickListener
{
	public static final int RESULT_OK = 1;
	public static final int RESULT_CANCEL = 2;
	private CheckBox check_iso15693 = null;
	private CheckBox check_iso14443a = null;
	private CheckBox check_onlyReadNew = null;
	private CheckBox check_mathAFI = null;
	private CheckBox check_buzzer = null;
	private EditText ed_afiVal = null;
	private Button btn_ok = null;
	private Button btn_cancel = null;
	private ListView list_ant = null;
	private CheckBox check_realShow = null;
	private ArrayList<HashMap<String, Object>> listAntData = new ArrayList<HashMap<String, Object>>();
	
	private CheckboxAdapter listAntAdapter = null;
	private int mAntCnt = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory_set);

		check_realShow = (CheckBox) findViewById(R.id.check_realShow);
		check_iso15693 = (CheckBox) findViewById(R.id.check_ISO15693);
		check_iso14443a = (CheckBox) findViewById(R.id.check_ISO14443A);
		check_mathAFI = (CheckBox) findViewById(R.id.check_matchAFI);
		check_onlyReadNew = (CheckBox) findViewById(R.id.check_onlyReadNew);
		check_buzzer = (CheckBox) findViewById(R.id.check_buzzer);
		ed_afiVal = (EditText) findViewById(R.id.ed_afiVal);
		btn_ok = (Button) findViewById(R.id.btn_inventorySetOk);
		btn_cancel = (Button) findViewById(R.id.btn_inventorySetCancel);
		list_ant = (ListView) findViewById(R.id.list_ant);
		btn_ok.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		
		listAntAdapter = new CheckboxAdapter(this, listAntData);
		list_ant.setAdapter(listAntAdapter);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		boolean bRealShowTag = bundle.getBoolean("bRealShowTag");
		boolean bUseISO15693 = bundle.getBoolean("bUseISO15693");
		boolean bUseISO14443A = bundle.getBoolean("bUseISO14443A");
		boolean bOnlyReadNew = bundle.getBoolean("OnlyReadNew");
		boolean bMathAFI = bundle.getBoolean("MathAFI");
		boolean bBuzzer = bundle.getBoolean("bBuzzer");
		byte mAFIVal = bundle.getByte("AFI");
		long mAntCfg = bundle.getLong("mAntCfg");

		check_realShow.setChecked(bRealShowTag);
		check_iso15693.setChecked(bUseISO15693);
		check_iso14443a.setChecked(bUseISO14443A);
		check_onlyReadNew.setChecked(bOnlyReadNew);
		check_mathAFI.setChecked(bMathAFI);
		check_buzzer.setChecked(bBuzzer);
		ed_afiVal.setText(String.format("%02X", mAFIVal));
		
		//check_iso15693.setFocusable(true);
		
		//Load antenna
		mAntCnt = MainActivity.m_reader.RDR_GetAntennaInterfaceCount();
		if (mAntCnt>1)
		{
			for (int i = 0; i < mAntCnt; i++)
			{
				int antNum = i+1;
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ant_name", "Antenna#" + antNum);
				if ((mAntCfg&(1<<i))!=0)
				{
					map.put("selected", true);
				}
				else 
				{
					map.put("selected", false);
				}
				listAntData.add(map);
			}
			listAntAdapter.notifyDataSetChanged();
		}
		else {
			list_ant.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btn_inventorySetOk:
			int afiTmp = 0;
			boolean bErr = false;
			Intent intent = new Intent();
			Bundle bundle = new Bundle();

			try
			{
				afiTmp = Integer.parseInt(ed_afiVal.getText().toString(), 16);
				if (afiTmp < 0 || afiTmp >= 256)
				{
					bErr = true;
				}
			}
			catch (Exception e)
			{
				bErr = true;

			}
			if (bErr)
			{
				new AlertDialog.Builder(this).setTitle("")
						.setMessage(getString(R.string.tx_msg_inputHexAFI))
						.setPositiveButton(getString(R.string.tx_msg_certain), null).show();
				break;
			}
			bundle.putBoolean("bUseISO15693", check_iso15693.isChecked());
			bundle.putBoolean("bUseISO14443A", check_iso14443a.isChecked());
			bundle.putBoolean("OnlyReadNew", check_onlyReadNew.isChecked());
			bundle.putBoolean("MathAFI", check_mathAFI.isChecked());
			bundle.putByte("AFI", (byte) (afiTmp & 0xff));
			bundle.putBoolean("bBuzzer", check_buzzer.isChecked());
			bundle.putBoolean("bRealShowTag", check_realShow.isChecked());
			//Set antenna of inventory.
			long mAntCfg = 0x00000000;
			HashMap<Integer, Boolean> state = listAntAdapter.state;
			for (int j = 0; j < listAntAdapter.getCount(); j++)
			{
				if (state.get(j) != null && state.get(j))
				{
					mAntCfg|=(1<<j);
				}
			}
			bundle.putLong("mAntCfg", mAntCfg);
			intent.putExtras(bundle);
			this.setResult(RESULT_OK, intent);
			this.finish();
			break;
		default:
			this.finish();
			break;
		}
	}
	
	
	public class CheckboxAdapter extends BaseAdapter
	{
		Context context;
		ArrayList<HashMap<String, Object>> listData;
		@SuppressLint("UseSparseArrays")
		HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

		public CheckboxAdapter(Context context,
				ArrayList<HashMap<String, Object>> listData)
		{
			this.context = context;
			this.listData = listData;
		}

		public int getCount()
		{
			// TODO Auto-generated method stub
			return listData.size();
		}

		public Object getItem(int position)
		{
			// TODO Auto-generated method stub
			return listData.get(position);
		}

		public long getItemId(int position)
		{
			// TODO Auto-generated method stub
			return position;
		}

		// 重写View
		public View getView(final int position, View convertView,
				ViewGroup parent)
		{
			// TODO Auto-generated method stub
			LayoutInflater mInflater = LayoutInflater.from(context);
			convertView = mInflater.inflate(R.xml.sel_ant_item, null);
			TextView tv_money_name = (TextView) convertView
					.findViewById(R.id.tv_ant_name);
			tv_money_name.setText((String) listData.get(position).get(
					"ant_name"));
			CheckBox sel_check = (CheckBox) convertView
					.findViewById(R.id.sel_ant);
			if ((Boolean) listData.get(position).get("selected"))
			{
				sel_check.setChecked(true);
				state.put(position, true);
			}
			else
			{
				//state.remove(position);
				sel_check.setChecked(false);
				state.put(position, false);
			}
			sel_check.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked)
				{
					state.put(position, isChecked);
				}
			});
			return convertView;
		}
	}
}
