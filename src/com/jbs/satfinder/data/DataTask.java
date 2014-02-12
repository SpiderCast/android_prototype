package com.jbs.satfinder.data;


import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jbs.satfinder.data.SystemInfo;


public class DataTask extends AsyncTask<DataTaskParam, Integer, String>
{
	public static final int PROGRESS_RETRY					= 1;
	public static final int PROGRESS_RETRY_FAIL				= PROGRESS_RETRY + 1;
	public static final int PROGRESS_ALL_DONE 				= PROGRESS_RETRY_FAIL + 1;
	
	public static final int PROGRESS_GET_SVRIP_DONE			= PROGRESS_ALL_DONE + 1;
	public static final int PROGRESS_GET_SVRIP_FAIL			= PROGRESS_GET_SVRIP_DONE + 1;
	
	public static final int PROGRESS_GET_SYSTEMINFO_DONE	= PROGRESS_GET_SVRIP_FAIL + 1;
	
	public static final int PROGRESS_GET_SAT_LIST	 		= PROGRESS_GET_SYSTEMINFO_DONE + 1;
	public static final int PROGRESS_GET_SAT_LIST_DONE 		= PROGRESS_GET_SAT_LIST + 1;
	public static final int PROGRESS_GET_TP_LIST 			= PROGRESS_GET_SAT_LIST_DONE + 1;
	public static final int PROGRESS_GET_TP_LIST_DONE 		= PROGRESS_GET_TP_LIST + 1;
	
	public static final int PROGRESS_LOCK_TRY				= PROGRESS_GET_TP_LIST_DONE + 1;
	public static final int PROGRESS_LOCK_TRY_FAIL			= PROGRESS_LOCK_TRY + 1;
	public static final int PROGRESS_LOCK_TRY_DONE			= PROGRESS_LOCK_TRY_FAIL + 1;
	
	public static final int PROGRESS_SCAN					= PROGRESS_LOCK_TRY + 1;
	public static final int PROGRESS_SCAN_DONE				= PROGRESS_SCAN + 1;
	
	public static final int PROGRESS_START_STREAMING		= PROGRESS_SCAN_DONE + 1;
	public static final int PROGRESS_START_STREAMING_DONE	= PROGRESS_START_STREAMING + 1;
	
	
	public static final int PROGRESS_TP_UPDATE_FAIL			= PROGRESS_START_STREAMING_DONE + 1;
	public static final int PROGRESS_TP_UPDATE_DONE			= PROGRESS_TP_UPDATE_FAIL + 1;
	
	public static final int PROGRESS_TP_ADD_FAIL			= PROGRESS_TP_UPDATE_DONE + 1;
	public static final int PROGRESS_TP_ADD_DONE			= PROGRESS_TP_ADD_FAIL + 1;
	
	public static final int PROGRESS_TP_DELETE_FAIL			= PROGRESS_TP_ADD_DONE + 1;
	public static final int PROGRESS_TP_DELETE_DONE			= PROGRESS_TP_DELETE_FAIL + 1;
	
	//--------------------------------------------------------------------------------------------//
	public static final int RETRY_COUNT					= 5;
	
	protected static String DEV_IP = null;
	
	private static SystemInfo systemInfo = null;
	
	//DbManager mDbManager = null;
	
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
	protected String requestToHttp(String url, int timeout)
	{
		StringBuilder json = new StringBuilder();

		try
		{
			URL httpUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection)httpUrl.openConnection();
			
			if(conn != null)
			{
				conn.setConnectTimeout(timeout);
				conn.setUseCaches(false);

				if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
				{
					BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

					while(true)
					{
						String line = br.readLine();

						if(line == null) break;
						json.append(line + '\n');
					}

					//Log.w("satip", json.toString());

					br.close();
				}

				conn.disconnect();
			}
		}
		catch(Exception ex)
		{
			Log.e("satip", ex.toString());
			return null;
		}
		
		//----------------------------------------------------------------------------------------//
		if(json.toString() == null || json.toString().length() == 0)
			return null;
		
