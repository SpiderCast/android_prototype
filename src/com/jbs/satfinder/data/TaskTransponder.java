package com.jbs.satfinder.data;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;



public class TaskTransponder extends DataTask
{
	//public static final int RETRY_COUNT					= 5;
	
	// 앞에 추가하면 안됨. 시작은 반드시 PROGRESS_ALL_DONE

	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	@Override
	protected void onProgressUpdate(Integer... progress) 
	{
		//Log.i("satip", "onProgressUpdate() - " + progress[0]);
		
		//progressBar.setProgress(progress[0]);
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void onPostExecute(String strRead)
	{
		//Log.i("satip", "onPostExecute()");
		//Log.i("satip", "READ : " + strRead);
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void onCancelled()
	{
		//Log.i("satip", "onCancelled()");
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	@Override
	protected String doInBackground(DataTaskParam... params)
	{
		Log.w("satip", "doInBackground()");
		DataTaskParam param = params[0];
		String strRead = null;
		//mDbManager = param.mDbManager;
		
		switch(param.opType)
		{
			case DataTaskParam.OP_TP_UPDATE:
			{
				opTpUpdate(param);
				break;
			}
			case DataTaskParam.OP_TP_ADD:
			{
				opTpAdd(param);
				break;
			}
			case DataTaskParam.OP_TP_DELETE:
			{
				opTpDelete(param);
				break;
			}
			
		}
		
		return strRead;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected void opTpUpdate(DataTaskParam param)
	{
		Log.w("satip", "opTpEdit()");
		Log.w("satip", "URL : " + param.strUrl);
		
		int retry = 0;
		
		while( retry++ < RETRY_COUNT && doInTpUpdate(param) == false )
		{
			Message msg = param.handler.obtainMessage();
			msg.what = PROGRESS_RETRY;
			msg.arg1 = retry; 
			param.handler.sendMessage(msg);
		}
		
		if( retry >= RETRY_COUNT )
		{
			if(param.handler != null)
			{
				Message msg = param.handler.obtainMessage();
				msg.what = PROGRESS_RETRY_FAIL;
				param.handler.sendMessage(msg);
			}
		}
		else
		{					
//			if(param.handler != null)
//			{
//				Message msg = param.handler.obtainMessage();
//				msg.what = PROGRESS_START_STREAMING_DONE;
//				param.handler.sendMessage(msg);
//			}
		}	
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected boolean doInTpUpdate(DataTaskParam param)
	{
		return true;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected void opTpAdd(DataTaskParam param)
	{
		Log.w("satip", "opTpAdd()");
		int retry = 0;
		
		/* "?opcode=add_dvbs_tp&freq=10780&sym=22000&org_netid=0&netid=0&tsid=0&polar=ver&delivery=dvbs2&modulation=8psk&sat_id=1" */
		param.strUrl = String.format("http://%s/cgi-bin/index.cgi?opcode=add_dvbs_tp&freq=%d&sym=%d&org_netid=0&netid=0&tsid=0&polar=%s&delivery=%s&modulation=%s&sat_id=%d"
									, DEV_IP
									, param.mTp.getFreq()
									, param.mTp.getSym()
									, param.mTp.getPolar()
									, param.mTp.getSystem()
									, param.mTp.getModulation()
									, param.mTp.getSatId());
		
		Log.w("satip", "URL : " + param.strUrl);
		
		if( doInTpAdd(param) == false )
		{
			if(param.handler != null)
			{
				Message msg = param.handler.obtainMessage();
				msg.what = PROGRESS_TP_ADD_FAIL;
				param.handler.sendMessage(msg);
			}
		}
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected boolean doInTpAdd(DataTaskParam param)
	{
		Log.w("satip", "doInTpAdd()");
		Log.w("satip", "URL : " + param.strUrl);

		//----------------------------------------------------------------------------------------//
		String strJson = requestToHttp(param.strUrl, 20000);
		String strDvbs;
		
		try
		{
			JSONObject joAll = new JSONObject(strJson);
			if( joAll.getString("opcode").equals("add_dvbs_tp") == false )
				return false;
		}
		catch(JSONException exJson)
		{
			Log.e("satip", "STREAMING START FAIL.");
			return false;
		}
		
		//----------------------------------------------------------------------------------------//
		try
		{
			JSONObject joAll = new JSONObject(strJson);
			String myip = joAll.getString("my_ip");
			String answer = joAll.getString("answer");
			int rf_id = joAll.getInt("rf_id");
			
			if( answer.equals("true") &&  param.handler != null)
			{
				Log.w("satip", "send msg");
				Message msg = param.handler.obtainMessage();
				msg.what = PROGRESS_TP_ADD_DONE;
				//msg.obj = handle;
				param.handler.sendMessage(msg);
			}			
		}
		catch(JSONException e)
		{
			Log.e("satip", e.toString());
			return false;
		}
				
		return true;
	}	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected void opTpDelete(DataTaskParam param)
	{
		Log.w("satip", "opTpDelete()");
		
		int retry = 0;
		param.strUrl = String.format("http://%s/cgi-bin/index.cgi?opcode=delete_dvbs_tp&rf_id=%d", 
									DEV_IP, param.mTp.getRfId());
		Log.w("satip", "URL : " + param.strUrl);
		
		
		while( retry++ < RETRY_COUNT && doInTpDelete(param) == false )
		{
			Message msg = param.handler.obtainMessage();
			msg.what = PROGRESS_RETRY;
			msg.arg1 = retry; 
			param.handler.sendMessage(msg);
		}
		
		if( retry >= RETRY_COUNT )
		{
			if(param.handler != null)
			{
				Message msg = param.handler.obtainMessage();
				msg.what = PROGRESS_TP_DELETE_FAIL;
				param.handler.sendMessage(msg);
			}
		}
		else
		{					
//			if(param.handler != null)
//			{
//				Message msg = param.handler.obtainMessage();
//				msg.what = PROGRESS_START_STREAMING_DONE;
//				param.handler.sendMessage(msg);
//			}
		}	
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected boolean doInTpDelete(DataTaskParam param)
	{
		Log.w("satip", "doInTpDelete()");
		Log.w("satip", "URL : " + param.strUrl);

		//----------------------------------------------------------------------------------------//
		String strJson = requestToHttp(param.strUrl, 20000);
		String strDvbs;
		
		try
		{
			JSONObject joAll = new JSONObject(strJson);
			if( joAll.getString("opcode").equals("delete_dvbs_tp") == false )
				return false;
		}
		catch(JSONException exJson)
		{
			Log.e("satip", "STREAMING START FAIL.");
			return false;
		}
		
		//----------------------------------------------------------------------------------------//
		try
		{
			JSONObject joAll = new JSONObject(strJson);
			String myip = joAll.getString("my_ip");
			String answer = joAll.getString("answer");
			int rf_id = joAll.getInt("rf_id");
			
			if( answer.equals("true") &&  param.handler != null)
			{
				Log.w("satip", "send msg");
				Message msg = param.handler.obtainMessage();
				msg.what = PROGRESS_TP_DELETE_DONE;
				//msg.obj = handle;
				param.handler.sendMessage(msg);
			}			
		}
		catch(JSONException e)
		{
			Log.e("satip", e.toString());
			return false;
		}
		
		return true;
	}	
}
