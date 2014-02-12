package com.jbs.satfinder;

import java.util.ArrayList;

import com.jbs.satfinder.data.Channel;
import com.jbs.satfinder.data.Channels;
import com.jbs.satfinder.data.DataTask;
import com.jbs.satfinder.data.DataTaskParam;
import com.jbs.satfinder.data.DbManager;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ChannelListFTA extends Activity 
{
	Channels mChannels = null;
	ListView mListView = null;
	ChannelListAllAdapter mListAdapter = null;
	ArrayList<Channel> mArrChannels;
	String mStreamHandle = null;
	
	DbManager mDbManager = null;
	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	Log.w("satip", "onCreate() in ChannelListAll");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_list);

		
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
    public void onStart()
    {
    	super.onStart();
    	
    	mDbManager = new DbManager();
    	
		if(mDbManager.openDatabase(this) == true)
			Log.i("ggbus", "DB OPEN");
		else
			Log.e("ggbus", "DB OPEN ERROR...!!!"); 
		
		updateList();
    }
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
    public void onPause()
    {
    	mDbManager.closeDatabase();
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
	protected boolean updateList()
	{
		mChannels = new Channels();
		mArrChannels = mChannels.getChannels(mDbManager, Channels.CHANNEL_FTA);
		
		//------------------------------------------------------------------------------------//
		mListView=(ListView)findViewById(R.id.lvChannel);

		//------------------------------------------------------------------------------------//
		// ListView Header
//		String strMsgHeader = "";
//		strMsgHeader += "여러 방법을 통하여 수집된 모든 업체의 목록입니다.\n"; // ~29
//		strMsgHeader += "신뢰할 수 없는 업체가 포함되어 있을 수 있습니다.\n"; // ~ 58
//		strMsgHeader += "자료 출처를 확인해 주세요.";
//		
//		View header = getLayoutInflater().inflate(R.layout.company_list_help, null, false);
//		TextView tvMessage = (TextView)header.findViewById(R.id.tvMessage);
//		tvMessage.setText(strMsgHeader);
//		
//		mListView.addHeaderView(header);
		
//		Spannable tspan = (Spannable)tvMessage.getText();
//		tspan.setSpan(new ForegroundColorSpan(0xFF0000FF), 11, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		tspan.setSpan(new ForegroundColorSpan(0xFFFF0000), 29, 44, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		tspan.setSpan(new ForegroundColorSpan(0xFF0000FF), 58, 66, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		//------------------------------------------------------------------------------------//
		mListAdapter = new ChannelListAllAdapter(ChannelListFTA.this, mArrChannels);

		//MyList=(ListView)findViewById(R.id.list);
		mListView.setAdapter(mListAdapter);
		mListView.setOnItemClickListener(mItemClickListener);
				
		return true;
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
}
//------------------------------------------------------------------------------------------------//
//END OF class ChannelListAll
//------------------------------------------------------------------------------------------------//

//------------------------------------------------------------------------------------------------//
//	어댑터 클래스
//------------------------------------------------------------------------------------------------//
class ChannelListFTAAdapter extends BaseAdapter
{
//	Context maincon;
	LayoutInflater mInflater;
	ArrayList<Channel> mArrChannels;
	int layout;
	Context m_context;

	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public ChannelListFTAAdapter(Context context, ArrayList<Channel> arrSrc)
	{
		m_context = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mArrChannels = arrSrc;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public int getCount() {
		return mArrChannels.size();
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public String getItem(int position) {
		return mArrChannels.get(position).getName();
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
			view = mInflater.inflate(R.layout.channel_list_item, parent, false);

		//----------------------------------------------------------------------------------------//
		ImageView ivType = (ImageView) view.findViewById(R.id.ivType);
		TextView tvName = (TextView)view.findViewById(R.id.tvName);
		ImageView ivScrambled = (ImageView) view.findViewById(R.id.ivScrambled);
		
		if(mArrChannels.get(position).getVidPid() == 0)
			ivType.setImageResource(R.drawable.ico_radio);
		else
			ivType.setImageResource(R.drawable.ico_tv);
		
		tvName.setText(mArrChannels.get(position).getName());

		if(mArrChannels.get(position).getCasId() == 0 )
			ivScrambled.setVisibility(View.INVISIBLE);
		else
			ivScrambled.setVisibility(View.VISIBLE);
		
		return view;
	}
}