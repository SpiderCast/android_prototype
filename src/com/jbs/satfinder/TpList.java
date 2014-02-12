package com.jbs.satfinder;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jbs.satfinder.data.DataTask;
import com.jbs.satfinder.data.DataTaskParam;
import com.jbs.satfinder.data.DbManager;
import com.jbs.satfinder.data.Satellite;
import com.jbs.satfinder.data.Satellites;
import com.jbs.satfinder.data.TaskTransponder;
import com.jbs.satfinder.data.Transponder;
import com.jbs.satfinder.data.Transponders;

public class TpList extends Activity 
{
	private static final int DLG_DOWNLOAD_PROGRESS = 1;
	
	private ProgressDialog mDlgProcess;
	
	ListView mListView = null;
	TpListAdapter mListAdapter = null;
	private static ArrayList<Transponder> mArrTransponders = null;
	public Transponders mTransponders = null;
	
	private int mSatId = -1;
	public int mSelectedRow = 0;
	
	RelativeLayout vwTpEdit = null;
	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tp_list);
		
		Intent intent = getIntent();
		mSatId = intent.getIntExtra("sat_id", -1);
		
    	//----------------------------------------------------------------------------------------//
		if(DbManager.openDatabase(getApplicationContext()) == true)
			Log.i("ggbus", "DB OPEN");
		else
			Log.e("ggbus", "DB OPEN ERROR...!!!");
		
		//----------------------------------------------------------------------------------------//
		
		mTransponders = new Transponders();
		updateList();				
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
    public void onStart()
    {
    	super.onStart();
    	

    }
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
    public void onPause()
    {
    	super.onPause();
    }		
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tp_list, menu);
		return true;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected Dialog onCreateDialog(int id)
	{
		switch(id)
		{
			case DLG_DOWNLOAD_PROGRESS:
			{
				//mShownDownloadDlg = DLG_DOWNLOAD_PROGRESS;
				
				mDlgProcess = new ProgressDialog(this);
				mDlgProcess.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				mDlgProcess.setTitle("Receiving Data...");
				mDlgProcess.setMessage("Waiting...");
				mDlgProcess.setProgressNumberFormat("%1d / %2d");
				mDlgProcess.setMax(0);
				mDlgProcess.setCancelable(false); // back button X
	
				return mDlgProcess;
			}
		}
		return null;
	}		
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView parent, View view, int position, long id)
		{	
			mSelectedRow = position; //set the selected row
			
			showTpLockTest();
		}
	};
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void showTpLockTest()
	{
		TpList.this.vwTpEdit = (RelativeLayout)
				View.inflate(TpList.this, R.layout.tp_edit, null);
		
		//------------------------------------------------------------------------------------//
		EditText etFreq = (EditText)vwTpEdit.findViewById(R.id.etFreq);
		etFreq.setText("" + TpList.mArrTransponders.get(mSelectedRow).getFreq());
		Log.w("satip", "Freq : " + TpList.mArrTransponders.get(mSelectedRow).getFreq());
		
		EditText etSymb = (EditText)vwTpEdit.findViewById(R.id.etSymb);
		etSymb.setText("" + TpList.mArrTransponders.get(mSelectedRow).getSym());
		Log.w("satip", "Symb : " + TpList.mArrTransponders.get(mSelectedRow).getSym());
		
		RadioButton rbPolVer = (RadioButton)vwTpEdit.findViewById(R.id.rbPolVer);
		RadioButton rbPolHor = (RadioButton)vwTpEdit.findViewById(R.id.rbPolHor);
		
		String strPolar = TpList.mArrTransponders.get(mSelectedRow).getPolar();
		
		if(strPolar.equalsIgnoreCase("VER") == true)
		{
			rbPolVer.setChecked(true);
			rbPolHor.setChecked(false);
		}
		else
		{
			rbPolVer.setChecked(false);
			rbPolHor.setChecked(true);
		}
		
		//------------------------------------------------------------------------------------//
		//new AlertDialog.Builder(TpList.this)
		CustomAlertDialog dlgTpEdit = new CustomAlertDialog(TpList.this);
		dlgTpEdit.setTitle("TP Lock Test");
		//.setIcon(R.drawable.icon)
		dlgTpEdit.setView(vwTpEdit);
		dlgTpEdit.setPositiveButton("Scan...", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				TpList.this.scan();
			}
		});
		
		dlgTpEdit.setNeutralButton("Try Lock", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				TpList.this.tryLock();
			}
		});
		
		dlgTpEdit.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				dialog.dismiss();
			}
		});
		
		dlgTpEdit.show();			
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void showTpEdit()
	{
		TpList.this.vwTpEdit = (RelativeLayout)
				View.inflate(TpList.this, R.layout.tp_edit, null);
		
		//------------------------------------------------------------------------------------//
		EditText etFreq = (EditText)vwTpEdit.findViewById(R.id.etFreq);
		etFreq.setText("" + TpList.mArrTransponders.get(mSelectedRow).getFreq());
		Log.w("satip", "Freq : " + TpList.mArrTransponders.get(mSelectedRow).getFreq());
		
		EditText etSymb = (EditText)vwTpEdit.findViewById(R.id.etSymb);
		etSymb.setText("" + TpList.mArrTransponders.get(mSelectedRow).getSym());
		Log.w("satip", "Symb : " + TpList.mArrTransponders.get(mSelectedRow).getSym());
		
		RadioButton rbPolVer = (RadioButton)vwTpEdit.findViewById(R.id.rbPolVer);
		RadioButton rbPolHor = (RadioButton)vwTpEdit.findViewById(R.id.rbPolHor);
		
		String strPolar = TpList.mArrTransponders.get(mSelectedRow).getPolar();
		
		if(strPolar.equalsIgnoreCase("VER") == true)
		{
			rbPolVer.setChecked(true);
			rbPolHor.setChecked(false);
		}
		else
		{
			rbPolVer.setChecked(false);
			rbPolHor.setChecked(true);
		}
		
		//------------------------------------------------------------------------------------//
		//new AlertDialog.Builder(TpList.this)
		CustomAlertDialog dlgTpEdit = new CustomAlertDialog(TpList.this);
		dlgTpEdit.setTitle("TP Lock Test");
		//.setIcon(R.drawable.icon)
		dlgTpEdit.setView(vwTpEdit);
		dlgTpEdit.setPositiveButton("Scan...", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				TpList.this.scan();
			}
		});
		
		dlgTpEdit.setNeutralButton("Try Lock", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				TpList.this.tryLock();
			}
		});
		
		dlgTpEdit.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				dialog.dismiss();
			}
		});
		
		dlgTpEdit.show();	
		
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void showTpAdd()
	{
		TpList.this.vwTpEdit = (RelativeLayout)
				View.inflate(TpList.this, R.layout.tp_edit, null);
		
		//------------------------------------------------------------------------------------//
		EditText etFreq = (EditText)vwTpEdit.findViewById(R.id.etFreq);
		etFreq.setText("" + TpList.mArrTransponders.get(mSelectedRow).getFreq());
		Log.w("satip", "Freq : " + TpList.mArrTransponders.get(mSelectedRow).getFreq());
		
		EditText etSymb = (EditText)vwTpEdit.findViewById(R.id.etSymb);
		etSymb.setText("" + TpList.mArrTransponders.get(mSelectedRow).getSym());
		Log.w("satip", "Symb : " + TpList.mArrTransponders.get(mSelectedRow).getSym());
		
		RadioButton rbPolVer = (RadioButton)vwTpEdit.findViewById(R.id.rbPolVer);
		RadioButton rbPolHor = (RadioButton)vwTpEdit.findViewById(R.id.rbPolHor);
		
		String strPolar = TpList.mArrTransponders.get(mSelectedRow).getPolar();
		
		if(strPolar.equalsIgnoreCase("VER") == true)
		{
			rbPolVer.setChecked(true);
			rbPolHor.setChecked(false);
		}
		else
		{
			rbPolVer.setChecked(false);
			rbPolHor.setChecked(true);
		}
		
		//------------------------------------------------------------------------------------//
		//new AlertDialog.Builder(TpList.this)
		CustomAlertDialog dlgTpEdit = new CustomAlertDialog(TpList.this);
		dlgTpEdit.setTitle("Transponder add...");
		//.setIcon(R.drawable.icon)
		dlgTpEdit.setView(vwTpEdit);
		dlgTpEdit.setPositiveButton("Save", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				Log.w("satip", "TP ADD");
				
				
				EditText etFreq = (EditText)vwTpEdit.findViewById(R.id.etFreq);
				int freq = Integer.parseInt(etFreq.getText().toString());
				
				EditText etSymb = (EditText)vwTpEdit.findViewById(R.id.etSymb);
				int symb = Integer.parseInt(etSymb.getText().toString());
				
				RadioButton rbPolVer = (RadioButton)vwTpEdit.findViewById(R.id.rbPolVer);
				String strPolar = null;
				
				if(rbPolVer.isChecked())
					strPolar = "ver";
				else
					strPolar = "hor";
				
				Transponder tp = new Transponder(-1, freq, symb, 0, 0, 0, strPolar, "dvbs", "qpsk", 0, mSatId);
				
				Log.w("satip", "rfId = " + tp.getRfId() + " Symb = " + tp.getSym() + " Freq = " + tp.getFreq());
				
				DataTaskParam param = new DataTaskParam();
				param.opType = DataTaskParam.OP_TP_ADD;
				param.handler = TpList.this.mHandler;
				param.mTp = tp;

				//showDialog(DLG_DOWNLOAD_PROGRESS);
				new TaskTransponder().execute(param);
				
				dialog.dismiss();
			}
		});
		
		dlgTpEdit.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				dialog.dismiss();
			}
		});
		
		dlgTpEdit.show();			
		
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void showTpDelete()
	{
		Transponder tp = mArrTransponders.get(mSelectedRow);
		Log.w("satip", "tp_id = " + tp.getRfId());
		
		AlertDialog.Builder dlgDelete;
		dlgDelete = new AlertDialog.Builder(TpList.this);
		dlgDelete.setTitle("Transponder Delete?");
		//dlgDelete.setMessage("서버로부터 데이타를 가져올 수 없습니다.\n\n네트웍 상태를 확인해주세요.");
		dlgDelete.setPositiveButton("Delete", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				Log.w("satip", "TP Delete");
				
				Transponder tp = mArrTransponders.get(mSelectedRow);
				
				Log.w("satip", "rfId = " + tp.getRfId() + " Symb = " + tp.getSym() + " Freq = " + tp.getFreq());
				
				DataTaskParam param = new DataTaskParam();
				param.opType = DataTaskParam.OP_TP_DELETE;
				param.handler = TpList.this.mHandler;
				param.mTp = tp;

				//showDialog(DLG_DOWNLOAD_PROGRESS);
				new TaskTransponder().execute(param);
			}
		});
		dlgDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				Log.w("satip", "Cancel");
			}
		});
		dlgDelete.show();
		
	}	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
