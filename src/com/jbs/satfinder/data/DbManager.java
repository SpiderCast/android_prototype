package com.jbs.satfinder.data;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

//------------------------------------------------------------------------------------------------//
//
//------------------------------------------------------------------------------------------------//
public class DbManager 
{
	//Context context = null;
	static SQLiteHelper dbHelper = null;
	static SQLiteDatabase db = null;
	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public static boolean openDatabase(Context context)
	{
		if(dbHelper == null)
			dbHelper = new SQLiteHelper(context);
		
		if(dbHelper == null) return false;
		
		if(db == null)
			db = dbHelper.getWritableDatabase();

		if(db == null) return false;
		
		return true;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public static void closeDatabase()
	{
		dbHelper.close();
		db = null;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public static SQLiteDatabase getDb()
	{
		if (db != null)
			return db;
		else
			return null;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public static Satellite getSatInfoBySatId(int satId)
	{
		if(getDb() == null) return null;
		
		Cursor cursor = db.query(true, "dvbs",   
	            new String[]{"sat_id", "name", 
							"lnb", "lnbf_low", "lnbf_hi", "lnbf_ref", "lnbp", 
							"_22khz", "diseqc", "diseqc_port", "sat_pos", "key_tp"},   
	            "sat_id=" + satId, null, null, null, null, null); 
		
		if( cursor.getCount() == 0 ) return null;
		
		cursor.moveToFirst();
		
		Satellite sat = new Satellite(	cursor.getInt(0), 			// sat_id
											cursor.getString(1), 	// name
											cursor.getString(2), 	// lnb
											cursor.getInt(3), 		// lnbf_low
											cursor.getInt(4), 		// lnbf_hi
											cursor.getInt(5), 		// lnbf_ref
											cursor.getString(6), 	// lnbp
											cursor.getString(7), 	// _22khz
											cursor.getString(8),	// diseqc
											cursor.getInt(9), 		// diseqc_port
											cursor.getInt(10),		// sat_pos
											cursor.getInt(11) 		// key_tp
											);
		
		cursor.close();
		
		return sat;
	}	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public static void updateSatInfo(Satellite sat)
	{
		if(getDb() == null) return;
		
		try
		{
			ContentValues values = new ContentValues();
			values.put("sat_id", sat.getSatId());
			values.put("name", sat.getName());
			values.put("lnb", sat.getLnb());
			values.put("lnbf_low", sat.getLnbfLow());
			values.put("lnbf_hi", sat.getLnbfHi());
			values.put("lnbf_ref", sat.getLnbfRef());
			values.put("lnbp", sat.getLnbp());
			values.put("_22khz", sat.get22Khz());
			values.put("diseqc", sat.getDiseqc());
			values.put("diseqc_port", sat.getDiseqcPort());
			values.put("sat_pos", sat.getSatPos());
			values.put("key_tp", sat.getKeyTp());
			
			//Log.w("satip", values.toString());
			getDb().insert("dvbs", null, values);
		}
		catch(SQLException sqle)
		{
			Log.e("satip", "ERROR ERROR ERROR -> updateSatInfo()");
		}
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public static Transponder getTpInfoByRfId(int rfId)
	{
		if(getDb() == null) return null;
		
		Cursor cursor = db.query(true, "dvbs_tp",   
	            new String[]{"rf_id", "freq", "sym", "polar", "system", "modulation", "sat_id", 
							"answer", "strength", "snr", "ber", "unc"},   
	            "rf_id=" + rfId, null, null, null, null, null); 
		
		if( cursor.getCount() == 0 ) return null;
		
		cursor.moveToFirst();
		
		Transponder tp = new Transponder(	cursor.getInt(0), // rf_id
											cursor.getInt(1), // freq
											cursor.getInt(2), // sym
											0, // org_netid
											0, // netid
											0, // tsid
											cursor.getString(3), // polar
											cursor.getString(4), // system
											cursor.getString(5), // modulation
											0, // has_cach
											cursor.getInt(6), // sat_id
											cursor.getString(7), // answer
											cursor.getInt(8), // strength
											cursor.getInt(9), // snr
											cursor.getInt(10), // ber
											cursor.getInt(11) // unc
											);
		
		cursor.close();
		
		return tp;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public static void updateTpInfo(Transponder tp)
	{
		if(getDb() == null) return;
		
		try
		{
			ContentValues values = new ContentValues();
			values.put("rf_id", tp.getRfId());
			values.put("freq", tp.getFreq());
			values.put("sym", tp.getSym());
			values.put("org_netid", tp.getOrgNetId());
			values.put("netid", tp.getNetId());
			values.put("tsid", tp.getTsId());
			values.put("polar", tp.getPolar());
			values.put("has_cach", tp.getHasCach());
			values.put("sat_id", tp.getSatId());
			
			//Log.w("satip", values.toString());
			getDb().insert("dvbs_tp", null, values);
		}
		catch(SQLException sqle)
		{
			Log.e("satip", "ERROR ERROR ERROR -> updateTpInfo()");
		}
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public static void updateTpInfoTryLock(int rfId, String answer, int strength, int snr, int ber, int unc)
	{
		if(getDb() == null) return;
		
		try
		{
			ContentValues values = new ContentValues();
			values.put("answer", answer);
			values.put("strength", strength);
			values.put("snr", snr);
			values.put("ber", ber);
			values.put("unc", unc);
			
			//Log.w("satip", values.toString());
			getDb().update("dvbs_tp", values, "rf_id=" + rfId, null);
		}
		catch(SQLException sqle)
		{
			Log.e("satip", "ERROR ERROR ERROR -> updateTpInfo()");
		}
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public static void insertChannel(Channel ch)
	{
		if(getDb() == null) return;
		
		try
		{
			ContentValues values = new ContentValues();
			values.put("ch_id", ch.getChId());
			values.put("name", ch.getName());
			values.put("fe_type", ch.getFeType());
			values.put("vid_pid", ch.getVidPid());
			values.put("aud_pid", ch.getAudPid());
			values.put("pcr_pid", ch.getPcrPid());
			values.put("vid_type", ch.getVidType());
			values.put("aud_type", ch.getAudtype());
			values.put("service_id", ch.getServiceId());
			values.put("cas_id", ch.getCasId());
			values.put("rf_id", ch.getRfId());
			
			Log.w("satip", values.toString());
			getDb().insert("channel", null, values);
		}
		catch(SQLException sqle)
		{
			Log.e("satip", "ERROR ERROR ERROR -> insertChannel()");
		}
		finally
		{
			Log.w("satip", "finally in insertChannel()");
		}
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public static void deleteChannelByRfId(int rfId)
	{
		String query = "";
		query += "DELETE FROM channel WHERE rf_id = " + rfId + ";";
		
		getDb().execSQL(query);				
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public static void deleteSatAll()
	{
		String query = "";
		query += "DELETE FROM dvbs;";
		
		getDb().execSQL(query);				
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public static void deleteTpAll()
	{
		String query = "";
		query += "DELETE FROM dvbs_tp;";
		
		getDb().execSQL(query);				
	}		
}
//------------------------------------------------------------------------------------------------//
//
//------------------------------------------------------------------------------------------------//
