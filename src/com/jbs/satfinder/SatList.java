package com.jbs.satfinder;

//------------------------------------------------------------------------------------------------//
//
//------------------------------------------------------------------------------------------------//
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
import android.view.Menu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import com.jbs.satfinder.data.DataTask;
import com.jbs.satfinder.data.DataTaskParam;
import com.jbs.satfinder.data.DbManager;
import com.jbs.satfinder.data.Satellite;
import com.jbs.satfinder.data.Satellites;

public class SatList extends Activity 
{
	private static final int DLG_DOWNLOAD_PROGRESS = 1;
	//--------------------------------------------------------------------------------------------//
	protected static final int OP_GOTO_TP_LIST		= 1;
	protected static final int OP_GOTO_SAT_EDIT		= 1 + OP_GOTO_TP_LIST;
	
	//--------------------------------------------------------------------------------------------//
	private ProgressDialog mDlgProcess;
	
	protected int mNextOperation = 0;
	
	ListView mListView = null;
	SatListAdapter mListAdapter = null;
	ArrayList<Satellite> mArrSatellites = null;
	public Satellites mSatellites = null;
	
	protected int mSelectedRow = 0;

	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sat_list);
		
    	//----------------------------------------------------------------------------------------//
		if(DbManager.openDatabase(getApplicationContext()) == true)
			Log.i("ggbus", "DB OPEN");
		else
			Log.e("ggbus", "DB OPEN ERROR...!!!");
		
		//----------------------------------------------------------------------------------------//
		mSatellites = new Satellites();
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
		getMenuInflater().inflate(R.menu.sat_list, menu);
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
			
			mNextOperation = OP_GOTO_TP_LIST;
			downloadTpListFromDevice();	
		}
	};	
	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public void gotoTpList(int position)
	{
		Log.w("satip", "gotoSatEdit() - " + position);
		Intent intent = new Intent(SatList.this, TpList.class);
		intent.putExtra("sat_id", mArrSatellites.get(position).getSatId());
		startActivity(intent);
	}	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public void gotoSatEdit(int position)
	{
		Log.w("satip", "gotoSatEdit() - " + position);
		Intent intent = new Intent(SatList.this, SatEdit.class);
		intent.putExtra("sat_id", mArrSatellites.get(position).getSatId());
		startActivity(intent);
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected boolean updateList()
	{
		mArrSatellites = mSatellites.getSatellites(-1);
		
		//----------------------------------------------------------------------------------------//
		mListView=(ListView)findViewById(R.id.lvSatellites);
		
		//---------------------------------------------------------------------------------------//
		mListAdapter = new SatListAdapter(SatList.this, mArrSatellites);

		mListView.setAdapter(mListAdapter);
		mListView.setOnItemClickListener(mItemClickListener);

		return true;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void downloadTpListFromDevice()
	{
		Log.e("satip", "GET TP. LIST DATA FROM DEVICE.....2 !!!!!");
		DbManager.deleteTpAll();
		
		DataTaskParam param = new DataTaskParam();
		param.opType = DataTaskParam.OP_GET_TP_LIST;
		param.handler = mHandler;
		param.mSat = mArrSatellites.get(mSelectedRow);

		showDialog(DLG_DOWNLOAD_PROGRESS);
		new DataTask().execute(param);
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
					//TextView tvLastUpdate = (TextView)findViewById(R.id.tvLastUpdate);
					//tvLastUpdate.setText("Update");
					
					if( mNextOperation == OP_GOTO_TP_LIST )
						gotoTpList(mSelectedRow);
					else if( mNextOperation == OP_GOTO_SAT_EDIT )
						gotoSatEdit(mSelectedRow);
					
					break;
				}				
				case DataTask.PROGRESS_RETRY_FAIL:
				{
					Log.e("satip", "PROGRESS_RETRY_FAIL : ");
					dismissDialog(DLG_DOWNLOAD_PROGRESS);
				
					AlertDialog.Builder dlgFail;
					dlgFail = new AlertDialog.Builder(SatList.this);
					dlgFail.setTitle("데이타 수신 실패...");
					dlgFail.setMessage("서버로부터 데이타를 가져올 수 없습니다.\n\n네트웍 상태를 확인해주세요.");
					dlgFail.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					});
					dlgFail.show();
					
					break;
				}			
			}
		}
	};	
}

