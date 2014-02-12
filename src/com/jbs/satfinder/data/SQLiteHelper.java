package com.jbs.satfinder.data;

import com.jbs.satfinder.Common;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//------------------------------------------------------------------------------------------------//
//
//------------------------------------------------------------------------------------------------//
public class SQLiteHelper extends SQLiteOpenHelper
{
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public SQLiteHelper(Context context)
	{
		super(context, "db_ggbus", null, 
				Common.VER_MAJOR * 10000 + Common.VER_MINOR * 100 + Common.VER_PATCH);
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String query = "";

		//----------------------------------------------------------------------------------------//
		query = "";
		query += "CREATE TABLE IF NOT EXISTS dvbs (";
		query += "sat_id 		INTEGER PRIMARY KEY NOT NULL UNIQUE, ";
		query += "name 			CHAR NOT NULL, ";
		query += "lnb 			CHAR DEFAULT universal, ";
		query += "lnbf_low 		INTEGER DEFAULT 9750, ";
		query += "lnbf_hi 		INTEGER DEFAULT 10600, ";
		query += "lnbf_ref 		INTEGER DEFAULT 11700, ";
		query += "lnbp 			CHAR DEFAULT auto, ";
		query += "_22khz 		CHAR DEFAULT auto, ";
		query += "diseqc 		CHAR DEFAULT none, ";
		query += "diseqc_port 	INTEGER DEFAULT 0, ";
		query += "sat_pos 		INTEGER DEFAULT 0, ";
		query += "key_tp 		INTEGER DEFAULT 1 ";
		query += ");";
		Log.w("ggbus", query);
		
		db.execSQL(query);
		
		//----------------------------------------------------------------------------------------//
		query = "";
		query += "CREATE TABLE dvbs_tp (";
		query += "rf_id 		INTEGER PRIMARY KEY NOT NULL UNIQUE, ";
		query += "freq 			INTEGER NOT NULL, ";
		query += "sym 			INTEGER NOT NULL, ";
		query += "org_netid		INTEGER DEFAULT 0, ";
		query += "netid 		INTEGER DEFAULT 0, ";
		query += "tsid 			INTEGER DEFAULT 0, ";
		query += "polar 		CHAR DEFAULT hor, ";
		query += "system		CHAR DEFAULT dvbs, ";
		query += "modulation	CHAR DEFAULT qpsk, ";
		query += "has_cach 		INTEGER DEFAULT 0, ";
		query += "sat_id 		INTEGER NOT NULL, ";
		query += "answer		CHAR DEFAULT false, ";
		query += "strength  	INTEGER DEFAULT 0, ";
		query += "snr       	INTEGER DEFAULT 0, ";
		query += "ber       	INTEGER DEFAULT 0, ";
		query += "unc       	INTEGER DEFAULT 0 ";
		query += ");";
		Log.w("ggbus", query);

		db.execSQL(query);
		
		//----------------------------------------------------------------------------------------//
		query = "";
		query += "CREATE TABLE channel (";
		query += "ch_id 		INTEGER PRIMARY KEY NOT NULL UNIQUE, ";
		query += "name 			CHAR NOT NULL, ";
		query += "fe_type 		CHAR NOT NULL, ";
		query += "vid_pid		INTEGER DEFAULT -1, ";
		query += "aud_pid 		INTEGER DEFAULT -1, ";
		query += "pcr_pid 		INTEGER DEFAULT -1, ";
		query += "vid_type 		INTEGER DEFAULT -1, ";
		query += "aud_type 		INTEGER DEFAULT -1, ";
		query += "service_id 	INTEGER DEFAULT -1, ";
		query += "cas_id		INTEGER DEFAULT -1, ";
		query += "rf_id  		INTEGER DEFAULT -1 ";
		query += ");";
		Log.w("ggbus", query);

		db.execSQL(query);		
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.e("ggbus", "onUpdate()");
		db.execSQL("DROP TABLE IF EXISTS dvbs;");
		db.execSQL("DROP TABLE IF EXISTS dvbs_tp;");
		db.execSQL("DROP TABLE IF EXISTS channel;");

		onCreate(db);
	}
}
//------------------------------------------------------------------------------------------------//
//END OF public class SQLiteHelper
//------------------------------------------------------------------------------------------------//

