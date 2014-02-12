package com.jbs.satfinder.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

//------------------------------------------------------------------------------------------------//
//
//------------------------------------------------------------------------------------------------//
public class Satellites 
{
	ArrayList<Satellite> mArrSatellites;
	boolean mHasSatellites = false;

	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	public Satellites()
	{
		
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	public ArrayList getSatellites(int satId)
	{
		SQLiteDatabase db;
		
		if( (db = DbManager.getDb()) == null )
			return null;
		
		if(mArrSatellites == null)
			mArrSatellites = new ArrayList<Satellite>();
		
		if(mArrSatellites.size() > 0)
			mArrSatellites.clear();
		
		String query = "";
		query += "SELECT sat_id, name, lnb, lnbf_low, lnbf_hi, lnbf_ref, lnbp, ";
		query += "_22khz, diseqc, diseqc_port, sat_pos, key_tp ";
		query += "FROM dvbs ";
		
		if(satId > 0)
			query += "WHERE sat_id = " + satId;
		
		query += ";";
		
		Log.w("satip", "getSatellites() : " + query);
		
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		Log.w("satip", "Count : " + cursor.getCount());
		
		if(cursor.getCount() == 0)
		{
			Log.e("satip", "ERROR-getSatellites() : Has no data.");
		}
		
		for(int i = 0; i < cursor.getCount(); i++)
		{
			Satellite sat = new Satellite(cursor.getInt(0)		// sat_id
										, cursor.getString(1)		// name
										, cursor.getString(2)		// lnb
										, cursor.getInt(3)			// lnbf_low
										, cursor.getInt(4)			// lnbf_hi
										, cursor.getInt(5)			// lnbf_ref
										, cursor.getString(6)		// lnbp
										, cursor.getString(7)		// _22khz
										, cursor.getString(8)		// diseqc
										, cursor.getInt(9)			// diseqc_port
										, cursor.getInt(10)			// sat_pos
										, cursor.getInt(11)			// key_tp
										);
			
			//Log.w("satip", sat.toString());
			mArrSatellites.add(sat);
			cursor.moveToNext();
		}
		
		cursor.close();
		
		if(mArrSatellites.size() == 0)
		{
			mHasSatellites = false;
			Satellite satellite = new Satellite(-1, null, null, -1, -1, -1, null, null, null, -1, -1, -1);
			mArrSatellites.add(satellite);
		}
		else
		{
			mHasSatellites = true;
		}

/*		
		if(arrSatellites == null)
			arrSatellites = new ArrayList<Satellite>();
		
		if(arrSatellites.size() > 0)
			arrSatellites.clear();
		
		for(int i = 0; i < 10; i++)
		{
			Satellite satellite = new Satellite("Satellite" + i, i * 10);
			
			arrSatellites.add(satellite);
		}
		
		mHasSatellites = true;
*/		
		return mArrSatellites;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	public Satellite getSatellite(int satId)
	{
		Satellite sat = null;
		
		ArrayList<Satellite> arrSatellites = getSatellites(satId);
		
		if(arrSatellites.size() > 0)
			sat = arrSatellites.get(0);
			
		return sat;
	}	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public boolean HasSatellite()
	{
		return mHasSatellites;
	}
}
//------------------------------------------------------------------------------------------------//
//
//------------------------------------------------------------------------------------------------//
