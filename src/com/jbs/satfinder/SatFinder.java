package com.jbs.satfinder;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jbs.satfinder.data.DataTask;
import com.jbs.satfinder.data.DataTaskParam;
import com.jbs.satfinder.data.DbManager;

public class SatFinder extends Activity 
{
	//--------------------------------------------------------------------------------------------//
	private static final int DLG_CONNECTING_SERVER = 1;
	private static final int DLG_DOWNLOAD_PROGRESS = DLG_CONNECTING_SERVER + 1;
	
	private ProgressDialog mDlgProcess;
	
	TextView tvDebug = null;
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sat_finter);
    }
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
    public void onStart()
    {
    	super.onStart();
    	
    	//----------------------------------------------------------------------------------------//
		if(DbManager.openDatabase(getApplicationContext()) == true)
			Log.i("ggbus", "DB OPEN");
		else
			Log.e("ggbus", "DB OPEN ERROR...!!!");
		
		//----------------------------------------------------------------------------------------//
//		 Button btnStart = (Button)findViewById(R.id.btnStart);
//		 btnStart.setVisibility(View.INVISIBLE);
		
//		 Button btnChannel = (Button)findViewById(R.id.btnChannel);
//		 btnChannel.setVisibility(View.INVISIBLE);
		 
		 tvDebug = (TextView)findViewById(R.id.tvDebug);
		 
		 connectingToServer();		 
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
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sat_finter, menu);
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
			case DLG_CONNECTING_SERVER:
			{
				//mShownDownloadDlg = DLG_DOWNLOAD_PROGRESS;
				
				mDlgProcess = new ProgressDialog(this);
				mDlgProcess.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				mDlgProcess.setTitle("Connecting server...");
				mDlgProcess.setMessage("Waiting...");
				//mDlgProcess.setProgressNumberFormat("%1d / %2d");
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
	public void mOnClick(View v)
	{
		switch(v.getId())
		{
			case R.id.btnCntSvr:
			{	
//				Button btnStart = (Button)findViewById(R.id.btnStart);
//				btnStart.setVisibility(View.INVISIBLE);
				
//				Button btnChannel = (Button)findViewById(R.id.btnChannel);
//				btnChannel.setVisibility(View.INVISIBLE);
				
				connectingToServer();
				
				break;
			}
			case R.id.btnStart:
			{	
				//downloadSatListFromDevice();
				gotoSatList();
				
				break;
			}
			case R.id.btnChannel:
			{	
				gotoChannelList();	
				
				break;
			}
			case R.id.btnReload:
			{	
				downloadSatListFromDevice();	
				
				break;
			}	
		}

	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void connectingToServer()
	{
		Log.e("satip", "Connecting to SERVER...");
		tvDebug.setText("Connecting to SERVER...");
		
		DataTaskParam param = new DataTaskParam();
		param.opType = DataTaskParam.OP_CONNECTING_SERVER;
		param.mContext = this;
		param.handler = mHandler;

		//showDialog(DLG_DOWNLOAD_PROGRESS);
		new DataTask().execute(param);
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void downloadSystemInfoFromDevice()
	{
		Log.e("satip", "GET SYSTEM INFO FROM DEVICE.....2 !!!!!");
		tvDebug.setText("GET SYSTEM INFO FROM DEVICE.....2");
		
		DataTaskParam param = new DataTaskParam();
		param.opType = DataTaskParam.OP_GET_SYSTEMINFO;
		param.handler = mHandler;

		//showDialog(DLG_DOWNLOAD_PROGRESS);
		new DataTask().execute(param);
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void gotoSatList()
	{
		Log.w("satip", "gotoSatList()");
		
		Intent intent = new Intent(this, SatList.class);
		startActivity(intent);
	}	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void gotoChannelList()
	{
		Log.w("satip", "gotoChannelList()");
		
		Intent intent = new Intent(this, ChannelList.class);
		startActivity(intent);
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void downloadSatListFromDevice()
	{
		Log.e("satip", "GET SAT. LIST DATA FROM DEVICE.....2 !!!!!");
		tvDebug.setText("GET SAT. LIST DATA FROM DEVICE.....2");
		
		DbManager.deleteSatAll();
		
		DataTaskParam param = new DataTaskParam();
		param.opType = DataTaskParam.OP_GET_SAT_LIST;
		param.handler = mHandler;

		showDialog(DLG_DOWNLOAD_PROGRESS);
		new DataTask().execute(param);
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
				case DataTask.PROGRESS_GET_SVRIP_DONE:
				{
					Log.w("satpi", "PROGRESS_GET_SVRIP_DONE");
					tvDebug.setText("PROGRESS_GET_SVRIP_DONE");
					downloadSystemInfoFromDevice();
					break;
				}
				case DataTask.PROGRESS_GET_SVRIP_FAIL:
				{
					Log.w("satpi", "PROGRESS_GET_SVRIP_FAIL");
					tvDebug.setText("PROGRESS_GET_SVRIP_FAIL");

					break;
				}				
				case DataTask.PROGRESS_GET_SYSTEMINFO_DONE:
				{
					Log.w("satpi", "PROGRESS_GET_SYSTEMINFO_DONE");
					tvDebug.setText("PROGRESS_GET_SYSTEMINFO_DONE");
					
					Button btnStart = (Button)findViewById(R.id.btnStart);
					btnStart.setVisibility(View.VISIBLE);
					
					Button btnChannel = (Button)findViewById(R.id.btnChannel);
					btnChannel.setVisibility(View.VISIBLE);
					break;
				}
				case DataTask.PROGRESS_GET_SAT_LIST:
				{
					mDlgProcess.setMessage("Get Satellites info...");
					mDlgProcess.setProgress(msg.arg1);
					mDlgProcess.setMax(msg.arg2);
					break;
				}
				case DataTask.PROGRESS_GET_SAT_LIST_DONE:
				{
					mDlgProcess.setMessage("Done...");
					mDlgProcess.dismiss();
					gotoSatList();
					//downloadTpListFromDevice();
					break;
				}		
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
					TextView tvLastUpdate = (TextView)findViewById(R.id.tvLastUpdate);
					tvLastUpdate.setText("Update");
					//gotoSatList();
					break;
				}				
				case DataTask.PROGRESS_RETRY_FAIL:
				{
					Log.e("satip", "PROGRESS_RETRY_FAIL : ");
					dismissDialog(DLG_DOWNLOAD_PROGRESS);
				
					AlertDialog.Builder dlgFail;
					dlgFail = new AlertDialog.Builder(SatFinder.this);
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
