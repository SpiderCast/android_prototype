package com.jbs.satfinder;

import java.util.ArrayList;
import java.util.Iterator;

import com.jbs.satfinder.data.DataTask;
import com.jbs.satfinder.data.DataTaskParam;
import com.jbs.satfinder.data.DbManager;
import com.jbs.satfinder.data.Transponder;
import com.jbs.satfinder.data.Transponders;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jbs.satfinder.data.Channel;
import com.jbs.satfinder.data.Channels;

public class Scan extends Activity 
{
	ListView mListView = null;
	ScanListAdapter mListAdapter = null;
	
	ArrayList<Channel> mArrChannels = null;
	Channels mChannels = null;
	String mStreamHandle = null;
	
	int mRfId = -1;
	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan);
		
		Intent intent = getIntent();
		mRfId = intent.getIntExtra("rf_id", -1);
//		
	
		
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
		
		DbManager.deleteChannelByRfId(mRfId);
		scan(mRfId);
		mChannels = new Channels();
//		updateList();		
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
	protected void onResume()
	{
		super.onResume();
		Log.e("satip", "onResume() !!!!!");
		stopStreaming();
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void scan(int rfId)
	{
		Log.e("satip", "SCAN() !!!!!");
		
		DataTaskParam param = new DataTaskParam();
		param.opType = DataTaskParam.OP_SCAN;
		param.handler = mHandler;
		param.mTp = DbManager.getTpInfoByRfId(rfId);
		
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
				case DataTask.PROGRESS_SCAN_DONE:
				{
					Log.w("satpi", "PROGRESS_SCAN_DONE");
					
					ProgressBar pbScanning = (ProgressBar)findViewById(R.id.pbScanning);
					pbScanning.setVisibility(ProgressBar.INVISIBLE);
					
					TextView tvScanning = (TextView)findViewById(R.id.tvScanning);
					tvScanning.setText("Scan Complete.");
					
					updateList();
					break;
				}
				case DataTask.PROGRESS_START_STREAMING_DONE:
				{
					mStreamHandle = (String)msg.obj;
					String strStream = "http://" + DataTask.getDeviceIpAddr() + "/streaming/0x" + mStreamHandle + ".ts";
					Log.w("satip", strStream);
					
					Intent i = new Intent(Intent.ACTION_VIEW);
					Uri uri = Uri.parse(strStream);
					i.setDataAndType(uri, "video/*");
					startActivity(i);
					
					break;
				}
				case DataTask.PROGRESS_RETRY_FAIL:
				{
//					Log.e("satip", "PROGRESS_RETRY_FAIL : ");
//					dismissDialog(DLG_DOWNLOAD_PROGRESS);
//				
//					AlertDialog.Builder dlgFail;
//					dlgFail = new AlertDialog.Builder(SatFinder.this);
//					dlgFail.setTitle("데이타 수신 실패...");
//					dlgFail.setMessage("서버로부터 데이타를 가져올 수 없습니다.\n\n네트웍 상태를 확인해주세요.");
//					dlgFail.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int whichButton) {
//						}
//					});
//					dlgFail.show();
					
					break;
				}				
			}
		}
	};
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void stopStreaming()
	{
		if(mStreamHandle == null) return;
		
		DataTaskParam param = new DataTaskParam();
		param.opType = DataTaskParam.OP_STOP_STREAM;
		param.handler = mHandler;
		// "?opcode=streaming&stop=xxxxx"
		param.strUrl = "http://" + DataTask.getDeviceIpAddr() 
						+ "/cgi-bin/index.cgi?opcode=streaming&stop=" 
						+ mStreamHandle;

		//showDialog(DLG_DOWNLOAD_PROGRESS);
		new DataTask().execute(param);
		
		mStreamHandle = null;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void startStreaming(int position)
	{
		if(mStreamHandle != null)
		{
			stopStreaming();
			
			try
			{
				Thread.sleep(1000);
			}
			catch(Exception ex)
			{
				
			}
		}
		
		DataTaskParam param = new DataTaskParam();
		param.opType = DataTaskParam.OP_START_STREAM;
		param.handler = mHandler;
		// "?opcode=streaming&type=http&fe_id=0&ch_id=1"
		param.strUrl = "http://" + DataTask.getDeviceIpAddr() 
						+ "/cgi-bin/index.cgi?opcode=streaming&type=http&fe_id=0&ch_id=" 
						+ mArrChannels.get(position).getChId();

		//showDialog(DLG_DOWNLOAD_PROGRESS);
		new DataTask().execute(param);
	}	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView parent, View view, int position, long id)
		{
			Log.w("satip", "Start Streaming...");
			
			startStreaming(position);
		}
	};
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected boolean updateList()
	{
		mArrChannels = mChannels.getChannelsByRfId(mRfId);
		
		//----------------------------------------------------------------------------------------//
		mListView=(ListView)findViewById(R.id.lvChannel);
		
		//---------------------------------------------------------------------------------------//
		mListAdapter = new ScanListAdapter(Scan.this, mArrChannels);

		mListView.setAdapter(mListAdapter);
		mListView.setOnItemClickListener(mItemClickListener);
		
		//---------------------------------------------------------------------------------------//
		int tvCount = 0;
		int radioCount = 0;
		
		Iterator iter = mArrChannels.iterator();
		
		while(iter.hasNext())
		{
			Channel ch = (Channel)iter.next();
			
			if( ch.getVidPid() != 0 )
				tvCount += 1;
			else
				radioCount += 1;
		}
		
		TextView tvTV = (TextView)findViewById(R.id.tvTV);
		tvTV.setText("TV : " + tvCount);
		
		TextView tvRadio = (TextView)findViewById(R.id.tvRadio);
		tvRadio.setText("Radio : " + radioCount);

		return true;
	}	
}

//------------------------------------------------------------------------------------------------//
//
//------------------------------------------------------------------------------------------------//
class ScanListAdapter extends BaseAdapter
{
	//Context maincon;
	LayoutInflater mInflater;
	ArrayList<Channel> mChannels;
	int layout;
	Context mContext;
	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public ScanListAdapter(Context context, ArrayList<Channel> arrTransponders)
	{
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mChannels = arrTransponders;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public int getCount() 
	{
		return mChannels.size();
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public String getItem(int position) 
	{
		return "" + mChannels.get(position).getRfId();
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
			view = mInflater.inflate(R.layout.channel_list_item, parent, false);

		//----------------------------------------------------------------------------------------//
		ImageView ivType = (ImageView) view.findViewById(R.id.ivType);
		TextView tvName = (TextView)view.findViewById(R.id.tvName);
		ImageView ivScrambled = (ImageView) view.findViewById(R.id.ivScrambled);
		
		if(mChannels.get(position).getVidPid() == 0)
			ivType.setImageResource(R.drawable.ico_radio);
		else
			ivType.setImageResource(R.drawable.ico_tv);
		
		tvName.setText(mChannels.get(position).getName());

		if(mChannels.get(position).getCasId() == 0 )
			ivScrambled.setVisibility(View.INVISIBLE);
		else
			ivScrambled.setVisibility(View.VISIBLE);
		
		return view;
	}
}
