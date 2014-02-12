package com.jbs.satfinder.data;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Channels 
{
	public final static byte CHANNEL_ALL		= 1;
	public final static byte CHANNEL_FAV		= CHANNEL_ALL + 1; 
	public final static byte CHANNEL_FTA		= CHANNEL_FAV + 1;
	public final static byte CHANNEL_SCRAMBLED	= CHANNEL_FTA + 1;
	
	
	ArrayList<Channel> mChannels;
	boolean mHasChannels = false;
	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	public Channels()
	{
		
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	public ArrayList getChannels(DbManager dbManager, byte chType)
	{
		SQLiteDatabase db;
		
		if( (db = dbManager.getDb()) == null )
			return null;
		
		if(mChannels == null)
			mChannels = new ArrayList<Channel>();
		
		if(mChannels.size() > 0)
			mChannels.clear();
		
		String query = "";
		query += "SELECT ch_id, name, fe_type, vid_pid, aud_pid, pcr_pid, vid_type, aud_type, service_id, cas_id, rf_id ";
		query += "FROM channel ";
		
		if( chType == Channels.CHANNEL_FTA )
			query += "WHERE cas_id = 0 ";
		else if( chType == Channels.CHANNEL_SCRAMBLED )
			query += "WHERE cas_id > 0 ";
		
		query += "ORDER BY name ";
		query += ";";

		Log.w("satip", "getChannels() : " + query);
		
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		Log.w("satip", "Count : " + cursor.getCount());
		
		if(cursor.getCount() == 0)
		{
			Log.e("satip", "ERROR-getChannels() : Has no CHANNELS.");
		}
		
		for(int i = 0; i < cursor.getCount(); i++)
		{
			Channel ch = new Channel(	cursor.getInt(0)		// ch_id
										, cursor.getString(1)	// name
										, cursor.getString(2)	// fe_type
										, cursor.getInt(3)		// vid_pid
										, cursor.getInt(4)		// aud_pid
										, cursor.getInt(5)		// pcr_pid
										, cursor.getInt(6)		// vid_type
										, cursor.getInt(7)		// aud_type
										, cursor.getInt(8)		// service_id
										, cursor.getInt(9)		// cas_id
										, cursor.getInt(10)		// rf_id	
										);
			
			//Log.w("satip", transponder.toString());
			mChannels.add(ch);
			cursor.moveToNext();
		}
		
		cursor.close();
		
		if(mChannels.size() == 0)
		{
			mHasChannels = false;
			Channel ch = new Channel(-1, null, null, -1, -1, -1, -1, -1, -1, -1, -1);
			mChannels.add(ch);
		}
		else
		{
			mHasChannels = true;
		}
		
		return mChannels;
	}

	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	public ArrayList getChannelsByRfId(int rfId)
	{
		SQLiteDatabase db;
		
		if( (db = DbManager.getDb()) == null )
			return null;
		
		if(mChannels == null)
			mChannels = new ArrayList<Channel>();
		
		if(mChannels.size() > 0)
			mChannels.clear();
		
		String query = "";
		query += "SELECT ch_id, name, fe_type, vid_pid, aud_pid, pcr_pid, vid_type, aud_type, service_id, cas_id, rf_id ";
		query += "FROM channel ";
		query += "WHERE rf_id = " + rfId + " ";
		//query += "ORDER BY name ";
		query += ";";

		Log.w("satip", "getChannels() : " + query);
		
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		Log.w("satip", "Count : " + cursor.getCount());
		
		if(cursor.getCount() == 0)
		{
			Log.e("satip", "ERROR-getChannels() : Has no CHANNELS.");
		}
		
		for(int i = 0; i < cursor.getCount(); i++)
		{
			Channel ch = new Channel(	cursor.getInt(0)		// ch_id
										, cursor.getString(1)	// name
										, cursor.getString(2)	// fe_type
										, cursor.getInt(3)		// vid_pid
										, cursor.getInt(4)		// aud_pid
										, cursor.getInt(5)		// pcr_pid
										, cursor.getInt(6)		// vid_type
										, cursor.getInt(7)		// aud_type
										, cursor.getInt(8)		// service_id
										, cursor.getInt(9)		// cas_id
										, cursor.getInt(10)		// rf_id	
										);
			
			//Log.w("ggbus", transponder.toString());
			mChannels.add(ch);
			cursor.moveToNext();
		}
		
		cursor.close();
		
		if(mChannels.size() == 0)
		{
			mHasChannels = false;
			Channel ch = new Channel(-1, null, null, -1, -1, -1, -1, -1, -1, -1, -1);
			mChannels.add(ch);
		}
		else
		{
			mHasChannels = true;
		}
		
		return mChannels;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public boolean HasChannels()
	{
		return mHasChannels;
	}	
}
