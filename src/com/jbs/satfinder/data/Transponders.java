package com.jbs.satfinder.data;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Transponders 
{
	ArrayList<Transponder> arrTransponders;
	boolean mHasTransponder = false;

	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	public Transponders()
	{
		
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	public ArrayList getTransponders(int satId)
	{
		SQLiteDatabase db;
		
		if( (db = DbManager.getDb()) == null )
			return null;
		
		if(arrTransponders == null)
			arrTransponders = new ArrayList<Transponder>();
		
		if(arrTransponders.size() > 0)
			arrTransponders.clear();
		
		String query = "";
		query += "SELECT rf_id, freq, sym, org_netid, netid, tsid, polar, system, modulation, ";
		query += "has_cach, sat_id, answer, strength, snr, ber, unc ";
		query += "FROM dvbs_tp ";
		query += "WHERE sat_id = " + satId + ";";

		//Log.w("satip", "getTransponders() : " + query);
		
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		//Log.w("satip", "Count : " + cursor.getCount());
		
		if(cursor.getCount() == 0)
		{
			Log.e("satip", "ERROR-getTransponders() : Has no data.");
		}
		
		for(int i = 0; i < cursor.getCount(); i++)
		{
			Transponder transponder = new Transponder(cursor.getInt(0)	// rf_id
										, cursor.getInt(1)			// freq
										, cursor.getInt(2)			// sym
										, cursor.getInt(3)			// org_netid
										, cursor.getInt(4)			// netid
										, cursor.getInt(5)			// tsid
										, cursor.getString(6)		// polar
										, cursor.getString(7)		// system
										, cursor.getString(8)		// modulation
										, cursor.getInt(9)			// has_cach
										, cursor.getInt(10)			// sat_id
										, cursor.getString(11)		// answer
										, cursor.getInt(12)			// strength
										, cursor.getInt(13)			// snr
										, cursor.getInt(14)			// ber
										, cursor.getInt(15)			// unc
										);
			
			//Log.w("satip", transponder.toString());
			arrTransponders.add(transponder);
			cursor.moveToNext();
		}
		
		cursor.close();
		
		if(arrTransponders.size() == 0)
		{
			mHasTransponder = false;
			Transponder transponder = new Transponder(-1, -1, -1, -1, -1, -1, null, null, null, -1, -1);
			arrTransponders.add(transponder);
		}
		else
		{
			mHasTransponder = true;
		}
		
		return arrTransponders;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public boolean HasTransponder()
	{
		return mHasTransponder;
	}
}