//------------------------------------------------------------------------------------------------//
//어댑터 클래스
//------------------------------------------------------------------------------------------------//
class SatListAdapter extends BaseAdapter
{
	//Context maincon;
	LayoutInflater mInflater;
	ArrayList<Satellite> mArrSatellites;
	int layout;
	Context mContext;

	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public SatListAdapter(Context context, ArrayList<Satellite> arrHistory)
	{
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mArrSatellites = arrHistory;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public int getCount() 
	{
		return mArrSatellites.size();
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public String getItem(int position) 
	{
		return mArrSatellites.get(position).getName();
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public long getItemId(int position)
	{
		return position;
	}
	//--------------------------------------------------------------------------------------------//
	// 각 항목의 뷰 생성
	//--------------------------------------------------------------------------------------------//
	public View getView(int position, View view, ViewGroup parent)
	{		
		if (view == null)
			view = mInflater.inflate(R.layout.sat_list_item, parent, false);
		
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
				((SatList)mContext).mSelectedRow = pos;
				
				((SatList)mContext).mNextOperation = SatList.OP_GOTO_SAT_EDIT;
				((SatList)mContext).downloadTpListFromDevice();
				
				//((SatList)mContext).gotoSatEdit(pos);
			}  
		});  
		
		//----------------------------------------------------------------------------------------//
		TextView tvName = (TextView)view.findViewById(R.id.tvSatName);
		tvName.setText(mArrSatellites.get(position).getName());
		
		//----------------------------------------------------------------------------------------//
		TextView tvSatPosition = (TextView)view.findViewById(R.id.tvPosition);
		double satPos = mArrSatellites.get(position).getSatPos();
		
		if(satPos > 1800)
			tvSatPosition.setText( (satPos - 1800) / 10 + " W");
		else
			tvSatPosition.setText( satPos / 10 + " E");

		//----------------------------------------------------------------------------------------//
//		TextView tvLNBFValue = (TextView)view.findViewById(R.id.tvLNBFValue);
//		String lnbValue = "";
//		
//		if( mArrSatellites.get(position).getLnb().equals("SINGLE") == true )
//			lnbValue = "" + mArrSatellites.get(position).getLnbfLow();
//		else// if( mArrSatellites.get(position).getLnb().equals("UNIVERSAL") == true )
//			lnbValue = "" + mArrSatellites.get(position).getLnbfLow() + "/" + mArrSatellites.get(position).getLnbfHi();
//		
//		tvLNBFValue.setText(lnbValue);
		
		//----------------------------------------------------------------------------------------//
//		TextView tvLNBPValue = (TextView)view.findViewById(R.id.tvLNBPValue);
//		tvLNBPValue.setText(mArrSatellites.get(position).getLnbp());
		
		//----------------------------------------------------------------------------------------//
//		TextView tvTPValue = (TextView)view.findViewById(R.id.tvTPValue);
//		tvTPValue.setText(""+mArrSatellites.get(position).getKeyTp());
		
		//----------------------------------------------------------------------------------------//
//		TextView tvDiSEqCValue = (TextView)view.findViewById(R.id.tvDiSEqCValue);
//		tvDiSEqCValue.setText(""+mArrSatellites.get(position).getDiseqc());
		
		//----------------------------------------------------------------------------------------//
//		TextView tv22KValue = (TextView)view.findViewById(R.id.tv22KValue);
//		tv22KValue.setText(mArrSatellites.get(position).get22Khz());
		
		//----------------------------------------------------------------------------------------//
//		TextView tvPortValue = (TextView)view.findViewById(R.id.tvPortValue);
//		tvPortValue.setText(""+mArrSatellites.get(position).getDiseqcPort());

		return view;
	}
}
//------------------------------------------------------------------------------------------------//
//END OF class SatyListAdapter
//------------------------------------------------------------------------------------------------//