/*	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed(); //지워야 실행됨

		AlertDialog.Builder d = new AlertDialog.Builder(this);
		d.setMessage("정말 종료하시겠습니까?");
		d.setPositiveButton("예", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// process전체 종료
				finish();
			}
		});
		d.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		d.show();
	} 
*/	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected boolean updateList()
	{
		mArrTransponders = mTransponders.getTransponders(mSatId);
		
		//----------------------------------------------------------------------------------------//
		mListView=(ListView)findViewById(R.id.lvTp);
		
		//---------------------------------------------------------------------------------------//
		mListAdapter = new TpListAdapter(TpList.this, mArrTransponders);

		mListView.setAdapter(mListAdapter);
		mListView.setOnItemClickListener(mItemClickListener);

		return true;
	}	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public void tryLock()
	{
		Log.w("satip", "tryLock()");
		Transponder tp = mArrTransponders.get(mSelectedRow);
		
		Log.w("satip", "rfId = " + tp.getRfId() + " Symb = " + tp.getSym() + " Freq = " + tp.getFreq());
		
		DataTaskParam param = new DataTaskParam();
		param.opType = DataTaskParam.OP_TRY_LOCK;
		param.handler = TpList.this.mHandler;
		param.mTp = tp;

		//showDialog(DLG_DOWNLOAD_PROGRESS);
		new DataTask().execute(param);
		
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public void scan()
	{
		Log.w("satip", "scan()");
		/*
		Transponder tp = mArrTransponders.get(mSelectedRow);
		
		Log.w("satip", "rfId = " + tp.getRfId() + " Symb = " + tp.getSym() + " Freq = " + tp.getFreq());
		
		DataTaskParam param = new DataTaskParam();
		param.opType = DataTaskParam.OP_SCAN;
		param.handler = TpList.this.mHandler;

		//showDialog(DLG_DOWNLOAD_PROGRESS);
		new DataTask().execute(param);		
		*/
		
		Intent intent = new Intent(TpList.this, Scan.class);
		intent.putExtra("rf_id", mArrTransponders.get(mSelectedRow).getRfId());
		startActivity(intent);
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void updateTpEditView()
	{
		Transponder tp = DbManager.getTpInfoByRfId(TpList.this.mArrTransponders.get(TpList.this.mSelectedRow).getRfId());
		
		ProgressBar pbPower = (ProgressBar)vwTpEdit.findViewById(R.id.pbPower);
		ProgressBar pbQuality = (ProgressBar)vwTpEdit.findViewById(R.id.pbQuality);
		
		TextView tvPowerValue = (TextView)vwTpEdit.findViewById(R.id.tvPowerValue);
		TextView tvQualityValue = (TextView)vwTpEdit.findViewById(R.id.tvQualityValue);
		
		tvPowerValue.setText("(" + tp.getStrength() + "%)");
		tvQualityValue.setText("(" + tp.getSnr() + "%)");
		
		pbPower.setMax(100);
		pbPower.setProgress(tp.getStrength());
		
		pbQuality.setMax(100);
		pbQuality.setProgress(tp.getSnr());
		
		updateList();
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void downloadTpListFromDevice()
	{
		Log.e("satip", "GET TP. LIST DATA FROM DEVICE.....2 !!!!!");
		Satellite sat = (new Satellites()).getSatellite(mSatId);
		
		DbManager.deleteTpAll();
		
		DataTaskParam param = new DataTaskParam();
		param.opType = DataTaskParam.OP_GET_TP_LIST;
		param.handler = mHandler;
		param.mSat = sat;

		showDialog(DLG_DOWNLOAD_PROGRESS);
		new DataTask().execute(param);
	}	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public void showTpMenu(int position)
	{
		mSelectedRow = position;
		
		new AlertDialog.Builder(TpList.this)
		.setTitle("Transponder")
		//.setIcon(R.drawable.icon)
		.setItems(new String[] {"Edit", "Add", "Delete"}, 
				new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				switch(which)
				{
					case 0:
						Log.w("satip", "Edit");
						showTpEdit();
						break;
					case 1:
						Log.w("satip", "Add");
						showTpAdd();
						break;
					case 2:
						Log.w("satip", "Delete");
						showTpDelete();
						break;
				}
			}
		})

		.setNegativeButton("Cancel", null)
		.show();		
	}	
	//--------------------------------------------------------------------------------------------//
	// 핸들러 처리
	//--------------------------------------------------------------------------------------------//
	Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case DataTask.PROGRESS_GET_TP_LIST:
				{
					mDlgProcess.setMessage("Get Transponder info...");
					mDlgProcess.setProgress(msg.arg1);
					mDlgProcess.setMax(msg.arg2);
					break;
				}
				case DataTask.PROGRESS_GET_TP_LIST_DONE:
				{
					mDlgProcess.setMessage("Done...");
					mDlgProcess.dismiss();
					
					updateList();
					break;
				}				
				case DataTask.PROGRESS_RETRY_FAIL:
				{
					Log.e("satip", "PROGRESS_RETRY_FAIL : ");
					dismissDialog(DLG_DOWNLOAD_PROGRESS);
				
					AlertDialog.Builder dlgFail;
					dlgFail = new AlertDialog.Builder(TpList.this);
					dlgFail.setTitle("데이타 수신 실패...");
					dlgFail.setMessage("서버로부터 데이타를 가져올 수 없습니다.\n\n네트웍 상태를 확인해주세요.");
					dlgFail.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					});
					dlgFail.show();
					
					break;
				}
				case TaskTransponder.PROGRESS_TP_ADD_FAIL:
				{
					Log.w("satpi", "PROGRESS_TP_ADD_FAIL");
					
					AlertDialog.Builder dlgDelete;
					dlgDelete = new AlertDialog.Builder(TpList.this);
					dlgDelete.setTitle("Transponder ADD FAIL.");

					dlgDelete.setNegativeButton("Close", new DialogInterface.OnClickListener() 
					{
						public void onClick(DialogInterface dialog, int whichButton) 
						{
							Log.w("satip", "Cancel");
						}
					});
					dlgDelete.show();
					break;
				}					
				case TaskTransponder.PROGRESS_TP_ADD_DONE:
				{
					Log.w("satpi", "PROGRESS_TP_ADD_DONE");
					
					downloadTpListFromDevice();
					break;
				}					
				case TaskTransponder.PROGRESS_TP_DELETE_FAIL:
				{
					Log.w("satpi", "PROGRESS_TP_DELETE_FAIL");
					
					AlertDialog.Builder dlgDelete;
					dlgDelete = new AlertDialog.Builder(TpList.this);
					dlgDelete.setTitle("Transponder Delete FAIL.");

					dlgDelete.setNegativeButton("Close", new DialogInterface.OnClickListener() 
					{
						public void onClick(DialogInterface dialog, int whichButton) 
						{
							Log.w("satip", "Cancel");
						}
					});
					dlgDelete.show();
					break;
				}			
				case TaskTransponder.PROGRESS_TP_DELETE_DONE:
				{
					Log.w("satpi", "PROGRESS_TP_DELETE_DONE");
					
					downloadTpListFromDevice();
					break;
				}
				case TaskTransponder.PROGRESS_LOCK_TRY_FAIL:
				{
					Log.w("satpi", "PROGRESS_LOCK_TRY_FAIL");
					
					AlertDialog.Builder dlgDelete;
					dlgDelete = new AlertDialog.Builder(TpList.this);
					dlgDelete.setTitle("PROGRESS_LOCK_TRY_FAIL");

					dlgDelete.setNegativeButton("Close", new DialogInterface.OnClickListener() 
					{
						public void onClick(DialogInterface dialog, int whichButton) 
						{
							Log.w("satip", "Cancel");
						}
					});
					dlgDelete.show();
					break;
				}				
				case DataTask.PROGRESS_LOCK_TRY_DONE:
				{
					updateTpEditView();

					break;
				}
						
			}
		}
	};	
}
//------------------------------------------------------------------------------------------------//
//
//------------------------------------------------------------------------------------------------//
class TpListAdapter extends BaseAdapter
{
	//Context maincon;
	LayoutInflater mInflater;
	ArrayList<Transponder> mArrTransponders;
	int layout;
	Context mContext;
	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public TpListAdapter(Context context, ArrayList<Transponder> arrTransponders)
	{
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mArrTransponders = arrTransponders;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public int getCount() 
	{
		return mArrTransponders.size();
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public String getItem(int position) 
	{
		return "" + mArrTransponders.get(position).getRfId();
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public long getItemId(int position)
	{
		return position;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public View getView(int position, View view, ViewGroup parent)
	{
		if (view == null)
			view = mInflater.inflate(R.layout.tp_list_item, parent, false);

		//----------------------------------------------------------------------------------------//
		ImageButton btnMore = (ImageButton)view.findViewById(R.id.btnMore);
		btnMore.setFocusable(false) ;
		btnMore.setTag(position);
		
		btnMore.setOnClickListener(new OnClickListener() 
		{  		  
			@Override  
			public void onClick(View v) 
			{  
				int pos = (Integer)v.getTag();
				Log.w("satip", "button click position=" + pos);  
				
				((TpList)mContext).showTpMenu(pos);
			}  
		});  
		
		//----------------------------------------------------------------------------------------//
		TextView tvFreq = (TextView)view.findViewById(R.id.tvFreq);
		
		Transponder tp = (Transponder)mArrTransponders.get(position);
		String strFreq = "";
		strFreq += tp.getFreq() + " / " + tp.getPolar() + " / " + tp.getSym();
		tvFreq.setText(strFreq);
		
		ProgressBar pbPower = (ProgressBar)view.findViewById(R.id.pbPower);
		ProgressBar pbQuality = (ProgressBar)view.findViewById(R.id.pbQuality);
		
		pbPower.setMax(100);
		pbPower.setProgress(tp.getStrength());
		
		pbQuality.setMax(100);
		pbQuality.setProgress(tp.getSnr());
		
//		TextView tvPosition = (TextView)view.findViewById(R.id.tvPosition);
//		
//		tvName.setText(mArrSatellites.get(position).getName());
//		tvPosition.setText(mArrSatellites.get(position).getPosition() + " E");

//		public int		getRfId()		{ return mRfId; }
//		public int 		getFreq() 		{ return mFreq; }
//		public int	 	getSym() 		{ return mSym; }
//		public int	 	getOrgNetId() 	{ return mOrgNetId; }
//		public int 		getNetId()	 	{ return mNetId; }
//		public int	 	getTsId() 		{ return mTsId; }
//		public String 	getPolar() 		{ return mPolar; }
//		public int	 	getHasCach() 	{ return mHasCach; }
//		public int	 	getSatId()	 	{ return mSatId; }
		return view;
	}
}
//------------------------------------------------------------------------------------------------//
//END OF class SatyListAdapter
//------------------------------------------------------------------------------------------------//