		return json.toString();
	}	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected InetAddress getBroadcastAddress(Context context)
	{
		try
		{
		    WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		    DhcpInfo dhcp = wifi.getDhcpInfo();
		    // handle null somehow
	
		    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		    byte[] quads = new byte[4];
		    for (int k = 0; k < 4; k++)
		    	quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		    
		    //return InetAddress.getByAddress(quads);
		    return InetAddress.getByName("255.255.255.255");
		}
		catch(IOException ioe)
		{
			Log.e("satip", "WIFI Error : " + ioe.toString());
		}
	    return null;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected boolean parseImHere(String strImHere) throws JSONException
	{
		JSONObject joAll = new JSONObject(strImHere);
		JSONObject joImHere = joAll.getJSONObject("imhere");
		
		if(joImHere == null) return false;
		
		Log.w("satip", joImHere.toString());
		
		String strIpAddr = joImHere.getString("my_ip");
		
		if( strIpAddr == null ) return false;
				
		Log.w("satip", strIpAddr);
		DEV_IP = strIpAddr;
		
		return true;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void sendWhereAU(DataTaskParam param/*Context context, String data*/)
	{
		DatagramSocket recvSocket = null;
		DatagramSocket sendSocket = null;
		boolean bIsGetServerIP = false;
		String strWhereAU;	
		int retryCount = 0;
		
		try
		{
			recvSocket = new DatagramSocket(1124);
			sendSocket = new DatagramSocket(1123);
			
			JSONObject jsIp = new JSONObject();
			jsIp.put("ip", "client IP");
			
			JSONObject jsWhereAU = new JSONObject();
			jsWhereAU.put("whereau", jsIp);

			strWhereAU = jsWhereAU.toString();
			
			//------------------------------------------------------------------------------------//
			sendSocket.setBroadcast(true);
			DatagramPacket packet = new DatagramPacket(strWhereAU.getBytes(), strWhereAU.length(),
			    getBroadcastAddress(param.mContext), 1123);
			
			Log.w("satip", "sendWhereAU : " + strWhereAU);
			
			recvSocket.setSoTimeout(200);
			
			while(bIsGetServerIP == false)
			{
				Log.w("satip", "strWhereAU : " + strWhereAU);
				
				sendSocket.send(packet);
				Thread.sleep(200);
			
				byte[] buf = new byte[1024];
				try 
				{
					//while (true) 
					{
						DatagramPacket recvPacket = new DatagramPacket(buf, buf.length);
						recvSocket.receive(recvPacket);
						String s = new String(recvPacket.getData(), 0, recvPacket.getLength());
						Log.d("satip", "Received response " + s);
						
						bIsGetServerIP = parseImHere(s);
					}
				} catch (SocketTimeoutException e) 
				{
					Log.d("satip", "Receive timed out");
				}
				
				retryCount++;
				
				if(retryCount > 10)
					break;
			}
		}
		catch(JSONException jsone)
		{
			Log.e("satip", "JSONException : " + jsone.toString());
			jsone.printStackTrace();
		}
		catch(InterruptedException ie)
		{
			Log.e("satip", "SocketException : " + ie.toString());
		}
		catch(SocketException se)
		{
			Log.e("satip", "SocketException : " + se.toString());
		}
		catch(IOException ioe)
		{
			Log.e("satip", "SocketException : " + ioe.toString());
		}
	
		sendSocket.close();
		recvSocket.close();
		
		if(bIsGetServerIP == true)
		{
			if(param.handler != null)
			{
				Message msg = null;
				msg = param.handler.obtainMessage();
				msg.what = PROGRESS_GET_SVRIP_DONE;
				param.handler.sendMessage(msg);
			}
		}
		else
		{
			if(param.handler != null)
			{
				Message msg = null;
				msg = param.handler.obtainMessage();
				msg.what = PROGRESS_GET_SVRIP_FAIL;
				param.handler.sendMessage(msg);
			}			
		}
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected boolean doInGetSystemInfo(DataTaskParam param)
	{
		Log.w("satip", "doInGetSystemInfo()");
		Log.w("satip", "URL : " + param.strUrl);

		//----------------------------------------------------------------------------------------//
		String strJson = requestToHttp(param.strUrl, 5000);
		String strDvbs;
		
		try
		{
			JSONObject jsonObj = new JSONObject(strJson);
			strDvbs = jsonObj.getString("fe_list");
			
			Log.w("satip", strDvbs);
		}
		catch(JSONException exJson)
		{
			Log.e("satip", "HAS NO SYSTEM INFO");
			return false;
		}
		
		//----------------------------------------------------------------------------------------//
		try
		{
			Message msg = null;
			
			JSONObject joAll = new JSONObject(strJson);
			String strMyIp = joAll.getString("my_ip");
			String strModelName = joAll.getString("model_name");
			String strHWVersion = joAll.getString("hw_ver");
			String strSWVersion = joAll.getString("sw_ver");
			int	count = joAll.getInt("count");
			
			systemInfo = new SystemInfo(strMyIp, strModelName, strHWVersion, strSWVersion, count);
			
			JSONArray arrJson = new JSONArray(strDvbs);
			
			Log.i("satip", "FRONTEND COUNT = " + arrJson.length());
			
			for(int i = 0; i < arrJson.length(); i++)
			{
				JSONObject jo = arrJson.getJSONObject(i);
				
				Frontend fe = new Frontend(jo.getInt("fe_id")
											, jo.getString("path")
											, jo.getString("type")
											, jo.getString("model_name")
											);
				
				systemInfo.addFrontend(fe);
			}
		}
		catch(JSONException e)
		{
			Log.e("satip", e.toString());
			return false;
		}
		
		//DbManager.dumpCompanyAll();
		
		return true;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	\	
	public static SystemInfo getSystemInfo()
	{
		return systemInfo;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected boolean doInGetSatList(DataTaskParam param)
	{
		Log.w("satip", "doInGetSatList()");
		Log.w("satip", "URL : " + param.strUrl);

		//----------------------------------------------------------------------------------------//
		String strJson = requestToHttp(param.strUrl, 5000);		
		String strDvbs;
		
		try
		{
			JSONObject jsonObj = new JSONObject(strJson);
			strDvbs = jsonObj.getString("dvbs");
			
			Log.w("satip", strDvbs);
		}
		catch(JSONException exJson)
		{
			Log.e("satip", "HAS NO SAT. INFO");
			return false;
		}
		
		//----------------------------------------------------------------------------------------//
		try
		{
			Message msg = null;
			JSONArray arrJson = new JSONArray(strDvbs);
			Log.i("satip", "SAT. COUNT = " + arrJson.length());
			
			if(param.handler != null)
			{
				msg = param.handler.obtainMessage();
				msg.what = PROGRESS_GET_SAT_LIST;
				msg.arg1 = 0;
				msg.arg2 = arrJson.length();
				param.handler.sendMessage(msg);
			}
			
			for(int i = 0; i < arrJson.length(); i++)
			{
				JSONObject objJson = arrJson.getJSONObject(i);
				
				Satellite sat = new Satellite(objJson.getInt("sat_id")
											, objJson.getString("name")
											, objJson.getString("lnb")
											, objJson.getInt("lnbf_lo")
											, objJson.getInt("lnbf_hi")
											, objJson.getInt("lnbf_ref")
											, objJson.getString("lnbp")
											, objJson.getString("_22khz")
											, objJson.getString("diseqc")
											, objJson.getInt("diseqc_port")
											, objJson.getInt("sat_pos")
											, objJson.getInt("key_tp")
											);
				
				DbManager.updateSatInfo(sat);
				
				if(param.handler != null)
				{
					msg = param.handler.obtainMessage();
					msg.what = PROGRESS_GET_SAT_LIST;
					msg.arg1 = i + 1;
					msg.arg2 = arrJson.length();
					param.handler.sendMessage(msg);
				}
			}
		}
		catch(JSONException e)
		{
			Log.e("satip", e.toString());
			return false;
		}
		
		//DbManager.dumpCompanyAll();
		
		return true;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected boolean doInGetTpList(DataTaskParam param)
	{
		Log.w("satip", "doInGetTpList()");
		Log.w("satip", "URL : " + param.strUrl);

		//----------------------------------------------------------------------------------------//
		String strJson = requestToHttp(param.strUrl, 5000);		
		String strDvbs;
		
		try
		{
			JSONObject jsonObj = new JSONObject(strJson);
			strDvbs = jsonObj.getString("dvbs_tp");
			
			//Log.w("satip", strDvbs);
		}
		catch(JSONException exJson)
		{
			Log.e("satip", "HAS NO TP. INFO");
			return false;
		}
		
		//----------------------------------------------------------------------------------------//
		try
		{
			Message msg = null;
			JSONArray arrJson = new JSONArray(strDvbs);
			Log.i("satip", "TP. COUNT = " + arrJson.length());
			
			if(param.handler != null)
			{
				msg = param.handler.obtainMessage();
				msg.what = PROGRESS_GET_TP_LIST;
				msg.arg1 = 0;
				msg.arg2 = arrJson.length();
				param.handler.sendMessage(msg);
			}
			
			for(int i = 0; i < arrJson.length(); i++)
			{
				JSONObject objJson = arrJson.getJSONObject(i);
				
				Transponder transponder = new Transponder(objJson.getInt("rf_id")
											, objJson.getInt("freq")
											, objJson.getInt("sym")
											, objJson.getInt("org_netid")
											, objJson.getInt("netid")
											, objJson.getInt("tsid")
											, objJson.getString("polar")
											, objJson.getString("system")
											, objJson.getString("modulation")
											, objJson.getInt("has_cach")
											, objJson.getInt("sat_id")
											);
				
				DbManager.updateTpInfo(transponder);
				
				if(param.handler != null)
				{
					msg = param.handler.obtainMessage();
					msg.what = PROGRESS_GET_TP_LIST;
					msg.arg1 = i + 1;
					msg.arg2 = arrJson.length();
					param.handler.sendMessage(msg);
				}
			}
		}
		catch(JSONException e)
		{
			Log.e("satip", e.toString());
			return false;
		}
		
		//DbManager.dumpCompanyAll();
		
		return true;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected void opGetSystemInfo(DataTaskParam param)
	{
		Log.w("satip", "OP_GET_SYDTEM_INFO");
		
		int retry = 0;
		param.strUrl = "http://" + DEV_IP + "/cgi-bin/index.cgi?opcode=system_info";
		Log.w("satip", "URL : " + param.strUrl);
		
		while( retry++ < RETRY_COUNT && doInGetSystemInfo(param) == false )
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
			if(param.handler != null)
			{
				Message msg = param.handler.obtainMessage();
				msg.what = PROGRESS_GET_SYSTEMINFO_DONE;
				param.handler.sendMessage(msg);
			}
		}		
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void opGetSatList(DataTaskParam param)
	{
		Log.w("satip", "OP_GET_SAT_LIST");
		
		int retry = 0;
		param.strUrl = "http://" + DEV_IP + "/cgi-bin/index.cgi?opcode=dvbs_list";
		Log.w("satip", "URL : " + param.strUrl);
		
		while( retry++ < RETRY_COUNT && doInGetSatList(param) == false )
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
			if(param.handler != null)
			{
				Message msg = param.handler.obtainMessage();
				msg.what = PROGRESS_GET_SAT_LIST_DONE;
				param.handler.sendMessage(msg);
			}
		}		
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void opGetTpList(DataTaskParam param)
	{
		Log.w("satip", "OP_GET_TP_LIST");
		
		int retry = 0;
		param.strUrl = "http://" + DEV_IP + "/cgi-bin/index.cgi?opcode=dvbs_tp_list&sat_id=" + param.mSat.getSatId();
		Log.w("satip", "URL : " + param.strUrl);
		
		while( retry++ < RETRY_COUNT && doInGetTpList(param) == false )
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
			if(param.handler != null)
			{
				Message msg = param.handler.obtainMessage();
				msg.what = PROGRESS_GET_TP_LIST_DONE;
				param.handler.sendMessage(msg);
			}
		}		
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void opTryLock(DataTaskParam param)
	{
		Log.w("satip", "OP_TRY_LOCK");
		
		int retry = 0;
		param.strUrl = String.format("http://%s/cgi-bin/index.cgi?opcode=try_lock&fe_id=0&rf_id=%d", 
									DEV_IP, param.mTp.getRfId());
		Log.w("satip", "URL : " + param.strUrl);
		
		if( doInTryLock(param) == false )
		{
			if(param.handler != null)
			{
				Message msg = param.handler.obtainMessage();
				msg.what = PROGRESS_LOCK_TRY_FAIL;
				param.handler.sendMessage(msg);
			}
		}
		else
		{					
			if(param.handler != null)
			{
				Message msg = param.handler.obtainMessage();
				msg.what = PROGRESS_LOCK_TRY_DONE;
				param.handler.sendMessage(msg);
			}
		}		
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected boolean doInTryLock(DataTaskParam param)
	{
		Log.w("satip", "doInGetSystemInfo()");
		Log.w("satip", "URL : " + param.strUrl);

		//----------------------------------------------------------------------------------------//
		String strJson = requestToHttp(param.strUrl, 10000);
		String strDvbs;
		
		try
		{
			JSONObject joAll = new JSONObject(strJson);
			if( joAll.getString("opcode").equals("try_lock") == false )
				return false;
		}
		catch(JSONException exJson)
		{
			Log.e("satip", "TRY LOCK FAIL.");
			return false;
		}
		
		//----------------------------------------------------------------------------------------//
		try
		{
			Message msg = null;
			
			JSONObject joAll = new JSONObject(strJson);
			String myip = joAll.getString("my_ip");
			String answer = joAll.getString("answer");
			int strength = joAll.getInt("strength");
			int snr = joAll.getInt("snr");
			int	ber = joAll.getInt("ber");
			int	unc = joAll.getInt("unc");
			
			DbManager.updateTpInfoTryLock(param.mTp.getRfId(), joAll.getString("answer"), joAll.getInt("strength"),
					joAll.getInt("snr"), joAll.getInt("ber"), joAll.getInt("unc"));
		}
		catch(JSONException e)
		{
			Log.e("satip", e.toString());
			return false;
		}
		
		//DbManager.dumpCompanyAll();
		
		return true;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void opScan(DataTaskParam param)
	{
		Log.w("satip", "OP_SCAN");
		
		int retry = 0;
		param.strUrl = String.format("http://%s/cgi-bin/index.cgi?opcode=scan&fe_id=0&rf_id=%d", 
									DEV_IP, param.mTp.getRfId());
		Log.w("satip", "URL : " + param.strUrl);
		
		while( retry++ < RETRY_COUNT && doInScan(param) == false )
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
			if(param.handler != null)
			{
				Message msg = param.handler.obtainMessage();
				msg.what = PROGRESS_SCAN_DONE;
				param.handler.sendMessage(msg);
			}
		}		
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected boolean doInScan(DataTaskParam param)
	{
		Log.w("satip", "doInScan()");
		Log.w("satip", "URL : " + param.strUrl);

		//----------------------------------------------------------------------------------------//
		String strJson = requestToHttp(param.strUrl, 20000);
		String strDvbs;
		
		try
		{
			JSONObject joAll = new JSONObject(strJson);
			if( joAll.getString("opcode").equals("channel_list") == false )
				return false;
		}
		catch(JSONException exJson)
		{
			Log.e("satip", "TRY SCAN FAIL.");
			return false;
		}
		
		//----------------------------------------------------------------------------------------//
		try
		{
			Message msg = null;
			
			JSONObject joAll = new JSONObject(strJson);
			String myip = joAll.getString("my_ip");
			String type = joAll.getString("type");
			int rfId = joAll.getInt("rf_id");
			int count = joAll.getInt("count");

			
			JSONArray arrJson = new JSONArray(joAll.getString("channel"));
			
			Log.i("satip", "CHANNEL COUNT = " + arrJson.length());
			
			for(int i = 0; i < arrJson.length(); i++)
			{
				JSONObject jo = arrJson.getJSONObject(i);
				
				Channel ch = new Channel(	jo.getInt("ch_id")
											, jo.getString("name")
											, jo.getString("fe_type")
											, jo.getInt("vid_pid")
											, jo.getInt("aud_pid")
											, jo.getInt("pcr_pid")
											, jo.getInt("vid_type")
											, jo.getInt("aud_type")
											, jo.getInt("service_id")
											, jo.getInt("cas_id")
											, jo.getInt("rf_id")
											);
				
				DbManager.insertChannel(ch);
			}
		}
		catch(JSONException jsone)
		{
			Log.e("satip", jsone.toString());
			return false;
		}
		catch(Exception e)
		{
			Log.e("satip", e.toString());
			return false;
		}
		finally
		{
			Log.w("satip", "finally in doInScan()");
		}
		//DbManager.dumpCompanyAll();
		
		return true;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected void opStartStream(DataTaskParam param)
	{
		Log.w("satip", "opStartStream()");
		Log.w("satip", "URL : " + param.strUrl);
		
		int retry = 0;
		
		while( retry++ < RETRY_COUNT && doInStartStream(param) == false )
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
	protected boolean doInStartStream(DataTaskParam param)
	{
		Log.w("satip", "doInScan()");
		Log.w("satip", "URL : " + param.strUrl);

		//----------------------------------------------------------------------------------------//
		String strJson = requestToHttp(param.strUrl, 20000);
		String strDvbs;
		
		try
		{
			JSONObject joAll = new JSONObject(strJson);
			if( joAll.getString("opcode").equals("streaming") == false )
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
			String handle = joAll.getString("handle");
			
			if( answer.equals("true") &&  param.handler != null)
			{
				Log.w("satip", "send msg");
				Message msg = param.handler.obtainMessage();
				msg.what = PROGRESS_START_STREAMING_DONE;
				msg.obj = handle;
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
	protected void opStopStream(DataTaskParam param)
	{
		Log.w("satip", "opStopStream()");
		Log.w("satip", "URL : " + param.strUrl);
		
		int retry = 0;
		
		while( retry++ < RETRY_COUNT && doInStopStream(param) == false )
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
//				if(param.handler != null)
//				{
//					Message msg = param.handler.obtainMessage();
//					msg.what = PROGRESS_START_STREAMING_DONE;
//					param.handler.sendMessage(msg);
//				}
		}				
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected boolean doInStopStream(DataTaskParam param)
	{
		Log.w("satip", "doInStopStream()");
		Log.w("satip", "URL : " + param.strUrl);

		//----------------------------------------------------------------------------------------//
		String strJson = requestToHttp(param.strUrl, 20000);
		String strDvbs;
		
		try
		{
			JSONObject joAll = new JSONObject(strJson);
			if( joAll.getString("opcode").equals("streaming") == false )
				return false;
		}
		catch(JSONException exJson)
		{
			Log.e("satip", "STREAMING STOP FAIL.");
			return false;
		}
		
		//----------------------------------------------------------------------------------------//
		try
		{
			JSONObject joAll = new JSONObject(strJson);
			String myip = joAll.getString("my_ip");
			String answer = joAll.getString("answer");
			String handle = joAll.getString("handle");
			
//			if( answer.equals("true") &&  param.handler != null)
//			{
//				Log.w("satip", "send msg");
//				Message msg = param.handler.obtainMessage();
//				msg.what = PROGRESS_START_STREAMING_DONE;
//				msg.obj = handle;
//				param.handler.sendMessage(msg);
//			}			
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
	@Override
	protected String doInBackground(DataTaskParam... params)
	{
		Log.w("satip", "doInBackground()");
		DataTaskParam param = params[0];
		String strRead = null;
		//mDbManager = param.mDbManager;
		
		switch(param.opType)
		{
			case DataTaskParam.OP_CONNECTING_SERVER:
			{
				sendWhereAU(param);
				break;
			}
			case DataTaskParam.OP_GET_SYSTEMINFO:
			{
				opGetSystemInfo(param);
				break;
			}
			case DataTaskParam.OP_GET_SAT_LIST:
			{
				opGetSatList(param);				
				break;		
			}
			case DataTaskParam.OP_GET_TP_LIST:
			{
				opGetTpList(param);	
				break;		
			}	
			case DataTaskParam.OP_TRY_LOCK:
			{
				opTryLock(param);
				break;
			}
			case DataTaskParam.OP_SCAN:
			{
				opScan(param);
				break;
			}
			case DataTaskParam.OP_START_STREAM:
			{
				opStartStream(param);
				break;
			}
			case DataTaskParam.OP_STOP_STREAM:
			{
				opStopStream(param);
				break;
			}
		}
		
		return strRead;
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
	public static String getDeviceIpAddr()
	{
		return DEV_IP;
	}
}
